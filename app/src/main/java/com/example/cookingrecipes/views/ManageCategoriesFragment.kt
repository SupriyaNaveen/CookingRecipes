package com.example.cookingrecipes.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.cookingrecipes.R
import com.example.cookingrecipes.databinding.FragmentManageCategoriesBinding
import com.example.cookingrecipes.model.data.Category
import com.example.cookingrecipes.viewmodel.CategoryViewModel
import com.example.cookingrecipes.viewmodel.CategoryViewModelFactory
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.dialog_new_category.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 */
class ManageCategoriesFragment : Fragment(), CoroutineScope, ManageCategoriesRecyclerViewAdapter.ItemClickListener {

    private val job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    companion object {

        fun newInstance(): ManageCategoriesFragment {
            return ManageCategoriesFragment()
        }
    }

    @Inject
    lateinit var mCategoriesViewModelFactory: CategoryViewModelFactory
    private lateinit var mCategoryViewModel: CategoryViewModel

    private lateinit var manageCategoriesAdapter: ManageCategoriesRecyclerViewAdapter

    private lateinit var mDataBinding: FragmentManageCategoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage_categories, container, false)
        setHasOptionsMenu(true)

        mCategoryViewModel = ViewModelProviders.of(this, this.mCategoriesViewModelFactory).get(
            CategoryViewModel::class.java
        )

        manageCategoriesAdapter = ManageCategoriesRecyclerViewAdapter(ArrayList(), this)
        initializeRecycler()
        getCategoryList()

        mDataBinding.buttonAddCategories.setOnClickListener {
            showCreateCategoryDialog()
        }
        activity!!.toolbar.title = getString(R.string.manage_categories)

        return mDataBinding.root
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    private fun getCategoryList() {
        launch {
            mCategoryViewModel.loadCategories().observe(activity!!, Observer {
                manageCategoriesAdapter.updateCategories(it)
            })
        }
    }

    private fun initializeRecycler() {
        val gridLayoutManager = GridLayoutManager(activity!!, 1)
        gridLayoutManager.orientation = RecyclerView.VERTICAL
        mDataBinding.recyclerViewCategoryRecipes.apply {
            setHasFixedSize(true)
            layoutManager = gridLayoutManager
        }

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                showDeleteCategoryDialog(manageCategoriesAdapter.categoryList[position])
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(mDataBinding.recyclerViewCategoryRecipes)

        mDataBinding.recyclerViewCategoryRecipes.adapter = manageCategoriesAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_tool_bar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Actions on click menu items
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_delete_all -> {
            if (manageCategoriesAdapter.itemCount > 0)
                showDeleteAllConfirmationDialog()
            else
                Toast.makeText(activity!!, getString(R.string.error_no_recipes_to_delete), Toast.LENGTH_LONG).show()
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
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.title_new_category))

        val view = layoutInflater.inflate(R.layout.dialog_new_category, null)

        val categoryEditText = view.edit_text_category_name as EditText

        categoryEditText.onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
            categoryEditText.post {
                val inputMethodManager =
                    activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
    private fun showDeleteCategoryDialog(category: Category) {
        val builder = AlertDialog.Builder(activity!!)

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
            manageCategoriesAdapter.notifyDataSetChanged()
            dialog.cancel()
        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }

    override fun onItemClicked(category: Category) {
        showUpdateCategoryDialog(category)
    }

    override fun onDeleteCategory(category: Category) {
        showDeleteCategoryDialog(category)
    }

    @SuppressLint("InflateParams")
    private fun showUpdateCategoryDialog(category: Category) {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.title_update_category))

        val view = layoutInflater.inflate(R.layout.dialog_new_category, null)

        val categoryEditText = view.edit_text_category_name as EditText
        categoryEditText.setText(category.categoryName)

        builder.setView(view)

        // set up the ok button
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
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

    /**
     * Show confirmation dialog for all recipes delete.
     * Once all recipes deleted, clear all mapping table as there is no recipes to mapping to.
     */
    private fun showDeleteAllConfirmationDialog() {
        val builder = android.app.AlertDialog.Builder(activity!!)

        // Set the alert dialog title
        builder.setTitle(getString(R.string.category))

        // Display a message on alert dialog
        builder.setMessage(getString(R.string.message_confirm_category_delete))

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            launch {
                mCategoryViewModel.deleteAllCategoryFromDB()
            }
        }
        builder.setNegativeButton(getString(com.example.cookingrecipes.R.string.cancel)) { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.cancel()
        }

        // Finally, make the alert dialog using builder
        val dialog: android.app.AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }
}
