package com.example.cookingrecipes.views

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.appcompat.widget.AppCompatSpinner
import com.example.cookingrecipes.model.data.Category

class MultiSelectSpinner(context: Context, attr: AttributeSet) : AppCompatSpinner(context, attr),
    DialogInterface.OnMultiChoiceClickListener {

    private var listener: OnMultipleItemsSelectedListener? = null

    private lateinit var allCategoryList: ArrayList<Category>
    private lateinit var mSelection: BooleanArray
    private lateinit var defaultSelectedCategories: ArrayList<Category>
    private var categoryAdapter: ArrayAdapter<String> = ArrayAdapter(context, android.R.layout.simple_spinner_item)

    private val selectedCategories: ArrayList<Category>
        get() {
            val selection = ArrayList<Category>()
            for (categoryIndex in allCategoryList.indices) {
                if (mSelection[categoryIndex]) {
                    selection.add(allCategoryList[categoryIndex])
                }
            }
            return selection
        }

    private val selectedItemsAsString: String
        get() {
            val sb = StringBuilder()
            var foundOne = false

            for (categoryIndex in allCategoryList.indices) {
                if (mSelection[categoryIndex]) {
                    if (foundOne) {
                        sb.append(", ")
                    }
                    foundOne = true
                    sb.append(allCategoryList[categoryIndex].categoryName.toUpperCase())
                }
            }
            if (sb.isBlank()) {
                sb.append("No Category selected.")
            }
            return sb.toString()
        }

    interface OnMultipleItemsSelectedListener {
        fun selectedCategories(selectedCategories: ArrayList<Category>)
    }

    init {
        super.setAdapter(categoryAdapter)
    }

    fun setListener(listener: OnMultipleItemsSelectedListener) {
        this.listener = listener
    }

    override fun onClick(dialog: DialogInterface, which: Int, isChecked: Boolean) {
        if (which < mSelection.size) {
            mSelection[which] = isChecked
            categoryAdapter.clear()
            categoryAdapter.add(selectedItemsAsString)
        } else {
            throw IllegalArgumentException(
                "Argument 'which' is out of bounds."
            )
        }
    }

    override fun performClick(): Boolean {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Please select at-least one category.")

        val choiceItem = ArrayList(allCategoryList.map { it.categoryName.toUpperCase() })
        val dialogList = choiceItem.toArray(arrayOfNulls<CharSequence>(allCategoryList.size))
        builder.setMultiChoiceItems(dialogList, mSelection, this)

        builder.setPositiveButton("Submit") { _, _ ->
            setSelection(selectedCategories)
            listener!!.selectedCategories(selectedCategories)
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            setSelection(defaultSelectedCategories)
        }
        builder.show()
        return true
    }

    override fun setAdapter(adapter: SpinnerAdapter) {
        throw RuntimeException(
            "setAdapter is not supported by MultiSelectSpinner."
        )
    }

    fun setItems(items: ArrayList<Category>) {
        allCategoryList = items

        val selectedCategories = ArrayList<Category>()
        setSelection(selectedCategories)
    }

    fun setSelection(selectedCategories: ArrayList<Category>) {
        defaultSelectedCategories = selectedCategories

        mSelection = BooleanArray(allCategoryList.size)
        mSelection.fill(false)

        for (category in selectedCategories) {
            mSelection[allCategoryList.indexOf(category)] = true
        }
        categoryAdapter.clear()
        categoryAdapter.add(selectedItemsAsString)
    }
}
