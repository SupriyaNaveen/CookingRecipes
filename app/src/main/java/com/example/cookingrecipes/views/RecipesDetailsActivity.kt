package com.example.cookingrecipes.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import com.example.cookingrecipes.R
import com.example.cookingrecipes.databinding.ActivityRecipesDetailsBinding
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
    private lateinit var lifecycleOwner: LifecycleOwner

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
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        lifecycleOwner = this
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_recipes_details)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        mRecipesDetailsViewModel = ViewModelProviders.of(this, mRecipesDetailsViewModelFactory).get(
                RecipesViewModel::class.java
        )

        loadData(id = intent.getIntExtra(Constants.RECIPES_ID_INTENT_KEY, 0))

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
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(mDataBinding.recipes?.recipeLink)))
    }

    private fun loadData(id: Int) {

        launch {
            mDataBinding.recipes = mRecipesDetailsViewModel.loadRecipesDetails(id)
            val url = Utils.getYoutubeThumbnailUrlFromVideoUrl(mDataBinding.textViewPlayUrl.toString())
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
     * Setting menu in action bar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_recipes_details, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Actions on click menu items
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_delete -> {
            launch {
                val rowId = mRecipesDetailsViewModel.deleteRecipesData(mDataBinding.recipes!!)
                if (rowId != -1)
                    finish()
            }
            true
        }
        R.id.action_update -> {
            if (mDataBinding.recipes?.recipeName == mDataBinding.textViewRecipesName.text.toString() &&
                    mDataBinding.recipes?.recipeDescription == mDataBinding.textViewRecipesDescription.text.toString()
            ) {
                Toast.makeText(this, getString(R.string.error_message_up_to_date), Toast.LENGTH_LONG).show()
            } else {
                mDataBinding.recipes?.recipeName = mDataBinding.textViewRecipesName.text.toString()
                mDataBinding.recipes?.recipeDescription = mDataBinding.textViewRecipesDescription.text.toString()

                launch {
                    val rowId = mRecipesDetailsViewModel.updateRecipesData(mDataBinding.recipes!!)

                    if (rowId != -1)
                        finish()
                }
            }
            true
        }
        else -> {
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}
