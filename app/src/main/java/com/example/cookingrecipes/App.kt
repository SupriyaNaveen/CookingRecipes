package com.example.cookingrecipes

import android.app.Activity
import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

import com.example.cookingrecipes.di.DaggerApplicationComponent
import com.example.cookingrecipes.di.module.AppModule
import com.example.cookingrecipes.di.module.RestApiModule
import com.example.cookingrecipes.utils.Constants


class App : Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        Timber.uprootAll()
        Timber.plant(Timber.DebugTree())

        DaggerApplicationComponent.builder()
            .appModule(AppModule(this))
            .restApiModule(RestApiModule(Constants.BASE_URL_REST_API))
            .build().inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

//{
//
//    companion object {
//        private lateinit var retrofit: Retrofit
//        private lateinit var restApi: RestApi
//        private lateinit var recipesModel: RecipesModel
//        private lateinit var recipesViewModel: RecipesViewModel
//        private lateinit var appDatabase: AppDatabase
//        private lateinit var categoryModel: CategoryModel
//        private lateinit var categoryViewModel: CategoryViewModel
//
////        fun injectRecipesApi() = restApi
//
//        fun injectRecipesViewModel() = recipesViewModel
//
////        fun injectRecipesDao() = appDatabase.recipesDao()
//
//        fun injectCategoriesViewModel() = categoryViewModel
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//
//        retrofit = Retrofit.Builder()
////            .addConverterFactory(GsonConverterFactory.create())
////            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
////            .baseUrl("https://randomapi.com/api/")
////            .build()
//
//        restApi = retrofit.create(RestApi::class.java)
//        appDatabase = Room.databaseBuilder(
//            applicationContext,
//            AppDatabase::class.java, "recipes-database"
//        ).build()
//
//        recipesModel = RecipesModel(restApi, appDatabase.recipesDao())
//        recipesViewModel = RecipesViewModel(recipesModel)
//
//        categoryModel = CategoryModel(restApi, appDatabase.categoryDao())
//        categoryViewModel = CategoryViewModel(categoryModel)
//    }
}