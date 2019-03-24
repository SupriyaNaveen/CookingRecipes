package com.example.cookingrecipes.model.api

import com.example.cookingrecipes.model.data.Category
import com.example.cookingrecipes.model.data.CookingRecipes
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Class includes definition of web API
 */
interface RestApi {

    @GET("api_url_path")
    fun getRecipes() : Observable<List<CookingRecipes>>

    @GET("api_url_path")
    fun getCategories() : Observable<List<Category>>
}
