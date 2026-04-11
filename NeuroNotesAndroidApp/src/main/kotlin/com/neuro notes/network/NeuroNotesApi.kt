package com.neuro notes.network

import com.neuro notes.data.DashboardStats
import retrofit2.http.GET

/**
 * Retrofit API interface for NeuroNotes AI backend.
 */
interface NeuroNotesApi {
    @GET("api/users/dashboard")
    suspend fun getDashboardStats(): DashboardStats
}

