package com.nomimon.shifty.response

import com.nomimon.shifty.model.LoginResponse
import com.nomimon.shifty.model.MyAvailableShiftsResponse
import io.reactivex.Single
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    companion object{
        const val BASE_URL = "https://api-courier-produk.skipthedishes.com/"
        // "https://api-courier-produk.skipthedishes.com/v2/couriers/a51e1c26-a453-441c-8182-662e9142fe29/shifts/scheduled?includeAvailable=true&timezone=Europe/London&hasCourierRefreshedOpenShifts=true%00
        // https://api-courier-produk.skipthedishes.com/v2/couriers/" + userID + "/shifts/" + URLEncoder.encode(shift_value) + "/confirm
    }

    @GET("v2/couriers/a51e1c26-a453-441c-8182-662e9142fe29/shifts/{shift_value}/confirm")
    fun confirmGrabAllShifts(@Path("shift_value") shiftValue: String
    ): Call<LoginResponse>

    @GET("v2/couriers/{userId}/shifts/scheduled?includeAvailable=true&timezone=Europe/London&hasCourierRefreshedOpenShifts=true")
    fun startRun(@Path("userId") userId: String): Call<MyAvailableShiftsResponse>

    @POST("v1/couriers/login")
    fun login(
        @Body requestBody: RequestBody
    ): Call<LoginResponse>

}