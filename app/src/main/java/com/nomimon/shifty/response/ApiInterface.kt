package com.nomimon.shifty.response

import com.nomimon.shifty.model.ConfirmStatus
import com.nomimon.shifty.model.LoginResponse
import com.nomimon.shifty.model.MyAvailableShiftsResponse
import io.reactivex.Single
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    companion object {
        const val BASE_URL = "https://api-courier-produk.skipthedishes.com/"
    }

    @POST("v2/couriers/{userId}/shifts/{encode}/confirm")
    fun confirmGrabAllShifts(
        @Path("userId") userId: String,
        @Path("encode") encode: String
    ): Call<ConfirmStatus>

    @GET("v2/couriers/{userId}/shifts/scheduled?includeAvailable=true&timezone=Europe/London&hasCourierRefreshedOpenShifts=true")
    fun startRun(@Path("userId") userId: String
    ): Call<MyAvailableShiftsResponse>

    @POST("v1/couriers/login")
    fun login(@Body requestBody: RequestBody
    ): Call<LoginResponse>

    @GET("https://run.mocky.io/v3/b755dcbc-8fca-410f-9173-1c6fb3a890c4")
    fun startMockRun(): Call<MyAvailableShiftsResponse>

    @GET("https://run.mocky.io/v3/f70d0235-ee7b-4c8c-b91a-76f4fa6168ae")
    fun confirmMockGrabAllShifts(
    ): Call<ConfirmStatus>

}