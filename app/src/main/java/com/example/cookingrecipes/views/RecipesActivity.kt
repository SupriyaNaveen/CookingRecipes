package com.example.cookingrecipes.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cookingrecipes.R
import com.example.cookingrecipes.databinding.ActivityRecipesBinding
import com.example.cookingrecipes.viewmodel.RecipesViewModel
import com.example.cookingrecipes.viewmodel.RecipesViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Activity lists all recipes in Local DB
 * TODO: Web api creation and integration to application
 */
class RecipesActivity : AppCompatActivity(), CoroutineScope {

    //Initialize variable before accessing it
    @Inject
    lateinit var mRecipesViewModelFactory: RecipesViewModelFactory
    private var mRecipesAdapter = RecipesAdapter(this, ArrayList())
    private lateinit var mRecipesViewModel: RecipesViewModel
    private lateinit var mDataBinding: ActivityRecipesBinding

    private var job: Job = Job()
    private lateinit var lifecycleOwner: LifecycleOwner

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    /**
     * List of recipes displayed.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        lifecycleOwner = this

        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_recipes)

        mDataBinding.buttonAddRecipes.setOnClickListener {
            val intent = Intent(this, AddRecipesActivity::class.java)
            startActivity(intent)
        }

        initializeRecycler()
        mRecipesViewModel = ViewModelProviders.of(this, mRecipesViewModelFactory).get(
                RecipesViewModel::class.java
        )

        mDataBinding.progressBar.visibility = View.VISIBLE
        loadData()
    }

    /**
     * Initialise the recycler view adapter.
     */
    private fun initializeRecycler() {
        val gridLayoutManager = GridLayoutManager(this, 1)
        gridLayoutManager.orientation = RecyclerView.VERTICAL
        mDataBinding.recyclerViewRecipes.apply {
            setHasFixedSize(true)
            layoutManager = gridLayoutManager
        }
        mDataBinding.recyclerViewRecipes.adapter = mRecipesAdapter
    }

    /**
     * Load recipes
     */
    private fun loadData() {
        launch {
            mRecipesViewModel.loadRecipes().observe(lifecycleOwner, Observer {
                mDataBinding.progressBar.visibility = View.INVISIBLE
                mRecipesAdapter.updateRecipes(it)
            })
        }
    }
}
