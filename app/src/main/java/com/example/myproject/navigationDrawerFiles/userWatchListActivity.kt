package com.example.myproject.navigationDrawerFiles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myproject.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_user_favourites.*
import kotlinx.android.synthetic.main.activity_user_watch_list.*

class userWatchListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_watch_list)
        setSupportActionBar(navigationDrawerWatchListToolBar)
        var x = supportActionBar
        x?.setTitle("Your WatchList")
        x?.setDisplayHomeAsUpEnabled(true)


        val adapter = navigationDrawerFragmentStagePagerAdapter(supportFragmentManager,1)
        watchListViewPager.adapter = adapter
        watchListViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(navigationDrawerWatchlistTabLayout))
        navigationDrawerWatchlistTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                watchListViewPager.currentItem = p0!!.position
            }

        })
    }
}
