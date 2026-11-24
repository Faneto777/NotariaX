package com.example.notariax.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BackendRetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val service: BackendApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendApiService::class.java)
    }
}