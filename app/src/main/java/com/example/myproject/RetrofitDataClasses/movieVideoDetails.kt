package com.example.myproject.RetrofitDataClasses

data class movieVideoDetails(
    val id: Int,
    val results: List<videoDetails>
)

data class videoDetails(
    val id: String,
    val iso_3166_1: String,
    val iso_639_1: String,
    val key: String,
    val name: String,
    val site: String,
    val size: Int,
    val type: String
)