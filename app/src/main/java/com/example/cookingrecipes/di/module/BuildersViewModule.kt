package com.example.cookingrecipes.di.module

import com.example.cookingrecipes.views.AddRecipesActivity
import com.example.cookingrecipes.views.HomeActivity
import com.example.cookingrecipes.views.RecipesActivity
import com.example.cookingrecipes.views.RecipesDetailsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * View realted module instance.
 */
@Module
abstract class BuildersViewModule {

    @ContributesAndroidInjector
    abstract fun contributeRecipesActivity(): RecipesActivity

    @ContributesAndroidInjector
    abstract fun contributeRecipesDetailsActivity(): RecipesDetailsActivity

    @ContributesAndroidInjector
    abstract fun contributeAddRecipesActivity(): AddRecipesActivity

    @ContributesAndroidInjector
    abstract fun contributeHomeActivity(): HomeActivity
}