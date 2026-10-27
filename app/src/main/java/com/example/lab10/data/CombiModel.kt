package com.example.lab10.data

import com.google.gson.annotations.SerializedName

data class CombiModel(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("placa")
    var placa: String,
    @SerializedName("empresa")
    var empresa: String,
    @SerializedName("ruta")
    var ruta: String,
    @SerializedName("capacidad")
    var capacidad: Int,
    @SerializedName("activo")
    var activo: Boolean
)
