package com.stockbit.mini.userlib.repositories.remote.request

import com.google.gson.annotations.SerializedName
import com.stockbit.mini.corelib.repositories.network.request.DeviceAuthRequest

data class SignInRequest(
    @SerializedName("login_id") private val username: String,
    @SerializedName("login_type") private val loginType: String,
    @SerializedName("password") private val password: String,
    val uuid: String = "",
    val deviceToken: String = ""
) : DeviceAuthRequest(uuid = uuid, deviceToken = deviceToken)