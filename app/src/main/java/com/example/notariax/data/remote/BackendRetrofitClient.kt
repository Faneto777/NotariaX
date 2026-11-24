package com.example.notariax.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BackendRetrofitClient {
    private const val BASE_URL = "https://x8ki-letl-twmt.n7.xano.io/api:7VNgQ7Kr/"

    val service: BackendApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendApiService::class.java)
    }
}
