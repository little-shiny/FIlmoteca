package com.campusdigitalfp.filmoteca.models

data class Film (
    val id : String = "",
    var comments: String = "",
    var director: String = "",
    var format: String = "",
    var genre: String = "",
    var imageUrl: String = "",
    var imbdUrl: String = "",
    var title: String = "",
    var year: Int = 0)
