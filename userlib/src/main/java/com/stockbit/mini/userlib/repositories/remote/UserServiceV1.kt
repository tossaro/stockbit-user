package com.stockbit.mini.userlib.repositories.remote

import com.haroldadmin.cnradapter.NetworkResponse
import com.stockbit.mini.userlib.repositories.remote.request.SignInRequest
import com.stockbit.mini.userlib.repositories.remote.request.SignInWithProviderRequest
import com.stockbit.mini.corelib.repositories.network.response.GenericErrorResponse
import com.stockbit.mini.corelib.repositories.network.response.RetrofitResponse
import com.stockbit.mini.userlib.repositories.remote.response.UserResponse
import com.stockbit.mini.corelib.tools.constants.ContentTypeConstant
import retrofit2.http.*

interface UserServiceV1 {
    @POST("sessions")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun signIn(@Body signInRequest: SignInRequest): NetworkResponse<RetrofitResponse<UserResponse>, GenericErrorResponse>

    @POST("sessions")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun signInWithProvider(@Body signInRequest: SignInWithProviderRequest): NetworkResponse<RetrofitResponse<UserResponse>, GenericErrorResponse>
}