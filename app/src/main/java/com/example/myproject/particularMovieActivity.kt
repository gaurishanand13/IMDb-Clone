package com.example.myproject

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.text.CaseMap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myproject.RetrofitDataClasses.*
import com.example.myproject.expandableListViewAdapter.customExpandableAdapter
import com.example.myproject.recylerViewAdapter.myAdapter
import com.example.myproject.recylerViewAdapter.myAdapterInterface
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import kotlinx.android.synthetic.main.activity_particular_movie.*
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener
import com.squareup.picasso.Picasso
import com.unnamed.b.atv.model.TreeNode
import com.unnamed.b.atv.view.AndroidTreeView
import kotlinx.android.synthetic.main.activity_particular_movie.view.*
import kotlinx.android.synthetic.main.fragment_movies.view.*
import kotlinx.android.synthetic.main.header_for_navigation_drawer.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.min


class particularMovieActivity : YouTubeBaseActivity(),YouTubePlayer.OnInitializedListener {


    var movieId = 0
    var movieYoutubeId = ""
    var sessionID : String? = null
    var movieInFavourites = false
    var movieInWatchList = false

    private val playbackEventListener = object : PlaybackEventListener {
        override fun onBuffering(arg0: Boolean) {}
        override fun onPaused() {}
        override fun onPlaying() {
            Log.i("PUI","PLAYING")
        }
        override fun onSeekTo(arg0: Int) {}
        override fun onStopped() {}
    }

    private val playerStateChangeListener = object : PlayerStateChangeListener {

        override fun onAdStarted() {}
        override fun onError(arg0: YouTubePlayer.ErrorReason) {
            Log.i("PUI","error $arg0")
        }
        override fun onLoaded(arg0: String) {}
        override fun onLoading() {}
        override fun onVideoEnded() {}
        override fun onVideoStarted() {}

    }

