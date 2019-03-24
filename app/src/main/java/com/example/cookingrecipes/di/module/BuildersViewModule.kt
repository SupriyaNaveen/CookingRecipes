package co.cdmunoz.cryptocurrencyapp.di.modules

import com.example.cookingrecipes.views.AddRecipesActivity
import com.example.cookingrecipes.views.RecipesActivity
import com.example.cookingrecipes.views.RecipesDetailsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BuildersViewModule {

    @ContributesAndroidInjector
    abstract fun contributeRecipesActivity(): RecipesActivity

    @ContributesAndroidInjector
    abstract fun contributeRecipesDetailsActivity(): RecipesDetailsActivity

    @ContributesAndroidInjector
    abstract fun contributeAddRecipesActivity(): AddRecipesActivity
}