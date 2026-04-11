package com.neuro notes.data

/**
 * Data class representing the dashboard statistics from the NeuroNotes AI backend.
 */
data class DashboardStats(
    val dayStreak: Int,
    val totalScore: Int,
    val attendanceDays: Int,
    val avgAccuracy: Double
)

