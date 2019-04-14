package com.example.cookingrecipes.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.cookingrecipes.R
import com.example.cookingrecipes.model.data.Category
import com.example.cookingrecipes.utils.Constants
import com.example.cookingrecipes.viewmodel.CategoryViewModel
import com.example.cookingrecipes.viewmodel.CategoryViewModelFactory
import com.google.android.material.tabs.TabLayout
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.dialog_new_category.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


/**
 * Displays recipes with its category.
 * When app is launched default category added.
 * The recipes doesn't belong to any category goes to default category.
 * Add recipes, All recipes, add category, delete category options provided.
 */
class HomeActivity : AppCompatActivity(), CoroutineScope {

    private val job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    @Inject
    lateinit var mCategoriesViewModelFactory: CategoryViewModelFactory
    lateinit var mCategoryViewModel: CategoryViewModel

    private lateinit var categoryPageAdpater: CategoriesPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        lifecycleOwner = this

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mCategoryViewModel = ViewModelProviders.of(this, this.mCategoriesViewModelFactory).get(
            CategoryViewModel::class.java
        )

        categoryPageAdpater = CategoriesPageAdapter(supportFragmentManager, ArrayList())
        view_pager_category.adapter = categoryPageAdpater
        tabs_container.setupWithViewPager(view_pager_category)
        getCategoryList()

        button_add_recipes.setOnClickListener {
            val intent = Intent(this, AddRecipesActivity::class.java)
            intent.putExtra(Constants.CURRENT_CATEGORY_POS, view_pager_category.currentItem)
            startActivity(intent)
        }

        tabs_container.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // called when tab selected
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // called when tab unselected
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                showUpdateCategoryDialog()
            }
        })
    }

    @SuppressLint("InflateParams")
    private fun showUpdateCategoryDialog() {
        val category = categoryPageAdpater.getCurrentCategory(view_pager_category.currentItem)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.title_update_category))

        val view = layoutInflater.inflate(R.layout.dialog_new_category, null)

        val categoryEditText = view.edit_text_category_name as EditText
        categoryEditText.setText(category.categoryName)

        builder.setView(view)

        // set up the ok button
        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            val newCategory = categoryEditText.text
            var isValid = true
            if (newCategory.isBlank()) {
                isValid = false
            }

            if (newCategory.toString() == category.categoryName) {
                isValid = false
            }

            if (isValid) {
                launch {
                    mCategoryViewModel.updateCategoryToDB(category.id!!, newCategory.toString())
                }
            }
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private lateinit var lifecycleOwner: LifecycleOwner

    private fun getCategoryList() {
        launch {
            mCategoryViewModel.loadCategories().observe(lifecycleOwner, Observer {
                categoryPageAdpater.updateCategories(it)
            })
        }
    }

    /**
     * Setting menu in action bar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Actions on click menu items
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_recipes_all -> {
            startActivity(Intent(this, RecipesActivity::class.java))
            true
        }
        R.id.action_add_category -> {
            showCreateCategoryDialog()
            true
        }
        R.id.action_delete_category -> {
            showDeleteCategoryDialog()
            true
        }
        else -> {
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    /**
     * Adds the new category to the application.
     */
    @SuppressLint("InflateParams")
    private fun showCreateCategoryDialog() {
        val context = this
        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.title_new_category))

        val view = layoutInflater.inflate(R.layout.dialog_new_category, null)

        val categoryEditText = view.edit_text_category_name as EditText

        categoryEditText.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            categoryEditText.post {
                val inputMethodManager =
                    this@HomeActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(categoryEditText, InputMethodManager.SHOW_IMPLICIT)
            }
        }
        categoryEditText.requestFocus()

        builder.setView(view)

        // set up the ok button
        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            val newCategory = categoryEditText.text
            var isValid = true
            if (newCategory.isBlank()) {
                categoryEditText.error = getString(R.string.error_message_empty_name)
                isValid = false
            }

            if (isValid) {
                launch {
                    mCategoryViewModel.addCategoryToDB(Category(categoryName = newCategory.toString()))
                }
            }

            if (isValid) {
                dialog.dismiss()
            }
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    /**
     * Deleting the default category is disabled.
     * Else, Show the message of category to be deleted?
     * If yes, delete the category. All recipes belongs to that category goes to default.
     */
    private fun showDeleteCategoryDialog() {
        val builder = AlertDialog.Builder(this)

        val category = categoryPageAdpater.getCurrentCategory(view_pager_category.currentItem)

        // Set the alert dialog title
        builder.setTitle(getString(R.string.label_remove_category) + category.categoryName.toUpperCase())

        // Display a message on alert dialog
        builder.setMessage(
            getString(R.string.message_confirm_delete_category) + " "
                    + category.categoryName
        )

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
            launch {
                mCategoryViewModel.deleteCategoryFromDB(category)
            }
        }

        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }
}