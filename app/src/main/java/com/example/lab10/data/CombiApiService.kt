package com.example.lab10.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CombiApiService {
    @GET("combis")
    suspend fun selectCombis(): ArrayList<CombiModel>

    @GET("combis/{id}")
    suspend fun selectCombi(@Path("id") id: String): Response<CombiModel>

    @Headers("Content-Type: application/json")
    @POST("combis")
    suspend fun insertCombi(@Body combi: CombiModel): Response<CombiModel>

    @PUT("combis/{id}")
    suspend fun updateCombi(@Path("id") id: String, @Body combi: CombiModel): Response<CombiModel>

    @DELETE("combis/{id}")
    suspend fun deleteCombi(@Path("id") id: String): Response<Unit>
}
