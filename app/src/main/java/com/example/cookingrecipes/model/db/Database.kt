package com.example.cookingrecipes.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cookingrecipes.model.data.Category
import com.example.cookingrecipes.model.data.CookingRecipes

@Database(entities = [CookingRecipes::class, Category::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipesDao(): RecipesDao
    abstract fun categoryDao() : CategoryDao
}