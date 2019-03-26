package com.example.cookingrecipes.di

import com.example.cookingrecipes.App
import com.example.cookingrecipes.di.module.AppModule
import com.example.cookingrecipes.di.module.BuildersViewModule
import com.example.cookingrecipes.di.module.RestApiModule
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Component, specifies who is going to be able to manually inject it:
 */
@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class,
        BuildersViewModule::class, AppModule::class, RestApiModule::class]
)

interface ApplicationComponent {
    fun inject(app: App)
}