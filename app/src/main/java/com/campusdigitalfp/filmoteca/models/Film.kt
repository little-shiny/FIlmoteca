package com.campusdigitalfp.filmoteca.models

data class Film (
    val id : String = "",
    var comments: String = "",
    var format: Int = 0,
    var genre: String = "",
    var imageResId: Int = 0,
    var imbdUrl: String = "",
    var title: String = "",
    var year: Int = 0)
