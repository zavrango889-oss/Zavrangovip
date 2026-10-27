package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_profiles")
data class GameProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileName: String,
    val playstyleTag: String,
    val general: Int,
    val redDot: Int,
    val scope2x: Int,
    val scope4x: Int,
    val scopeSniper: Int,
    val freeLook: Int,
    val dpi: Int = 411,
    val fireButtonSize: Int = 54,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    init {
        // Enforce strict 0..200 boundary on Free Fire MAX sensitivities
        require(general in 0..200) { "General sensitivity must be 0..200" }
        require(redDot in 0..200) { "RedDot sensitivity must be 0..200" }
        require(scope2x in 0..200) { "2x Scope sensitivity must be 0..200" }
        require(scope4x in 0..200) { "4x Scope sensitivity must be 0..200" }
        require(scopeSniper in 0..200) { "Sniper Scope sensitivity must be 0..200" }
        require(freeLook in 0..200) { "Free Look sensitivity must be 0..200" }
        require(fireButtonSize in 10..100) { "Fire button size must be 10..100" }
    }
}
