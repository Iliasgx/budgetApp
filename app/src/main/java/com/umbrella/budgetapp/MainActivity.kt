package com.umbrella.budgetapp

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main._activity.*

class MainActivity : AppCompatActivity(R.layout._activity) {
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
        initView()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        toolbar.visibility = View.VISIBLE

        setUpDrawerMenu()

        // Checks first item (Home)
        nav_view.setCheckedItem(R.id.globalHome)
    }

    private fun setUpDrawerMenu() {
        val config = AppBarConfiguration(topLevels, drawer_layout)

        nav_view.setupWithNavController(navController)
        toolbar.setupWithNavController(navController, config)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    fun logUserOut() {
        // TODO-UPCOMING: Log user out function
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.close()
        } else {
            if (topLevels.contains(navController.currentDestination?.id)) {
                drawer_layout.open()
            } else {
                navController.navigateUp()
            }
        }
    }

    override fun onDestroy() {
        drawer_layout.close()
        super.onDestroy()
    }
}
