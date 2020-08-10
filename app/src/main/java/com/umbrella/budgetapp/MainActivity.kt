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
                .Builder(
                        setOf( // Top level destinations
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
                ).setOpenableLayout(binding.drawerLayout)
                .build()

        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)
        NavigationUI.setupWithNavController(binding.toolbar, navController, config)

        setUpDrawerMenu()

        //Checks first item (Home)
        binding.navView.setCheckedItem(R.id.menu_group1_item1)
    }

    private fun setUpDrawerMenu() {
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.navigation_Drawer_Open, R.string.navigation_Drawer_Close)
        binding.drawerLayout.addDrawerListener(toggle)
        //Syncs drawerLayout on state restored.
        toggle.syncState()

        /*binding.navView.setNavigationItemSelectedListener { menuItem ->
            val id = when (menuItem.itemId) {
                R.id.menu_group1_item1 -> R.id.globalHome
                R.id.menu_group1_item2 -> R.id.globalRecords
                R.id.menu_group1_item3 -> R.id.globalStatistics
                R.id.menu_group2_item1 -> R.id.globalPlannedPayments
                R.id.menu_group2_item2 -> R.id.globalDebts
                R.id.menu_group2_item3 -> R.id.globalGoals
                R.id.menu_group3_item1 -> R.id.globalShoppingLists
                R.id.menu_group3_item2 -> R.id.globalStores
                R.id.menu_group4_item1 -> R.id.globalSettings
                R.id.menu_group4_item2 -> R.id.globalImports
                else -> -1
            }
            navController.navigate(id)
            menuItem.isChecked = true

            binding.drawerLayout.close()
            true
        }*/
    }

    fun logUserOut() {
        // TODO: 10/08/2020 Make this
    }

    override fun onBackPressed() {
        //Close drawer onBackPress
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.close()
        } else {
            //If drawer is closed and backStack is 0, open drawer. Else navigate up.
            if (supportFragmentManager.backStackEntryCount == 0) {
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