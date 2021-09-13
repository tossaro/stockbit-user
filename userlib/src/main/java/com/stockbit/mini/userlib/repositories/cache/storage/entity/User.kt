package com.stockbit.mini.userlib.repositories.cache.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val token: String
)