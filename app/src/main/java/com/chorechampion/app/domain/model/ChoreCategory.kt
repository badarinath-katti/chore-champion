package com.chorechampion.app.domain.model

data class ChoreCategory(
    val id: String,
    val name: String,
    val icon: String,
    val colorHex: String,
    val isDefault: Boolean = false
)
