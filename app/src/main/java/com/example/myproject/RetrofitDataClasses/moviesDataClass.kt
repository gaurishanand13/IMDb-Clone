package com.example.myproject.RetrofitDataClasses


//data class popularMovies(var page:Int , var total_pages:Int , var results: ArrayList<movieDetails>)
//data class movieDetails(var id:Int , var vote_average : Int,var title : String,)

data class popularMovies(
    val page : Int,
    val total_pages : Int,
    val results : ArrayList<Results>
)


data class Results (
    val vote_count : Int,
    val id : Int,
    val video : Boolean,
    val vote_average : Double,
    val title : String,
    val popularity : Double,
    val poster_path : String,
    val original_language : String,
    val original_title : String,
    val genre_ids : List<Int>,
    val backdrop_path : String,
    val adult : Boolean,
    val overview : String,
    val release_date : String
)


