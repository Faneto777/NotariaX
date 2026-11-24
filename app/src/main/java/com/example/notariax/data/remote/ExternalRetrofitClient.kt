package com.example.notariax.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ExternalRetrofitClient {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    val service: ExternalApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExternalApiService::class.java)
    }
}
