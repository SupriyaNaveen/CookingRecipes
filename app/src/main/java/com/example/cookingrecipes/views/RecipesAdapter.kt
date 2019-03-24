package com.example.cookingrecipes.views

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cookingrecipes.R
import com.example.cookingrecipes.model.data.CookingRecipes
import com.example.cookingrecipes.utils.Utils
import com.squareup.picasso.Picasso
import java.net.MalformedURLException
import java.util.regex.Pattern


class RecipesAdapter(
    context: Context,
    recipesArray: List<CookingRecipes>?
) : RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder>() {

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        val recipes = recipesList[position]
        holder?.recipesListItem(context, recipes)
        holder?.itemView?.setOnClickListener {
            val intent = Intent(context, RecipesDetailsActivity::class.java)
            intent.putExtra("recipes_id", recipes.id)
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.recipes_list_item,
            parent, false
        )
        return RecipesViewHolder(itemView)
    }

    private var recipesList = ArrayList<CookingRecipes>()
    private var context: Context

    init {
        this.recipesList = recipesArray as ArrayList<CookingRecipes>
        this.context = context as Context
    }

    override fun getItemCount(): Int {
        return recipesList.size
    }

    fun addRecipes(recipesData: List<CookingRecipes>) {
        val initPosition = recipesList.size
        recipesList.addAll(recipesData)
        notifyItemRangeInserted(initPosition, recipesList.size)
    }

    fun addRecipesData(recipesData: CookingRecipes) {
        val initPosition = recipesList.size
        recipesList.add(recipesData)
        notifyItemRangeInserted(initPosition, recipesList.size)
    }

    fun resetRecipes() {
        val initPosition = 0
        recipesList.clear()
        notifyItemRangeInserted(initPosition, recipesList.size)
    }

    class RecipesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var recipesName = itemView.findViewById<TextView>(R.id.recipes_name)
        private var recipesLink = itemView.findViewById<TextView>(R.id.recipes_video_link)
        private var recipesDescription = itemView.findViewById<TextView>(R.id.recipes_video_description)
        private var recipeImage = itemView.findViewById<ImageView>(R.id.image_view_thumbnail)

        fun recipesListItem(context: Context, recipes: CookingRecipes) {
            recipesName.text = recipes.recipeName
            recipesLink.text = recipes.recipeLink
            recipesDescription.text = recipes.recipeDescription

            var url = Utils.getYoutubeThumbnailUrlFromVideoUrl(recipes.recipeLink!!)
            try {
                Picasso.get()
                    .load(url)
                    .centerCrop().resize(200,200)
                    .placeholder(R.drawable.ic_menu_camera)
                    .error(R.drawable.ic_menu_camera)
                    .into(recipeImage)

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }

        }
    }
}
