package com.example.cookingrecipes.model

import androidx.lifecycle.LiveData
import com.example.cookingrecipes.model.data.Category
import com.example.cookingrecipes.model.data.CookingRecipes
import com.example.cookingrecipes.model.data.RecipesCategoryMapping
import com.example.cookingrecipes.model.db.CategoryDao
import com.example.cookingrecipes.model.db.RecipesCategoryMappingDao
import com.example.cookingrecipes.model.db.RecipesDao
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Repository module for handling data operations of Recipes.
 * Get recipes from REST API and update local DB.
 * Get recipes from Local DB for given offset, limit.
 */
class RecipesModel @Inject constructor(
    private val recipesDao: RecipesDao,
    private val recipesCategoryMappingDao: RecipesCategoryMappingDao,
    private val categoryDao: CategoryDao
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

    suspend fun deletesAllRecipes() {
        withContext(IO) {
            recipesDao.deleteAll()
        }
    }

    suspend fun getCategoriesFromDb(): LiveData<List<Category>> {
        return withContext(IO) { categoryDao.getCategories() }
    }

    suspend fun insertRecipesCategoryMapping(recipesCategoryMapping: ArrayList<RecipesCategoryMapping>) {
        return withContext(IO) {
            recipesCategoryMappingDao.insertAll(recipesCategoryMapping)
        }
    }

    suspend fun getRecipesCategoryMappingForRecipesId(recipesId: Int): List<RecipesCategoryMapping> {
        return withContext(IO) { recipesCategoryMappingDao.getRecipesCategoryMappingForRecipesId(recipesId) }
    }

    suspend fun deleteMappingForRecipesId(recipesId: Int): Int {
        return withContext(IO) {
            recipesCategoryMappingDao.deleteMappingForRecipesId(recipesId)
        }
    }

    suspend fun deleteAllMappingData() {
        withContext(IO) {
            recipesCategoryMappingDao.deleteAllMappingData()
        }
    }

    suspend fun updateMappingTable(recipesId: Int?, selectedCategoryList: ArrayList<Category>) {
        withContext(IO) {
            recipesCategoryMappingDao.deleteMappingForRecipesId(recipesId!!)
            if (selectedCategoryList.size > 0) {

                val recipesCategoryMappingList = ArrayList<RecipesCategoryMapping>()
                for (category in selectedCategoryList) {
                    recipesCategoryMappingList.add(
                        RecipesCategoryMapping(
                            recipesId,
                            category.id!!
                        )
                    )
                }
                recipesCategoryMappingDao.insertAll(recipesCategoryMappingList)
            }
        }
    }
}