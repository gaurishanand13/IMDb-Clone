package com.example.myproject.LoggedInFragments


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

import com.example.myproject.R
import com.example.myproject.RetrofitDataClasses.RetrofitClient
import com.example.myproject.RetrofitDataClasses.TVResult
import com.example.myproject.RetrofitDataClasses.movieVideoDetails
import com.example.myproject.RetrofitDataClasses.resultTV
import com.example.myproject.particularMovieActivity
import com.example.myproject.particularTVShowActivity
import com.example.myproject.recylerViewAdapter.myAdapter
import com.example.myproject.recylerViewAdapter.myAdapterInterface
import com.example.myproject.statePagerAdapter.liveTVShowsViewPagerAdapter
import com.example.myproject.tvShowsSeeAllActivity
import com.google.android.gms.dynamic.SupportFragmentWrapper
import kotlinx.android.synthetic.main.activity_logged_in.*
import kotlinx.android.synthetic.main.fragment_tv.*
import kotlinx.android.synthetic.main.fragment_tv.view.*
import kotlinx.android.synthetic.main.fragment_tv.view.TVviewPager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class TVFragment : Fragment() {

    val MIN_SCALE = 0.75f
    var call :  Call<resultTV>? = null
    var viewPagerCurrentPageNo = 0

    var adapter : myAdapter<TVResult>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tv, container, false)
    }

    fun updateDataInTheRecyclerView(recyclerView: RecyclerView)
    {
        call!!.enqueue(object : Callback<resultTV>{
            override fun onFailure(call: Call<resultTV>, t: Throwable) {
                Toast.makeText(context,t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<resultTV>, response: Response<resultTV>) {
                val list = response.body()
                val result = list!!.results
                recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
                adapter = myAdapter<TVResult>(result, context!!, 3)
                recyclerView.adapter = adapter
                settingUpTheInterfaceForThisParticularAdapter()
            }

        })
    }

    private fun settingUpTheInterfaceForThisParticularAdapter() {

        adapter?.myInterface = object : myAdapterInterface{

            override fun openParticularMoviesActivity(movieId: Int, movieName: String) {
                //here since there is no movie, therefore no action is to be performed here
            }

            override fun openParticularTVShowActivity(TVid: Int, TVShowName: String) {
                //First check if any video of the TV Show exist or not here
                val call = RetrofitClient.retrofitService?.getTVShowsVideosByItsKey(TVid)
                call?.enqueue(object  : Callback<movieVideoDetails> {
                    override fun onFailure(call: Call<movieVideoDetails>, t: Throwable) {
                        Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<movieVideoDetails>, response: Response<movieVideoDetails>) {
                        if(response.isSuccessful)
                        {
                            val data = response.body()
                            if(data!!.results.size>0)
                            {
                                var intent = Intent(context, particularTVShowActivity::class.java)
                                intent.putExtra("movieId",TVid)
                                intent.putExtra("movieName",TVShowName)
                                intent.putExtra("movieYoutubeId",data!!.results.get(0).key)
                                startActivity(intent)
                            }
                            else
                            {
                                Toast.makeText(context,"No Data Exits For This TV Show",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                })

            }

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.comingTodayTVSeeAllbtn.setOnClickListener {
            val intent = Intent(context, tvShowsSeeAllActivity::class.java)
            intent.putExtra("TITLE","Coming Today")
            intent.putExtra("TYPE",1)
            startActivity(intent)
        }
        view.popularTvShowsSeeAllbtn.setOnClickListener {
            val intent = Intent(context, tvShowsSeeAllActivity::class.java)
            intent.putExtra("TITLE","Popular TV Shows")
            intent.putExtra("TYPE",2)
            startActivity(intent)
        }
        view.topRatedTVShowsSeeAllbtn.setOnClickListener {
            val intent = Intent(context, tvShowsSeeAllActivity::class.java)
            intent.putExtra("TITLE","Top Rated TV Shows")
            intent.putExtra("TYPE",3)
            startActivity(intent)
        }

        val pages = ArrayList<Int>()
        pages.add(1)
        pages.add(2)

        //Now we will set up the data in the more decent way
        call = RetrofitClient.retrofitService?.comingTodayTVShows(pages)
        updateDataInTheRecyclerView(view.comingTodayTVShowRecyclerView)

        call = RetrofitClient.retrofitService?.popularTVShows(pages)
        updateDataInTheRecyclerView(view.popularTvShowsRecyclerView)

        call = RetrofitClient.retrofitService?.topRatedTVShows(pages)
        updateDataInTheRecyclerView(view.TopRatedTVShowsRecyclerView)

        call = RetrofitClient.retrofitService?.liveTVShowsOnTV(pages)

        call?.enqueue(object : Callback<resultTV>{
            override fun onFailure(call: Call<resultTV>, t: Throwable) {
                Toast.makeText(context,t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<resultTV>, response: Response<resultTV>) {
                if(response.isSuccessful)
                {
                    val data = response.body()
                    val list = data!!.results
                    TVviewPager.adapter = liveTVShowsViewPagerAdapter(list)
                }
            }
        })

        //This is just an extra feature added to this view pager to add the dept page transformation
        TVviewPager.setPageTransformer(true,DepthPageTransformer())

        creatingAutomatic_SlideShow_Of_The_ViewPager()
    }


    inner class DepthPageTransformer : ViewPager.PageTransformer {

        override fun transformPage(view: View, position: Float) {
            view.apply {
                val pageWidth = width
                when {
                    position < -1 -> { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        alpha = 0f
                    }
                    position <= 0 -> { // [-1,0]
                        // Use the default slide transition when moving to the left page
                        alpha = 1f
                        translationX = 0f
                        scaleX = 1f
                        scaleY = 1f
                    }
                    position <= 1 -> { // (0,1]
                        // Fade the page out.
                        alpha = 1 - position

                        // Counteract the default slide transition
                        translationX = pageWidth * -position

                        // Scale the page down (between MIN_SCALE and 1)
                        val scaleFactor = (MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position)))
                        scaleX = scaleFactor
                        scaleY = scaleFactor
                    }
                    else -> { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        alpha = 0f
                    }
                }
            }
        }
    }










    fun creatingAutomatic_SlideShow_Of_The_ViewPager()
    {
        //Therefore this function will be executed after every 3 seconds and after every 3 seconds it will go to the next slide
        val handler = Handler()
        val update = Runnable {
            TVviewPager?.setCurrentItem(TVviewPager.currentItem+1,true)
        }
        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask(){
            override fun run() {
                handler.post(update)
            }

        },4000,5000)
    }





}
