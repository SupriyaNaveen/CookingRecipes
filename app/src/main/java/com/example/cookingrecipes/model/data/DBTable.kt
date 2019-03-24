package com.example.cookingrecipes.model.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "recipes", indices = [Index(value = ["recipeLink"], unique = true)])
data class CookingRecipes(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,
    @ColumnInfo(name = "recipeName")
    val recipeName: String ?= null,
    @ColumnInfo(name = "recipeLink")
    val recipeLink: String ?= null,
    @ColumnInfo(name = "recipeDescription")
    val recipeDescription: String ?= null
):Serializable

@Entity(tableName = "category")
data class Category(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "categoryName")
    val categoryName: String
)
