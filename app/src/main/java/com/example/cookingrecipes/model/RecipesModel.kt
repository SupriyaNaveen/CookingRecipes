package com.example.cookingrecipes.model

import androidx.lifecycle.LiveData
import com.example.cookingrecipes.model.api.RestApi
import com.example.cookingrecipes.model.data.CookingRecipes
import com.example.cookingrecipes.model.db.RecipesDao
import com.example.cookingrecipes.utils.Utils
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
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
//    private fun getRecipesFromApi(): Observable<List<CookingRecipes>> {
//        return restApi.getRecipes()
//                .doOnNext {
//                    Timber.d("Dispatching ${it.size} users from API...")
//                    storeRecipesInDb(it)
//                }
//    }

    /**
     * Store all recipes details to local DB.
     */
//    @SuppressLint("CheckResult")
//    private fun storeRecipesInDb(recipes: List<CookingRecipes>) {
//
//        Observable.fromCallable { recipesDao.insertAll(recipes) }
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .subscribe {
//                    if (it != null) {
//                        Timber.d("Inserted $it users from API in DB...")
//                    }
//                }
//    }

    /**
     * Get all stored recipes from db, which is live data.
     */
    suspend fun getRecipesFromDb(): LiveData<List<CookingRecipes>> {
        return withContext(IO) { recipesDao.getRecipes() }
    }

    /**
     * Delete a recipe from DB. Using Co-routine
     */
    suspend fun deletesRecipes(cookingRecipes: CookingRecipes): Int {
        return withContext(IO) {
            recipesDao.delete(cookingRecipes)
        }
    }

    /**
     * Update a recipe in DB.
     */
    suspend fun updateRecipes(cookingRecipes: CookingRecipes): Int {
        return withContext(IO) {
            recipesDao.update(cookingRecipes)
        }
    }

    suspend fun getRecipesDetails(id: Int): CookingRecipes {
        return withContext(IO) { recipesDao.queryRecipesDetails(id) }
    }

    suspend fun addRecipesData(cookingRecipes: CookingRecipes): Long {
        return withContext(IO) {
            recipesDao.insertManual(cookingRecipes)
        }
    }
}