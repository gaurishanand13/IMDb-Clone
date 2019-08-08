package com.example.myproject.RetrofitDataClasses

import retrofit2.http.Field


data class MovieFavouriteDataClass(
    val media_type: String,
    val media_id:Int,
    val favorite:Boolean
)

data class answerPostMethodForMovies(
    val status_code:Int,
    val status_message:String
)

data class movieWatchListPostDataClass(
    val media_type : String,
    val media_id : Int,
    val watchlist : Boolean
)