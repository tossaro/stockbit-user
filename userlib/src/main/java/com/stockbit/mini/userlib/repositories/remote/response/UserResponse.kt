package com.stockbit.mini.userlib.repositories.remote.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("token") var token: String,
    @SerializedName("user_id") var userId: String,
    @SerializedName("email") var email: String,
    @SerializedName("name") var name: String
)