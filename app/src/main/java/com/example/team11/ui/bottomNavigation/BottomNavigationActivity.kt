package com.example.team11.ui.bottomNavigation

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.team11.R
import com.mapbox.mapboxsdk.Mapbox

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navigation)

        supportActionBar!!.hide()
        val navView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        val navController = findNavController(R.id.fragmentNavHost)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        Mapbox.getInstance(this, getString(R.string.access_token))
        navView.itemIconTintList = null
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigationMap,
                R.id.navigationList,
                R.id.navigationFavorites
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
