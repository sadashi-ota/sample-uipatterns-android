package com.sadashi.apps.ui.samples.activities

import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.sadashi.apps.ui.samples.R
import kotlinx.android.synthetic.main.activity_drawer_layout.*

class DrawerLayoutActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer_layout)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.drawer_layout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        (item.itemId == R.id.action_settings) && return true

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val message = when (item.itemId) {
            R.id.nav_camera -> "camera"
            R.id.nav_gallery -> "gallery"
            R.id.nav_slideshow -> "slideshow"
            R.id.nav_manage -> "manage"
            R.id.nav_share -> "share"
            R.id.nav_send -> "send"
            else -> "unknown"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
