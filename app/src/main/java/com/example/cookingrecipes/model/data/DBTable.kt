package com.example.cookingrecipes.model.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Cooking Recipes data class
 */
//TODO : indices = [Index(value = ["recipeLink"], unique = true)]
@Entity(tableName = "recipes")
data class CookingRecipes(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,
    @ColumnInfo(name = "recipeName")
    var recipeName: String? = null,
    @ColumnInfo(name = "recipeLink")
    val recipeLink: String ?= null,
    @ColumnInfo(name = "recipeDescription")
    var recipeDescription: String? = null
):Serializable

/**
 * Category data class
 */
@Entity(tableName = "category")
data class Category(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "categoryName")
    val categoryName: String
)
