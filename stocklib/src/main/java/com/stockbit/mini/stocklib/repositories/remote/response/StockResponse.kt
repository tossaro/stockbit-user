package com.stockbit.mini.stocklib.repositories.remote.response

import com.google.gson.annotations.SerializedName
import com.stockbit.mini.stocklib.repositories.cache.storage.entity.Stock

data class StockResponse(
    @SerializedName("Message") var message: String,
    @SerializedName("Type") var userId: Int,
    @SerializedName("MetaData") var metaData: MetaData,
    @SerializedName("Data") var data: List<Coin>
)

data class MetaData(
    @SerializedName("Count") var count: Int,
)

data class Coin(
    @SerializedName("CoinInfo") var coin_info: Stock,
    @SerializedName("RAW") var raw: RAW?,
)

data class RAW(
    @SerializedName("USD") val USD: USD
)

data class USD(
    @SerializedName("TOPTIERVOLUME24HOUR") val TOPTIERVOLUME24HOUR: Double,
    @SerializedName("CHANGE24HOUR") val CHANGE24HOUR: Double,
    @SerializedName("CHANGEPCTHOUR") val CHANGEPCTHOUR: Double
)