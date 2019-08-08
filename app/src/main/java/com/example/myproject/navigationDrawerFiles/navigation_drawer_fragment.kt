package com.example.myproject.navigationDrawerFiles


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.myproject.R
import com.example.myproject.RetrofitDataClasses.*
import com.example.myproject.particularMovieActivity
import com.example.myproject.particularTVShowActivity
import com.example.myproject.recylerViewAdapter.SeeAllClick
import com.example.myproject.recylerViewAdapter.myAdapter
import com.example.myproject.statePagerAdapter.fragmentStatePagerAdapter
import com.google.android.gms.dynamic.SupportFragmentWrapper
import kotlinx.android.synthetic.main.fragment_navigation_drawer_fragment.*
import kotlinx.android.synthetic.main.fragment_navigation_drawer_fragment.view.*
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class navigation_drawer_fragment : Fragment() {

    var media_type : String? = null
    var toShow : String? = null
    var moviesAdapter : myAdapter<Results>? =null
    var tvShowsAdapter : myAdapter<TVResult>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_navigation_drawer_fragment, container, false)

        val bundle = arguments
        media_type = bundle!!.getString("media_type")
        toShow = bundle.getString("toShow")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if(media_type == "movies"){
            setUpForMovies()

        }
        if(media_type == "tvShows"){
            setUpForTVShows()
        }
    }


    fun setUpForMovies(){
        var call : retrofit2.Call<popularMovies>?= null

        if(toShow == "favourites"){

            //This means we have to show movie favourites
            call = RetrofitClient.retrofitService?.getAllFavouriteMoviesOfTheUser(SESSION_ID_DETAILS.sessionID!!,1)
            call?.enqueue(object : Callback<popularMovies>{
                override fun onFailure(call: retrofit2.Call<popularMovies>, t: Throwable) {
                    Toast.makeText(context,t.message,Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: retrofit2.Call<popularMovies>, response: Response<popularMovies>) {
                    if(response.isSuccessful){
                        val data = response.body()

                        val layoutManager = GridLayoutManager(context,2, RecyclerView.VERTICAL,false)
                        navigationDrawerRecyclerView.layoutManager = layoutManager
                        moviesAdapter = myAdapter<Results>(data!!.results,context!!,2)
                        navigationDrawerRecyclerView.adapter = moviesAdapter
                        setUpOnClickInterfaceForMovies()
                    }
                }
            })

        }
        else if(toShow == "watchlist"){
            //this means we have to show movies watchList
            //This means we have to show movie favourites
            call = RetrofitClient.retrofitService?.getAllThe_movies_added_to_the_watchlist_of_the_user(SESSION_ID_DETAILS.sessionID!!,1)
            call?.enqueue(object : Callback<popularMovies>{
                override fun onFailure(call: retrofit2.Call<popularMovies>, t: Throwable) {
                    Toast.makeText(context,t.message,Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: retrofit2.Call<popularMovies>, response: Response<popularMovies>) {
                    if(response.isSuccessful){
                        val data = response.body()

                        val layoutManager = GridLayoutManager(context,2, RecyclerView.VERTICAL,false)
                        navigationDrawerRecyclerView.layoutManager = layoutManager
                        moviesAdapter = myAdapter<Results>(data!!.results,context!!,2)
                        navigationDrawerRecyclerView.adapter = moviesAdapter
                        setUpOnClickInterfaceForMovies()
                    }
                }
            })

        }
    }

    fun setUpForTVShows(){
        var call : retrofit2.Call<resultTV>?= null
        if(toShow == "favourites"){
            //this means we have to show tv shows favourites

            call = RetrofitClient.retrofitService?.getAllThefavouriteTVShows(SESSION_ID_DETAILS.sessionID!!,1)
            call?.enqueue(object : Callback<resultTV>{
                override fun onFailure(call: retrofit2.Call<resultTV>, t: Throwable) {
                    Toast.makeText(context,t.message,Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: retrofit2.Call<resultTV>, response: Response<resultTV>) {
                    if(response.isSuccessful){
                        val data = response.body()

                        val layoutManager = GridLayoutManager(context,2, RecyclerView.VERTICAL,false)
                        navigationDrawerRecyclerView.layoutManager = layoutManager
                        tvShowsAdapter = myAdapter<TVResult>(data!!.results,context!!,4)
                        navigationDrawerRecyclerView.adapter = tvShowsAdapter
                        setUpOnClickInterfaceForTVShows()
                    }
                }
            })

        }
        else if(toShow == "watchlist"){
            //this means we have to show tv Shows watchlist

            call = RetrofitClient.retrofitService?.getTheWatchListofTVShows(SESSION_ID_DETAILS.sessionID!!,1)
            call?.enqueue(object : Callback<resultTV>{
                override fun onFailure(call: retrofit2.Call<resultTV>, t: Throwable) {
                    Toast.makeText(context,t.message,Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: retrofit2.Call<resultTV>, response: Response<resultTV>) {
                    if(response.isSuccessful){
                        val data = response.body()
                        val layoutManager = GridLayoutManager(context,2, RecyclerView.VERTICAL,false)
                        navigationDrawerRecyclerView.layoutManager = layoutManager
                        tvShowsAdapter = myAdapter<TVResult>(data!!.results,context!!,4)
                        navigationDrawerRecyclerView.adapter = tvShowsAdapter
                        setUpOnClickInterfaceForTVShows()
                    }
                }
            })

        }

    }


    fun setUpOnClickInterfaceForMovies(){
        moviesAdapter?.seeAllClickInterface = object : SeeAllClick{
            override fun SeeAllMoviesActivityClick(movieId: Int, movieName: String) {

                val call = RetrofitClient.retrofitService?.getVideoOfMovieByItsID(movieId)
                call?.enqueue(object  : Callback<movieVideoDetails> {
                    override fun onFailure(call: retrofit2.Call<movieVideoDetails>, t: Throwable) {
                        Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: retrofit2.Call<movieVideoDetails>, response: Response<movieVideoDetails>) {
                        if(response.isSuccessful)
                        {
                            val data = response.body()
                            if(data!!.results.size>0)
                            {

                                var intent = Intent(context,particularMovieActivity::class.java)
                                intent.putExtra("movieId",movieId)
                                intent.putExtra("movieName",movieName)
                                intent.putExtra("movieYoutubeId",data!!.results.get(0).key)
                                startActivity(intent)
                            }
                            else
                            {
                                Toast.makeText(context,"No Data Exits For This Movie",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                })


            }

            override fun SeeAllTVShowsActivityClick(TVid: Int, TVShowName: String) {

            }


        }
    }

    fun setUpOnClickInterfaceForTVShows(){

        tvShowsAdapter?.seeAllClickInterface = object : SeeAllClick{

            override fun SeeAllMoviesActivityClick(movieId: Int, movieName: String) {
                //This time this will be empty
            }

            override fun SeeAllTVShowsActivityClick(TVid: Int, TVShowName: String) {
                //This time we  will get data here and check and push them
                val call = RetrofitClient.retrofitService?.getTVShowsVideosByItsKey(TVid)
                call?.enqueue(object  : Callback<movieVideoDetails> {
                    override fun onFailure(call: retrofit2.Call<movieVideoDetails>, t: Throwable) {
                        Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: retrofit2.Call<movieVideoDetails>, response: Response<movieVideoDetails>) {
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
}
