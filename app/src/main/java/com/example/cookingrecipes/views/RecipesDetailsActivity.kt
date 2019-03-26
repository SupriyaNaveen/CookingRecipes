package com.example.cookingrecipes.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cookingrecipes.R
import com.example.cookingrecipes.model.data.CookingRecipes
import com.example.cookingrecipes.utils.Constants
import com.example.cookingrecipes.utils.Utils
import com.example.cookingrecipes.viewmodel.RecipesDetailsViewModel
import com.example.cookingrecipes.viewmodel.RecipesDetailsViewModelFactory
import com.squareup.picasso.Picasso
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_recipes_details.*
import java.net.MalformedURLException
import javax.inject.Inject

/**
 * Show the recipes details. On selection of link redirects to youtube app.
 * Recipes can be updated or deleted.
 */
class RecipesDetailsActivity : AppCompatActivity() {

    //Init view model
    lateinit var mRecipesDetailsViewModel: RecipesDetailsViewModel

    @Inject
    lateinit var mRecipesDetailsViewModelFactory: RecipesDetailsViewModelFactory

    lateinit var cookingRecipes: CookingRecipes

    /**
     * Creates view to show recipes details.
     * Menu option delete, update provided.
     * onDelete, remove the recipes from db and finish the ui.
     * onUpdate, update the recipes to db and finish the ui.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes_details)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        initializeRecycler()

        mRecipesDetailsViewModel = ViewModelProviders.of(this, mRecipesDetailsViewModelFactory).get(
            RecipesDetailsViewModel::class.java
        )

        loadData(id = intent.getIntExtra(Constants.RECIPES_ID_INTENT_KEY, 0))

        mRecipesDetailsViewModel.recipesDetailsResult().observe(this,
            Observer<CookingRecipes> {
                if (it != null) {
                    cookingRecipes = it
                    text_view_recipes_name.setText(it.recipeName)
                    text_view_play_url.text = it.recipeLink
                    text_view_recipes_description.setText(it.recipeDescription)
                    val url = Utils.getYoutubeThumbnailUrlFromVideoUrl(it.recipeLink!!)
                    try {
                        Picasso.get()
                            .load(url)
                            .placeholder(R.drawable.ic_kitchen_black_24dp)
                            .error(R.drawable.ic_kitchen_black_24dp)
                            .into(image_view_play_url)

                    } catch (e: MalformedURLException) {
                        e.printStackTrace()
                    }

                }
            })

        mRecipesDetailsViewModel.recipesError().observe(this, Observer<String> {
            if (it != null) {
                Toast.makeText(
                    this, resources.getString(R.string.error_message_get_recipes_failed) + it,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        mRecipesDetailsViewModel.recipesDetailsLoader().observe(this, Observer<Boolean> {
            //            if (it == false) progress_bar.visibility = View.GONE
        })

        image_view_play_url.setOnClickListener {
            playYoutubeVideo()
        }

        text_view_play_url.setOnClickListener {
            playYoutubeVideo()
        }

        mRecipesDetailsViewModel.recipesDeleteResult().observe(this,
            Observer<Int> {
                if (it > 0) {
                    val intent = Intent()
                    intent.putExtra(Constants.DELETED_RECIPES_INTENT_KEY, cookingRecipes)
                    setResult(Constants.RESULT_CODE_DELETE, intent)
                    finish()//finishing activity
                }
            })

        mRecipesDetailsViewModel.recipesUpdateResult.observe(this,
            Observer<Int> {
                if (it > 0) {
                    val intent = Intent()
                    intent.putExtra(Constants.UPDATED_RECIPES_INTENT_KEY, cookingRecipes)
                    setResult(Constants.RESULT_CODE_UPDATE, intent)
                    finish()//finishing activity
                }
            })
    }

    /**
     * Start youtube application.
     */
    private fun playYoutubeVideo() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(text_view_play_url.text.toString())))
    }

    /**
     * TODO : Load categories
     */
    private fun initializeRecycler() {
        val gridLayoutManager = GridLayoutManager(this, 1)
        gridLayoutManager.orientation = RecyclerView.VERTICAL
//        recycler_view_recipes.apply {
//            setHasFixedSize(true)
//            layoutManager = gridLayoutManager
//        }
    }

    private fun loadData(id: Int) {
        mRecipesDetailsViewModel.loadRecipesDetails(id)
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
            // User chose the "Delete" item
            mRecipesDetailsViewModel.deleteRecipesDetails(cookingRecipes)
            true
        }
        R.id.action_update -> {
            if (cookingRecipes.recipeName == text_view_recipes_name.text.toString() &&
                cookingRecipes.recipeDescription == text_view_recipes_description.text.toString()
            ) {
                Toast.makeText(this, getString(R.string.error_message_up_to_date), Toast.LENGTH_LONG).show()
            } else {
                cookingRecipes.recipeName = text_view_recipes_name.text.toString()
                cookingRecipes.recipeDescription = text_view_recipes_description.text.toString()
                mRecipesDetailsViewModel.updateRecipesDetails(cookingRecipes)
            }
            true
        }
        else -> {
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        mRecipesDetailsViewModel.disposeElements()
        super.onDestroy()
    }
}
