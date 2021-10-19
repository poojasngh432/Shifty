package com.nomimon.shifty

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.nomimon.shifty.model.ConfirmStatus
import com.nomimon.shifty.model.MyAvailableShiftsResponse
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import java.util.*

class MyService : Service() {
    var runnable: Runnable? = null
    var handler = Handler()

    /* access modifiers changed from: private */
    fun startGrabbingShifts() {

        val checkShiftsApiCall = BaseApp.apiInterface.startRun(BaseApp.userId)
        checkShiftsApiCall.enqueue(object : retrofit2.Callback<MyAvailableShiftsResponse> {

            override fun onResponse(call: Call<MyAvailableShiftsResponse>, response: retrofit2.Response<MyAvailableShiftsResponse>) {
                if (response.body() != null && BaseApp.isLoggedIn) {
                    val availableShifts = response.body()!!.availableShifts
                    if (availableShifts != null && availableShifts.isNotEmpty()) {
                        Log.d("TESTING", "(MainActivity) : checkForAvailableShifts() - availableShifts size is : ${availableShifts.size}")
                        val noOfShifts = availableShifts.size
                        if (noOfShifts > 0) {
                            Log.d("TESTING", "(MainActivity) : storeAvailableIdsList() - no of shifts : $noOfShifts")
                            for ((index, shift) in availableShifts.withIndex()) {
                                MainActivity.availableShiftsIdList.add(shift.id)
                                Log.d("TESTING", "(MainActivity) : id : ${shift.id}")
                                try {
                                    val id = shift.id
                                    val callGrabShifts = BaseApp.apiInterface.confirmGrabAllShifts(BaseApp.userId, id)
                                    callGrabShifts.enqueue(object : retrofit2.Callback<ConfirmStatus> {
                                        override fun onResponse(call: Call<ConfirmStatus>, response: retrofit2.Response<ConfirmStatus>) {
                                            if (response.body() != null) {
                                                val shiftGrabStatus = response.body()!!.status
                                                if (shiftGrabStatus != null) {
                                                    if (shiftGrabStatus.contains("CONFIRM")) {
                                                        Log.d("TESTING", "(MyWorkManager) : shiftGrabStatus - ${shiftGrabStatus} for id - $id")
                                                        Log.d("TESTING", "(MyWorkManager) : noOfShiftsGrabbed - ${++BaseApp.noOfShiftsGrabbed} for id - $id")
                                                        MainActivity.sendNotification(this@MyService)
                                                        return
                                                    } else {
                                                        Log.d("TESTING", "(MyWorkManager) : shiftGrabStatus - ${shiftGrabStatus} for id - $id")
                                                        return
                                                    }
                                                } else {
                                                    Log.d("TESTING", "(MyWorkManager) : shiftGrabStatus is NULL - ${shiftGrabStatus} for id - $id")
                                                    return
                                                }
                                            }
                                        }

                                        override fun onFailure(call: Call<ConfirmStatus>, t: Throwable) {
                                            Log.d("TESTING", "(MyWorkManager) : call failure")
                                            return
                                        }
                                    })
                                } catch (e3: JSONException) {
                                    e3.printStackTrace()
                                }
                                val delay = if(MainActivity.DELAY3 >= 100) MainActivity.DELAY3 else 0
                                Thread.sleep(delay.toLong())
                            }
                        }
                    }
                }

            }

            override fun onFailure(call: Call<MyAvailableShiftsResponse>, t: Throwable) {
                Log.d("TESTING", "(MainActivity) : checkForAvailableShifts() Failure")
            }
        })
    }

    override fun onStartCommand(intent: Intent, i: Int, i2: Int): Int {
        if (intent.action?.contains("START")!!) {
            startGrabbingShifts()

            val r1 = object : Runnable {
                override fun run() {
                    startGrabbingShifts()
                    val delay = if(MainActivity.DELAY2 >= 100) MainActivity.DELAY2 else 0
                    handler.postDelayed(this, delay.toLong())
                }
            }
            runnable = r1
            handler.post(r1)
        } else {
            runnable?.let { handler.removeCallbacks(it) }
            stopSelf()
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}