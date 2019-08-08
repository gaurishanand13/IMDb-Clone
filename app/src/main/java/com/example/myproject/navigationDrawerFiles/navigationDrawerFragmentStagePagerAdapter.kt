package com.example.myproject.navigationDrawerFiles

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class navigationDrawerFragmentStagePagerAdapter(fm: FragmentManager,val favouritesOrWatchlist : Int):FragmentStatePagerAdapter(fm){

    //favouritesOrWatchlist -> 0 then it is for favourites
    //favouritesOrWatchlist -> 1 then it is for watchlist

    override fun getPageTitle(position: Int): CharSequence? {
        return super.getPageTitle(position)
    }

    override fun getItem(position: Int): Fragment {

        when(position){
            0->{
                // 0  is for movies in both cases
                if(favouritesOrWatchlist == 0){
                    val fragment = navigation_drawer_fragment()
                    val bundle = Bundle()
                    bundle.putString("media_type","movies")
                    bundle.putString("toShow","favourites")
                    fragment.arguments = bundle
                    return fragment
                }
                else if(favouritesOrWatchlist == 1){
                    val fragment = navigation_drawer_fragment()
                    val bundle = Bundle()
                    bundle.putString("media_type","movies")
                    bundle.putString("toShow","watchlist")
                    fragment.arguments = bundle
                    return fragment
                }
            }
            1->{
                if(favouritesOrWatchlist == 0){
                    val fragment = navigation_drawer_fragment()
                    val bundle = Bundle()
                    bundle.putString("media_type","tvShows")
                    bundle.putString("toShow","favourites")
                    fragment.arguments = bundle
                    return fragment
                }
                else if(favouritesOrWatchlist == 1){
                    val fragment = navigation_drawer_fragment()
                    val bundle = Bundle()
                    bundle.putString("media_type","tvShows")
                    bundle.putString("toShow","watchlist")
                    fragment.arguments = bundle
                    return fragment
                }
            }
        }
        Log.i("TAG","IT REACHED HERE")
        return navigation_drawer_fragment() //-> it will never reach here
    }


    override fun getCount() = 2

}