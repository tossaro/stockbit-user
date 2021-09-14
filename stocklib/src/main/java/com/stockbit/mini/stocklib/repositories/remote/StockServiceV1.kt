package com.stockbit.mini.stocklib.repositories.remote

import com.haroldadmin.cnradapter.NetworkResponse
import com.stockbit.mini.stocklib.repositories.remote.response.ErrorResponse
import com.stockbit.mini.stocklib.repositories.remote.response.StockResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface StockServiceV1 {
    @GET("data/top/totaltoptiervolfull")
    suspend fun stocks(
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("tsym") tsym: String
    ): NetworkResponse<StockResponse, ErrorResponse>
}