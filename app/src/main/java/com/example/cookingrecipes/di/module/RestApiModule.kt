package com.example.cookingrecipes.di.module

import com.example.cookingrecipes.model.api.RestApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * REST API instance provider.
 */
@Module
class RestApiModule(private val baseUrl: String) {

    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(baseUrl)
            .build()
    }

    @Provides
    @Singleton
    fun providesApiInterface(retrofit: Retrofit): RestApi = retrofit.create(
        RestApi::class.java
    )
}