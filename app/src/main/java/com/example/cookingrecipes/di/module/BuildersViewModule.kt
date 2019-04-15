package com.example.cookingrecipes.di.module

import com.example.cookingrecipes.views.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * View realted module instance.
 */
@Module
abstract class BuildersViewModule {

    @ContributesAndroidInjector
    abstract fun contributeRecipesActivity(): RecipesFragment

    @ContributesAndroidInjector
    abstract fun contributeRecipesDetailsActivity(): RecipesDetailsActivity

    @ContributesAndroidInjector
    abstract fun contributeAddRecipesActivity(): AddRecipesActivity

    @ContributesAndroidInjector
    abstract fun contributeHomeActivity(): HomeActivity

    @ContributesAndroidInjector
    abstract fun contributeCategoriesFragment(): CategoryFragment

    @ContributesAndroidInjector
    abstract fun contributeCategoriesRecipesFragment(): CategoriesRecipesFragment

    @ContributesAndroidInjector
    abstract fun contributeManageCategoriesFragment(): ManageCategoriesFragment
}