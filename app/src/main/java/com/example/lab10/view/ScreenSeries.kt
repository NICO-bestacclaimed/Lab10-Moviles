package com.example.lab10.view

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lab10.data.SerieApiService
import com.example.lab10.data.SerieModel
import kotlinx.coroutines.delay

@Composable
fun ContenidoSeriesListado(navController: NavHostController, servicio: SerieApiService) {
    val listaSeries: SnapshotStateList<SerieModel> = remember { mutableStateListOf() }
    LaunchedEffect(Unit) {
        try {
            val listado = servicio.selectSeries()
            listaSeries.clear()
            listado.forEach { listaSeries.add(it) }
        } catch (e: Exception) {
            Log.e("SERIES-LISTADO", "Error al obtener series: ${e.message}", e)
        }
    }
    LazyColumn {
        item {
            Row(
                modifier = Modifier.fillParentMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.1f)
                )
                Text(
                    text = "SERIE",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.7f)
                )
                Text(
                    text = "Accion",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.2f)
                )
            }
        }
        items(listaSeries) { item ->
            Row(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .fillParentMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.id}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.1f)
                )
                Text(
                    text = item.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.6f)
                )
                IconButton(
                    onClick = {
                        navController.navigate("serieVer/${item.id}")
                        Log.e("SERIE-VER", "ID = ${item.id}")
                    },
                    modifier = Modifier.weight(0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Ver"
                    )
                }
                IconButton(
                    onClick = {
                        navController.navigate("serieDel/${item.id}")
                        Log.e("SERIE-DEL", "ID = ${item.id}")
                    },
                    modifier = Modifier.weight(0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Ver"
                    )
                }
            }
        }
    }
}

@Composable
fun ContenidoSerieEditar(navController: NavHostController, servicio: SerieApiService, pid: Int = 0) {
    var id by remember { mutableStateOf(pid) }
    var name by remember { mutableStateOf<String?>("") }
    var release_date by remember { mutableStateOf<String?>("") }
    var rating by remember { mutableStateOf<String?>("") }
    var category by remember { mutableStateOf<String?>("") }
    var grabar by remember { mutableStateOf(false) }

    if (id != 0) {
        LaunchedEffect(id) {
            try {
                val objSerie = servicio.selectSerie(id.toString())
                delay(100)
                if (objSerie.isSuccessful && objSerie.body() != null) {
                    name = objSerie.body()?.name
                    release_date = objSerie.body()?.release_date
                    rating = objSerie.body()?.rating?.toString()
                    category = objSerie.body()?.category
                }
            } catch (e: Exception) {
                Log.e("SERIE-EDITAR", "Error al cargar serie ID=$id: ${e.message}", e)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        TextField(
            value = id.toString(),
            onValueChange = { },
            label = { Text("ID (solo lectura)") },
            readOnly = true,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = name ?: "",
            onValueChange = { name = it },
            label = { Text("Name: ") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = release_date ?: "",
            onValueChange = { release_date = it },
            label = { Text("Release Date:") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = rating ?: "",
            onValueChange = { rating = it },
            label = { Text("Rating:") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = category ?: "",
            onValueChange = { category = it },
            label = { Text("Category:") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                grabar = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Grabar", fontSize = 16.sp)
        }
        Spacer(Modifier.height(32.dp))
    }

    if (grabar) {
        val objSerie = SerieModel(id, name ?: "", release_date ?: "", rating?.toIntOrNull() ?: 0, category ?: "")
        LaunchedEffect(grabar) {
            try {
                if (id == 0) {
                    val res = servicio.insertSerie(objSerie)
                    Log.d("SERIE-GRABAR", "Insert response: code=${res.code()} body=${res.body()}")
                } else {
                    val res = servicio.updateSerie(id.toString(), objSerie)
                    Log.d("SERIE-GRABAR", "Update response: code=${res.code()} body=${res.body()}")
                }
            } catch (e: Exception) {
                Log.e("SERIE-GRABAR", "Error al guardar serie: ${e.message}", e)
            } finally {
                grabar = false
                navController.navigate("series") {
                    popUpTo("series") { inclusive = true }
                }
            }
        }
    }
}

@Composable
fun ContenidoSerieEliminar(navController: NavHostController, servicio: SerieApiService, id: Int) {
    var showDialog by remember { mutableStateOf(true) }
    var borrar by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Confirmar Eliminación") },
            text = { Text("¿Está seguro de eliminar la Serie?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        borrar = true
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        navController.navigate("series")
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (borrar) {
        LaunchedEffect(borrar) {
            try {
                val res = servicio.deleteSerie(id.toString())
                Log.d("SERIE-BORRAR", "Delete response: code=${res.code()} body=${res.body()}")
            } catch (e: Exception) {
                Log.e("SERIE-BORRAR", "Error al eliminar serie: ${e.message}", e)
            } finally {
                borrar = false
                navController.navigate("series") {
                    popUpTo("series") { inclusive = true }
                }
            }
        }
    }
}
