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
import com.example.myproject.RetrofitDataClasses.Results
import com.example.myproject.RetrofitDataClasses.RetrofitClient
import com.example.myproject.RetrofitDataClasses.movieVideoDetails
import com.example.myproject.RetrofitDataClasses.popularMovies
import com.example.myproject.recylerViewAdapter.SeeAllClick
import com.example.myproject.recylerViewAdapter.myAdapter
import kotlinx.android.synthetic.main.activity_movies_see_all.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MoviesSeeAllActivity : AppCompatActivity() {

    var list = ArrayList<Results>()
    val pageNos = ArrayList<Int>()
    //Setting the callBack for the retrofit
    var call: Call<popularMovies>? = null

    //setting the adapter
    val layoutManager = GridLayoutManager(this@MoviesSeeAllActivity,2,RecyclerView.VERTICAL,false)
    var adapter : myAdapter<Results>? = null

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

    fun setCall()
    {

        if(intent.getIntExtra("TYPE",0) ===1)
        {
            call = RetrofitClient.retrofitService?.getupcomingMovieDetails(pageNos)
        }

        else if(intent.getIntExtra("TYPE",0) ===2)
        {
            call = RetrofitClient.retrofitService?.getPopularMovieDetails(pageNos)
        }

        else if(intent.getIntExtra("TYPE",0) ===3)
        {
            call = RetrofitClient.retrofitService?.movieInTheatreDetails(pageNos)
        }

        else if(intent.getIntExtra("TYPE",0) ===4)
        {
            call = RetrofitClient.retrofitService?.getTopRatedMovieDetails(pageNos)
        }

    }


    fun update_Data_In_the_Recycler_View()
    {
        call?.enqueue(object  : Callback<popularMovies> {
            override fun onFailure(call: Call<popularMovies>, t: Throwable) {
                Toast.makeText(this@MoviesSeeAllActivity,t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<popularMovies>, response: Response<popularMovies>) {
                if(response.isSuccessful)
                {
                    val smallList = response.body()
                    list.addAll(smallList!!.results)
                    moviesSeeAllRecyclerView.layoutManager = layoutManager
                    adapter!!.notifyDataSetChanged()
                    progressBar.visibility = View.GONE
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies_see_all)

        setSupportActionBar(seeAllMoviesToolBar)
        val x = supportActionBar
        x?.setTitle(intent.getStringExtra("TITLE"))
        x?.setDisplayHomeAsUpEnabled(true)

        adapter = myAdapter<Results>(list,this@MoviesSeeAllActivity,2)
        moviesSeeAllRecyclerView.adapter = adapter
        setUpTheOnClickInterface()


        pageNos.add(1)
        setCall()
        update_Data_In_the_Recycler_View()

        var isScrolling = false
        moviesSeeAllRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
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
                    pageNos.clear()
                    pageNos.add((totalItemsInRecyclerView/20)+1)
                    setCall()
                    update_Data_In_the_Recycler_View()
                }
            }
        })

    }

    private fun setUpTheOnClickInterface() {
        adapter?.seeAllClickInterface = object : SeeAllClick{
            override fun SeeAllMoviesActivityClick(movieId: Int, movieName: String) {
                val call = RetrofitClient.retrofitService?.getVideoOfMovieByItsID(movieId)
                call?.enqueue(object  : Callback<movieVideoDetails> {
                    override fun onFailure(call: Call<movieVideoDetails>, t: Throwable) {
                        Toast.makeText(this@MoviesSeeAllActivity,t.message,Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<movieVideoDetails>, response: Response<movieVideoDetails>) {
                        if(response.isSuccessful)
                        {
                            val data = response.body()
                            if(data!!.results.size>0)
                            {

                                var intent = Intent(this@MoviesSeeAllActivity,particularMovieActivity::class.java)
                                intent.putExtra("movieId",movieId)
                                intent.putExtra("movieName",movieName)
                                intent.putExtra("movieYoutubeId",data!!.results.get(0).key)
                                startActivity(intent)
                            }
                            else
                            {
                                Toast.makeText(this@MoviesSeeAllActivity,"No Data Exits For This Movie",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                })

            }

            override fun SeeAllTVShowsActivityClick(TVid: Int, TVShowName: String) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }


        }
    }
}
