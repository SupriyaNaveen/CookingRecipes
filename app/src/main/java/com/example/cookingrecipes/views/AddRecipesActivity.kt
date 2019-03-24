package com.example.cookingrecipes.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.example.cookingrecipes.R
import com.example.cookingrecipes.model.data.CookingRecipes
import com.example.cookingrecipes.utils.Constants
import com.example.cookingrecipes.viewmodel.AddRecipesViewModel
import com.example.cookingrecipes.viewmodel.AddRecipesViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_recipes_add.*
import javax.inject.Inject


class AddRecipesActivity : BaseActivity() {

    lateinit var mRecipesAddViewModel: AddRecipesViewModel

    @Inject
    lateinit var mAddRecipesViewModelFactory: AddRecipesViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes_add)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        mRecipesAddViewModel = ViewModelProviders.of(this, mAddRecipesViewModelFactory).get(
            AddRecipesViewModel::class.java
        )

        button_add_recipes.setOnClickListener { v: View? ->

            val isValidInput = isValidInput()
            if (isValidInput) {
                var cookingRecipes = CookingRecipes(
                    recipeName = text_view_recipe_name.text.toString(),
                    recipeLink = text_view_recipes_link.text.toString(),
                    recipeDescription = text_view_recipes_description.text.toString()
                )
                mRecipesAddViewModel.addRecipesToDb(cookingRecipes)
                text_view_recipe_name.text?.clear()
                text_view_recipes_link.text?.clear()
                text_view_recipes_description.text?.clear()
                //TODO Result of insert db query check
                val intent = Intent()
                intent.putExtra(Constants.ADDED_RECIPES, cookingRecipes)
                setResult(Activity.RESULT_OK, intent)
                finish()//finishing activity
            }
        }
    }

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
            text_view_recipes_link.setError(null)
        }

        return isValid
    }
}
