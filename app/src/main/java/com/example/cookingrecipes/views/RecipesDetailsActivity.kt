package com.example.cookingrecipes.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.cookingrecipes.R
import com.example.cookingrecipes.databinding.ActivityRecipesDetailsBinding
import com.example.cookingrecipes.model.data.CookingRecipes
import com.example.cookingrecipes.utils.Constants
import com.example.cookingrecipes.utils.Utils
import com.example.cookingrecipes.viewmodel.RecipesDetailsViewModel
import com.example.cookingrecipes.viewmodel.RecipesDetailsViewModelFactory
import com.squareup.picasso.Picasso
import dagger.android.AndroidInjection
import java.net.MalformedURLException
import javax.inject.Inject


/**
 * Show the recipes details. On selection of link redirects to youtube app.
 * Recipes can be updated or deleted.
 */
class RecipesDetailsActivity : AppCompatActivity() {

    //Init view model
    private lateinit var mRecipesDetailsViewModel: RecipesDetailsViewModel

    @Inject
    lateinit var mRecipesDetailsViewModelFactory: RecipesDetailsViewModelFactory

    private lateinit var mDataBinding: ActivityRecipesDetailsBinding

    /**
     * Creates view to show recipes details.
     * Menu option delete, update provided.
     * onDelete, remove the recipes from db and finish the ui.
     * onUpdate, update the recipes to db and finish the ui.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_recipes_details)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        mRecipesDetailsViewModel = ViewModelProviders.of(this, mRecipesDetailsViewModelFactory).get(
            RecipesDetailsViewModel::class.java
        )

        loadData(id = intent.getIntExtra(Constants.RECIPES_ID_INTENT_KEY, 0))

        mRecipesDetailsViewModel.recipesDetailsResult().observe(this,
            Observer<CookingRecipes> {
                if (it != null) {
                    mDataBinding.recipes = it
                    val url = Utils.getYoutubeThumbnailUrlFromVideoUrl(it.recipeLink!!)
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
            })

        mRecipesDetailsViewModel.recipesError().observe(this, Observer<String> {
            if (it != null) {
                Toast.makeText(
                    this, resources.getString(R.string.error_message_get_recipes_failed) + it,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        mDataBinding.imageViewPlayUrl.setOnClickListener {
            playYoutubeVideo()
        }

        mDataBinding.textViewPlayUrl.setOnClickListener {
            playYoutubeVideo()
        }

        mRecipesDetailsViewModel.recipesDeleteResult().observe(this,
            Observer<Int> {
                if (it > 0) {
                    val intent = Intent()
                    intent.putExtra(Constants.DELETED_RECIPES_INTENT_KEY, mDataBinding.recipes)
                    setResult(Constants.RESULT_CODE_DELETE, intent)
                    finish()//finishing activity
                }
            })

        mRecipesDetailsViewModel.recipesUpdateResult.observe(this,
            Observer<Int> {
                if (it > 0) {
                    val intent = Intent()
                    intent.putExtra(Constants.UPDATED_RECIPES_INTENT_KEY, mDataBinding.recipes)
                    setResult(Constants.RESULT_CODE_UPDATE, intent)
                    finish()//finishing activity
                }
            })
    }

    /**
     * Start youtube application.
     */
    private fun playYoutubeVideo() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(mDataBinding.recipes?.recipeLink)))
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
            mRecipesDetailsViewModel.deleteRecipesDetails(mDataBinding.recipes!!)
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
                mRecipesDetailsViewModel.updateRecipesDetails(mDataBinding.recipes!!)
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
