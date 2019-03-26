package com.example.cookingrecipes.views

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.cookingrecipes.R
import com.example.cookingrecipes.model.data.CookingRecipes
import com.example.cookingrecipes.utils.Constants
import com.example.cookingrecipes.viewmodel.AddRecipesViewModel
import com.example.cookingrecipes.viewmodel.AddRecipesViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_recipes_add.*
import timber.log.Timber
import javax.inject.Inject

/**
 * Add recipes activity, allows the user to add CookingRecipes.
 * Recipes name, youtube link, desciption entered are added to local DB.
 * TODO: Add Rrecipes to REST API.
 */
class AddRecipesActivity : AppCompatActivity() {

    //Init view model
    private lateinit var mRecipesAddViewModel: AddRecipesViewModel

    //Inject view model factory
    @Inject
    lateinit var mAddRecipesViewModelFactory: AddRecipesViewModelFactory

    /**
     * Activity created : set view.
     * Init view model.
     * Handle recipes add query result and update UI(Show dialog and finish).
     * Handle recipes add error scenarios.
     * Invoke add recipes on add button selection.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes_add)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        mRecipesAddViewModel = ViewModelProviders.of(this, mAddRecipesViewModelFactory).get(
            AddRecipesViewModel::class.java
        )

        mRecipesAddViewModel.recipesAddResult().observe(
            this,
            Observer<Long> {
                if (it > 0) {
                    val cookingRecipes = CookingRecipes(
                        id = it.toInt(),
                        recipeName = text_view_recipe_name.text.toString(),
                        recipeLink = text_view_recipes_link.text.toString(),
                        recipeDescription = text_view_recipes_description.text.toString()
                    )
                    showRecipesAddedMessage(cookingRecipes)
                } else {
                    Timber.d("Row inserted id : $it")
                }
            }
        )

        mRecipesAddViewModel.recipesAddError().observe(this, Observer<String> {
            if (it != null) {
                Toast.makeText(
                    this, resources.getString(R.string.error_add_recipes),
                    Toast.LENGTH_SHORT
                ).show()
                text_view_recipes_link.error = getString(R.string.error_add_recipes)
            }
        })

        button_add_recipes.setOnClickListener {

            val isValidInput = isValidInput()
            if (isValidInput) {
                val cookingRecipes = CookingRecipes(
                    recipeName = text_view_recipe_name.text.toString(),
                    recipeLink = text_view_recipes_link.text.toString(),
                    recipeDescription = text_view_recipes_description.text.toString()
                )
                mRecipesAddViewModel.addRecipesDetails(cookingRecipes)

            }
        }
    }

    /**
     * Alert dialog to show the reciped added successfully.
     */
    private fun showRecipesAddedMessage(cookingRecipes: CookingRecipes) {
        val builder = AlertDialog.Builder(this)

        // Set the alert dialog title
        builder.setTitle(getString(R.string.recipes))

        // Display a message on alert dialog
        builder.setMessage(getString(R.string.add_recipes_success_message))

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton(getString(R.string.done)) { _, _ ->
            // Do something when user press the positive button
            val intent = Intent()
            intent.putExtra(Constants.ADDED_RECIPES_INTENT_KEY, cookingRecipes)
            setResult(Activity.RESULT_OK, intent)
            finish()//finishing activity
        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }

    /**
     * Validate the add recipes input.
     */
    private fun isValidInput(): Boolean {
        var isValid = true
        if (text_view_recipe_name.text.toString().isEmpty()) {
            text_view_recipe_name.error = getString(R.string.fld_error_recipe_name)
            isValid = false
        } else {
            text_view_recipe_name.error = null
        }

        if (text_view_recipes_link.toString().isEmpty()) {
            text_view_recipes_link.error = getString(R.string.fld_error_recipes_link)
            isValid = false
        } else {
            text_view_recipes_link.error = null
        }

        return isValid
    }

    override fun onDestroy() {
        mRecipesAddViewModel.disposeElements()
        super.onDestroy()
    }
}
