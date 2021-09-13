package com.stockbit.mini.stocklib.repositories.cache.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Stock(
    @PrimaryKey
    @SerializedName("Id") val id: String,
    @SerializedName("Name") val name: String,
    @SerializedName("FullName") val fullname: String,
    var price: String,
    var status: String
)