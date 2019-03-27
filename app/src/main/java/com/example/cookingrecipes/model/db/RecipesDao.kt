package com.example.cookingrecipes.model.db

import androidx.room.*
import com.example.cookingrecipes.model.data.CookingRecipes
import io.reactivex.Single

/**
 * The Data Access Object for the CookingRecipes class.
 */
@Dao
interface RecipesDao {

    @Query("SELECT * FROM recipes")
    fun getRecipes(): Single<List<CookingRecipes>>

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

    @Query("SELECT * FROM recipes limit :limit offset :offset")
    fun queryRecipesOnLimit(limit: Int, offset: Int): Single<List<CookingRecipes>>

    @Query(value = "SELECT * FROM recipes WHERE id = :id")
    fun queryRecipesDetails(id: Int): Single<CookingRecipes>
}
