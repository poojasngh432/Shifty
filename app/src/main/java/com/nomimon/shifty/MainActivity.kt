package com.nomimon.shifty

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import com.nomimon.shifty.databinding.ActivityMainBinding
import com.nomimon.shifty.model.AvailableShift
import com.nomimon.shifty.model.ConfirmStatus
import com.nomimon.shifty.model.MyAvailableShiftsResponse
import com.nomimon.shifty.response.MyWorkManager
import io.reactivex.disposables.CompositeDisposable
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.math.log


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var buttonText: String = "START"
    private var myWorkManager: OneTimeWorkRequest? = null
    private var isStartButtonClicked = false
    var myAvailableShifts: ArrayList<AvailableShift> = ArrayList<AvailableShift>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startRunApi()
        setData()
        setOnClickListeners()
    }

    private fun setData() {
        binding.startBtn.text = buttonText
        var loginState = ""
        if (BaseApp.isLoggedIn) {
            loginState = "You're logged in!"
        } else {
            loginState = "Please login!"
        }
        binding.welcome.text = this.getString(R.string.welcome_text, BaseApp.userName, loginState)
        binding.shiftsNum.text = getString(R.string.shifts_num, BaseApp.noOfShiftsGrabbed.toString())
    }

    private fun setOnClickListeners() {
        binding.startBtn.setOnClickListener {
            if ("START".equals(binding.startBtn.text)) {
                binding.startBtn.apply {
                    text = "STOP"
                    setBackgroundColor(this.context.resources.getColor(R.color.start_grey))
                }
                isStartButtonClicked = true
                startGrabbingShifts()
            } else if ("STOP".equals(binding.startBtn.text)) {
                binding.startBtn.apply {
                    text = "START"
                    setBackgroundColor(this.context.resources.getColor(R.color.orange_stop))

                    stopGrabbingShifts()
                }
                isStartButtonClicked = false
            }
        }

        binding.logoutImageview.setOnClickListener {
            val pref: SharedPreferences = this.getSharedPreferences(BaseApp.PREF_NAME, Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString("EMAIL_PREF", "")
            editor.putString("PASSWORD_PREF", "")
            editor.putBoolean("isLoggedIn", false)
            editor.apply()
            val intent =  Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun startGrabbingShifts() {
        Log.d("TESTING", "startGrabbingShifts started")
        val noOfShifts = myAvailableShifts.size
        if (noOfShifts > 0) {
            Log.d("TESTING", "no of shifts : " + noOfShifts)
            Toast.makeText(this@MainActivity, "No of available shifts available: $noOfShifts", Toast.LENGTH_SHORT)
                .show()
            val lisOfIds = mutableListOf<String>()
            if (myAvailableShifts.isNotEmpty()) {
                var lastindex = 0
                for (shift in myAvailableShifts) {
                    lastindex++
                    val isLastIndex: Boolean = lastindex == noOfShifts

                    val id = shift.id
                    lisOfIds.add(id)
                    Log.d("TESTING", "id : $id")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        val encodedId = URLEncoder.encode(id, StandardCharsets.UTF_8.toString())
                        Log.d("TESTING", "encodedId : " + encodedId)
                    }
                    if (isStartButtonClicked) {
                        Log.d("TESTING", "confirm shift api clicked and index is: $lastindex")
                        confirmShiftApi(id, isLastIndex)
                    }
                }
            }
        } else {
            if(isStartButtonClicked) {
                startRunApi()
            }
        }
    }

    private fun stopGrabbingShifts() {
        Log.d("TESTING", "stopGrabbingShifts started")
        val workManager = WorkManager.getInstance(this@MainActivity)
        //Cancels work with the given id if it isn't finished.
        myWorkManager?.let { it1 -> workManager.cancelWorkById(it1.id) }
    }

    private fun startRunApi() {
        Log.d("TESTING", "startRunApi started")
        val callStartRun = BaseApp.apiInterface.startRun(BaseApp.userId)
        callStartRun.enqueue(object : Callback<MyAvailableShiftsResponse> {
            override fun onResponse(call: Call<MyAvailableShiftsResponse>, response: Response<MyAvailableShiftsResponse>) {
                if (response.body() != null) {
                    val availableShifts = response.body()!!.availableShifts
                    if (availableShifts != null && availableShifts.isNotEmpty()) {
                        Log.d("TESTING", "availableShifts size is : ${availableShifts.size}")
                        addToMyAvailableShiftsList(availableShifts)
                    }
                    if(isStartButtonClicked) {
                        Log.d("TESTING", "isStartButtonClicked")
                        startRunApi()
                    }
                }
            }

            override fun onFailure(call: Call<MyAvailableShiftsResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Api call failure. Try again", Toast.LENGTH_SHORT).show()
                if(isStartButtonClicked) {
                    Log.d("TESTING", "isStartButtonClicked")
                    startRunApi()
                }
            }
        })
    }

    private fun addToMyAvailableShiftsList(availableShifts: List<AvailableShift>) {
      myAvailableShifts.addAll(availableShifts)
    }

    private fun confirmShiftApi(id: String, isLastIndex: Boolean) {
        val data = Data.Builder()
        data.putString("SHIFT_ID", id)
        data.putBoolean("IS_LAST_INDEX", isLastIndex)

        myWorkManager = OneTimeWorkRequestBuilder<MyWorkManager>()
            .setInputData(data.build()).build()
        WorkManager.getInstance(this).enqueue(myWorkManager!!)
        //Getting work status By using request ID
        WorkManager.getInstance(this)
            .getWorkInfoByIdLiveData(myWorkManager!!.id)
            .observe(this, Observer { workInfo: WorkInfo? ->
                if (workInfo != null) {
                    val progress = workInfo.progress
                    Log.d("TESTING"," progress is - $progress")
                    binding.shiftsNum.text = getString(R.string.shifts_num, BaseApp.noOfShiftsGrabbed.toString())
                    // Do something with progress information
                    if (isLastIndex) {
                        Log.d("TESTING", "is it the last index? - $isLastIndex")
                        if(isStartButtonClicked) {
                            startRunApi()
                        }
                    }
                }
            })
    }

}