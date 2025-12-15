package com.campusdigitalfp.filmoteca.screens

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.campusdigitalfp.filmoteca.R
import com.campusdigitalfp.filmoteca.common.barraSuperior

@Composable
fun filmEditScreen(navController: NavHostController) {
    var titulo by remember { mutableStateOf("") }
    var director by remember { mutableStateOf("") }
    var anyo by remember { mutableIntStateOf(1997) }
    var url by remember { mutableStateOf("") }
    //var imagen by remember { mutableIntStateOf("") }
    var comentarios by remember { mutableStateOf("") }

    var expandedGenero by remember { mutableStateOf(false) }
    var expandedFormato by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val generoList = context.resources.getStringArray(R.array.genero_list).toList()
    val formatoList = context.resources.getStringArray(R.array.formato_list).toList()


    var genero by remember { mutableIntStateOf(0) }
    var formato by remember { mutableIntStateOf(1) }


    Scaffold(
        topBar = {
            barraSuperior(
                navController = navController,
                atras = true,
                onBackResult = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("result", Activity.RESULT_CANCELED)
                }
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
                ),
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            Spacer(Modifier.padding(5.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)

            ){
                Image(
                    painter = painterResource(R.drawable.film),
                    contentDescription = "Imagen de la película",
                    modifier = Modifier
                        .size(70.dp)
                        .weight(1f)
                )
                FilledButton(
                    onClick = {

                    },
                    texto = "Capturar fotografía",
                    modifier = Modifier.weight(2f)
                )
                FilledButton(
                    onClick = {

                    },
                    texto = "Seleccionar imagen",
                    modifier = Modifier.weight(2f)
                )
            }
            Spacer(Modifier.padding(5.dp))
            TextField(
                value = titulo,
                onValueChange = {newTitulo -> titulo = newTitulo},
                label = {Text("Título")},
                placeholder = {Text("Título de la película")},
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences
                )
            )
            Spacer(Modifier.padding(5.dp))
            TextField(
                value = director,
                onValueChange = {newText -> director = newText},
                label = {Text("Director")},
                placeholder = {Text("Director de la película")},
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences
                )
            )
            Spacer(Modifier.padding(5.dp))
            TextField(
                value = anyo.toString(),
                onValueChange = { newText ->
                    val number = newText.toIntOrNull()
                    if(number != null){
                        anyo = number
                    }
                },
                label = { Text("Año") },
                placeholder = { Text("Ej: 1997") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.padding(5.dp))
            TextField(
                value = url,
                onValueChange = { newText -> url = newText
                },
                label = { Text("Url a IMDB") },
                placeholder = { Text("Enlace a IMDB") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences
                    )
            )
            Spacer(Modifier.padding(5.dp))
            GenericSpinnerDropdown(
                items = generoList,
                selectedIndex = genero,
                onItemSelected = { genero = it},
                expanded = expandedGenero,
                onExpandedChange = {expandedGenero = it},
                label = "Seleccione el género"
            )
            Spacer(Modifier.padding(5.dp))
            GenericSpinnerDropdown(
                items = formatoList,
                selectedIndex = formato,
                onItemSelected = { formato = it},
                expanded = expandedFormato,
                onExpandedChange = {expandedFormato = it},
                label = "Seleccione el formato"
            )
            Spacer(Modifier.padding(5.dp))
            TextField(
                value = comentarios,
                onValueChange = { newText -> comentarios = newText
                },
                label = { Text("Comentarios") },
                placeholder = { Text("Comentarios") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences
                )
            )
            Spacer(Modifier.padding(5.dp))
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ){
                FilledButton(
                    onClick = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("editResult", "RESULT_OK")
                        navController.popBackStack()
                    },
                    texto = "Guardar",
                    modifier = Modifier
                        .weight(1f)
                )
                FilledButton(
                    onClick = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("editResult", "RESULT_CANCELLED")
                        navController.popBackStack()
                    },
                    texto = "Cancelar",
                    modifier = Modifier
                        .weight(1f)
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
        navController = navController
        )
}

