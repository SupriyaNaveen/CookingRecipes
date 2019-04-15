package com.example.cookingrecipes.model.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.cookingrecipes.model.data.Category

@Dao
interface CategoryDao {

    @Query("SELECT * FROM category")
    fun getCategories(): LiveData<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(categoryList: List<Category>)

    @Delete
    fun delete(category: Category): Int

    @Query("UPDATE category SET categoryName = :newCategoryName WHERE id = :categoryId")
    fun update(categoryId: Int, newCategoryName: String)

    @Query("DELETE FROM category")
    fun deleteAll()
}
