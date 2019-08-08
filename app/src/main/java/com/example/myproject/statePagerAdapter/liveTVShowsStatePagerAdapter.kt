package com.example.myproject.statePagerAdapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.myproject.R
import com.example.myproject.RetrofitDataClasses.TVResult
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.live_tv_shows_layout.view.*
import kotlinx.android.synthetic.main.movies_see_all_item_view.view.*


//Here we will make our TV pager adapter infinity i.e roundable
class liveTVShowsViewPagerAdapter(val list : ArrayList<TVResult>) : PagerAdapter() {


    //Since we are making our viewPager infinite scrollable. Therefore we should know from which position we should insert the data
    var customPosition = 0

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    //override fun getCount() = list.size
    override fun getCount() = Int.MAX_VALUE  //Here if we don't wan't infinite slides then write the above count. Here it will make it infinite

    override fun instantiateItem(container: ViewGroup, position: Int): Any
    {

        //Here we should idealy use the postion provided in the function paramater. But since we have made our view Pager infinitely scrollable. Therefore we are here using the custom position


        val layoutInflater = container.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.live_tv_shows_layout,container,false)
        if(customPosition == list.size)
        {
            customPosition = 0
        }
        Picasso.get().load("https://image.tmdb.org/t/p/original"+list.get(customPosition).poster_path).fit().into(view.LiveTVShowimageView)
        customPosition++
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as CardView)
    }
}
