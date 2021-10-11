package com.nomimon.shifty

interface BaseApi {
    val api: String
        get() = "https://api-courier-produk.skipthedishes.com/v1/couriers/login"
}