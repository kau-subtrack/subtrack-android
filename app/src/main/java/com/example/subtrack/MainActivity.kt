package com.example.subtrack

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Navigation 설정
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        // BottomNavigation 설정
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setupWithNavController(navController)
    }

    fun hideBottomNavigation(bool: Boolean) {
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomNavigation)
        if (bool) {
            bottomNavigation.visibility = View.GONE
        } else {
            bottomNavigation.visibility = View.VISIBLE
        }
    }
}