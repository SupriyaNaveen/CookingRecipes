package com.example.cookingrecipes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.cookingrecipes.model.RecipesModel
import com.example.cookingrecipes.model.data.CookingRecipes
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
}


