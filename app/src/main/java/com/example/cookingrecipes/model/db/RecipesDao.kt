package com.example.cookingrecipes.model.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cookingrecipes.model.data.CookingRecipes
import io.reactivex.Single

@Dao
interface RecipesDao {

    @Query("SELECT * FROM recipes")
    fun getRecipes(): Single<List<CookingRecipes>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recipes: CookingRecipes): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(recipesList: List<CookingRecipes>)

    @Query("SELECT * FROM recipes limit :limit offset :offset")
    fun queryRecipesOnLimit(limit: Int, offset: Int): Single<List<CookingRecipes>>

    @Query(value = "SELECT * FROM recipes WHERE id = :id")
    fun queryRecipesDetails(id: Int): Single<CookingRecipes>
}
