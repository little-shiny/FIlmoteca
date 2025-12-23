package com.campusdigitalfp.filmoteca.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.campusdigitalfp.filmoteca.R
import com.campusdigitalfp.filmoteca.common.FilmDataSource
import com.campusdigitalfp.filmoteca.common.barraSuperior

@Composable
fun filmEditScreen(navController: NavHostController, filmIndex: Int?) {

    //Recuperamos la pelicula
    val film = filmIndex?.let { FilmDataSource.films[it] }

    // Validacion por si el indice es incorrecto
    if (film == null) {
        Scaffold(
            topBar = {
                barraSuperior(navController = navController, esHome = false, atras = true)
            }
        ) {
            Text(
                text = "Película no encontrada",
                modifier = Modifier
                    .padding(16.dp)
            )
        }
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
    val context = LocalContext.current
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
                onValueChange = { titulo = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            TextField(
                value = director,
                onValueChange = { director = it },
                label = { Text("Director") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            TextField(
                value = anyo.toString(),
                onValueChange = { anyo = it.toIntOrNull() ?: anyo },
                label = { Text("Año") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            TextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("URL IMDb") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            GenericSpinnerDropdown(
                items = generoList,
                selectedIndex = genero,
                onItemSelected = { genero = it },
                expanded = expandedGenero,
                onExpandedChange = { expandedGenero = it },
                label = "Género"
            )
            Spacer(Modifier.height(8.dp))
            GenericSpinnerDropdown(
                items = formatoList,
                selectedIndex = formato,
                onItemSelected = { formato = it },
                expanded = expandedFormato,
                onExpandedChange = { expandedFormato = it },
                label = "Formato"
            )
            Spacer(Modifier.height(8.dp))
            TextField(
                value = comentarios,
                onValueChange = { comentarios = it },
                label = { Text("Comentarios") },
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
                        // Guardar cambios directamente en FilmDataSource
                        film.title = titulo
                        film.director = director
                        film.year = anyo
                        film.imdbUrl = url
                        film.comments = comentarios
                        film.genre = genero
                        film.format = formato

                        // Devolver resultado a la pantalla anterior
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("result", Activity.RESULT_OK)

                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f),
                    texto = "Guardar"
                )
                FilledButton(
                    onClick = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("result", Activity.RESULT_CANCELED)
                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f),
                    texto = "Cancelar"
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
    label: String = "Selecciona una opción",
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


@Preview(showBackground = true)
@Composable
fun FilmEditScreenPreview(){
    val navController = rememberNavController()

    filmEditScreen(
        navController = navController,
        filmIndex = 1
        )
}

