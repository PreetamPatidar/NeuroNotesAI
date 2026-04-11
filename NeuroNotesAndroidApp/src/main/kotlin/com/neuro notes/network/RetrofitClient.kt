package com.neuro notes.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton object to initialize Retrofit with the backend base URL for Android emulator.
 * Base URL: http://10.0.2.2:8000 (points to host's localhost:8000 from emulator)
 */
object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8000/"

    val api: NeuroNotesApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NeuroNotesApi::class.java)
    }
}

