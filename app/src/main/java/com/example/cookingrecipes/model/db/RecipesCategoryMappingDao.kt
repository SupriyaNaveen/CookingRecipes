package com.example.cookingrecipes.model.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cookingrecipes.model.data.RecipesCategoryMapping

@Dao
interface RecipesCategoryMappingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipesCategoryMapping(recipesCategoryMapping: RecipesCategoryMapping): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(recipesCategoryMappingList: ArrayList<RecipesCategoryMapping>)

    @Query("SELECT * FROM recipes_category WHERE categoryId=:categoryId")
    fun getRecipesCategoryMappingForCategoryId(categoryId: Int): LiveData<List<RecipesCategoryMapping>>

    @Query("SELECT * FROM recipes_category WHERE recipesId=:recipesId")
    fun getRecipesCategoryMappingForRecipesId(recipesId: Int): List<RecipesCategoryMapping>

    @Query("DELETE FROM recipes_category WHERE recipesId=:recipesId")
    fun deleteMappingForRecipesId(recipesId: Int): Int

    @Query("DELETE FROM recipes_category")
    fun deleteAllMappingData()

    @Query("DELETE FROM recipes_category WHERE categoryId=:categoryId")
    fun deleteMappingForCategoryId(categoryId: Int)
}
