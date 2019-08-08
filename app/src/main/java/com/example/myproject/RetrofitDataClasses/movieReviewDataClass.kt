package com.example.myproject.RetrofitDataClasses

import android.icu.text.CaseMap


data class particularMovieReview(
    val author: String,
    val content: String
)


data class movieReviewDataClass(
    val id: Int,
    val page: Int,
    val results: ArrayList<particularMovieReview>,
    val total_pages: Int,
    val total_results: Int
)

data class expandableData(
    val title: String,
    val results: ArrayList<particularMovieReview>
)