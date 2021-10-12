package com.nomimon.shifty

import android.app.Application
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.nomimon.shifty.response.ApiInterface
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BaseApp : Application() {

    companion object {
        var email: String = ""
        var password: String = ""
        var isLoggedIn: Boolean = false
        var userId: String = ""
        var userName: String = ""
        var userToken: String = ""
        val PREF_NAME = "SHIFTY_LOGIN_PREFS"
        val appVersion = BuildConfig.VERSION_NAME
        lateinit var apiInterfaceLogin: ApiInterface
        lateinit var apiInterface: ApiInterface
        const val DEBUG = true
        const val REQUEST_TIMEOUT_DURATION = 10
        lateinit var deviceName: String
        lateinit var okHttpClient: OkHttpClient.Builder
        var noOfShiftsGrabbed = 0
    }

    override fun onCreate() {
        super.onCreate()
        fetchPrefs()
        initData()
    }

    private fun fetchPrefs() {
        val sharedPref = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        email = sharedPref.getString("EMAIL_PREF", "").toString()
        password = sharedPref.getString("PASSWORD_PREF", "").toString()
        isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        userToken = sharedPref.getString("TOKEN_PREF", "").toString()
        userName = sharedPref.getString("NAME_PREF", "").toString()
        userId = sharedPref.getString("ID_PREF", "").toString()
    }

    private fun initData() {
        deviceName = Build.MANUFACTURER + " " + Build.MODEL
        if (deviceName.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                deviceName = Settings.Global.getString(this.contentResolver,"device_name")
            }
        }

        if (!isLoggedIn) {
            //LOGIN PAGE
            // Define the interceptor, add authentication headers
            val interceptor1 = Interceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Content-Type", "text/plain")
                    .addHeader("Content-Length", Constants.CONTENT_LENGTH)
                    .addHeader("Accept", Constants.ACCEPT)
                    .addHeader("app-token", Constants.APP_TOKEN)
                    .addHeader("app-version", appVersion)
                    .addHeader("app-build", Constants.APP_BUILD)
                    .addHeader("model", deviceName)
                    .build()
                chain.proceed(newRequest)
            }

            okHttpClient = OkHttpClient.Builder()
            okHttpClient.interceptors().add(interceptor1)
            val client = okHttpClient.build()

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(ApiInterface.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            apiInterfaceLogin = retrofit.create(ApiInterface::class.java)
        }

            //MAIN PAGE
            // Define the interceptor, add authentication headers
            val interceptor = Interceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("app-token", Constants.APP_TOKEN)
                    .addHeader("Cache-Control", "no-cache")
                    .addHeader("user-token", userToken)
                    .addHeader("app-version", appVersion)
                    .addHeader("device-id", "A49C2BC0-77EC-4626-98A0-8B141D2F3BCC")
                    .addHeader("platform-version", "10")
                    .addHeader("platform", "Android")
                    .addHeader("Content-Length", Constants.CONTENT_LENGTH)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Server", "Jetty(9.4.38.v20210224)")
                    .addHeader("Accept", Constants.ACCEPT)
                    .addHeader("X-CDN", "Imperva")
                    .addHeader("User-Agent", "SkipTheDishes-COURAPP-SkipTheDishes / (Android - 4.11.0)")
                    .addHeader("app-build", Constants.APP_BUILD)
                    .addHeader("model", BaseApp.deviceName)
                    .build()
                chain.proceed(newRequest)
            }
            okHttpClient = OkHttpClient.Builder()
            okHttpClient.interceptors().add(interceptor)
            val client = okHttpClient.build()

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(ApiInterface.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            apiInterface = retrofit.create(ApiInterface::class.java)
    }

}