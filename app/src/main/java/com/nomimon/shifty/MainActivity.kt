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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var requestBodyParams: MutableMap<String, String>
    private val disposables = CompositeDisposable()
    private var buttonText: String = "START"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setData()
        setOnClickListeners()
    }

    private fun setData() {
        binding.startBtn.text = buttonText
    }

    private fun setOnClickListeners() {
        binding.startBtn.setOnClickListener {
            if ("START".equals(buttonText)) {
                binding.startBtn.apply {
                    text = "STOP"
                    setBackgroundColor(this.context.resources.getColor(R.color.orange_stop))
                }
                startRunApi()
            } else {
                binding.startBtn.apply {
                    text = "START"
                    setBackgroundColor(this.context.resources.getColor(R.color.start_grey))
                }
            }
        }
    }

    private fun startRunApi() {

        val call: Call<MyAvailableShiftsResponse> = BaseApp.apiInterface.startRun(BaseApp.userId)
        call.enqueue(object : Callback<MyAvailableShiftsResponse> {
            override fun onResponse(call: Call<MyAvailableShiftsResponse>, response: Response<MyAvailableShiftsResponse>) {
                if (response.body() != null) {
                    val availableShifts = response.body()!!.availableShifts
                    val noOfShifts = availableShifts?.size
                    Log.d("TESTING", "no of shifts : " + noOfShifts)
                    Toast.makeText(this@MainActivity, "No of available shifts: $noOfShifts", Toast.LENGTH_SHORT).show()
                    val lisOfIds = mutableListOf<String>()
                    if (availableShifts != null) {
                        for (shift in availableShifts) {
                            val id = shift.id
                            lisOfIds.add(id)
                            Log.d("TESTING", "id : " + id)
                            var encodedId = URLEncoder.encode(id)
                            Log.d("TESTING", "encodedId : " + encodedId)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                encodedId = URLEncoder.encode(id, StandardCharsets.UTF_8.toString())
                            }
                            Log.d("TESTING", "encodedId : " + encodedId)
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
        val call: Call<ConfirmStatus> = BaseApp.apiInterface.confirmGrabAllShifts(BaseApp.userId, id)
        call.enqueue(object : Callback<ConfirmStatus> {
            override fun onResponse(call: Call<ConfirmStatus>, response: Response<ConfirmStatus>) {
                if (response.body() != null) {
                    val status = response.body()!!.status
                    if (status.contains("CONFIRM")) {
                        val shiftNumber = BaseApp.noOfShiftsGrabbed++
                        Toast.makeText(this@MainActivity, "Shift no. $shiftNumber GRABBED!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ConfirmStatus>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Api call failure. Try again", Toast.LENGTH_SHORT).show()
            }
        })
    }
}