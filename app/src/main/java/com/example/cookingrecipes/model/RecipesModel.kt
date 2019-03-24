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
class RecipesModel @Inject constructor(
    private val restApi: RestApi,
    private val recipesDao: RecipesDao,
    private val utils: Utils
) {

    /**
     *
     */
    private fun getRecipesFromApi(): Observable<List<CookingRecipes>> {
        return restApi.getRecipes()
            .doOnNext {
                Timber.d("Dispatching ${it.size} users from API...")
                storeRecipesInDb(it)
            }
    }

    /**
     *
     */
    fun storeRecipesInDb(recipes: List<CookingRecipes>) {
        Observable.fromCallable { recipesDao.insertAll(recipes) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe {
                Timber.d("Inserted $it users from API in DB...")
            }
    }

    fun getRecipesOnLimit(limit: Int, offset: Int): Observable<List<CookingRecipes>> {
        val hasConnection = utils.isConnectedToInternet()
        var observableFromApi: Observable<List<CookingRecipes>>? = null
//        TODO("Web api should be called after api created")
//        if (hasConnection) {
//            observableFromApi = getRecipesFromApi()
//        }
        val observableFromDb = getRecipesFromDb(limit, offset)

        return if (hasConnection && observableFromApi != null) Observable.concatArrayEager(
            observableFromApi,
            observableFromDb
        )
        else
            return observableFromDb
    }

    private fun getRecipesFromDb(limit: Int, offset: Int): Observable<List<CookingRecipes>> {
        return recipesDao.queryRecipesOnLimit(limit, offset)
            .toObservable()
            .doOnNext {
                //Print log it.size :)
                Log.e("REPOSITORY DB *** ", it.size.toString())
            }
    }
}