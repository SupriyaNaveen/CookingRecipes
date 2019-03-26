package com.example.cookingrecipes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class AddRecipesViewModelFactory @Inject constructor(
    private val addRecipesViewModel: AddRecipesViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddRecipesViewModel::class.java)) {
            return addRecipesViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}
