package com.example.cookingrecipes.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.cookingrecipes.R
import com.example.cookingrecipes.databinding.FragmentCategoryBinding
import com.example.cookingrecipes.viewmodel.CategoryViewModel
import com.example.cookingrecipes.viewmodel.CategoryViewModelFactory
import com.google.android.material.tabs.TabLayout
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
 * Displays recipes with its category.
 * When app is launched default category added.
 * The recipes doesn't belong to any category goes to default category.
 * Add recipes, All recipes, add category, delete category options provided.
 */
class CategoryFragment : Fragment(), CoroutineScope {

    private val job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    @Inject
    lateinit var mCategoriesViewModelFactory: CategoryViewModelFactory
    private lateinit var mCategoryViewModel: CategoryViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    private lateinit var categoryPageAdpater: CategoriesPageAdapter

    companion object {

        fun newInstance(): CategoryFragment {
            return CategoryFragment()
        }
    }

    private lateinit var mBinder: FragmentCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false)
        activity!!.toolbar.title = getString(R.string.category)

        mCategoryViewModel = ViewModelProviders.of(this, this.mCategoriesViewModelFactory).get(
            CategoryViewModel::class.java
        )

        mBinder.tabsContainer.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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

        categoryPageAdpater = CategoriesPageAdapter(activity!!.supportFragmentManager, ArrayList())
        mBinder.viewPagerCategory.adapter = categoryPageAdpater
        mBinder.tabsContainer.setupWithViewPager(mBinder.viewPagerCategory)
        getCategoryList()

        return mBinder.root
    }

    @SuppressLint("InflateParams")
    private fun showUpdateCategoryDialog() {
        val category = categoryPageAdpater.getCurrentCategory(mBinder.viewPagerCategory.currentItem)
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

    private fun getCategoryList() {
        launch {
            mCategoryViewModel.loadCategories().observe(activity!!, Observer {
                if (it.isEmpty()) {
                    mBinder.viewPagerCategory.visibility = GONE
                    mBinder.textViewZeroCategory.visibility = VISIBLE
                } else {
                    mBinder.textViewZeroCategory.visibility = GONE
                    mBinder.viewPagerCategory.visibility = VISIBLE
                    categoryPageAdpater.updateCategories(it)
                }
            })
        }
    }
}