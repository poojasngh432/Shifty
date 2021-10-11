package com.nomimon.shifty

import android.app.*
import android.content.Intent
import android.os.IBinder

class myService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
//    var counter = 0
//
//    /* access modifiers changed from: private */ /* renamed from: h */
//    var f141h: Handler? = null
//
//    /* renamed from: m */
//    var f142m: User? = null
//    var myModel: String? = null
//    var name: String? = null
//
//    /* renamed from: r */
//    private var f143r: Runnable? = null
//    var shift_value: String? = null
//    var stdVersion: String? = null
//    var token: String? = null
//    var userID: String? = null
//    fun onBind(intent: Intent?): IBinder? {
//        return null
//    }
//
//    /* access modifiers changed from: private */
//    fun updateNotification(): Notification {
//        val builder: NotificationCompat.Builder
//        val okHttpClient = OkHttpClient()
//        okHttpClient.newCall(Builder()
//            .url("https://api-courier-produk.skipthedishes.com/v2/couriers/" + userID + "/shifts/scheduled?includeAvailable=true&timezone=Europe/London&hasCourierRefreshedOpenShifts=true%00")
//            .header("app-token", "31983a5d-37b1-4390-bd1c-8184e855e5da")
//            .header("Accept", "application/json")
//            .header("Cache-Control", "no-cache")
//            .header("user-token", token)
//            .header("app-version", stdVersion)
//            .header("device-id", "A49C2BC0-77EC-4626-98A0-8B141D2F3BCC")
//            .header("platform-version", Build.VERSION.SDK_INT.toString())
//            .header("platform", "Android")
//            .header("Content-Length", "0")
//            .header(HttpConnection.CONTENT_TYPE, "application/json;charset=utf-8")
//            .header("Server", "Jetty(9.4.38.v20210224)")
//            .header("X-CDN", "Imperva")
//            .header("User-Agent", "SkipTheDishes-COURAPP-SkipTheDishes / (Android - 4.11.0)")
//            .header("app-build", "272")
//            .header("model", myModel).build())
//            .enqueue(object : Callback() {
//                fun onFailure(request: Request?, iOException: IOException) {
//                    Log.w("failure Response", iOException.getMessage().toString())
//                }
//
//                @Throws(IOException::class)
//                fun onResponse(response: Response) {
//                    var jSONObject: JSONObject?
//                    var jSONArray: JSONArray? = null
//                    try {
//                        jSONObject = JSONObject(response.body().string())
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                        jSONObject = null
//                    }
//                    if (jSONObject!!.has("error")) {
//                        Log.d("My Response", "ERROR")
//                        return
//                    }
//                    try {
//                        jSONArray = jSONObject.getJSONArray("availableShifts")
//                    } catch (e2: JSONException) {
//                        e2.printStackTrace()
//                    }
//                    if (jSONArray!!.length() == 0) {
//                        Log.d("Available Shifts", "No 0")
//                        return
//                    }
//                    val length = jSONArray.length()
//                    for (i in 0 until length) {
//                        try {
//                            shift_value = jSONArray.getJSONObject(i).getString("id")
//                            OkHttpClient().newCall(Builder().url(URL("https://api-courier-produk.skipthedishes.com/v2/couriers/" + userID + "/shifts/" + URLEncoder.encode(shift_value) + "/confirm"))
//                                .post(RequestBody.create(MediaType.parse("application/json"), JSONObject().toString()))
//                                .header("app-token", "31983a5d-37b1-4390-bd1c-8184e855e5da")
//                                .header("app-version", stdVersion)
//                                .header("device-id", "A49C2BC0-77EC-4626-98A0-8B141D2F3BCC")
//                                .header("platform-version", Build.VERSION.SDK_INT.toString())
//                                .header("platform", "Android").header("user-token", token)
//                                .header("Content-Length", "0").header("app-build", "272")
//                                .header("model", myModel).build()).enqueue(object : Callback() {
//                                fun onFailure(request: Request?, iOException: IOException) {
//                                    Log.d("Not Working", iOException.getMessage().toString())
//                                }
//
//                                @Throws(IOException::class)
//                                fun onResponse(response: Response?) {
//                                    Log.d("Response", "Shift Grabbed")
//                                    counter++
//                                }
//                            })
//                        } catch (e3: JSONException) {
//                            e3.printStackTrace()
//                        }
//                    }
//                }
//            })
//        val sb = StringBuilder()
//        sb.append(counter)
//        sb.append("")
//        val sb2 = sb.toString()
//        val applicationContext: Context = ApplicationProvider.getApplicationContext()
//        val activity = PendingIntent.getActivity(applicationContext, 0, Intent(applicationContext, MainActivity::class.java), 268435456)
//        val notificationManager = getSystemService("notification") as NotificationManager?
//        if (Build.VERSION.SDK_INT >= 26) {
//            val notificationChannel = NotificationChannel("alex_channel", "Shift Grabber", 1)
//            notificationChannel.description = "Skip Open Shifts Only"
//            notificationManager!!.createNotificationChannel(notificationChannel)
//            builder = NotificationCompat.Builder(this as Context, "alex_channel")
//        } else {
//            builder = NotificationCompat.Builder(applicationContext)
//        }
//        val ticker = builder.setContentIntent(activity).setContentTitle("Open Shift Grabber")
//            .setTicker("info")
//        return ticker.setContentText("$sb2 Shifts Grabbed").setContentIntent(activity)
//            .setOngoing(true).build()
//    }
//
//    fun onStartCommand(intent: Intent, i: Int, i2: Int): Int {
//        val user: User = ApplicationProvider.getApplicationContext<Context>() as User
//        f142m = user
//        name = user.getName()
//        token = f142m.getToken()
//        userID = f142m.getUserID()
//        stdVersion = f142m.getUserID()
//        myModel = f142m.getMyModel()
//        if (intent.action!!.contains("start")) {
//            startForeground(101, updateNotification())
//            f141h = Handler()
//            val r1: C10032 = object : Runnable {
//                override fun run() {
//                    val myservice = this@myService
//                    myservice.startForeground(101, myservice.updateNotification())
//                    f141h.postDelayed(this, 800)
//                }
//            }
//            f143r = r1
//            f141h.post(r1)
//        } else {
//            f141h.removeCallbacks(f143r)
//            stopForeground(true)
//            stopSelf()
//        }
//        return 1
//    }
}
