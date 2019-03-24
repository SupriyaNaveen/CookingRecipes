package com.example.cookingrecipes.model

import com.example.cookingrecipes.model.data.CookingRecipes
import com.example.cookingrecipes.model.db.RecipesDao
import io.reactivex.Single
import javax.inject.Inject

/**
 *
 */
class AddRecipesModel @Inject constructor(
    private val recipesDao: RecipesDao
) {

    fun addRecipesData(cookingRecipes: CookingRecipes): Single<Long>? {
        return Single.fromCallable<Long> { recipesDao.insert(cookingRecipes) }
    }
}