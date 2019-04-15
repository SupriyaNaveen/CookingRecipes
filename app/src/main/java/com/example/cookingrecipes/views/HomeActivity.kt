package com.example.cookingrecipes.views

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.cookingrecipes.R
import com.google.android.material.navigation.NavigationView
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*


/**
 * Displays recipes with its category.
 * When app is launched default category added.
 * The recipes doesn't belong to any category goes to default category.
 * Add recipes, All recipes, add category, delete category options provided.
 */
class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        supportFragmentManager.beginTransaction().add(R.id.nav_content_wrapper, RecipesFragment.newInstance()).commit()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val transaction = supportFragmentManager.beginTransaction()
        when (item.itemId) {
            R.id.nav_all_recipes -> {
                transaction.replace(R.id.nav_content_wrapper, RecipesFragment.newInstance()).commit()
            }
            R.id.nav_categories -> {
                transaction.replace(R.id.nav_content_wrapper, CategoryFragment.newInstance()).commit()
            }
            R.id.nav_manage_category -> {
                transaction.replace(R.id.nav_content_wrapper, ManageCategoriesFragment.newInstance()).commit()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}