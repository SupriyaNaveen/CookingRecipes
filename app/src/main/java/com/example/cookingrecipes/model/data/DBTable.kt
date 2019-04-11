package com.example.cookingrecipes.model.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Cooking Recipes data class
 */
@Entity(tableName = "recipes", indices = [Index(value = ["recipeLink"], unique = true)])
data class CookingRecipes(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Int? = null,
        @ColumnInfo(name = "recipeName")
        var recipeName: String? = null,
        @ColumnInfo(name = "recipeLink")
        val recipeLink: String? = null,
        @ColumnInfo(name = "recipeDescription")
        var recipeDescription: String? = null
) : Serializable

/**
 * Category data class
 */
@Entity(tableName = "category", indices = [Index(value = ["categoryName"], unique = true)])
data class Category(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Int? = null,
        @ColumnInfo(name = "categoryName")
        val categoryName: String
) : Serializable {
    override fun toString(): String {
        return categoryName.toUpperCase()
    }
}

/**
 * Category-recipes mapping data class
 */
@Entity(tableName = "recipes_category", primaryKeys = ["recipesId", "categoryId"])
data class RecipesCategoryMapping(
        @ColumnInfo(name = "recipesId")
        val recipesId: Int,
        @ColumnInfo(name = "categoryId")
        val categoryId: Int
)

