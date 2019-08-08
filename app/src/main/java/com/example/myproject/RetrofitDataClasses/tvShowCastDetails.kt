package com.example.myproject.RetrofitDataClasses

data class tvShowCasts(
    val cast : ArrayList<tvShowParticularCastDetails>,
    val id : Int
)

data class tvShowParticularCastDetails(
    val character: String,
    val credit_id: String,
    val gender: Int,
    val id: Int,
    val name: String,
    val order: Int,
    val profile_path: String
)