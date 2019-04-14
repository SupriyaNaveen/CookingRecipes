package com.example.cookingrecipes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.cookingrecipes.model.RecipesModel
import com.example.cookingrecipes.model.data.Category
import com.example.cookingrecipes.model.data.CookingRecipes
import com.example.cookingrecipes.model.data.RecipesCategoryMapping
import javax.inject.Inject

class RecipesViewModel @Inject constructor(private val recipesModel: RecipesModel) : ViewModel() {

    suspend fun loadRecipes(): LiveData<List<CookingRecipes>> {
        return recipesModel.getRecipesFromDb()
    }

    suspend fun loadRecipesDetails(id: Int): CookingRecipes {
        return recipesModel.getRecipesDetails(id)
    }

    suspend fun deleteRecipesData(cookingRecipes: CookingRecipes): Int {
        return recipesModel.deletesRecipes(cookingRecipes)
    }

    suspend fun updateRecipesData(cookingRecipes: CookingRecipes): Int {
        return recipesModel.updateRecipes(cookingRecipes)
    }

    suspend fun addRecipesDetails(cookingRecipes: CookingRecipes): Long {
        return recipesModel.addRecipesData(cookingRecipes)
    }

    suspend fun deleteAllRecipesData() {
        recipesModel.deletesAllRecipes()
    }

    suspend fun getCategoriesList(): LiveData<List<Category>> {
        return recipesModel.getCategoriesFromDb()
    }

    suspend fun insertRecipesCategoryMapping(recipesCategoryMapping: ArrayList<RecipesCategoryMapping>) {
        return recipesModel.insertRecipesCategoryMapping(recipesCategoryMapping)
    }

    suspend fun getRecipesCategoryMappingForRecipesId(recipesId: Int): List<RecipesCategoryMapping> {
        return recipesModel.getRecipesCategoryMappingForRecipesId(recipesId)
    }

    suspend fun deleteMappingForRecipesId(recipesId: Int): Int {
        return recipesModel.deleteMappingForRecipesId(recipesId)
    }

    suspend fun deleteAllMappingData() {
        recipesModel.deleteAllMappingData()
    }

    suspend fun updateMappingTable(recipesId: Int?, selectedCategoryList: ArrayList<Category>) {
        recipesModel.updateMappingTable(recipesId, selectedCategoryList)
    }
}


