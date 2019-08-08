package com.example.myproject.LoggedInFragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myproject.Config
import com.example.myproject.MoviesSeeAllActivity

import com.example.myproject.R
import com.example.myproject.RetrofitDataClasses.Results
import com.example.myproject.RetrofitDataClasses.RetrofitClient
import com.example.myproject.RetrofitDataClasses.movieVideoDetails
import com.example.myproject.RetrofitDataClasses.popularMovies
import com.example.myproject.particularMovieActivity
import com.example.myproject.recylerViewAdapter.myAdapter
import com.example.myproject.recylerViewAdapter.myAdapterInterface
import kotlinx.android.synthetic.main.activity_particular_movie.*
import kotlinx.android.synthetic.main.fragment_movies.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MoviesFragment : Fragment() {

    var adapter : myAdapter<Results>? = null


    fun settingUpTheInterface()
    {
        adapter?.myInterface = object : myAdapterInterface{

            override fun openParticularMoviesActivity(movieId: Int, movieName: String) {

                //First we will see if their exist atleast 1 video of the movie
                val call = RetrofitClient.retrofitService?.getVideoOfMovieByItsID(movieId)
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

            override fun openParticularTVShowActivity(TVid: Int, TVShowName: String) {

            }

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_movies, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.ComingSoonMoviesSeeAllbtn.setOnClickListener{
            val intent = Intent(context,MoviesSeeAllActivity::class.java)
            intent.putExtra("TITLE","Coming Soon")
            intent.putExtra("TYPE",1)
            startActivity(intent)
        }
        view.popularMoviesSeeAllbtn.setOnClickListener{
            val intent = Intent(context,MoviesSeeAllActivity::class.java)
            intent.putExtra("TITLE","Popular Movies")
            intent.putExtra("TYPE",2)
            startActivity(intent)
        }
        view.inTheatresMoviesSeeAllbtn.setOnClickListener {
            val intent = Intent(context,MoviesSeeAllActivity::class.java)
            intent.putExtra("TITLE","In Theatres")
            intent.putExtra("TYPE",3)
            startActivity(intent)
        }
        view.topRatedMoviesSeeAllbtn.setOnClickListener {
            val intent = Intent(context, MoviesSeeAllActivity::class.java)
            intent.putExtra("TITLE", "Top Rated Movies")
            intent.putExtra("TYPE", 4)
            startActivity(intent)
        }


        val pages = ArrayList<Int>()
        pages.add(1)

        val call1 = RetrofitClient.retrofitService?.getPopularMovieDetails(pages)
        call1?.enqueue(object  : Callback<popularMovies>{
            override fun onFailure(call: Call<popularMovies>, t: Throwable) {
                Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<popularMovies>, response: Response<popularMovies>) {
                if(response.isSuccessful)
                {
                    val list = response.body()
                    view.popularMoviesRecyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                    adapter = myAdapter<Results>(list!!.results, context!!, 1)
                    view.popularMoviesRecyclerView.adapter = adapter
                    settingUpTheInterface()
                }
            }
        })


        val call2 = RetrofitClient.retrofitService?.getTopRatedMovieDetails(pages)
        call2?.enqueue(object  : Callback<popularMovies>{
            override fun onFailure(call: Call<popularMovies>, t: Throwable) {
                Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<popularMovies>, response: Response<popularMovies>) {
                if(response.isSuccessful)
                {
                    val list = response.body()
                    view.TopRatedMoviesRecyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                    adapter = myAdapter<Results>(list!!.results, context!!, 1)
                    view.TopRatedMoviesRecyclerView.adapter = adapter
                    settingUpTheInterface()
                }
            }
        })

        val call3 = RetrofitClient.retrofitService?.getupcomingMovieDetails(pages)
        call3?.enqueue(object  : Callback<popularMovies>{
            override fun onFailure(call: Call<popularMovies>, t: Throwable) {
                Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<popularMovies>, response: Response<popularMovies>) {
                if(response.isSuccessful)
                {
                    val list = response.body()
                    view.ComingSoonMoviesRecyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                    adapter = myAdapter<Results>(list!!.results, context!!, 1)
                    view.ComingSoonMoviesRecyclerView.adapter = adapter
                    settingUpTheInterface()
                }
            }
        })

        val call4 = RetrofitClient.retrofitService?.movieInTheatreDetails(pages)
        call4?.enqueue(object  : Callback<popularMovies>{
            override fun onFailure(call: Call<popularMovies>, t: Throwable) {
                Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<popularMovies>, response: Response<popularMovies>) {
                if(response.isSuccessful)
                {
                    val list = response.body()
                    view.inTheatresMoviesRecyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                    adapter = myAdapter<Results>(list!!.results, context!!, 1)
                    view.inTheatresMoviesRecyclerView.adapter = adapter
                    settingUpTheInterface()
                }
            }
        })


    }
}
