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
    @SerializedName("DISPLAY") var display: DISPLAY?,
)

data class DISPLAY(
    @SerializedName("USD") val USD: USD
)

data class USD(
    @SerializedName("FROMSYMBOL") val FROMSYMBOL: String,
    @SerializedName("TOSYMBOL") val TOSYMBOL: String,
    @SerializedName("MARKET") val MARKET: String,
    @SerializedName("TYPE") val TYPE: String,
    @SerializedName("FLAGS") val FLAGS: Double,
    @SerializedName("PRICE") val PRICE: String,
    @SerializedName("LASTUPDATE") val LASTUPDATE: String,
    @SerializedName("LASTVOLUME") val LASTVOLUME: String,
    @SerializedName("LASTVOLUMETO") val LASTVOLUMETO: String,
    @SerializedName("LASTTRADEID") val LASTTRADEID: String,
    @SerializedName("VOLUMEDAY") val VOLUMEDAY: String,
    @SerializedName("VOLUMEDAYTO") val VOLUMEDAYTO: String,
    @SerializedName("VOLUME24HOUR") val VOLUME24HOUR: String,
    @SerializedName("VOLUME24HOURTO") val VOLUME24HOURTO: String,
    @SerializedName("VOLUMEHOUR") val VOLUMEHOUR: String,
    @SerializedName("VOLUMEHOURTO") val VOLUMEHOURTO: String,
    @SerializedName("MKTCAPPENALTY") val MKTCAPPENALTY: String,
    @SerializedName("CHANGEPCTHOUR") val CHANGEPCTHOUR: String
)