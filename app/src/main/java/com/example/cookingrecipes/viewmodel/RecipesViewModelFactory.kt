package com.example.cookingrecipes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class RecipesViewModelFactory @Inject constructor(
    private val recipesViewModel: RecipesViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipesViewModel::class.java!!)) {
            return recipesViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}
