package com.nomimon.shifty.response

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nomimon.shifty.BaseApp
import com.nomimon.shifty.MainActivity
import com.nomimon.shifty.R
import com.nomimon.shifty.model.ConfirmStatus
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyWorkManager(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {

    private lateinit var callGrabShifts: Call<ConfirmStatus>
    var isLastIndex: Boolean = false

    /**
     * The Result returned from doWork() informs the WorkManager service whether the work succeeded and,
     * in the case of failure, whether or not the work should be retried.
     */
    override fun doWork(): Result {
        val id = inputData.getString("SHIFT_ID") ?: "NULL"
        isLastIndex = inputData.getBoolean("IS_LAST_INDEX", false)
        val response: String?
        try {
                // Do the work here
                response = callConfirmShiftApi(id)
            } catch (e: Exception) {
                e.printStackTrace()
                return Result.failure()
            }

            // Indicate whether the task done successfully
            return if(response.contains("Confirm")) Result.success() else  Result.retry()
    }

    private fun callConfirmShiftApi(id: String): String {
        var shiftGrabStatus: String = ""
        val parse: MediaType? = MediaType.parse("application/json")
        val requestBody = RequestBody.create(parse, JSONObject().toString())
        callGrabShifts = BaseApp.apiInterface.confirmGrabAllShifts(BaseApp.userId, id)

        callGrabShifts.enqueue(object : Callback<ConfirmStatus> {
            override fun onResponse(call: Call<ConfirmStatus>, response: Response<ConfirmStatus>) {
                if (response.body() != null) {
                    shiftGrabStatus = response.body()!!.status
                    if (shiftGrabStatus.contains("CONFIRM")) {
                        Log.d("TESTING"," - $shiftGrabStatus")
                        val shiftNumber = BaseApp.noOfShiftsGrabbed++
                        Log.d("TESTING","shift grabbed number $shiftNumber")
                        sendNotification(applicationContext)
                    }

                    if (isLastIndex) {
                        startRunApi()
                        Log.d("TESTING","isLastIndex $isLastIndex")
                    }
                }
            }

            override fun onFailure(call: Call<ConfirmStatus>, t: Throwable) {
                if (callGrabShifts.isCanceled) {
                  Log.d("TESTING","cancelled")
                } else {
                    Log.d("TESTING","failure")
                }
            }
        })

        return shiftGrabStatus
    }

    private fun sendNotification(context: Context) {
        val mBuilder = NotificationCompat.Builder(context.applicationContext, "notify_001")
        val i = Intent(context.applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, i, 0)
        val bigText = NotificationCompat.BigTextStyle()
        bigText.bigText("Shifty")
        bigText.setBigContentTitle("Success")
        bigText.setSummaryText("Shift grabbed")
        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setSmallIcon(R.drawable.ic_shifty)
        mBuilder.setContentTitle("Looking for more available shifts")
//        mBuilder.setContentText("Your text")
        mBuilder.priority = Notification.PRIORITY_MAX
        mBuilder.setStyle(bigText)
        val mNotificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "Your_channel_id"
            val channel = NotificationChannel(channelId, "Shifty Channel", NotificationManager.IMPORTANCE_HIGH)
            mNotificationManager.createNotificationChannel(channel)
            mBuilder.setChannelId(channelId)
        }
        mNotificationManager.notify(0, mBuilder.build())
    }


    private fun startRunApi() {
//       val callStartRun = BaseApp.apiInterface.startRun(BaseApp.userId)
//        callStartRun.enqueue(object : Callback<MyAvailableShiftsResponse> {
//            override fun onResponse(call: Call<MyAvailableShiftsResponse>, response: Response<MyAvailableShiftsResponse>) {
//                if (response.body() != null) {
//                    val availableShifts = response.body()!!.availableShifts
//                    val noOfShifts = availableShifts?.size ?: 0
//                    Log.d("TESTING", "no of shifts : " + noOfShifts)
//                    val lisOfIds = mutableListOf<String>()
//                    if (availableShifts != null) {
//                        var lastindex = 0
//                        for (shift in availableShifts) {
//                            lastindex++
//                            val isLastIndex: Boolean = lastindex == noOfShifts
//
//                            val id = shift.id
//                            lisOfIds.add(id)
//                            Log.d("TESTING", "id : " + id)
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                                val encodedId = URLEncoder.encode(id, StandardCharsets.UTF_8.toString())
//                                Log.d("TESTING", "encodedId : " + encodedId)
//                            }
//                            callConfirmShiftApi(id)
//                        }
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<MyAvailableShiftsResponse>, t: Throwable) {
//
//            }
//        })
    }
}