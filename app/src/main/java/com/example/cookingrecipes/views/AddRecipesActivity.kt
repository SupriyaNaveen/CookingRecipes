package com.example.cookingrecipes.views

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.cookingrecipes.R
import com.example.cookingrecipes.databinding.ActivityRecipesAddBinding
import com.example.cookingrecipes.model.data.Category
import com.example.cookingrecipes.model.data.CookingRecipes
import com.example.cookingrecipes.model.data.RecipesCategoryMapping
import com.example.cookingrecipes.utils.Constants
import com.example.cookingrecipes.viewmodel.RecipesViewModel
import com.example.cookingrecipes.viewmodel.RecipesViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


/**
 * Add recipes activity, allows the user to add CookingRecipes.
 * Recipes name, youtube link, description entered are added to local DB.
 * The selected category also updated to mapping table.
 * TODO: Add Recipes to REST API.
 */
class AddRecipesActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var category: Category
    //Init view model
    private lateinit var mRecipesAddViewModel: RecipesViewModel

    //Inject view model factory
    @Inject
    lateinit var mAddRecipesViewModelFactory: RecipesViewModelFactory

    private lateinit var dataBinding: ActivityRecipesAddBinding

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

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
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_recipes_add)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val extras = intent.extras
        if (extras != null) {
            dataBinding.textViewRecipesLink.setText(extras.getString(Intent.EXTRA_TEXT))
            dataBinding.textViewRecipeName.setText(extras.getString(Intent.EXTRA_SUBJECT))
        }

        mRecipesAddViewModel = ViewModelProviders.of(this, this.mAddRecipesViewModelFactory).get(
                RecipesViewModel::class.java
        )

        dataBinding.buttonAddRecipes.setOnClickListener {

            val isValidInput = isValidInput()
            if (isValidInput) {
                val cookingRecipes = CookingRecipes(
                        recipeName = dataBinding.textViewRecipeName.text.toString(),
                        recipeLink = dataBinding.textViewRecipesLink.text.toString(),
                        recipeDescription = dataBinding.textViewRecipesDescription.text.toString()
                )

                launch {
                    val rowId = mRecipesAddViewModel.addRecipesDetails(cookingRecipes)
                    if (rowId > 0) {
                        //Insert recipes category table
                        val id = mRecipesAddViewModel.insertRecipesCategoryMapping(RecipesCategoryMapping(rowId.toInt(), category.id!!))
                        Timber.d("Id = $id")
                        showRecipesAddedMessage()
                    } else {
                        Timber.d("Row inserted failed id : $it")
                    }
                }
            }
        }

        populateCategoryList(intent.getIntExtra(Constants.CURRENT_CATEGORY_POS, 0))
    }

    private fun populateCategoryList(categoryPosition: Int) {

        launch {
            mRecipesAddViewModel.getCategoriesList().observe(this@AddRecipesActivity, Observer {
                //Category list can not be empty as default category added.
                if (!it.isEmpty()) {
                    val adapter = ArrayAdapter(this@AddRecipesActivity, android.R.layout.simple_spinner_item, it)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    dataBinding.spinnerRecipesCategory.adapter = adapter

                    //Set the selected category if add is called from category page
                    category = it[categoryPosition]
                    dataBinding.spinnerRecipesCategory.setSelection(categoryPosition)

                    dataBinding.spinnerRecipesCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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


    /**
     * Alert dialog to show the recipes added successfully.
     */
    private fun showRecipesAddedMessage() {
        val builder = AlertDialog.Builder(this)

        // Set the alert dialog title
        builder.setTitle(getString(R.string.recipes))

        // Display a message on alert dialog
        builder.setMessage(getString(R.string.add_recipes_success_message))

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton(getString(R.string.done)) { _, _ ->
            finish()
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
        if (dataBinding.textViewRecipeName.text.toString().isEmpty()) {
            dataBinding.textViewRecipeName.error = getString(R.string.fld_error_recipe_name)
            isValid = false
        } else {
            dataBinding.textViewRecipeName.error = null
        }

        if (dataBinding.textViewRecipesLink.toString().isEmpty()) {
            dataBinding.textViewRecipesLink.error = getString(R.string.fld_error_recipes_link)
            isValid = false
        } else {
            dataBinding.textViewRecipesLink.error = null
        }

        return isValid
    }
}
