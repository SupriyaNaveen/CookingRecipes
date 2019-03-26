package com.example.cookingrecipes.model

import com.example.cookingrecipes.model.data.CookingRecipes
import com.example.cookingrecipes.model.db.RecipesDao
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

/**
 * Repository module for handling data operations of Recipes Details.
 * Get Recipes details for the given recipe id.
 * Delete recipe from db for given recipe.
 * Update recipe from db for given recipe.
 */
class RecipesDetailsModel @Inject constructor(
    private val recipesDao: RecipesDao
) {

    /**
     * Get recipe details for specified id.
     */
    fun getRecipesDetails(id: Int): Observable<CookingRecipes> {
        return recipesDao.queryRecipesDetails(id)
            .toObservable()
            .doOnNext {
                Timber.d("$it")
            }
    }

    /**
     * Delete recipes from db for specified recipes data.
     */
    fun deleteRecipesFromDb(cookingRecipes: CookingRecipes): Single<Int> {
        return Single.fromCallable<Int> { recipesDao.delete(cookingRecipes) }
    }

    /**
     * Update recipes in db for specified recipes data.
     */
    fun updateRecipesToDb(cookingRecipes: CookingRecipes): Single<Int> {
        return Single.fromCallable<Int> { recipesDao.update(cookingRecipes) }
    }
}