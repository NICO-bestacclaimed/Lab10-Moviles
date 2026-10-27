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
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lab10.data.CombiApiService
import com.example.lab10.data.CombiModel
import kotlinx.coroutines.delay

@Composable
fun ContenidoCombisListado(navController: NavHostController, servicio: CombiApiService) {
    val listaCombis: SnapshotStateList<CombiModel> = remember { mutableStateListOf() }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            val listado = servicio.selectCombis()
            listaCombis.clear()
            listado.forEach { listaCombis.add(it) }
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Error al conectar con el servidor: ${e.message}"
            Log.e("COMBIS-LISTADO", errorMessage ?: "", e)
        } finally {
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        if (isLoading) {
            Text(text = "Cargando combis...", fontSize = 16.sp, modifier = Modifier.padding(16.dp))
        } else if (errorMessage != null) {
            Text(text = errorMessage ?: "", color = Color.Red, modifier = Modifier.padding(16.dp))
            Button(onClick = {
                navController.navigate("combis") { popUpTo("combis") { inclusive = true } }
            }) {
                Text("Reintentar")
            }
        } else if (listaCombis.isEmpty()) {
            Text(text = "No hay combis registradas.", fontSize = 16.sp, modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Row(
                        modifier = Modifier.fillParentMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "PLACA", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.3f))
                        Text(text = "EMPRESA / RUTA", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f))
                        Text(text = "Acción", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.2f))
                    }
                }
                items(listaCombis) { item ->
                    Row(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .fillParentMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(0.3f)) {
                            Text(text = item.placa, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Cap: ${item.capacidad} | ${if (item.activo) "Activo" else "Inactivo"}", fontSize = 12.sp)
                        }
                        Column(modifier = Modifier.weight(0.5f)) {
                            Text(text = item.empresa, fontSize = 14.sp)
                            Text(text = item.ruta, fontSize = 12.sp)
                        }
                        IconButton(
                            onClick = {
                                navController.navigate("combiVer/${item.id}")
                            },
                            modifier = Modifier.weight(0.1f)
                        ) {
                            Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Editar")
                        }
                        IconButton(
                            onClick = {
                                navController.navigate("combiDel/${item.id}")
                            },
                            modifier = Modifier.weight(0.1f)
                        ) {
                            Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Eliminar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContenidoCombiEditar(navController: NavHostController, servicio: CombiApiService, pid: Int = 0) {
    var id by remember { mutableStateOf(pid) }
    var placa by remember { mutableStateOf("") }
    var empresa by remember { mutableStateOf("") }
    var ruta by remember { mutableStateOf("") }
    var capacidad by remember { mutableStateOf("") }
    var activo by remember { mutableStateOf(true) }
    var grabar by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    if (id != 0) {
        LaunchedEffect(id) {
            try {
                val res = servicio.selectCombi(id.toString())
                if (res.isSuccessful && res.body() != null) {
                    val c = res.body()!!
                    placa = c.placa
                    empresa = c.empresa
                    ruta = c.ruta
                    capacidad = c.capacidad.toString()
                    activo = c.activo
                }
            } catch (e: Exception) {
                Log.e("COMBIS-EDITAR", "Error al cargar combi ID=$id", e)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        if (id != 0) {
            TextField(
                value = id.toString(),
                onValueChange = {},
                label = { Text("ID (solo lectura)") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
        }
        TextField(
            value = placa,
            onValueChange = { placa = it },
            label = { Text("Placa (Ej: ABC-123)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = empresa,
            onValueChange = { empresa = it },
            label = { Text("Empresa") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = ruta,
            onValueChange = { ruta = it },
            label = { Text("Ruta") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = capacidad,
            onValueChange = { capacidad = it },
            label = { Text("Capacidad") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = activo, onCheckedChange = { activo = it })
            Text("Activo")
        }
        if (errorMsg != null) {
            Spacer(Modifier.height(8.dp))
            Text(text = errorMsg ?: "", color = Color.Red)
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                if (!isSaving) grabar = true
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isSaving) "Guardando..." else "Grabar", fontSize = 16.sp)
        }
        Spacer(Modifier.height(32.dp))
    }

    if (grabar) {
        val capInt = capacidad.toIntOrNull() ?: 0
        val combi = CombiModel(
            id = if (id == 0) null else id,
            placa = placa.trim().uppercase(),
            empresa = empresa.trim(),
            ruta = ruta.trim(),
            capacidad = capInt,
            activo = activo
        )
        LaunchedEffect(grabar) {
            try {
                isSaving = true
                if (id == 0) {
                    val res = servicio.insertCombi(combi)
                    if (!res.isSuccessful) {
                        errorMsg = "Error al crear: ${res.errorBody()?.string() ?: res.code()}"
                        isSaving = false
                        grabar = false
                        return@LaunchedEffect
                    }
                } else {
                    val res = servicio.updateCombi(id.toString(), combi)
                    if (!res.isSuccessful) {
                        errorMsg = "Error al actualizar: ${res.errorBody()?.string() ?: res.code()}"
                        isSaving = false
                        grabar = false
                        return@LaunchedEffect
                    }
                }
                grabar = false
                navController.navigate("combis") {
                    popUpTo("combis") { inclusive = true }
                }
            } catch (e: Exception) {
                errorMsg = "Excepción: ${e.message}"
                Log.e("COMBIS-GRABAR", errorMsg ?: "", e)
                isSaving = false
                grabar = false
            }
        }
    }
}

@Composable
fun ContenidoCombiEliminar(navController: NavHostController, servicio: CombiApiService, id: Int) {
    var showDialog by remember { mutableStateOf(true) }
    var borrar by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Está seguro de eliminar la Combi con ID $id?") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    borrar = true
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDialog = false
                    navController.navigate("combis")
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (borrar) {
        LaunchedEffect(borrar) {
            try {
                servicio.deleteCombi(id.toString())
            } catch (e: Exception) {
                Log.e("COMBIS-BORRAR", "Error al eliminar", e)
            } finally {
                borrar = false
                navController.navigate("combis") {
                    popUpTo("combis") { inclusive = true }
                }
            }
        }
    }
}
