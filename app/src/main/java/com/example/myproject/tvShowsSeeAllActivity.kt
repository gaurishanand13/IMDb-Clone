package com.example.myproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myproject.RetrofitDataClasses.*
import com.example.myproject.recylerViewAdapter.SeeAllClick
import com.example.myproject.recylerViewAdapter.myAdapter
import kotlinx.android.synthetic.main.activity_tv_shows_see_all.*
import kotlinx.android.synthetic.main.activity_tv_shows_see_all.progressBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class tvShowsSeeAllActivity : AppCompatActivity() {

    var listOfTVShows = ArrayList<TVResult>()
    var pageList = ArrayList<Int>()
    var totalPages = 4

    var call: Call<resultTV>? = null

    val layoutManager = GridLayoutManager(this,2, RecyclerView.VERTICAL,false)
    var adapter : myAdapter<TVResult>? = null

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId)
        {
            android.R.id.home ->{
                onBackPressed()
            }
            else -> return false
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_shows_see_all)

        setSupportActionBar(seeAllTVShowsToolBar)
        val x = supportActionBar
        x?.title = intent.getStringExtra("TITLE")
        val displayHomeAsUpEnabled = x?.setDisplayHomeAsUpEnabled(true)

        adapter = myAdapter<TVResult>(listOfTVShows, this, 4)
        seeAllTVShowsRecyclerView.layoutManager = layoutManager
        seeAllTVShowsRecyclerView.adapter = adapter


        pageList.add(1)
        setCall()
        update_Data_In_the_Recycler_View()

        var isScrolling = false
        seeAllTVShowsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //Now if the user scrolls first we will do the is scrolling variable true
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val currentVisibleItemsOnScreen =layoutManager.childCount
                val scrolledOutItems = layoutManager.findFirstVisibleItemPosition()
                val totalItemsInRecyclerView = layoutManager.itemCount
                if(isScrolling == true && (currentVisibleItemsOnScreen + scrolledOutItems == totalItemsInRecyclerView))
                {
                    isScrolling = false
                    progressBar.visibility = View.VISIBLE
                    pageList.clear()
                    pageList.add((totalItemsInRecyclerView/20)+1)
                    if(pageList.get(0)<totalPages)
                    {
                        setCall()
                        update_Data_In_the_Recycler_View()
                    }
                }
            }
        })
    }

    fun setUpOnClickInterfaceForTVShows(){

        adapter?.seeAllClickInterface = object : SeeAllClick {

            override fun SeeAllMoviesActivityClick(movieId: Int, movieName: String) {
                //This time this will be empty
            }

            override fun SeeAllTVShowsActivityClick(TVid: Int, TVShowName: String) {
                //This time we  will get data here and check and push them
                val call = RetrofitClient.retrofitService?.getTVShowsVideosByItsKey(TVid)
                call?.enqueue(object  : Callback<movieVideoDetails> {
                    override fun onFailure(call: retrofit2.Call<movieVideoDetails>, t: Throwable) {
                        Toast.makeText(this@tvShowsSeeAllActivity,t.message,Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: retrofit2.Call<movieVideoDetails>, response: Response<movieVideoDetails>) {
                        if(response.isSuccessful)
                        {
                            val data = response.body()
                            if(data!!.results.size>0)
                            {
                                var intent = Intent(this@tvShowsSeeAllActivity, particularTVShowActivity::class.java)
                                intent.putExtra("movieId",TVid)
                                intent.putExtra("movieName",TVShowName)
                                intent.putExtra("movieYoutubeId",data!!.results.get(0).key)
                                startActivity(intent)
                            }
                            else
                            {
                                Toast.makeText(this@tvShowsSeeAllActivity,"No Data Exits For This TV Show",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                })
            }
        }

    }

    private fun update_Data_In_the_Recycler_View() {
        call?.enqueue(object : Callback<resultTV>{
            override fun onFailure(call: Call<resultTV>, t: Throwable) {
                Toast.makeText(this@tvShowsSeeAllActivity,t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<resultTV>, response: Response<resultTV>) {
               if(response.isSuccessful)
               {
                   val smallList = response.body()
                   totalPages = smallList!!.total_pages
                   listOfTVShows.addAll(smallList!!.results)
                   adapter!!.notifyDataSetChanged()
                   progressBar.visibility = View.GONE
               }
            }

        })
    }

    private fun setCall() {
        if(intent.getIntExtra("TYPE",0) ===1)
        {
            call = RetrofitClient.retrofitService?.comingTodayTVShows(pageList)
        }

        else if(intent.getIntExtra("TYPE",0) ===2)
        {
            call = RetrofitClient.retrofitService?.popularTVShows(pageList)
        }

        else if(intent.getIntExtra("TYPE",0) ===3)
        {
            call = RetrofitClient.retrofitService?.topRatedTVShows(pageList)
        }
    }
}
