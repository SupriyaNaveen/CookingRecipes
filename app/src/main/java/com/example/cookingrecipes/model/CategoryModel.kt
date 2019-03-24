package com.example.Category.model

import com.example.cookingrecipes.model.api.RestApi
import com.example.cookingrecipes.model.data.Category
import com.example.cookingrecipes.model.db.CategoryDao
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 *
 */
class CategoryModel @Inject constructor(private val restApi: RestApi, private val categoryDao: CategoryDao) {

    /**
     *
     */
    fun getCategories(): Observable<List<Category>> {
        return Observable.concatArray(getCategoriesFromDb(), getCategoriesFromApi())
    }

    /**
     *
     */
    private fun getCategoriesFromDb(): Observable<List<Category>> {
        return categoryDao.getRecipes().filter { it.isNotEmpty() }
            .toObservable()
            .doOnNext {
                Timber.d("Dispatching ${it.size} users from DB...")
            }
    }

    /**
     *
     */
    private fun getCategoriesFromApi(): Observable<List<Category>> {
        return restApi.getCategories()
            .doOnNext {
                Timber.d("Dispatching ${it.size} users from API...")
                storeCategoriesInDb(it)
            }
    }

    /**
     *
     */
    private fun storeCategoriesInDb(recipes: List<Category>) {
        Observable.fromCallable { categoryDao.insertAll(recipes) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe {
                Timber.d("Inserted ${recipes.size} users from API in DB...")
            }
    }

}