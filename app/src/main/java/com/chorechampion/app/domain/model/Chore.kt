package com.chorechampion.app.domain.model

data class Chore(
    val id: String,
    val title: String,
    val description: String? = null,
    val categoryId: String,
    val defaultWeightage: Int,
    val createdBy: String,
    val isTemplate: Boolean = false,
    val createdAt: Long
)
