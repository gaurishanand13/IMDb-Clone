package com.example.myproject.RetrofitDataClasses


data class detailsOfCertification(var certification:String,var meaning:String,var order : Int)

data class countries(var countrywiseDetails : ArrayList<detailsOfCertification>)

data class certificationData(var certifications: ArrayList<countries>)