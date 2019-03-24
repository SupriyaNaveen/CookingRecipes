package com.example.cookingrecipes.model.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cookingrecipes.model.data.Category
import io.reactivex.Single

@Dao
interface CategoryDao {

    @Query("SELECT * FROM category")
    fun getRecipes(): Single<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(categoryList: List<Category>)
}
