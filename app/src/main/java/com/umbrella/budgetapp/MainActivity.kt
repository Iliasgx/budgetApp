package com.umbrella.budgetapp

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.viewmodels.UserViewModel
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
        //Function only used in testing period.
        getPreferences(MODE_PRIVATE).edit().clear().apply()
        getLoggedUser()
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

    private fun getLoggedUser() {
        val user = getPreferences(MODE_PRIVATE).getLong(getString(R.string.logged_user), 0L)

        val model by viewModels<UserViewModel>()

        if (user == 0L) {
            model.getFirstUserOrNull().observe(this, Observer {
                if (it == null) {
                    // TODO: 14/09/2020 create new user (screen)
                } else {
                    getPreferences(MODE_PRIVATE).edit().putLong(getString(R.string.logged_user), it.id!!).apply()
                    Memory.loggedUser = it
                    model.getFirstUserOrNull().removeObservers(this)
                }
            })
        } else {
            model.getUserById(user).observe(this@MainActivity, Observer {
                Memory.loggedUser = it.copy()
                model.getUserById(user).removeObservers(this@MainActivity)
            })
        }
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
