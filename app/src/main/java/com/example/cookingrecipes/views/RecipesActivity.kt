package com.example.cookingrecipes.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cookingrecipes.R
import com.example.cookingrecipes.model.data.CookingRecipes
import com.example.cookingrecipes.utils.Constants
import com.example.cookingrecipes.utils.InfiniteScrollListener
import com.example.cookingrecipes.viewmodel.RecipesViewModel
import com.example.cookingrecipes.viewmodel.RecipesViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_recipes.*
import javax.inject.Inject

/**
 * Activity lists all recipes in Local DB
 * TODO: Web api creation and integration to application
 */
class RecipesActivity : AppCompatActivity() {

    //Initialize variable before accessing it
    @Inject
    lateinit var mRecipesViewModelFactory: RecipesViewModelFactory
    var mRecipesAdapter = RecipesAdapter(this, ArrayList())
    lateinit var mRecipesViewModel: RecipesViewModel
    var currentPage = 0

    /**
     * List of recipes displayed.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes)

        button_add_recipes.setOnClickListener {
            val intent = Intent(this, AddRecipesActivity::class.java)
            startActivityForResult(intent, Constants.REQUEST_CODE_ADD_RECIPES)
        }

        initializeRecycler()
        mRecipesViewModel = ViewModelProviders.of(this, mRecipesViewModelFactory).get(
            RecipesViewModel::class.java
        )

        progress_bar.visibility = View.VISIBLE
        currentPage = 0
        loadData()

        mRecipesViewModel.recipesResult().observe(this,
            Observer<List<CookingRecipes>> {
                progress_bar.visibility = View.INVISIBLE
                if (it != null) {
                    val position = mRecipesAdapter.itemCount
                    mRecipesAdapter.addRecipes(it)
                    recycler_view_recipes.adapter = mRecipesAdapter
                    recycler_view_recipes.scrollToPosition(position - Constants.LIST_SCROLLING)
                }
            })

        mRecipesViewModel.recipesError().observe(this, Observer<String> {
            progress_bar.visibility = View.INVISIBLE
            if (it != null) {
                Toast.makeText(
                    this, resources.getString(R.string.error_message_get_recipes_failed) + it,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        mRecipesViewModel.recipesLoader().observe(this, Observer<Boolean> {
            if (it == false) progress_bar.visibility = View.GONE
        })

    }

    /**
     * Initialise the recycler view adapter.
     */
    private fun initializeRecycler() {
        val gridLayoutManager = GridLayoutManager(this, 1)
        gridLayoutManager.orientation = RecyclerView.VERTICAL
        recycler_view_recipes.apply {
            setHasFixedSize(true)
            layoutManager = gridLayoutManager
            addOnScrollListener(InfiniteScrollListener({ loadData() }, gridLayoutManager))
        }
    }

    /**
     * Load recipes
     */
    private fun loadData() {
        mRecipesViewModel.loadRecipes(Constants.LIMIT, currentPage * Constants.OFFSET)
        currentPage++
    }

    /**
     * Handle onActivityResult.
     * Check if result is add, delete or update. Accordingly update adapter.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.REQUEST_CODE_ADD_RECIPES) {
            if (resultCode == Activity.RESULT_OK) {
                val position = mRecipesAdapter.itemCount
                if (data != null) {
                    mRecipesAdapter.addRecipesData(data.extras?.get(Constants.ADDED_RECIPES_INTENT_KEY) as CookingRecipes)
                    recycler_view_recipes.adapter = mRecipesAdapter
                    recycler_view_recipes.scrollToPosition(position - Constants.LIST_SCROLLING)
                }
            }
        } else if (requestCode == Constants.REQUEST_CODE_RECIPES_DETAILS) {
            if (resultCode == Constants.RESULT_CODE_DELETE) {
                val position = mRecipesAdapter.itemCount
                if (data != null) {
                    mRecipesAdapter.deleteRecipesData(data.extras?.get(Constants.DELETED_RECIPES_INTENT_KEY) as CookingRecipes)
                    recycler_view_recipes.adapter = mRecipesAdapter
                    recycler_view_recipes.scrollToPosition(position - Constants.LIST_SCROLLING)
                }
            } else if (resultCode == Constants.RESULT_CODE_UPDATE) {
                val position = mRecipesAdapter.itemCount
                if (data != null) {
                    mRecipesAdapter.updateRecipesData(data.extras?.get(Constants.UPDATED_RECIPES_INTENT_KEY) as CookingRecipes)
                    recycler_view_recipes.adapter = mRecipesAdapter
                    recycler_view_recipes.scrollToPosition(position - Constants.LIST_SCROLLING)
                }
            }
        }

    }

    override fun onDestroy() {
        mRecipesViewModel.disposeElements()
        super.onDestroy()
    }
}
