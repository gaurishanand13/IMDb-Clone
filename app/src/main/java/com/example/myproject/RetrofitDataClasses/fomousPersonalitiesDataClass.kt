package com.example.myproject.RetrofitDataClasses

data class celeb_Details_by_ID(
    val adult: Boolean,
    val also_known_as: List<String>,
    val biography: String,
    val birthday: Any,
    val deathday: Any,
    val gender: Int,
    val homepage: Any,
    val id: Int,
    val imdb_id: String,
    val known_for_department: String,
    val name: String,
    val place_of_birth: Any,
    val popularity: Double,
    val profile_path: String
)







data class fomousPersonalitiesDataClass(
    val page: Int,
    val results: ArrayList<celebsResult>,
    val total_pages: Int,
    val total_results: Int
)

data class celebsResult(
    val adult: Boolean,
    val id: Int,
    val known_for: List<KnownFor>,
    val name: String,
    val popularity: Double,
    val profile_path: String
)

data class KnownFor(
    val adult: Boolean,
    val backdrop_path: String,
    val genre_ids: List<Int>,
    val id: Int,
    val media_type: String,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String,
    val release_date: String,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int
)