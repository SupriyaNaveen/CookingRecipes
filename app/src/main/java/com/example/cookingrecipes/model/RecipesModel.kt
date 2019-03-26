package com.example.cookingrecipes.model

import android.annotation.SuppressLint
import com.example.cookingrecipes.model.api.RestApi
import com.example.cookingrecipes.model.data.CookingRecipes
import com.example.cookingrecipes.model.db.RecipesDao
import com.example.cookingrecipes.utils.Utils
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * Repository module for handling data operations of Recipes.
 * Get recipes from REST API and update local DB.
 * Get recipes from Local DB for given offset, limit.
 */
class RecipesModel @Inject constructor(
    private val restApi: RestApi,
    private val recipesDao: RecipesDao,
    private val utils: Utils
) {

    /**
     * Get recipes from web API.
     */
    private fun getRecipesFromApi(): Observable<List<CookingRecipes>> {
        return restApi.getRecipes()
            .doOnNext {
                Timber.d("Dispatching ${it.size} users from API...")
                storeRecipesInDb(it)
            }
    }

    /**
     * Store all recipes details to local DB.
     */
    @SuppressLint("CheckResult")
    private fun storeRecipesInDb(recipes: List<CookingRecipes>) {
        Observable.fromCallable { recipesDao.insertAll(recipes) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe {
                if (it != null) {
                    Timber.d("Inserted $it users from API in DB...")
                }
            }
    }

    /**
     * Get recipes for given limit.
     * Check for internet connection,
     * If yes, then fetch recipes from both API and locan DB.
     */
    fun getRecipesOnLimit(limit: Int, offset: Int): Observable<List<CookingRecipes>> {
//        val hasConnection = utils.isConnectedToInternet()
//        var observableFromApi: Observable<List<CookingRecipes>>? = null
//        TODO("Web api should be called after api created")
//        if (hasConnection) {
//            observableFromApi = getRecipesFromApi()
//        }

//        return if (hasConnection && observableFromApi != null) Observable.concatArrayEager(
//            observableFromApi,
//            observableFromDb
//        )
//        else
        return getRecipesFromDb(limit, offset)
    }

    /**
     * Get recipes from local DB.
     */
    private fun getRecipesFromDb(limit: Int, offset: Int): Observable<List<CookingRecipes>> {
        return recipesDao.queryRecipesOnLimit(limit, offset)
            .toObservable()
            .doOnNext {
                Timber.d(it.size.toString())
            }
    }
}