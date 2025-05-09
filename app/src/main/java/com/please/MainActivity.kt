package com.please

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.please.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Make status bar transparent but keep stable layout
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        
        setupBottomNavigation()
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavView.setupWithNavController(navController)
        
        // 화면 전환 시 바텀 네비게이션 표시/숨김 설정 및 메뉴 변경
        var previousMenuType = ""
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.sellerHomeFragment,
                R.id.sellerDeliveryFragment,
                R.id.sellerSubscriptionFragment,
                R.id.sellerManualFragment -> {
                    if (previousMenuType != "seller") {
                        binding.bottomNavView.menu.clear()
                        binding.bottomNavView.inflateMenu(R.menu.menu_seller_bottom_nav)
                        previousMenuType = "seller"
                    }
                    binding.bottomNavView.visibility = View.VISIBLE
                }
                R.id.driverHomeFragment,
                R.id.driverCollectFragment,
                R.id.driverDeliverFragment,
                R.id.driverMapFragment -> {
                    if (previousMenuType != "driver") {
                        binding.bottomNavView.menu.clear()
                        binding.bottomNavView.inflateMenu(R.menu.menu_driver_bottom_nav)
                        previousMenuType = "driver"
                    }
                    binding.bottomNavView.visibility = View.VISIBLE
                }
                else -> binding.bottomNavView.visibility = View.GONE
            }
        }
    }
}
