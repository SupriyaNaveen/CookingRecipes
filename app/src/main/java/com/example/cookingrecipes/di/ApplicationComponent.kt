package com.example.cookingrecipes.di

import co.cdmunoz.cryptocurrencyapp.di.modules.BuildersViewModule
import com.example.cookingrecipes.App
import com.example.cookingrecipes.di.module.AppModule
import com.example.cookingrecipes.di.module.RestApiModule
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class,
        BuildersViewModule::class, AppModule::class, RestApiModule::class]
)

interface ApplicationComponent {
    fun inject(app: App)

//    @Component.Builder
//    interface Builder {
//
//        fun build(): ApplicationComponent
//
//        @BindsInstance
//        fun appBind(app: App): Builder
//
//        @BindsInstance
//        fun appModule(appModule: AppModule): AppModule
//
//        @BindsInstance
//        fun restApiModule(restApiModule: RestApiModule) : RestApiModule
//
//    }
}