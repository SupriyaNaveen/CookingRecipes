package com.example.cookingrecipes.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cookingrecipes.R
import com.example.cookingrecipes.model.data.Category
import kotlinx.android.synthetic.main.fragment_manage_categories_list_row.view.*

class ManageCategoriesRecyclerViewAdapter(
    categoriesData: List<Category>,
    itemClickListener: ItemClickListener
) : RecyclerView.Adapter<ManageCategoriesRecyclerViewAdapter.ViewHolder>() {

    var categoryList: ArrayList<Category> = ArrayList()
    private var mItemClickListener: ItemClickListener

    init {
        categoryList = categoriesData as ArrayList<Category>
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_manage_categories_list_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = categoryList[position]
        holder.mContentView.text = item.categoryName

        with(holder.mView) {
            tag = item
            setOnClickListener { mItemClickListener.onItemClicked(item) }
        }

        holder.mDeleteCategory.setOnClickListener { mItemClickListener.onDeleteCategory(item) }
    }

    override fun getItemCount(): Int = categoryList.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mContentView: TextView = mView.text_view_category_name
        val mDeleteCategory: ImageView = mView.image_view_delete_category
    }

    fun updateCategories(categoriesData: List<Category>) {
        categoryList = ArrayList(categoriesData)
        notifyDataSetChanged()
    }

    interface ItemClickListener {
        fun onItemClicked(category: Category)
        fun onDeleteCategory(category: Category)
    }
}
