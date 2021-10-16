package com.nomimon.shifty

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import com.nomimon.shifty.databinding.ActivityMainBinding
import com.nomimon.shifty.model.AvailableShift
import com.nomimon.shifty.model.MyAvailableShiftsResponse
import com.nomimon.shifty.response.MyWorkManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        private var isStartButtonClicked = false
        var availableShiftsIdList: ArrayList<String> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setData()
        checkForAvailableShifts()
        setOnClickListeners()
    }

    private fun checkForAvailableShifts() {
        val checkShiftsApiCall = BaseApp.apiInterface.startRun(BaseApp.userId)
        checkShiftsApiCall.enqueue(object : Callback<MyAvailableShiftsResponse> {
            override fun onResponse(call: Call<MyAvailableShiftsResponse>, response: Response<MyAvailableShiftsResponse>) {
                if (response.body() != null && BaseApp.isLoggedIn) {
                    val availableShifts = response.body()!!.availableShifts
                    if (availableShifts != null && availableShifts.isNotEmpty()) {
                        Log.d("TESTING", "(MainActivity) : checkForAvailableShifts() - availableShifts size is : ${availableShifts.size}")
                        storeAvailableIdsList(availableShifts)
                    } else {
                        Log.d("TESTING", "(MainActivity) : checkForAvailableShifts() shifts empty - calling again")
                        checkForAvailableShifts()
                    }
                }
            }

            override fun onFailure(call: Call<MyAvailableShiftsResponse>, t: Throwable) {
                Log.d("TESTING", "(MainActivity) : checkForAvailableShifts() Failure")
                if (isStartButtonClicked) {
                    Log.d("TESTING", "checkForAvailableShifts failure when start is active so calling again")
                    checkForAvailableShifts()
                }
            }
        })
    }

    private fun storeAvailableIdsList(availableShifts: List<AvailableShift>) {
        availableShiftsIdList.clear()
        val noOfShifts = availableShifts.size
        if (noOfShifts > 0) {
            Log.d("TESTING", "(MainActivity) : storeAvailableIdsList() - no of shifts : $noOfShifts")
            for ((index, shift) in availableShifts.withIndex()) {
                availableShiftsIdList.add(shift.id)
                Log.d("TESTING", "(MainActivity) : id : ${shift.id}")
            }
            if (availableShiftsIdList.size > 0 && isStartButtonClicked) {
                startGrabbingShifts()
            }
        }
    }

    private fun confirmShiftApi(availableShiftsIdList: ArrayList<String>) {
        val data = Data.Builder()
        data.putStringArray("IDS_LIST", availableShiftsIdList.toArray(arrayOfNulls<String>(availableShiftsIdList.size)))

        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<MyWorkManager>().setInputData(data.build())
            .build()
        WorkManager.getInstance(applicationContext).enqueue(oneTimeWorkRequest)
        val workmanager = WorkManager.getInstance(this)
        availableShiftsIdList.clear()
        if (isStartButtonClicked) {
            //Getting work status By using request ID
            workmanager.getWorkInfoByIdLiveData(oneTimeWorkRequest.id)
                .observe(this, Observer { workInfo: WorkInfo? ->
                    if (workInfo != null && workInfo.state.isFinished) {
                        val progress = workInfo.progress
                        Log.d("TESTING", "(MainActivity) :  observing work manager - progress is - $progress HAS TO BE SUCCESS/CANCELLED/FAILED STATE")
                        Log.d("TESTING", "(MainActivity) : observing work manager - BaseApp.noOfShiftsGrabbed value - ${BaseApp.noOfShiftsGrabbed}")
                        binding.shiftsNum.text = getString(R.string.shifts_num, BaseApp.noOfShiftsGrabbed.toString())
                        checkForAvailableShifts()
                    }
                })
        } else {
            workmanager.cancelWorkById(oneTimeWorkRequest.id)
        }
    }

    private fun startGrabbingShifts() {
        Log.d("TESTING", "(MainActivity) : startGrabbingShifts() started")
        if (isStartButtonClicked) {
            if (availableShiftsIdList.size > 0) {
                confirmShiftApi(availableShiftsIdList)
            } else {
                Log.d("TESTING", "calling checkForAvailableShifts from inside startGrabbingShifts() when noOfShifts is 0")
                checkForAvailableShifts()
            }
        }
    }

    private fun setData() {
        binding.startStopBtn.text = "START"
        var loginStateText = ""
        if (BaseApp.isLoggedIn) {
            loginStateText = "You're logged in!"
        } else {
            loginStateText = "Please login!"
        }
        binding.welcome.text = this.getString(R.string.welcome_text, BaseApp.userName, loginStateText)
        binding.shiftsNum.text = getString(R.string.shifts_num, BaseApp.noOfShiftsGrabbed.toString())
    }

    private fun stopGrabbingShifts() {
        Log.d("TESTING", "(MainActivity) : stopGrabbingShifts started")
        confirmShiftApi(availableShiftsIdList)
    }

    private fun setOnClickListeners() {
        binding.startStopBtn.setOnClickListener {
            if ("START".equals(binding.startStopBtn.text)) {
                isStartButtonClicked = true
                binding.startStopBtn.apply {
                    text = "STOP"
                    setBackgroundColor(this.context.resources.getColor(R.color.start_grey))
                }
                startGrabbingShifts()
            } else if ("STOP".equals(binding.startStopBtn.text)) {
                isStartButtonClicked = false
                binding.startStopBtn.apply {
                    text = "START"
                    setBackgroundColor(this.context.resources.getColor(R.color.orange_stop))
                }
                stopGrabbingShifts()
            }
        }

        binding.logoutImageview.setOnClickListener {
            val pref: SharedPreferences = this.getSharedPreferences(BaseApp.PREF_NAME, Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString("EMAIL_PREF", "")
            editor.putString("PASSWORD_PREF", "")
            editor.putBoolean("isLoggedIn", false)
            editor.apply()
            BaseApp.isLoggedIn = false
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("TESTING", "onRestart - calling checkForAvailableShifts()")
        checkForAvailableShifts()
        setDataForRestartState()
    }

    private fun setDataForRestartState() {
        if (isStartButtonClicked) {
            binding.startStopBtn.apply {
                text = "STOP"
                setBackgroundColor(this.context.resources.getColor(R.color.start_grey))
            }
        } else {
            binding.startStopBtn.apply {
                text = "START"
                setBackgroundColor(this.context.resources.getColor(R.color.orange_stop))
            }
        }
    }

}