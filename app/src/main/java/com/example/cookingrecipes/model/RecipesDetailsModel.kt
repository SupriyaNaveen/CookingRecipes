package com.example.cookingrecipes.model

import android.util.Log
import com.example.cookingrecipes.model.api.RestApi
import com.example.cookingrecipes.model.data.CookingRecipes
import com.example.cookingrecipes.model.db.RecipesDao
import com.example.cookingrecipes.utils.Utils
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 *
 */
class RecipesDetailsModel @Inject constructor(
    private val recipesDao: RecipesDao
) {

    fun getRecipesDetails(id: Int): Observable<CookingRecipes> {
        return getRecipesDetailsFromDb(id)
    }

    private fun getRecipesDetailsFromDb(id: Int): Observable<CookingRecipes> {
        return recipesDao.queryRecipesDetails(id)
            .toObservable()
            .doOnNext {
                //Print log it.size :)
                Log.e("REPOSITORY DB *** ", it.recipeName)
            }
    }
}