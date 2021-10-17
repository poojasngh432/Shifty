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
import com.nomimon.shifty.model.MyAvailableShiftsResponse
import org.json.JSONException
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

        updateNotification()

//        if (idsArrayList != null) {
//            try {
//                callConfirmShiftApi(idsArrayList)
////                if (response.contains("CONFIRM")) {
////                    Log.d("TESTING","(MyWorkManager) : doWork() CONFIRM RESPONSE from callConfirmShiftApi(idsArrayList)")
////                    return Result.success()
////                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                return Result.failure()
//            }
//        }

        return Result.success()
    }

    fun updateNotification() {

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
                                Thread.sleep(1000)
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

//    private fun callConfirmShiftApi(idsArrayList: ArrayList<String>) {
//        for ((index, id) in idsArrayList.withIndex()) {
//            Thread.sleep(500)
//            Log.d("TESTING","(MyWorkManager) : for loop and calling API id - $id and index - $index")
//            callApiForId(id)
//            Thread.sleep(1000)
//        }
//    }
//
//    private fun callApiForId(id: String) {
//        Log.d("TESTING","(MyWorkManager) : callApiForId() for id - $id")
//
//        try {
//            callGrabShifts = BaseApp.apiInterface.confirmGrabAllShifts(BaseApp.userId, id)
//            callGrabShifts.enqueue(object : Callback<ConfirmStatus> {
//                override fun onResponse(call: Call<ConfirmStatus>, response: Response<ConfirmStatus>) {
//                    if (response.body() != null) {
//                        val shiftGrabStatus = response.body()!!.status
//                        if (shiftGrabStatus != null) {
//                            if (shiftGrabStatus.contains("CONFIRM")) {
//                                Log.d("TESTING","(MyWorkManager) : shiftGrabStatus - ${shiftGrabStatus} for id - $id")
//                                Log.d("TESTING","(MyWorkManager) : noOfShiftsGrabbed - ${++BaseApp.noOfShiftsGrabbed} for id - $id")
//                                Thread.sleep(400)
//                                sendNotification(applicationContext)
//                                return
//                            } else {
//                                Log.d("TESTING","(MyWorkManager) : shiftGrabStatus - ${shiftGrabStatus} for id - $id")
//                                return
//                            }
//                        } else {
//                            Log.d("TESTING","(MyWorkManager) : shiftGrabStatus is NULL - ${shiftGrabStatus} for id - $id")
//                            Thread.sleep(400)
//                            return
//                        }
//                    }
//                }
//
//                override fun onFailure(call: Call<ConfirmStatus>, t: Throwable) {
//                    Log.d("TESTING","(MyWorkManager) : call failure")
//                    return
//                }
//            })
//            Thread.sleep(1000)
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//    }

}