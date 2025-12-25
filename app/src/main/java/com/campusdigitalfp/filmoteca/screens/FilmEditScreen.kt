package com.campusdigitalfp.filmoteca.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.campusdigitalfp.filmoteca.R
import com.campusdigitalfp.filmoteca.common.FilmDataSource
import com.campusdigitalfp.filmoteca.common.Logger
import com.campusdigitalfp.filmoteca.common.barraSuperior

private const val TAG = "FilmEditScreen"
@Composable
fun filmEditScreen(navController: NavHostController, filmId: Int?) {

    val context = LocalContext.current

    //Recuperamos la pelicula por id
    val film = FilmDataSource.films.firstOrNull { it.id == filmId}

    // Validar filmId nulo
    if (filmId == null || film == null) {
        Scaffold(
            topBar = {
                barraSuperior(navController = navController, esHome = false, atras = true)
            }
        ) {
            Text(
                text = stringResource(R.string.pel_cula_no_encontrada),
                modifier = Modifier
                    .padding(16.dp)
            )
        }
        // Log de error
        filmId?.let { stringResource(R.string.pel_cula_no_encontrada_para_filmid, it) }
            ?.let { Logger.log(context,TAG, it) }
        return
    }

    //cargamos los datos en las variables
    var titulo by remember { mutableStateOf(film.title ?: "") }
    var director by remember { mutableStateOf(film.director ?: "") }
    var anyo by remember { mutableIntStateOf(film.year) }
    var url by remember { mutableStateOf(film.imdbUrl ?: "") }
    var comentarios by remember { mutableStateOf(film.comments ?: "") }

    var genero by remember { mutableIntStateOf(film.genre) }
    var formato by remember { mutableIntStateOf(film.format) }

    // Dropdowns
    val generoList = context.resources.getStringArray(R.array.genero_list).toList()
    val formatoList = context.resources.getStringArray(R.array.formato_list).toList()
    var expandedGenero by remember { mutableStateOf(false) }
    var expandedFormato by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            barraSuperior(
                navController = navController,
                atras = true
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        ) {
            Spacer(Modifier.height(8.dp))
            // Campos de texto
            TextField(
                value = titulo,
                onValueChange = {
                    titulo = it
                    Logger.log(context, TAG, context.getString(R.string.t_tulo_cambiado_a, it))
                },
                label = { Text(stringResource(R.string.t_tulo)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            TextField(
                value = director,
                onValueChange = {
                    director = it
                    Logger.log(context, TAG, context.getString(R.string.director_cambiado_a, it))
                },
                label = { Text(stringResource(R.string.director)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            TextField(
                value = anyo.toString(),
                onValueChange = {
                    anyo = it.toIntOrNull() ?: anyo
                    Logger.log(context, TAG, context.getString(R.string.a_o_cambiado_a, anyo))
                },
                label = { Text(stringResource(R.string.a_o)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            TextField(
                value = url,
                onValueChange = {
                    url = it
                    Logger.log(context, TAG, context.getString(R.string.url_imdb_cambiada_a, it))
                },
                label = { Text(stringResource(R.string.url_imdb)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            GenericSpinnerDropdown(
                items = generoList,
                selectedIndex = genero,
                onItemSelected = {
                    genero = it
                    Logger.log(context, TAG, context.getString(R.string.g_nero_cambiado_a, generoList[it]))
                },
                expanded = expandedGenero,
                onExpandedChange = { expandedGenero = it },
                label = stringResource(R.string.g_nero)
            )
            Spacer(Modifier.height(8.dp))
            GenericSpinnerDropdown(
                items = formatoList,
                selectedIndex = formato,
                onItemSelected = {
                    formato = it
                    Logger.log(context, TAG, context.getString(R.string.formato_cambiado_a, formatoList[it]))
                },
                expanded = expandedFormato,
                onExpandedChange = { expandedFormato = it },
                label = stringResource(R.string.formato)
            )
            Spacer(Modifier.height(8.dp))
            TextField(
                value = comentarios,
                onValueChange = {
                    comentarios = it
                    Logger.log(context, TAG, context.getString(R.string.comentarios_cambiados))
                },

                label = { Text(stringResource(R.string.comentarios)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledButton(
                    onClick = {
                        // Guardar cambios
                        film.title = titulo
                        film.director = director
                        film.year = anyo
                        film.imdbUrl = url
                        film.comments = comentarios
                        film.genre = genero
                        film.format = formato

                        //log
                        Logger.log(context, TAG,
                            context.getString(R.string.cambios_guardados_correctamente_para_la_pel_cula, film.title))

                        // Devolver resultado a la pantalla anterior
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("result", Activity.RESULT_OK)

                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f),
                    texto = stringResource(R.string.guardar)
                )
                FilledButton(
                    onClick = {
                        Logger.log(context,"Filmoteca", context.getString(
                            R.string.cambios_descartados_por_el_usuario_en_la_pel_cula, film
                                .title
                        ))
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("result", Activity.RESULT_CANCELED)
                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f),
                    texto = stringResource(R.string.cancelar)
                )
            }
        }
    }
}

// Caja para un dropdownmenu estilo spinner de java
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GenericSpinnerDropdown(
    items: List<T>,                             // Lista de opciones
    selectedIndex: Int,                         // Índice seleccionado actualmente
    onItemSelected: (Int) -> Unit,              // Callback con el índice seleccionado
    expanded: Boolean,                           // Estado de expansión del dropdown
    onExpandedChange: (Boolean) -> Unit,        // Función para cambiar expansión
    label: String = stringResource(R.string.selecciona_una_opci_n),
    itemToString: (T) -> String = { it.toString() } // Cómo mostrar cada item
) {
    if (items.isEmpty()) return

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { onExpandedChange(it) }
    ) {
        TextField(
            value = itemToString(items[selectedIndex]),
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = { Text(itemToString(item)) },
                    onClick = {
                        onItemSelected(index)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }

}



