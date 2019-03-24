package com.example.cookingrecipes.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cookingrecipes.R
import com.example.cookingrecipes.model.data.CookingRecipes
import com.example.cookingrecipes.utils.Utils
import com.example.cookingrecipes.viewmodel.RecipesDetailsViewModel
import com.example.cookingrecipes.viewmodel.RecipesDetailsViewModelFactory
import com.squareup.picasso.Picasso
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_recipes.*
import kotlinx.android.synthetic.main.activity_recipes_details.*
import java.net.MalformedURLException
import javax.inject.Inject


class RecipesDetailsActivity : BaseActivity() {

    lateinit var mRecipesDetailsViewModel: RecipesDetailsViewModel

    @Inject
    lateinit var mRecipesDetailsViewModelFactory: RecipesDetailsViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes_details)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        initializeRecycler()

        mRecipesDetailsViewModel = ViewModelProviders.of(this, mRecipesDetailsViewModelFactory).get(
            RecipesDetailsViewModel::class.java
        )


        loadData(id = intent.getIntExtra("recipes_id", 0))

        mRecipesDetailsViewModel.recipesDetailsResult().observe(this,
            Observer<CookingRecipes> {
                if (it != null) {
                    text_view_recipes_name.text = it.recipeName
                    text_view_recipes_link.text = it.recipeLink
                    text_view_play_url.text = it.recipeLink
                    text_view_recipes_description.text = it.recipeDescription
                    var url = Utils.getYoutubeThumbnailUrlFromVideoUrl(it.recipeLink!!)
                    try {
                        Picasso.get()
                            .load(url)
                            .placeholder(R.drawable.ic_menu_camera)
                            .error(R.drawable.ic_menu_camera)
                            .into(image_view_play_url)

                    } catch (e: MalformedURLException) {
                        e.printStackTrace()
                    }

                }
            })

        mRecipesDetailsViewModel.recipesDetailsError().observe(this, Observer<String> {
            if (it != null) {
                Toast.makeText(
                    this, resources.getString(R.string.error_message_get_recipes_failed) + it,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        mRecipesDetailsViewModel.recipesDetailsLoader().observe(this, Observer<Boolean> {
            //            if (it == false) progress_bar.visibility = View.GONE
        })

        image_view_play_url.setOnClickListener { v: View? ->
            playYoutubeVideo()
        }

        text_view_play_url.setOnClickListener { v: View? ->
            playYoutubeVideo()
        }
    }


    private fun playYoutubeVideo() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(text_view_recipes_link.text.toString())))
    }

    private fun initializeRecycler() {
        val gridLayoutManager = GridLayoutManager(this, 1)
        gridLayoutManager.orientation = RecyclerView.VERTICAL
//        recycler_view_recipes.apply {
//            setHasFixedSize(true)
//            layoutManager = gridLayoutManager
//        }
    }

    private fun loadData(id: Int) {
        mRecipesDetailsViewModel.loadRecipesDetails(id)
    }

}
