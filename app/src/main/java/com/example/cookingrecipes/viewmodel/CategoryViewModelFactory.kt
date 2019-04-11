package com.example.cookingrecipes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class CategoryViewModelFactory @Inject constructor(
        private val categoryViewModel: CategoryViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            return categoryViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}
