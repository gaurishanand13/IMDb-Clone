package com.example.myproject

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.myproject.RetrofitDataClasses.*
import com.example.myproject.RetrofitDataClasses.searchdataClasses.searchData
import com.example.myproject.loginDetails.*
import com.google.android.gms.common.internal.ConnectionErrorMessages
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


public interface myInterface{

    //login request methods
    @GET("/3/authentication/token/new?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e")
    fun getRequestToken() : Call<tokenDataClass>

    @POST("/3/authentication/token/validate_with_login?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e")
    fun validateRequestToken(@Body LoginValidityCheck: loginValidityCheck) : Call<answerOfLoginDetailsValidity>

    //We could have also created the body the of the above post method by like this using field but the above method looks more good
    @FormUrlEncoded
    @POST("/3/authentication/session/new?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e")
    fun createSessionId(@Field("request_token") request_token:String) : Call<sessionIdDetails>

    @DELETE("/3/authentication/session?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e")
    fun deleteSession(@Field("session_id") sessionID: String) : Call<sessionIDDelete>





    //Get request for languages avaialabe on imdb
    @GET("/3/configuration/languages?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e")
    fun languages(): Call<ArrayList<languageDetails>>






    //Get request for movie certification whether it is for adult or not
    @GET("/3/certification/movie/list?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e")
    fun getMovieCertificates() : Call<ArrayList<certificationData>>

    @GET("/3/certification/tv/list?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e")
    fun getTVCertificates() : Call<ArrayList<certificationData>>









    //Get request for movies
    @GET("/3/movie/popular?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US")
    fun getPopularMovieDetails(@Query("page") page :ArrayList<Int>) : Call<popularMovies>

    @GET("/3/movie/top_rated?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US")
    fun getTopRatedMovieDetails(@Query("page") page: ArrayList<Int>) : Call<popularMovies>

    @GET("/3/movie/upcoming?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US")
    fun getupcomingMovieDetails(@Query("page") page : ArrayList<Int>) : Call<popularMovies>

    @GET("/3/movie/now_playing?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e")
    fun movieInTheatreDetails(@Query("page") page :ArrayList<Int>) : Call<popularMovies>

    @GET("/3/movie/{movieId}?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US")
    fun getMovieDetailsByID(@Path("movieId") id : Int) : Call<particularMovieDetails>

    @GET("/3/movie/{movieId}/videos?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US")
    fun getVideoOfMovieByItsID(@Path("movieId") id : Int) : Call<movieVideoDetails>

    @GET("/3/movie/{movieId}/similar?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US&page=1")
    fun getSimilarMovies(@Path("movieId") id : Int) : Call<popularMovies>

    @GET("/3/movie/{movieId}/recommendations?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US&page=1")
    fun getReccommendationForAParticularMovie(@Path("movieId") id : Int) : Call<popularMovies>

    @GET("/3/movie/{movieId}/credits?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e\n")
    fun getTheCastOfTheMovie(@Path("movieId") id : Int) : Call<movieCastDataClass>

    @GET("/3/movie/{movieId}/reviews?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US")
    fun getMovieReviewByID(@Path("movieId") id : Int) : Call<movieReviewDataClass>

    @POST("/3/account/{account_id}/favorite?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e")
    fun postMovieAsFavourite(@Query("session_id") sessionID : String, @Body movieFavouriteDataClass : MovieFavouriteDataClass) : Call<answerPostMethodForMovies>

    @POST("/3/account/{account_id}/watchlist?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e")
    fun postMovieToTheWatchListOfTheUserDatabase(@Query("session_id") sessionID : String,@Body movieWatchListPostDataClass : movieWatchListPostDataClass) : Call<answerPostMethodForMovies>

    @GET("/3/account/{account_id}/favorite/movies?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US&sort_by=created_at.asc")
    fun getAllFavouriteMoviesOfTheUser(@Query("session_id") sessionID : String, @Query("page") page :Int) : Call<popularMovies>

    @GET("/3/account/{account_id}/watchlist/movies?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US&sort_by=created_at.asc")
    fun getAllThe_movies_added_to_the_watchlist_of_the_user(@Query("session_id") sessionID : String, @Query("page") page :Int) : Call<popularMovies>

    @FormUrlEncoded
    @POST("/3/movie/{movie_id}/rating?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e")
    fun rateTheMovie(@Path("movie_id") movieId:Int , @Field("value") value:Int, @Query("session_id") sessionID : String) : Call<answerPostMethodForMovies>








    //Get request for TV Shows
    @GET("/3/tv/top_rated?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US")
    fun topRatedTVShows(@Query("page") page :ArrayList<Int>) : Call<resultTV>

    @GET("/3/tv/popular?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US")
    fun popularTVShows(@Query("page") page :ArrayList<Int>) : Call<resultTV>

    @GET("/3/tv/airing_today?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US")
    fun comingTodayTVShows(@Query("page") page :ArrayList<Int>) : Call<resultTV>

    @GET("/3/tv/on_the_air?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US")
    fun liveTVShowsOnTV(@Query("page") page :ArrayList<Int>) : Call<resultTV>

    @GET("/3/account/{account_id}/favorite/tv?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US&sort_by=created_at.asc")
    fun getAllThefavouriteTVShows(@Query("session_id") sessionID : String, @Query("page") page :Int) : Call<resultTV>

    @GET("/3/account/{account_id}/watchlist/tv?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US&sort_by=created_at.asc")
    fun getTheWatchListofTVShows(@Query("session_id") sessionID : String, @Query("page") page :Int) : Call<resultTV>

    @GET("/3/tv/{tv_id}/videos?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US")
    fun getTVShowsVideosByItsKey(@Path("tv_id") tvID:Int) : Call<movieVideoDetails>

    @GET("/3/tv/{tv_id}?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US")
    fun getTVShowDetailByItsID(@Path("tv_id") TVid:Int) : Call<tvShowDetailsBY_ID>

    @GET("/3/tv/{tv_id}/similar?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US&page=1")
    fun getSimilarTVShowsBy_ItsID(@Path("tv_id") tvID:Int) : Call<resultTV>

    @GET("/3/tv/{tv_id}/recommendations?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US&page=1")
    fun getRecommendedTVShowsBy_itsID(@Path("tv_id") tvID:Int) : Call<resultTV>

    @GET("/3/tv/{tv_id}/credits?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US")
    fun getTVShowCast(@Path("tv_id") tvID:Int) : Call<tvShowCasts>

    @GET("/3/tv/{tv_id}/reviews?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US&page=1")
    fun getTVShowReviewsByID(@Path("tv_id") tvID:Int) : Call<movieReviewDataClass>



















    //Details for famous personalities
    @GET("/3/person/popular?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US")
    fun getPopularPersonalities(@Query("page") page :ArrayList<Int>) : Call<fomousPersonalitiesDataClass>

    @GET("/3/person/{celebId}?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e&language=en-US")
    fun getPopularPersonDetailsByID(@Path("celebId") id :Int) :Call<celeb_Details_by_ID>





    @GET("/3/search/multi?api_key=1ed8f9ef59abb304ccc3f26d6af29c8e")
    fun searchQuery(@Query("query") query: String) : Call<searchData>  //If space comes in the query instead of space add the "%20" as it is mentioned in their url

}