package com.example.cookingrecipes.views

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.example.cookingrecipes.model.data.Category


class CategoriesPageAdapter(fragmentManager: FragmentManager, categoryArray: ArrayList<Category>) : FragmentStatePagerAdapter(fragmentManager) {

    private var categoryList: ArrayList<Category> = categoryArray

    override fun getItem(position: Int): Fragment =
        CategoriesRecipesFragment.newInstance(position + 1, categoryList[position].id!!)

    override fun getCount(): Int {
        return categoryList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return categoryList[position].categoryName
    }

    fun getCurrentCategory(position: Int): Category {
        return categoryList[position]
    }

    /**
     * Update adapter when list of recipes added.
     */
    fun updateCategories(categoryDataList: List<Category>) {
        categoryList = ArrayList(categoryDataList)
        notifyDataSetChanged()
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

}