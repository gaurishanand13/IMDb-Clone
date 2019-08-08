package com.example.myproject

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myproject.RetrofitDataClasses.*
import com.example.myproject.recylerViewAdapter.myAdapter
import com.example.myproject.recylerViewAdapter.myAdapterInterface
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_particular_movie.*
import kotlinx.android.synthetic.main.activity_particular_tvshow.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class particularTVShowActivity : YouTubeBaseActivity(),YouTubePlayer.OnInitializedListener {


    var TVShowID= 0
    var TVShowYoutubeId = ""
    var sessionID : String? = null
    var movieInFavourites = false
    var movieInWatchList = false

    private val playbackEventListener = object : YouTubePlayer.PlaybackEventListener {
        override fun onBuffering(arg0: Boolean) {}
        override fun onPaused() {}
        override fun onPlaying() {
            Log.i("PUI","PLAYING")
        }
        override fun onSeekTo(arg0: Int) {}
        override fun onStopped() {}
    }

    private val playerStateChangeListener = object : YouTubePlayer.PlayerStateChangeListener {

        override fun onAdStarted() {}
        override fun onError(arg0: YouTubePlayer.ErrorReason) {
            Log.i("PUI","error $arg0")
        }
        override fun onLoaded(arg0: String) {}
        override fun onLoading() {}
        override fun onVideoEnded() {}
        override fun onVideoStarted() {}

    }

    override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, p1: YouTubePlayer?, p2: Boolean) {

        p1!!.setPlayerStateChangeListener(playerStateChangeListener)
        p1!!.setPlaybackEventListener(playbackEventListener)

        if(!p2)
        {
            p1!!.cueVideo(TVShowYoutubeId)
        }
    }

    override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
        Log.i("PUI","initialization")
        Toast.makeText(this@particularTVShowActivity,"Failed to Initialize", Toast.LENGTH_LONG).show()
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
            TVyoutubeView.initialize(Config.YOUTUBE_API_KEY,this)

        }
    }

    override fun onResume() {
        super.onResume()
        TVyoutubeView.initialize(Config.YOUTUBE_API_KEY,this)

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_particular_tvshow)
        TVShowID = intent.getIntExtra("movieId",0)
        TVShowYoutubeId = intent.getStringExtra("movieYoutubeId")
        val preferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        sessionID = preferences.getString("sessionID","")
        setTVShowDetails()
        setCast()
        setRecommendedTVShows()
        setSimilarTVShows()

        TVReviewButton.setOnClickListener {
            val intent = Intent(this@particularTVShowActivity,movieReviewActivity::class.java)
            intent.putExtra("TVShowID",TVShowID)
            intent.putExtra("type",1)
            startActivity(intent)
        }




        //First check if the TVShow is already in the favourites list or not. If it is already in the list , then reflect that accordingly in the favourite button
        var totalPages = 1 // 1 because 1 is the min page limit
        val favouriteCheckCall = RetrofitClient.retrofitService?.getAllThefavouriteTVShows(sessionID!!,1)
        favouriteCheckCall?.enqueue(object : Callback<resultTV>{
            override fun onFailure(call: Call<resultTV>, t: Throwable) {
                Toast.makeText(this@particularTVShowActivity,t.message,Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<resultTV>, response: Response<resultTV>) {
                if(response.isSuccessful)
                {
                    val data = response.body()
                    val result = data!!.results
                    totalPages = data!!.total_pages
                    for(x in result)
                    {
                        if(x.id == TVShowID)
                        {
                            movieInFavourites = true
                            TVaddToFavouritesButton.setBackgroundColor(Color.parseColor("#C2185B"))
                        }
                    }
                    Log.i("TAG","1")
                    Log.i("TAG",totalPages.toString())
                    if(totalPages == 1 || totalPages == 0){
                        //then it will not go to that function. Therefore implement the on click here only
                        Log.i("TAG","10")
                        setUpFavouriteButtonOnClick()
                    }
                    if(movieInFavourites == false)
                    {
                        //then only we will search in other pages
                        checkIfTVShowsIsFavouriteInOtherPages(totalPages)
                    }
                }
            }

        })

        //Now check if the the TV Show is in the watchList or not. If it is , then show it in the app
        totalPages = 1 // 1 because 1 is the min page limit
        val watchListCheckCall = RetrofitClient.retrofitService?.getTheWatchListofTVShows(sessionID!!,1)
        watchListCheckCall?.enqueue(object : Callback<resultTV>{
            override fun onFailure(call: Call<resultTV>, t: Throwable) {
                Toast.makeText(this@particularTVShowActivity,t.message,Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<resultTV>, response: Response<resultTV>) {
                if(response.isSuccessful)
                {
                    val data = response.body()
                    val result = data!!.results
                    totalPages = data!!.total_pages
                    for(x in result)
                    {
                        if(x.id == TVShowID)
                        {
                            movieInWatchList = true
                            Log.i("TAG","marked as watchlist")
                            TVaddToWatchListButton.setImageResource(android.R.color.transparent)
                            TVaddToWatchListButton.setImageResource(R.drawable.in_watchlist)
                        }
                    }
                    if(totalPages == 1 || totalPages == 0){
                        //then it will not go to that function. Therefore implement the on click here only
                        setUpWatchListButtonOnClick()
                    }
                    if(movieInWatchList == false)
                    {
                        //then only we will search in other pages
                        checkIfTVShowsIsInWatchListInOtherPages(totalPages)
                    }
                }
            }

        })

    }

    fun setTVShowDetails(){

        val call = RetrofitClient.retrofitService?.getTVShowDetailByItsID(TVShowID)
        call?.enqueue(object : Callback<tvShowDetailsBY_ID>{
            override fun onFailure(call: Call<tvShowDetailsBY_ID>, t: Throwable) {
                Toast.makeText(this@particularTVShowActivity,t.message,Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<tvShowDetailsBY_ID>, response: Response<tvShowDetailsBY_ID>) {

                if(response.isSuccessful){

                    val data = response.body()
                    //Now we will set The details of the movie in the expandable TextView
                    val title = data!!.name
                    val genre = data?.genres
                    runOnUiThread {
                        Picasso.get().load("https://image.tmdb.org/t/p/original" + data?.poster_path).fit().into(titleTVShowImageView)
                    }
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
                    var overview = data!!.overview

                    TV_expand_text_view.text = "${title}\n${genreInString}\n\nOVERVIEW\n${overview}\n"

                    val titleLength = title!!.length
                    val genreLength = genreInString.length
                    val spannableText = SpannableString(TV_expand_text_view.text)
                    spannableText.setSpan(RelativeSizeSpan(1.7f),0,titleLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableText.setSpan(ForegroundColorSpan(Color.WHITE),0,titleLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableText.setSpan(RelativeSizeSpan(1.1f),(titleLength+ genreLength + 2),(titleLength + genreLength -1) + 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableText.setSpan(ForegroundColorSpan(Color.YELLOW),(titleLength+ genreLength + 2),(titleLength + genreLength -1) + 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    TV_expand_text_view.text = spannableText
                }

            }

        })
    }

    fun setCast(){

        val call = RetrofitClient.retrofitService?.getTVShowCast(TVShowID)
        call?.enqueue(object  : Callback<tvShowCasts> {
            override fun onFailure(call: Call<tvShowCasts>, t: Throwable) {

                Toast.makeText(this@particularTVShowActivity,t.message,Toast.LENGTH_LONG).show()

            }

            override fun onResponse(call: Call<tvShowCasts>, response: Response<tvShowCasts>) {

                if(response.isSuccessful){
                    val data = response.body()
                    val casts = data!!.cast
                    TVShowCastRecyclerView.layoutManager = LinearLayoutManager(this@particularTVShowActivity,LinearLayoutManager.HORIZONTAL,false)
                    TVShowCastRecyclerView.adapter = myAdapter<tvShowParticularCastDetails>(casts,this@particularTVShowActivity,8)
                }

            }

        })

    }


    fun setSimilarTVShows(){

        val call = RetrofitClient.retrofitService?.getSimilarTVShowsBy_ItsID(TVShowID)
        call!!.enqueue(object : Callback<resultTV>{
            override fun onFailure(call: Call<resultTV>, t: Throwable) {
                Toast.makeText(this@particularTVShowActivity,t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<resultTV>, response: Response<resultTV>) {
                val list = response.body()
                val result = list!!.results
                similarTVShowsRecyclerView.layoutManager = LinearLayoutManager(this@particularTVShowActivity, LinearLayoutManager.HORIZONTAL,false)
                val adapter = myAdapter<TVResult>(result, this@particularTVShowActivity, 3)
                similarTVShowsRecyclerView.adapter = adapter
                adapter?.myInterface = object : myAdapterInterface{
                    override fun openParticularMoviesActivity(movieId: Int, movieName: String) {

                    }

                    override fun openParticularTVShowActivity(TVid: Int, TVShowName: String) {
                        //even for clicked item check if atleast one video exist

                        val call = RetrofitClient.retrofitService?.getTVShowsVideosByItsKey(TVid)
                        call?.enqueue(object  : Callback<movieVideoDetails> {
                            override fun onFailure(call: Call<movieVideoDetails>, t: Throwable) {
                                Toast.makeText(this@particularTVShowActivity,t.message,Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(call: Call<movieVideoDetails>, response: Response<movieVideoDetails>) {
                                if(response.isSuccessful)
                                {
                                    val data = response.body()
                                    if(data!!.results.size>0)
                                    {
                                        var intent = Intent(this@particularTVShowActivity, particularTVShowActivity::class.java)
                                        intent.putExtra("movieId",TVid)
                                        intent.putExtra("movieName",TVShowName)
                                        intent.putExtra("movieYoutubeId",data!!.results.get(0).key)
                                        startActivity(intent)
                                    }
                                    else
                                    {
                                        Toast.makeText(this@particularTVShowActivity,"No Data Exits For This TV Show",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        })
                    }

                }
            }

        })
    }

    fun setRecommendedTVShows(){

        val call = RetrofitClient.retrofitService?.getRecommendedTVShowsBy_itsID(TVShowID)
        call!!.enqueue(object : Callback<resultTV>{
            override fun onFailure(call: Call<resultTV>, t: Throwable) {
                Toast.makeText(this@particularTVShowActivity,t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<resultTV>, response: Response<resultTV>) {
                val list = response.body()
                val result = list!!.results
                recommendedTVShowsRecyclerView.layoutManager = LinearLayoutManager(this@particularTVShowActivity, LinearLayoutManager.HORIZONTAL,false)
                val adapter = myAdapter<TVResult>(result, this@particularTVShowActivity, 3)
                recommendedTVShowsRecyclerView.adapter = adapter
                adapter?.myInterface = object : myAdapterInterface{
                    override fun openParticularMoviesActivity(movieId: Int, movieName: String) {

                    }

                    override fun openParticularTVShowActivity(TVid: Int, TVShowName: String) {
                        //even for clicked item check if atleast one video exist

                        val call = RetrofitClient.retrofitService?.getTVShowsVideosByItsKey(TVid)
                        call?.enqueue(object  : Callback<movieVideoDetails> {
                            override fun onFailure(call: Call<movieVideoDetails>, t: Throwable) {
                                Toast.makeText(this@particularTVShowActivity,t.message,Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(call: Call<movieVideoDetails>, response: Response<movieVideoDetails>) {
                                if(response.isSuccessful)
                                {
                                    val data = response.body()
                                    if(data!!.results.size>0)
                                    {
                                        var intent = Intent(this@particularTVShowActivity, particularTVShowActivity::class.java)
                                        intent.putExtra("movieId",TVid)
                                        intent.putExtra("movieName",TVShowName)
                                        intent.putExtra("movieYoutubeId",data!!.results.get(0).key)
                                        startActivity(intent)
                                    }
                                    else
                                    {
                                        Toast.makeText(this@particularTVShowActivity,"No Data Exits For This TV Show",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        })
                    }

                }

            }

        })
    }


    fun checkIfTVShowsIsFavouriteInOtherPages(pages : Int){
        for(i in 2..pages)
        {
            if(movieInFavourites == false)
            {
                val favouriteCheckCall = RetrofitClient.retrofitService?.getAllThefavouriteTVShows(sessionID!!,i)
                favouriteCheckCall?.enqueue(object : Callback<resultTV>{
                    override fun onFailure(call: Call<resultTV>, t: Throwable) {
                        Toast.makeText(this@particularTVShowActivity,t.message,Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<resultTV>, response: Response<resultTV>) {
                        if(response.isSuccessful)
                        {
                            val data = response.body()
                            val result = data!!.results
                            for(x in result)
                            {
                                if(x.id == TVShowID)
                                {
                                    movieInFavourites = true
                                    TVaddToFavouritesButton.setBackgroundColor(Color.parseColor("#C2185B"))
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



    fun checkIfTVShowsIsInWatchListInOtherPages(pages: Int)
    {
        for(i in 2..pages)
        {
            if(movieInFavourites == false)
            {
                val watchListCheckCall = RetrofitClient.retrofitService?.getTheWatchListofTVShows(sessionID!!,i)
                watchListCheckCall?.enqueue(object : Callback<resultTV>{
                    override fun onFailure(call: Call<resultTV>, t: Throwable) {
                        Toast.makeText(this@particularTVShowActivity,t.message,Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<resultTV>, response: Response<resultTV>) {
                        if(response.isSuccessful)
                        {
                            val data = response.body()
                            val result = data!!.results
                            for(x in result)
                            {
                                if(x.id == TVShowID)
                                {
                                    movieInWatchList = true
                                    TVaddToWatchListButton.setImageResource(android.R.color.transparent)
                                    TVaddToWatchListButton.setImageResource(R.drawable.in_watchlist)
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
        Log.i("TAG","1")
        TVaddToFavouritesButton.setOnClickListener {
            if(movieInFavourites == true)
            {
                movieInFavourites = false
                TVaddToFavouritesButton.setBackgroundColor(Color.parseColor("#303F9F"))

                val call = RetrofitClient.retrofitService?.postMovieAsFavourite(sessionID!!, MovieFavouriteDataClass("tv",TVShowID,false))
                call?.enqueue(object : Callback<answerPostMethodForMovies>{
                    override fun onFailure(call: Call<answerPostMethodForMovies>, t: Throwable) {
                        Toast.makeText(this@particularTVShowActivity,t.message,Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<answerPostMethodForMovies>, response: Response<answerPostMethodForMovies>) {
                        if(response.isSuccessful)
                        {
                            Toast.makeText(this@particularTVShowActivity,"REMOVED TV SHOW FROM FAVOURITES",Toast.LENGTH_SHORT).show()
                        }
                    }

                })
            }
            else{
                movieInFavourites = true
                TVaddToFavouritesButton.setBackgroundColor(Color.parseColor("#C2185B"))
                val call = RetrofitClient.retrofitService?.postMovieAsFavourite(sessionID!!, MovieFavouriteDataClass("tv",TVShowID,true))
                call?.enqueue(object : Callback<answerPostMethodForMovies>{
                    override fun onFailure(call: Call<answerPostMethodForMovies>, t: Throwable) {
                        Toast.makeText(this@particularTVShowActivity,t.message,Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<answerPostMethodForMovies>, response: Response<answerPostMethodForMovies>) {
                        if(response.isSuccessful)
                        {
                            Toast.makeText(this@particularTVShowActivity,"ADDED TV SHOW TO FAVOURITES",Toast.LENGTH_SHORT).show()
                        }
                    }

                })
            }
        }
    }


    fun setUpWatchListButtonOnClick() {

        TVaddToWatchListButton.setOnClickListener {
            if(movieInWatchList == true)
            {
                movieInWatchList = false
                TVaddToWatchListButton.setImageResource(android.R.color.transparent)
                TVaddToWatchListButton.setImageResource(R.drawable.not_watchlist)

                val x = movieWatchListPostDataClass("tv",TVShowID,false)
                val call = RetrofitClient.retrofitService?.postMovieToTheWatchListOfTheUserDatabase( sessionID!!,x)
                call?.enqueue(object : Callback<answerPostMethodForMovies>{
                    override fun onFailure(call: Call<answerPostMethodForMovies>, t: Throwable) {
                        Toast.makeText(this@particularTVShowActivity,t.message,Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<answerPostMethodForMovies>, response: Response<answerPostMethodForMovies>) {
                        if(response.isSuccessful)
                        {
                            Toast.makeText(this@particularTVShowActivity,"REMOVED FROM WATCHLIST",Toast.LENGTH_SHORT).show()
                        }
                    }

                })
            }
            else{
                movieInWatchList = true
                TVaddToWatchListButton.setImageResource(android.R.color.transparent)
                TVaddToWatchListButton.setImageResource(R.drawable.in_watchlist)
                val x = movieWatchListPostDataClass("tv",TVShowID,true)
                val call = RetrofitClient.retrofitService?.postMovieToTheWatchListOfTheUserDatabase(sessionID!!,x)
                call?.enqueue(object : Callback<answerPostMethodForMovies>{
                    override fun onFailure(call: Call<answerPostMethodForMovies>, t: Throwable) {
                        Toast.makeText(this@particularTVShowActivity,t.message,Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<answerPostMethodForMovies>, response: Response<answerPostMethodForMovies>) {
                        if(response.isSuccessful)
                        {
                            Toast.makeText(this@particularTVShowActivity,"ADDED TO THE WATCHLIST SUCCESSFULLY",Toast.LENGTH_SHORT).show()
                        }
                    }

                })
            }
        }
    }
}
