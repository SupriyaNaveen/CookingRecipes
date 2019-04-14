package com.example.cookingrecipes.model

import androidx.lifecycle.LiveData
import com.example.cookingrecipes.model.data.Category
import com.example.cookingrecipes.model.data.CookingRecipes
import com.example.cookingrecipes.model.data.RecipesCategoryMapping
import com.example.cookingrecipes.model.db.CategoryDao
import com.example.cookingrecipes.model.db.RecipesCategoryMappingDao
import com.example.cookingrecipes.model.db.RecipesDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 *
 */
class CategoryModel @Inject constructor(
        private val recipesDao: RecipesDao,
        private val categoryDao: CategoryDao,
        private var recipesCategoryMappingDao: RecipesCategoryMappingDao) {

    /**
     * Get all stored categories from db, which is live data.
     */
    suspend fun getCategoriesFromDb(): LiveData<List<Category>> {
        return withContext(Dispatchers.IO) { categoryDao.getCategories() }
    }

    suspend fun addCategoryToDB(category: Category) {
        withContext(Dispatchers.IO) { categoryDao.insert(category) }
    }

    suspend fun getRecipesCategoryMappingForCategoryId(categoryId: Int): LiveData<List<RecipesCategoryMapping>> {
        return withContext(Dispatchers.IO) {
            recipesCategoryMappingDao.getRecipesCategoryMappingForCategoryId(categoryId)
        }
    }

    fun getRecipesWithListOfRecipeIds(idList: List<Int>): LiveData<List<CookingRecipes>> {
        return recipesDao.getRecipesWithListOfRecipeIds(idList)
    }

    suspend fun deleteCategoryFromDB(category: Category): Int {
        return withContext(Dispatchers.IO) {
            recipesCategoryMappingDao.deleteMappingForCategoryId(category.id!!)
            categoryDao.delete(category)
        }
    }

    suspend fun updateCategoryToDB(categoryId: Int, newCategoryName: String) {
        withContext(Dispatchers.IO) {
            categoryDao.update(categoryId, newCategoryName)
        }
    }
}