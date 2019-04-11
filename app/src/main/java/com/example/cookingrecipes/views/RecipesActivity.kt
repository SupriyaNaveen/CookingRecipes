package com.example.cookingrecipes.views

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
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

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

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
     * On swipe delete the recipes and update the mapping table.
     */
    private fun initializeRecycler() {
        val gridLayoutManager = GridLayoutManager(this, 1)
        gridLayoutManager.orientation = RecyclerView.VERTICAL
        mDataBinding.recyclerViewRecipes.apply {
            setHasFixedSize(true)
            layoutManager = gridLayoutManager
        }
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                launch {
                    mRecipesViewModel.deleteRecipesData(mRecipesAdapter.recipesList[position])
                    mRecipesViewModel.deleteMappingForRecipesId(mRecipesAdapter.recipesList[position].id!!)
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(mDataBinding.recyclerViewRecipes)

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

    /**
     * Setting menu in action bar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_recipes, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Actions on click menu items
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_delete_all -> {
            if (mRecipesAdapter.itemCount > 0)
                showDeleteAllConfirmationDialog()
            else
                Toast.makeText(this, getString(R.string.error_no_recipes_to_delete), Toast.LENGTH_LONG).show()
            true
        }
        else -> {
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    /**
     * Show confirmation dialog for all recipes delete.
     * Once all recipes deleted, clear all mapping table as there is no recipes to mapping to.
     */
    private fun showDeleteAllConfirmationDialog() {
        val builder = AlertDialog.Builder(this)

        // Set the alert dialog title
        builder.setTitle(getString(R.string.recipes))

        // Display a message on alert dialog
        builder.setMessage(getString(R.string.message_confirm_delete))

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            launch {
                mRecipesViewModel.deleteAllRecipesData()
                mRecipesViewModel.deleteAllMappingData()
            }
        }
        builder.setNegativeButton(getString(com.example.cookingrecipes.R.string.cancel)) { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.cancel()
        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }
}
