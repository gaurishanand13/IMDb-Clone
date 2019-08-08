package com.example.myproject.statePagerAdapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.myproject.LoggedInFragments.*

class fragmentStatePagerAdapter(fm : FragmentManager) :FragmentStatePagerAdapter(fm){

    override fun getPageTitle(position: Int): CharSequence? {
        return "OBJECT ${position+1}"
    }

    override fun getItem(position: Int): Fragment {
        when(position){
            0->{
                return MoviesFragment()
            }
            1 ->{
                return TVFragment()
            }
            2 -> {
                return CelebsFragment()
            }
            else -> return HomeFragment()
        }
    }
    override fun getCount() = 3

}