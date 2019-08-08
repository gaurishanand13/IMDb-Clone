package com.example.myproject.RetrofitDataClasses

import com.example.myproject.myInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient{
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val retrofitService = retrofit.create(myInterface::class.java)
}


object SESSION_ID_DETAILS{
    var sessionID : String?=null
}