package com.example.cookingrecipes.model.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.cookingrecipes.model.data.CookingRecipes

/**
 * The Data Access Object for the CookingRecipes class.
 */
@Dao
interface RecipesDao {

    @Query("SELECT * FROM recipes")
    fun getRecipes(): LiveData<List<CookingRecipes>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recipes: CookingRecipes): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertManual(recipes: CookingRecipes): Long

    @Delete
    fun delete(cookingRecipes: CookingRecipes): Int

    @Update
    fun update(cookingRecipes: CookingRecipes): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(recipesList: List<CookingRecipes>)

    @Query(value = "SELECT * FROM recipes WHERE id = :id")
    fun queryRecipesDetails(id: Int): CookingRecipes

    @Query("SELECT * FROM recipes WHERE id IN (:idList)")
    fun getRecipesWithListOfRecipeIds(idList: List<Int>): LiveData<List<CookingRecipes>>

    @Query("DELETE FROM recipes")
    fun deleteAll()
}
