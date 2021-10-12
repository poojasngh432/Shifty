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
import com.nomimon.shifty.databinding.ActivityMainBinding
import com.nomimon.shifty.model.ConfirmStatus
import com.nomimon.shifty.model.MyAvailableShiftsResponse
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
    private lateinit var callStartRun: Call<MyAvailableShiftsResponse>
    private lateinit var callGrabShifts: Call<ConfirmStatus>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        callStartRun = BaseApp.apiInterface.startRun(BaseApp.userId)
        startRunApi()
    }

    private fun setOnClickListeners() {
        binding.startBtn.setOnClickListener {
            if ("START".equals(binding.startBtn.text)) {
                binding.startBtn.apply {
                    text = "STOP"
                    setBackgroundColor(this.context.resources.getColor(R.color.orange_stop))
                    if(callStartRun.isCanceled)
                        startRunApi()
                }
            } else if ("STOP".equals(binding.startBtn.text)){
                binding.startBtn.apply {
                    text = "START"
                    setBackgroundColor(this.context.resources.getColor(R.color.start_grey))
                    if (callStartRun.isExecuted)
                        callStartRun.cancel()
                }
            }
        }
    }

    private fun startRunApi() {
        callStartRun = BaseApp.apiInterface.startRun(BaseApp.userId)
        callStartRun.enqueue(object : Callback<MyAvailableShiftsResponse> {
            override fun onResponse(call: Call<MyAvailableShiftsResponse>, response: Response<MyAvailableShiftsResponse>) {
                if (response.body() != null) {
                    val availableShifts = response.body()!!.availableShifts
                    val noOfShifts = availableShifts?.size ?: 0
                    Log.d("TESTING", "no of shifts : " + noOfShifts)
                    binding.shiftsNo.text = getString(R.string.shifts_num, noOfShifts.toString())
                    Toast.makeText(this@MainActivity, "No of available shifts available: $noOfShifts", Toast.LENGTH_SHORT).show()
                    val lisOfIds = mutableListOf<String>()
                    if (availableShifts != null) {
                        for (shift in availableShifts) {
                            val id = shift.id
                            lisOfIds.add(id)
                            Log.d("TESTING", "id : " + id)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                               val encodedId = URLEncoder.encode(id, StandardCharsets.UTF_8.toString())
                                Log.d("TESTING", "encodedId : " + encodedId)
                            }
                            confirmShiftApi(id)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<MyAvailableShiftsResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Api call failure. Try again", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun confirmShiftApi(id: String) {
        val parse: MediaType? = MediaType.parse("application/json")
        val requestBody = RequestBody.create(parse, JSONObject().toString())
        callGrabShifts = BaseApp.apiInterface.confirmGrabAllShifts(BaseApp.userId, id, requestBody)
        callGrabShifts.enqueue(object : Callback<ConfirmStatus> {
            override fun onResponse(call: Call<ConfirmStatus>, response: Response<ConfirmStatus>) {
                if (response.body() != null) {
                    val status = response.body()!!.status
                    if (status.contains("CONFIRM")) {
                        val shiftNumber = BaseApp.noOfShiftsGrabbed++
                        Toast.makeText(this@MainActivity, "Shift no. $shiftNumber GRABBED!", Toast.LENGTH_SHORT).show()
                    }
                }
                if(callStartRun.isCanceled)
                    callGrabShifts.cancel()
            }

            override fun onFailure(call: Call<ConfirmStatus>, t: Throwable) {
                if (callGrabShifts.isCanceled) {
                    Toast.makeText(this@MainActivity, "Api call was cancelled", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Api call failure. Try again", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}