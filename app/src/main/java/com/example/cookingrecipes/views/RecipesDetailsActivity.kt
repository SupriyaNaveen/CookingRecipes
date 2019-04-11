package com.example.cookingrecipes.views

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.cookingrecipes.R
import com.example.cookingrecipes.databinding.ActivityRecipesDetailsBinding
import com.example.cookingrecipes.model.data.Category
import com.example.cookingrecipes.utils.Constants
import com.example.cookingrecipes.utils.Utils
import com.example.cookingrecipes.viewmodel.RecipesViewModel
import com.example.cookingrecipes.viewmodel.RecipesViewModelFactory
import com.squareup.picasso.Picasso
import dagger.android.AndroidInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.MalformedURLException
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


/**
 * Show the recipes details. On selection of link redirects to youtube app.
 * Recipes can be updated or deleted.
 */
class RecipesDetailsActivity : AppCompatActivity(), CoroutineScope {

    //Init view model
    private lateinit var mRecipesDetailsViewModel: RecipesViewModel

    @Inject
    lateinit var mRecipesDetailsViewModelFactory: RecipesViewModelFactory

    private lateinit var mDataBinding: ActivityRecipesDetailsBinding

    //Defining job for co-routines
    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    /**
     * Creates view to show recipes details.
     * Menu option delete, update provided.
     * onDelete, remove the recipes from db and finish the ui.
     * onUpdate, update the recipes to db and finish the ui.
     * Recipes by default belongs to "DEFAULT" category.
     * User can change the category that they wish to.
     * Recipes video will be played by redirecting to youtube application.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_recipes_details)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        mRecipesDetailsViewModel = ViewModelProviders.of(this, this.mRecipesDetailsViewModelFactory).get(
                RecipesViewModel::class.java
        )

        //Get the recipe id from selected recipes. Load the recipe details.
        val recipesId = intent.getIntExtra(Constants.RECIPES_ID_INTENT_KEY, 0)
        loadData(recipesId)

        //Populate category list in spinner
        populateCategoryList(recipesId)

        //Play url in youtube application
        mDataBinding.imageViewPlayUrl.setOnClickListener {
            playYoutubeVideo()
        }

        mDataBinding.textViewPlayUrl.setOnClickListener {
            playYoutubeVideo()
        }

    }

    /**
     * Start youtube application.
     */
    private fun playYoutubeVideo() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(mDataBinding.recipes?.recipeLink)))
        } catch (exception: ActivityNotFoundException) {
            Timber.e(exception)
        }
    }

    /**
     * Loads the recipes details in UI.
     * Get the thumbnail from youtube url.
     * TODO : Validate youtube url.
     */
    private fun loadData(id: Int) {

        launch {
            mDataBinding.recipes = mRecipesDetailsViewModel.loadRecipesDetails(id)
            val url = Utils.getYoutubeThumbnailUrlFromVideoUrl(mDataBinding.recipes!!.recipeLink.toString())
            try {
                Picasso.get()
                        .load(url)
                        .placeholder(R.drawable.ic_kitchen_black_24dp)
                        .error(R.drawable.ic_kitchen_black_24dp)
                        .into(mDataBinding.imageViewPlayUrl)

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }

        }
    }

    /**
     * Setting menu in action bar for update and delete recipes.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_recipes_details, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Actions on click menu items.
     * Delete -> delete recipes from recipes table. Delete recipes from mapping table.
     * Update -> update recipes table and mapping table (if category changes)
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_delete -> {
            launch {
                val rowId = mRecipesDetailsViewModel.deleteRecipesData(mDataBinding.recipes!!)
                val rowMapId = mRecipesDetailsViewModel.deleteMappingForRecipesId(mDataBinding.recipes!!.id!!)
                Timber.i("Deleted id's = $rowId and $rowMapId")
                if (rowId != -1)
                    finish()
            }
            true
        }
        R.id.action_update -> {
            mDataBinding.recipes?.recipeName = mDataBinding.textViewRecipesName.text.toString()
            mDataBinding.recipes?.recipeDescription = mDataBinding.textViewRecipesDescription.text.toString()

            launch {
                val rowId = mRecipesDetailsViewModel.updateRecipesData(mDataBinding.recipes!!)
                if (category != null) {
                    val rowMapId = mRecipesDetailsViewModel.updateMappingForRecipesId(mDataBinding.recipes!!.id!!, category!!.id!!)
                    Timber.i("Updated id's = $rowId and $rowMapId")
                }
                if (rowId != -1)
                    finish()
            }
            true
        }
        else -> {
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private var category: Category? = null

    private fun populateCategoryList(recipesId: Int) {
        launch {
            val recipesCategoryMapping = mRecipesDetailsViewModel.getRecipesCategoryMappingForRecipesId(recipesId)
            mRecipesDetailsViewModel.getCategoriesList().observe(this@RecipesDetailsActivity, Observer {

                //Category list can not be null as we have "DEFAULT" category.
                if (!it.isEmpty()) {
                    val adapter = ArrayAdapter(this@RecipesDetailsActivity, android.R.layout.simple_spinner_item, it)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    mDataBinding.spinnerRecipesCategory.adapter = adapter

                    //Set the default value of spinner
                    for (item in it) {
                        if (item.id == recipesCategoryMapping.categoryId) {
                            category = item
                            mDataBinding.spinnerRecipesCategory.setSelection(adapter.getPosition(category))
                        }
                    }

                    //Spinner selected listener
                    mDataBinding.spinnerRecipesCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                            category = it[position]
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                        }
                    }
                }
            })
        }
    }
}
