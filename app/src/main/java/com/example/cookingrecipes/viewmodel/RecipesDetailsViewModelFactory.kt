package com.example.cookingrecipes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class RecipesDetailsViewModelFactory @Inject constructor(
    private val recipesDetailsViewModel: RecipesDetailsViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipesDetailsViewModel::class.java!!)) {
            return recipesDetailsViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}
