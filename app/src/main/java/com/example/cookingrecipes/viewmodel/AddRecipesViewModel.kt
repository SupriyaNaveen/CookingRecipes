package com.example.cookingrecipes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cookingrecipes.model.AddRecipesModel
import com.example.cookingrecipes.model.data.CookingRecipes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 *  ViewModel is helper class for the UI controller, that is responsible for preparing data for the UI.
 *  ViewModel objects are automatically retained during configuration
 *  changes so that data they hold is immediately available to the next activity or fragment instance.
 */
class AddRecipesViewModel @Inject constructor(private val addRecipesModel: AddRecipesModel) : ViewModel() {

    var recipesAddResult: MutableLiveData<Long> = MutableLiveData()
    var recipesAddError: MutableLiveData<String> = MutableLiveData()
    lateinit var disposableObserver: DisposableSingleObserver<Long>

    fun recipesAddResult(): LiveData<Long> {
        return recipesAddResult
    }

    fun recipesAddError(): LiveData<String> {
        return recipesAddError
    }

    fun addRecipesDetails(cookingRecipes: CookingRecipes) {

        disposableObserver = object : DisposableSingleObserver<Long>() {
            override fun onSuccess(t: Long) {
                recipesAddResult.postValue(t)
            }

            override fun onError(e: Throwable) {
                recipesAddError.postValue(e.message)
            }
        }

        addRecipesModel.addRecipesData(cookingRecipes)!!.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io()).subscribe(disposableObserver)
    }


    fun disposeElements() {
        try {
            if (!disposableObserver.isDisposed) disposableObserver.dispose()
        } catch (e: UninitializedPropertyAccessException) {
            Timber.e(e)
        }
    }
}
