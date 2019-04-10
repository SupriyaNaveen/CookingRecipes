package com.example.cookingrecipes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.cookingrecipes.model.CategoryModel
import com.example.cookingrecipes.model.data.Category
import com.example.cookingrecipes.model.data.CookingRecipes
import com.example.cookingrecipes.model.data.RecipesCategoryMapping
import javax.inject.Inject

class CategoryViewModel @Inject constructor(private val categoryModel: CategoryModel) : ViewModel() {

    suspend fun loadCategories(): LiveData<List<Category>> {
        return categoryModel.getCategoriesFromDb()
    }

    suspend fun addCategoryToDB(category: Category) {
        categoryModel.addCategoryToDB(category)
    }

    suspend fun getRecipesCategoryMappingForCategoryId(categoryId: Int): LiveData<List<RecipesCategoryMapping>> {
        return categoryModel.getRecipesCategoryMappingForCategoryId(categoryId)
    }

    fun getRecipesWithListOfRecipeIds(idList: List<Int>): LiveData<List<CookingRecipes>> {
        return categoryModel.getRecipesWithListOfRecipeIds(idList)
    }

    suspend fun deleteCategoryFromDB(category: Category): Int {
        return categoryModel.deleteCategoryFromDB(category)
    }

    suspend fun updateCategoryToDB(categoryId: Int, newCategoryName: String) {
        categoryModel.updateCategoryToDB(categoryId, newCategoryName)
    }
}


