package com.example.cookingrecipes.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cookingrecipes.R
import com.example.cookingrecipes.viewmodel.CategoryViewModel
import com.example.cookingrecipes.viewmodel.CategoryViewModelFactory
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


class CategoriesRecipesFragment : Fragment(), CoroutineScope {

    private var mPage: Int = 0
    private var mCategoryId: Int = 0
    private lateinit var mRecipesAdapter: RecipesAdapter

    companion object {
        private const val ARG_PAGE = "ARG_PAGE"
        private const val CATEGORY_ID = "CATEGORY_ID"

        fun newInstance(page: Int, categoryId: Int): CategoriesRecipesFragment {
            val args = Bundle()
            args.putInt(ARG_PAGE, page)
            args.putInt(CATEGORY_ID, categoryId)
            val fragment = CategoriesRecipesFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    @Inject
    lateinit var mCategoriesViewModelFactory: CategoryViewModelFactory
    lateinit var mCategoryViewModel: CategoryViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.layouts_category_containers, container, false)
        val recyclerView = v!!.findViewById(R.id.recycler_view_category_recipes) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(v.context)

        mCategoryViewModel = ViewModelProviders.of(this, mCategoriesViewModelFactory).get(
            CategoryViewModel::class.java
        )

        mRecipesAdapter = RecipesAdapter(v.context, ArrayList())
        recyclerView.adapter = mRecipesAdapter
        loadRecipes()
        return v
    }

    private fun loadRecipes() {
        launch {
            val context = activity as HomeActivity
            mCategoryViewModel.getRecipesCategoryMappingForCategoryId(mCategoryId)
                .observe(context, Observer { mappingList ->
                    mCategoryViewModel.getRecipesWithListOfRecipeIds(mappingList.map { it.recipesId })
                        .observe(context, Observer {
                            mRecipesAdapter.updateRecipes(it)
                        })
                })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPage = arguments!!.getInt(ARG_PAGE)
        mCategoryId = arguments!!.getInt(CATEGORY_ID)
    }
}