    fun setMovieDetails()
    {
        val call = RetrofitClient.retrofitService?.getMovieDetailsByID(movieId)
        call?.enqueue(object  : Callback<particularMovieDetails> {
            override fun onFailure(call: Call<particularMovieDetails>, t: Throwable) {
                Toast.makeText(this@particularMovieActivity,t.message,Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<particularMovieDetails>, response: Response<particularMovieDetails>) {
                if(response.isSuccessful)
                {

                    val data = response.body()
                    //Now we will set the data in the header for the movie

                    //First set the title
                    val title = data?.title
                    runOnUiThread {
                        Picasso.get().load("https://image.tmdb.org/t/p/original" + data?.poster_path).fit().into(titleMovieImageView)
                    }

                    val genre = data?.genres
                    var genreInString = ""
                    for(x in genre!!)
                    {
                        val name = x.name
                        genreInString = genreInString + name + " "
                        if(name!=genre!!.get(genre.size-1).name)
                        {
                            genreInString = genreInString + "|" + " "
                        }
                    }

                    val overview = data?.overview
                    expand_text_view.text = "${title}\n${genreInString}\n\nOVERVIEW\n${overview}\n"

                    val titleLength = title!!.length
                    val genreLength = genreInString.length
                    val spannableText = SpannableString(expand_text_view.text)
                    spannableText.setSpan(RelativeSizeSpan(1.7f),0,titleLength,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableText.setSpan(ForegroundColorSpan(Color.WHITE),0,titleLength,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableText.setSpan(RelativeSizeSpan(1.1f),(titleLength+ genreLength + 2),(titleLength + genreLength -1) + 12,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableText.setSpan(ForegroundColorSpan(Color.YELLOW),(titleLength+ genreLength + 2),(titleLength + genreLength -1) + 12,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    expand_text_view.text = spannableText
                }
            }
        })
    }

    fun setCast(){

        val call = RetrofitClient.retrofitService?.getTheCastOfTheMovie(movieId)
        call?.enqueue(object  : Callback<movieCastDataClass> {
            override fun onFailure(call: Call<movieCastDataClass>, t: Throwable) {
                Toast.makeText(this@particularMovieActivity,t.message,Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<movieCastDataClass>, response: Response<movieCastDataClass>)
            {
                if(response.isSuccessful)
                {
                    val data = response.body()
                    val casts = data!!.cast
                    movieCastRecyclerView.layoutManager = LinearLayoutManager(this@particularMovieActivity,LinearLayoutManager.HORIZONTAL,false)
                    movieCastRecyclerView.adapter = myAdapter<Cast>(casts,this@particularMovieActivity,7)
                }
            }
        })

    }
    override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, p1: YouTubePlayer?, p2: Boolean) {
        Log.i("PUI","initialization")
        p1!!.setPlayerStateChangeListener(playerStateChangeListener)
        p1!!.setPlaybackEventListener(playbackEventListener)

        if(!p2)
        {
            p1!!.cueVideo(movieYoutubeId)
        }
    }

    override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
        Log.i("PUI","initialization")
        Toast.makeText(this,"Failed to Initialize",Toast.LENGTH_LONG).show()
        if(p1!!.isUserRecoverableError)
        {
            p1.getErrorDialog(this,1).show()  //Here 1 is the request code
        }
        else
        {
            val error : String = "Error initializing YouTube player: ${p1.toString()}"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1)
        {
            //Retry initilisation if user has performed a recovery action
            youtubeView.initialize(Config.YOUTUBE_API_KEY,this)

        }
    }

    override fun onResume() {
        super.onResume()

        youtubeView.initialize(Config.YOUTUBE_API_KEY,this)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            android.R.id.home ->
            {
                onBackPressed()
                return true
            }
        }
        return false
    }

    //Base url for this client is "https://www.youtube.com/watch?v=key"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_particular_movie)

//        setActionBar(particularMovieToolBar)
//        actionBar!!.setDisplayHomeAsUpEnabled(true)

        movieId = intent.getIntExtra("movieId",0)
        movieYoutubeId = intent.getStringExtra("movieYoutubeId")

        //Now first we will get the movie details
        setMovieDetails()
        setCast()

        movieReviewButton.setOnClickListener {
            val intent = Intent(this@particularMovieActivity,movieReviewActivity::class.java)
            intent.putExtra("movieID",movieId)
            intent.putExtra("type",0)
            startActivity(intent)
        }

        //Now we will set the similar movie  and recommended movie details in the recycler view
        setSimilarMovies()
        setRecommendedMovies()


        val preferences = getSharedPreferences("myPref",Context.MODE_PRIVATE)
        sessionID = preferences.getString("sessionID","")



        //First check if the movie is already in the favourites list or not. If it is already in the list , then show them that in the favourite button
        var totalPages = 1 // 1 because 1 is the min page limit
        val favouriteCheckCall = RetrofitClient.retrofitService?.getAllFavouriteMoviesOfTheUser(sessionID!!,1)
        favouriteCheckCall?.enqueue(object : Callback<popularMovies>{
            override fun onFailure(call: Call<popularMovies>, t: Throwable) {
                Toast.makeText(this@particularMovieActivity,t.message,Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<popularMovies>, response: Response<popularMovies>) {
                if(response.isSuccessful)
                {
                    val data = response.body()
                    val result = data!!.results
                    totalPages = data!!.total_pages
                    for(x in result)
                    {
                        if(x.id == movieId)
                        {
                            movieInFavourites = true
                            addToFavouritesButton.setBackgroundColor(Color.parseColor("#C2185B"))
                        }
                    }
                    if(totalPages == 1){
                        //then it will not go to that function. Therefore implement the on click here only
                        setUpFavouriteButtonOnClick()
                    }
                    if(movieInFavourites == false)
                    {
                        //then only we will search in other pages
                        checkIfMovieIsFavouriteInOtherPages(totalPages)
                    }
                }
            }

        })

        //Now check if the the movie is in the watchList or not. If it is , then show it in the app
        totalPages = 1 // 1 because 1 is the min page limit
        val watchListCheckCall = RetrofitClient.retrofitService?.getAllThe_movies_added_to_the_watchlist_of_the_user(sessionID!!,1)
        watchListCheckCall?.enqueue(object : Callback<popularMovies>{
            override fun onFailure(call: Call<popularMovies>, t: Throwable) {
                Toast.makeText(this@particularMovieActivity,t.message,Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<popularMovies>, response: Response<popularMovies>) {
                if(response.isSuccessful)
                {
                    val data = response.body()
                    val result = data!!.results
                    totalPages = data!!.total_pages
                    for(x in result)
                    {
                        if(x.id == movieId)
                        {
                            movieInWatchList = true
                            Log.i("TAG","marked as watchlist")
                            addToWatchListButton.setImageResource(android.R.color.transparent)
                            addToWatchListButton.setImageResource(R.drawable.in_watchlist)
                        }
                    }
                    if(totalPages == 1){
                        //then it will not go to that function. Therefore implement the on click here only
                        setUpWatchListButtonOnClick()
                    }
                    if(movieInWatchList == false)
                    {
                        //then only we will search in other pages
                        checkIfMovieIsInWatchListInOtherPages(totalPages)
                    }
                }
            }

        })
    }

    fun checkIfMovieIsFavouriteInOtherPages(pages : Int){
        for(i in 2..pages)
        {
            if(movieInFavourites == false)
            {
                val favouriteCheckCall = RetrofitClient.retrofitService?.getAllFavouriteMoviesOfTheUser(sessionID!!,i)
                favouriteCheckCall?.enqueue(object : Callback<popularMovies>{
                    override fun onFailure(call: Call<popularMovies>, t: Throwable) {
                        Toast.makeText(this@particularMovieActivity,t.message,Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<popularMovies>, response: Response<popularMovies>) {
                        if(response.isSuccessful)
                        {
                            val data = response.body()
                            val result = data!!.results
                            for(x in result)
                            {
                                if(x.id == movieId)
                                {
                                    movieInFavourites = true
                                    addToFavouritesButton.setBackgroundColor(Color.parseColor("#C2185B"))
                                }
                            }

                            if(i == pages)
                            {
                               setUpFavouriteButtonOnClick()
                            }

                        }
                    }

                })
            }
        }
    }



    fun checkIfMovieIsInWatchListInOtherPages(pages: Int)
    {
        for(i in 2..pages)
        {
            if(movieInFavourites == false)
            {
                val watchListCheckCall = RetrofitClient.retrofitService?.getAllThe_movies_added_to_the_watchlist_of_the_user(sessionID!!,i)
                watchListCheckCall?.enqueue(object : Callback<popularMovies>{
                    override fun onFailure(call: Call<popularMovies>, t: Throwable) {
                        Toast.makeText(this@particularMovieActivity,t.message,Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<popularMovies>, response: Response<popularMovies>) {
                        if(response.isSuccessful)
                        {
                            val data = response.body()
                            val result = data!!.results
                            for(x in result)
                            {
                                if(x.id == movieId)
                                {
                                    movieInWatchList = true
                                    addToWatchListButton.setImageResource(android.R.color.transparent)
                                    addToWatchListButton.setImageResource(R.drawable.in_watchlist)
                                }
                            }

                            if(i == pages)
                            {
                                setUpWatchListButtonOnClick()
                            }

                        }
                    }

                })
            }
        }
    }


    fun setUpFavouriteButtonOnClick()
    {
        addToFavouritesButton.setOnClickListener {
            if(movieInFavourites == true)
            {
                movieInFavourites = false
                addToFavouritesButton.setBackgroundColor(Color.parseColor("#303F9F"))

                //also now update this information on the imdb database i.e unmark the movie as favourites
                val call = RetrofitClient.retrofitService?.postMovieAsFavourite(sessionID!!, MovieFavouriteDataClass("movie",movieId,false))
                call?.enqueue(object : Callback<answerPostMethodForMovies>{
                    override fun onFailure(call: Call<answerPostMethodForMovies>, t: Throwable) {
                        Toast.makeText(this@particularMovieActivity,t.message,Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<answerPostMethodForMovies>, response: Response<answerPostMethodForMovies>) {
                        if(response.isSuccessful)
                        {
                            Toast.makeText(this@particularMovieActivity,"REMOVED MOVIE FROM FAVOURITES",Toast.LENGTH_SHORT).show()
                        }
                    }

                })
            }
            else{
                movieInFavourites = true
                addToFavouritesButton.setBackgroundColor(Color.parseColor("#C2185B"))
                //also now update this information on the imdb database
                val call = RetrofitClient.retrofitService?.postMovieAsFavourite(sessionID!!, MovieFavouriteDataClass("movie",movieId,true))
                call?.enqueue(object : Callback<answerPostMethodForMovies>{
                    override fun onFailure(call: Call<answerPostMethodForMovies>, t: Throwable) {
                        Toast.makeText(this@particularMovieActivity,t.message,Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<answerPostMethodForMovies>, response: Response<answerPostMethodForMovies>) {
                        if(response.isSuccessful)
                        {
                            Toast.makeText(this@particularMovieActivity,"ADDED MOVIE TO FAVOURITES",Toast.LENGTH_SHORT).show()
                        }
                    }

                })
            }
        }
    }

    fun setUpWatchListButtonOnClick() {

        addToWatchListButton.setOnClickListener {
            if(movieInWatchList == true)
            {
                movieInWatchList = false
                addToWatchListButton.setImageResource(android.R.color.transparent)
                addToWatchListButton.setImageResource(R.drawable.not_watchlist)

                //also now update this information on the imdb database i.e unmark the movie as favourites
                val x = movieWatchListPostDataClass("movie",movieId,false)
                val call = RetrofitClient.retrofitService?.postMovieToTheWatchListOfTheUserDatabase( sessionID!!,x)
                call?.enqueue(object : Callback<answerPostMethodForMovies>{
                    override fun onFailure(call: Call<answerPostMethodForMovies>, t: Throwable) {
                        Toast.makeText(this@particularMovieActivity,t.message,Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<answerPostMethodForMovies>, response: Response<answerPostMethodForMovies>) {
                        if(response.isSuccessful)
                        {
                            Toast.makeText(this@particularMovieActivity,"REMOVED FROM WATCHLIST",Toast.LENGTH_SHORT).show()
                        }
                    }

                })
            }
            else{
                movieInWatchList = true
                addToWatchListButton.setImageResource(android.R.color.transparent)
                addToWatchListButton.setImageResource(R.drawable.in_watchlist)
                //also now update this information on the imdb database
                val x = movieWatchListPostDataClass("movie",movieId,true)
                val call = RetrofitClient.retrofitService?.postMovieToTheWatchListOfTheUserDatabase(sessionID!!,x)
                call?.enqueue(object : Callback<answerPostMethodForMovies>{
                    override fun onFailure(call: Call<answerPostMethodForMovies>, t: Throwable) {
                        Toast.makeText(this@particularMovieActivity,t.message,Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<answerPostMethodForMovies>, response: Response<answerPostMethodForMovies>) {
                        if(response.isSuccessful)
                        {
                            Toast.makeText(this@particularMovieActivity,"ADDED TO THE WATCHLIST SUCCESSFULLY",Toast.LENGTH_SHORT).show()
                        }
                    }

                })
            }
        }


    }

    fun setSimilarMovies()
    {
        val call = RetrofitClient.retrofitService?.getSimilarMovies(movieId)
        call?.enqueue(object  : Callback<popularMovies> {
            override fun onFailure(call: Call<popularMovies>, t: Throwable) {
                Toast.makeText(this@particularMovieActivity,t.message,Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<popularMovies>, response: Response<popularMovies>) {
                if(response.isSuccessful)
                {
                    val data = response.body()
                    similarMoviesRecyclerView.layoutManager = LinearLayoutManager(this@particularMovieActivity,LinearLayoutManager.HORIZONTAL,false)
                    val adapter = myAdapter<Results>(data!!.results,this@particularMovieActivity,1)
                    similarMoviesRecyclerView.adapter = adapter

                    adapter?.myInterface = object : myAdapterInterface{

                        override fun openParticularMoviesActivity(movieId: Int, movieName: String) {

                            //First we will see if their exist atleast 1 video of the movie
                            val call = RetrofitClient.retrofitService?.getVideoOfMovieByItsID(movieId)
                            call?.enqueue(object  : Callback<movieVideoDetails> {
                                override fun onFailure(call: Call<movieVideoDetails>, t: Throwable) {
                                    Toast.makeText(this@particularMovieActivity,t.message,Toast.LENGTH_LONG).show()
                                }

                                override fun onResponse(call: Call<movieVideoDetails>, response: Response<movieVideoDetails>) {
                                    if(response.isSuccessful)
                                    {
                                        val data = response.body()
                                        if(data!!.results.size>0)
                                        {

                                            var intent = Intent(this@particularMovieActivity,particularMovieActivity::class.java)
                                            intent.putExtra("movieId",movieId)
                                            intent.putExtra("movieName",movieName)
                                            intent.putExtra("movieYoutubeId",data!!.results.get(0).key)
                                            startActivity(intent)
                                        }
                                        else
                                        {
                                            Toast.makeText(this@particularMovieActivity,"No Data Exits For This Movie",Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            })

                        }

                        override fun openParticularTVShowActivity(TVid: Int, TVShowName: String)
                        {

                        }

                    }

                }
            }
        })
    }



    fun setRecommendedMovies()
    {
        val call = RetrofitClient.retrofitService?.getReccommendationForAParticularMovie(movieId)
        call?.enqueue(object  : Callback<popularMovies> {
            override fun onFailure(call: Call<popularMovies>, t: Throwable) {
                Toast.makeText(this@particularMovieActivity,t.message,Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<popularMovies>, response: Response<popularMovies>) {
                if(response.isSuccessful)
                {
                    val data = response.body()
                    recommendedMoviesRecyclerView.layoutManager = LinearLayoutManager(this@particularMovieActivity,LinearLayoutManager.HORIZONTAL,false)
                    val adapter = myAdapter<Results>(data!!.results,this@particularMovieActivity,1)
                    recommendedMoviesRecyclerView.adapter = adapter

                    adapter?.myInterface = object : myAdapterInterface{

                        override fun openParticularMoviesActivity(movieId: Int, movieName: String) {

                            //First we will see if their exist atleast 1 video of the movie
                            val call = RetrofitClient.retrofitService?.getVideoOfMovieByItsID(movieId)
                            call?.enqueue(object  : Callback<movieVideoDetails> {
                                override fun onFailure(call: Call<movieVideoDetails>, t: Throwable) {
                                    Toast.makeText(this@particularMovieActivity,t.message,Toast.LENGTH_LONG).show()
                                }

                                override fun onResponse(call: Call<movieVideoDetails>, response: Response<movieVideoDetails>) {
                                    if(response.isSuccessful)
                                    {
                                        val data = response.body()
                                        if(data!!.results.size>0)
                                        {

                                            var intent = Intent(this@particularMovieActivity,particularMovieActivity::class.java)
                                            intent.putExtra("movieId",movieId)
                                            intent.putExtra("movieName",movieName)
                                            intent.putExtra("movieYoutubeId",data!!.results.get(0).key)
                                            Log.i("TAG","$movieName")
                                            startActivity(intent)
                                        }
                                        else
                                        {
                                            Toast.makeText(this@particularMovieActivity,"No Data Exits For This Movie",Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            })

                        }

                        override fun openParticularTVShowActivity(TVid: Int, TVShowName: String) {

                        }

                    }
                }
            }
        })
    }
}
