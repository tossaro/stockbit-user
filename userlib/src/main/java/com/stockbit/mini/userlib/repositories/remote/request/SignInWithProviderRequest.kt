package com.stockbit.mini.userlib.repositories.remote.request

import com.google.gson.annotations.SerializedName
import com.stockbit.mini.corelib.repositories.network.request.DeviceAuthRequest

data class SignInWithProviderRequest(
    @SerializedName("provider") private var provider: String,
    @SerializedName("access_token") private var accessToken: String,
    val uuid: String = "",
    val deviceToken: String = ""
) : DeviceAuthRequest(uuid = uuid, deviceToken = deviceToken)