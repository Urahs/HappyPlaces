package com.example.happyplaces.models

data class HappyPlaceModel(
    var id: Int,
    var title: String,
    var image: String,
    var description: String,
    var date: String,
    var location: String,
    var latitude: Double,
    var longitude: Double,
)