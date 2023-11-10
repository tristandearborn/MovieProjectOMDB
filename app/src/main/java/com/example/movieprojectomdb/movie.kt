package com.example.movieprojectomdb

data class movie( //takes data from omdb api and parses through it for relevant data
    val Title: String,
    val Poster: String,
    val Year: String,
    val Ratings: String,
    val Runtime: String,
    val Genre: String,
    val imdbRating: String,
    val imdbID: String
)
