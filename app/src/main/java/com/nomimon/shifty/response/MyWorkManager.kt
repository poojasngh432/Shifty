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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class MyWorkManager(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {

    private lateinit var callGrabShifts: Call<ConfirmStatus>

    override fun doWork(): Result {
        val idsList = inputData.getStringArray("IDS_LIST")
        val idsArrayList = idsList?.toCollection(ArrayList())
        var response = ""
        if (idsArrayList != null) {
            try {
                response = callConfirmShiftApi(idsArrayList)
                if (response.contains("CONFIRM")) {
                    Log.d("TESTING","(MyWorkManager) : doWork() CONFIRM RESPONSE from callConfirmShiftApi(idsArrayList)")
                    return Result.success()
                } else {
                    Log.d("TESTING","(MyWorkManager) : doWork() response value - $response")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return Result.failure()
            }
        }

        return Result.success()
    }

    private fun callConfirmShiftApi(idsArrayList: ArrayList<String>): String {
        var response = ""
        for ((index, id) in idsArrayList.withIndex()) {
            Log.d("TESTING","(MyWorkManager) : callConfirmShiftApi() calling for id - $id and index - $index")
            response = callApiForId(id)
            Thread.sleep(800)
            if(index == idsArrayList.lastIndex) {
                response = "CONFIRM"
            }
        }
        return response
    }

    private fun callApiForId(id: String): String {
        var shiftGrabStatus = ""
        Log.d("TESTING","(MyWorkManager) : callApiForId() for id - $id")
        callGrabShifts = BaseApp.apiInterface.confirmGrabAllShifts(BaseApp.userId, id)
        callGrabShifts.enqueue(object : Callback<ConfirmStatus> {
            override fun onResponse(call: Call<ConfirmStatus>, response: Response<ConfirmStatus>) {
                if (response.body() != null) {
                    shiftGrabStatus = response.body()!!.status
                    if (shiftGrabStatus != null) {
                        if (shiftGrabStatus.contains("CONFIRM")) {
                            Log.d("TESTING","(MyWorkManager) : shiftGrabStatus - $shiftGrabStatus for id - $id")
                            Log.d("TESTING","(MyWorkManager) : noOfShiftsGrabbed - ${++BaseApp.noOfShiftsGrabbed}")
                            sendNotification(applicationContext)
                            shiftGrabStatus = "CONFIRM"
                            return
                        } else {
                            Log.d("TESTING","(MyWorkManager) : NOT CONFIRM - shiftGrabStatus - $shiftGrabStatus for id - $id")
                            shiftGrabStatus = "NOT CONFIRM"
                            return
                        }
                    } else {
                        Log.d("TESTING","(MyWorkManager) : NULL - shiftGrabStatus - $shiftGrabStatus for id - $id")
                        shiftGrabStatus = "RETRY"
                        return
                    }
                } else {
                    shiftGrabStatus = "NULL"
                    return
                }
            }

            override fun onFailure(call: Call<ConfirmStatus>, t: Throwable) {
                Log.d("TESTING","(MyWorkManager) : call failure")
                shiftGrabStatus = "FAILURE"
                return
            }
        })

        Log.d("TESTING","(MyWorkManager) : callApiForId() for shiftGrabStatus - $shiftGrabStatus")
        return shiftGrabStatus
    }

    private fun sendNotification(context: Context) {
        val mBuilder = NotificationCompat.Builder(context.applicationContext, "notify_001")
        val i = Intent(context.applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, i, 0)
        val bigText = NotificationCompat.BigTextStyle()
        bigText.bigText("Shifts grabbed successfully")
        bigText.setBigContentTitle("Success")
        bigText.setSummaryText("${BaseApp.noOfShiftsGrabbed} Shift grabbed")
        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setSmallIcon(R.drawable.ic_shifty)
//        mBuilder.setContentTitle("Looking for more available shifts")
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
}