package com.nomimon.shifty

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nomimon.shifty.databinding.ActivityMainBinding
import com.nomimon.shifty.model.MyAvailableShiftsResponse
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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

                    val lisOfIds = mutableListOf<String>()
                    if (availableShifts != null) {
                        for (shift in availableShifts) {
                            lisOfIds.add(shift.id)
                            Log.d("TESTING", "id : " + shift.id)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<MyAvailableShiftsResponse>, t: Throwable) {
//                editor.putBoolean("isLoggedIn", false)
                Toast.makeText(this@MainActivity, "Api call failure. Check your details", Toast.LENGTH_SHORT).show()
            }
        })






//
//        val response = BaseApp.apiInterface?.startRun()
//        response?.enqueue(object : Callback<LoginResponse> {
//            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
//                Toast.makeText(this@MainActivity, "Api call Success: ${BaseApp.email} ${BaseApp.password}", Toast.LENGTH_SHORT).show()
//                if (response?.body() != null) {
////                    val jSONObject = JSONObject(response.body().toString())
////                    val userToken = jSONObject.getString("token")
////                    val userId = jSONObject.getString("id")
////                    val userName = jSONObject.getString("name")
//                    val pref: SharedPreferences = binding.root.context.getSharedPreferences(BaseApp.PREF_NAME, Context.MODE_PRIVATE)
//                    val editor = pref.edit()
////                    editor.putString(BaseApp.userToken, userToken)
////                    editor.putString(BaseApp.userId, userId)
////                    editor.putString(BaseApp.userName, userName)
////                    editor.apply()
//                    val intent =  Intent(this@MainActivity, MainActivity::class.java)
//                    startActivity(intent)
//                }
//
//            }
//
//            override fun onFailure(call: retrofit2.Call<LoginResponse>, t: Throwable) {
//                Toast.makeText(this@MainActivity, "Api call Failure. Check your details.", Toast.LENGTH_SHORT).show()
//            }
//        })
    }
}