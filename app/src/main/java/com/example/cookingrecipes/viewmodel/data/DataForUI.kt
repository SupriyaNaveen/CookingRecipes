package com.example.cookingrecipes.viewmodel.data

import com.example.cookingrecipes.model.data.Category
import com.example.cookingrecipes.model.data.CookingRecipes

data class RecipesListForUI(val recipes: List<CookingRecipes>, val message: String, val error: Throwable? = null)

data class CategoryListForUI(val category: List<Category>, val message: String, val error: Throwable? = null)
