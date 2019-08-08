package com.example.myproject.navigationDrawerFiles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myproject.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_logged_in.*
import kotlinx.android.synthetic.main.activity_user_favourites.*
import kotlinx.android.synthetic.main.activity_user_watch_list.*

class userFavouritesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_favourites)
        setSupportActionBar(navigationDrawerFavouritesToolBar)
        var x = supportActionBar
        x?.setTitle("Favourites")
        x?.setDisplayHomeAsUpEnabled(true)

        val adapter = navigationDrawerFragmentStagePagerAdapter(supportFragmentManager,0)
        favouritesviewPager.adapter = adapter
        favouritesviewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(navigationDrawerFavouritesTabLayout))
        navigationDrawerFavouritesTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                favouritesviewPager.currentItem = p0!!.position
            }

        })

    }
}
