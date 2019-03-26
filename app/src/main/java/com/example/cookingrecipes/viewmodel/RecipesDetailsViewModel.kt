package com.example.cookingrecipes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cookingrecipes.model.RecipesDetailsModel
import com.example.cookingrecipes.model.data.CookingRecipes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class RecipesDetailsViewModel @Inject constructor(private val recipesDetailsModel: RecipesDetailsModel) : ViewModel() {

    var recipesDetailsResult: MutableLiveData<CookingRecipes> = MutableLiveData()
    var recipesError: MutableLiveData<String> = MutableLiveData()
    var recipesDetailsLoader: MutableLiveData<Boolean> = MutableLiveData()
    private lateinit var disposableObserver: DisposableObserver<CookingRecipes>

    var recipesDeleteResult: MutableLiveData<Int> = MutableLiveData()
    lateinit var disposableDeleteObserver: DisposableSingleObserver<Int>

    var recipesUpdateResult: MutableLiveData<Int> = MutableLiveData()
    lateinit var disposableUpdateObserver: DisposableSingleObserver<Int>

    fun recipesDetailsResult(): LiveData<CookingRecipes> {
        return recipesDetailsResult
    }

    fun recipesError(): LiveData<String> {
        return recipesError
    }

    fun recipesDetailsLoader(): LiveData<Boolean> {
        return recipesDetailsLoader
    }

    fun loadRecipesDetails(id: Int) {

        disposableObserver = object : DisposableObserver<CookingRecipes>() {
            override fun onComplete() {

            }

            override fun onNext(recipesDetails: CookingRecipes) {
                recipesDetailsResult.postValue(recipesDetails)
                recipesDetailsLoader.postValue(false)
            }

            override fun onError(e: Throwable) {
                recipesError.postValue(e.message)
                recipesDetailsLoader.postValue(false)
            }
        }

        recipesDetailsModel.getRecipesDetails(id)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(disposableObserver)
    }

    fun recipesDeleteResult(): LiveData<Int> {
        return recipesDeleteResult
    }

    fun deleteRecipesDetails(cookingRecipes: CookingRecipes) {

        disposableDeleteObserver = object : DisposableSingleObserver<Int>() {

            override fun onSuccess(rowId: Int) {
                recipesDeleteResult.postValue(rowId)
            }

            override fun onError(e: Throwable) {
                recipesError.postValue(e.message)
            }
        }

        recipesDetailsModel.deleteRecipesFromDb(cookingRecipes)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(disposableDeleteObserver)
    }

    fun updateRecipesDetails(cookingRecipes: CookingRecipes) {

        disposableUpdateObserver = object : DisposableSingleObserver<Int>() {

            override fun onSuccess(rowId: Int) {
                recipesUpdateResult.postValue(rowId)
            }

            override fun onError(e: Throwable) {
                recipesError.postValue(e.message)
            }
        }

        recipesDetailsModel.updateRecipesToDb(cookingRecipes)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(disposableUpdateObserver)
    }

    fun disposeElements() {
        try {
            if (!disposableObserver.isDisposed) disposableObserver.dispose()
            if (!disposableDeleteObserver.isDisposed) disposableDeleteObserver.dispose()
            if (!disposableUpdateObserver.isDisposed) disposableUpdateObserver.dispose()
        } catch (e: UninitializedPropertyAccessException) {
            Timber.e(e)
        }
    }
}


