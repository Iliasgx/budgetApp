package com.umbrella.budgetapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.umbrella.budgetapp.databinding.ActivityBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBinding

    private val navController by lazy { findNavController(R.id.nav_host) }

    private val topLevels = setOf( // Top level destinations
            R.id.home,
            R.id.debts,
            R.id.goals,
            R.id.statistics,
            //R.id.records,
            R.id.plannedPayments,
            //R.id.shoppingLists,
            R.id.stores
            //R.id.settingsDefault,
            //R.id.imports
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.visibility = View.VISIBLE

        val config = AppBarConfiguration
                .Builder(topLevels)
                .setOpenableLayout(binding.drawerLayout)
                .build()

        NavigationUI.setupActionBarWithNavController(this, navController, config);
        NavigationUI.setupWithNavController(binding.navView, navController);

        setUpDrawerMenu()

        //Checks first item (Home)
        binding.navView.setCheckedItem(R.id.globalHome)
    }

    private fun setUpDrawerMenu() {
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.navigation_Drawer_Open, R.string.navigation_Drawer_Close)
        binding.drawerLayout.addDrawerListener(toggle)

        //Syncs drawerLayout on state restored.
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener { menuItem ->

            navController.navigate(menuItem.itemId)

            //navController.navigate(id)
            menuItem.isChecked = true

            binding.drawerLayout.close()
            true
        }
    }

    fun logUserOut() {
        // TODO-UPCOMING: Log user out function
    }

    override fun onBackPressed() {
        //Close drawer onBackPress
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.close()
        } else {
            if (topLevels.contains(navController.currentDestination?.id)) {
                binding.drawerLayout.open()
            } else {
                navController.navigateUp()
            }
        }
    }

    override fun onDestroy() {
        binding.drawerLayout.close()
        super.onDestroy()
    }
}