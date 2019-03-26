package com.example.cookingrecipes.viewmodel

import androidx.lifecycle.ViewModel
import com.example.Category.model.CategoryModel
import javax.inject.Inject

class CategoryViewModel @Inject constructor(private val categoryModel: CategoryModel) : ViewModel() {

//    fun getCategories(): Observable<CategoryListForUI> {
        //Prepare the data for your UI, the recipes list
        //and maybe some additional data needed as well
//        return categoryModel.getCategories()
//            .map { CategoryListForUI(it, "Users loaded successfully!") }
//    }
}


