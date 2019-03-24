package com.example.cookingrecipes.viewmodel

import androidx.lifecycle.ViewModel
import com.example.cookingrecipes.model.AddRecipesModel
import com.example.cookingrecipes.model.data.CookingRecipes
import javax.inject.Inject

class AddRecipesViewModel @Inject constructor(private val addRecipesModel: AddRecipesModel) : ViewModel() {

    fun addRecipesToDb(cookingRecipes: CookingRecipes) {
        addRecipesModel.addRecipesDetails(cookingRecipes)
    }
}


