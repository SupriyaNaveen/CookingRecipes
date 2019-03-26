package com.example.cookingrecipes.utils


class Constants {
    companion object {
        const val OFFSET = 8
        const val LIMIT = 8
        const val START_ZERO_VALUE = "0"
        const val DATABASE_NAME = "recipes_db"
        const val LIST_SCROLLING = 8
        const val BASE_URL_REST_API = "https://randomapi.com/api/"

        const val REQUEST_CODE_ADD_RECIPES = 100
        const val REQUEST_CODE_RECIPES_DETAILS = 101
        const val RESULT_CODE_DELETE = 102
        const val RESULT_CODE_UPDATE = 103

        const val ADDED_RECIPES_INTENT_KEY = "added_recipes"
        const val DELETED_RECIPES_INTENT_KEY = "deleted_recipes"
        const val RECIPES_ID_INTENT_KEY = "recipes_id"
        const val UPDATED_RECIPES_INTENT_KEY = "update_recipes"
    }
}