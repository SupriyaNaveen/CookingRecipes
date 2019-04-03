package com.example.cookingrecipes.views

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.cookingrecipes.R
import com.example.cookingrecipes.databinding.RecipesListItemBinding
import com.example.cookingrecipes.model.data.CookingRecipes
import com.example.cookingrecipes.utils.Constants
import com.example.cookingrecipes.utils.Utils
import com.squareup.picasso.Picasso
import java.net.MalformedURLException

/**
 * Recipes list recycler view adapter.
 * Recipes details page invoked on recipes selection.
 * Update adapter on add, delete, update recipes.
 */
class RecipesAdapter(
        context: Context,
        recipesArray: List<CookingRecipes>?
) : RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder>() {

    /**
     * onBindViewHolder, handle single row of recipes UI.
     * Show the single item of list. Handle its click action.
     */
    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        val cookingRecipes = recipesList[position]

        holder.apply {
            bind(cookingRecipes)
            itemView.tag = cookingRecipes
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, RecipesDetailsActivity::class.java)
            intent.putExtra(Constants.RECIPES_ID_INTENT_KEY, cookingRecipes.id)
            context.startActivity(intent)
        }
    }

    /**
     * Set the layout for single item of recipes list.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
        val itemView: RecipesListItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.recipes_list_item, parent, false)
        return RecipesViewHolder(itemView)
    }

    private var recipesList = ArrayList<CookingRecipes>()
    private var context: Context

    init {
        this.recipesList = recipesArray as ArrayList<CookingRecipes>
        this.context = context
    }

    /**
     * Number of rows in adapter.
     */
    override fun getItemCount(): Int {
        return recipesList.size
    }

    /**
     * Update adapter when list of recipes added.
     */
    fun updateRecipes(recipesData: List<CookingRecipes>) {
        recipesList = ArrayList(recipesData)
        notifyDataSetChanged()
    }

    class RecipesViewHolder(private val binding: RecipesListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cookingRecipes: CookingRecipes) {
            binding.apply {
                recipes = cookingRecipes
                executePendingBindings()
                val url = Utils.getYoutubeThumbnailUrlFromVideoUrl(cookingRecipes.recipeLink!!)
                try {
                    Picasso.get()
                            .load(url)
                            .centerCrop().resize(200, 200)
                            .placeholder(R.drawable.ic_kitchen_black_24dp)
                            .error(R.drawable.ic_kitchen_black_24dp)
                            .into(binding.imageViewThumbnail)

                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
