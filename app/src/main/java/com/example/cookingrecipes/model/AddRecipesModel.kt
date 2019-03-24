package com.example.cookingrecipes.model

import android.annotation.SuppressLint
import android.util.Log
import com.example.cookingrecipes.model.data.CookingRecipes
import com.example.cookingrecipes.model.db.RecipesDao
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 *
 */
class AddRecipesModel @Inject constructor(
    private val recipesDao: RecipesDao
) {

    fun addRecipesDetails(cookingRecipes: CookingRecipes) {
        addRecipesDetailsToDb(cookingRecipes)
    }

    @SuppressLint("CheckResult")
    private fun addRecipesDetailsToDb(cookingRecipes: CookingRecipes) {
        Observable.fromCallable { recipesDao.insert(cookingRecipes) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe {
                Timber.d("Inserted $it users from API in DB...")
            }
    }
}