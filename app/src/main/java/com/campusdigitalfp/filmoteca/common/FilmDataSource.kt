package com.campusdigitalfp.filmoteca.common

import android.content.Context
import com.campusdigitalfp.filmoteca.R

object FilmDataSource {
    val films: MutableList<Film> = mutableListOf()
    fun loadData(context: Context) {}
    init {
        // Primera película: Harry Potter y la piedra filosofal
        val f1 = Film()
        f1.id = films.size
        f1.title = "Harry Potter y la piedra filosofal"
        f1.director = "Chris Columbus"
        f1.imageResId = R.drawable.harry_potter_y_la_piedra_filosofal
        f1.comments = "Una aventura mágica en Hogwarts."
        f1.format = Film.FORMAT_DVD
        f1.genre = Film.GENRE_ACTION // Cambia según corresponda
        f1.imdbUrl = "http://www.imdb.com/title/tt0241527"
        f1.year = 2001
        films.add(f1)

        // Segunda película: Regreso al futuro
        val f2 = Film()
        f2.id = films.size
        f2.title = "Regreso al futuro"
        f2.director = "Robert Zemeckis"
        f2.imageResId = R.drawable.regreso_al_futuro
        f2.comments = ""
        f2.format = Film.FORMAT_DIGITAL
        f2.genre = Film.GENRE_SCIFI
        f2.imdbUrl = "http://www.imdb.com/title/tt0088763"
        f2.year = 1985
        films.add(f2)

        // Tercera película: El rey león
        val f3 = Film()
        f3.id = films.size
        f3.title = "El rey león"
        f3.director = "Roger Allers, Rob Minkoff"
        f3.imageResId = R.drawable.el_rey_leon
        f3.comments = "Una historia de crecimiento y responsabilidad."
        f3.format = Film.FORMAT_BLURAY
        f3.genre = Film.GENRE_ACTION // Cambia según corresponda
        f3.imdbUrl = "http://www.imdb.com/title/tt0110357"
        f3.year = 1994
        films.add(f3)


        val f4 = Film()
        f4.id = films.size
        f4.title = "Kimetsu no Yaiba: Mugen-jō-hen"
        f4.director = "Haruo Sotozaki"
        f4.imageResId = R.drawable.guardianes
        f4.comments = "Guardianes de la noche: Tren infinito"
        f4.format = Film.FORMAT_DIGITAL
        f4.genre = Film.GENRE_ACTION // Cambia según corresponda
        f4.imdbUrl = "www.imdb.com/es-es/title/tt32820897/?reasonForLanguagePrompt=browser_header_mismatch"
        f4.year = 2025
        films.add(f4)

        // Añade más películas si deseas!
    }
}