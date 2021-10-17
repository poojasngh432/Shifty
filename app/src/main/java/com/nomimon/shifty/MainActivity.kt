package com.nomimon.shifty

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.nomimon.shifty.databinding.ActivityMainBinding
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        private var isStartButtonClicked = false
        var availableShiftsIdList: ArrayList<String> = ArrayList()
        var DELAY = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setData()
        setOnClickListeners()
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
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                binding.shiftsNum.text = getString(R.string.shifts_num, BaseApp.noOfShiftsGrabbed.toString())
                mainHandler.postDelayed(this, 100)
            }
        })
    }

    private fun setOnClickListeners() {
        binding.setdelayET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                DELAY = s.toString().toInt()
            }

        })
        binding.startStopBtn.setOnClickListener {
            try {
                BaseApp.noOfShiftsGrabbed = 0
                binding.shiftsNum.text = getString(R.string.shifts_num, BaseApp.noOfShiftsGrabbed.toString())
                val intent = Intent(this@MainActivity.getApplicationContext(), MyService::class.java)
                if ("START".equals(binding.startStopBtn.text)) {
                    isStartButtonClicked = true
                    binding.startStopBtn.apply {
                        text = "STOP"
                        setBackgroundColor(this.context.resources.getColor(R.color.start_grey))
                    }
                    sendNotification(applicationContext)
                    intent.action = "START"
                    startService(intent)
                } else if ("STOP".equals(binding.startStopBtn.text)) {
                    isStartButtonClicked = false
                    binding.startStopBtn.apply {
                        text = "START"
                        setBackgroundColor(this.context.resources.getColor(R.color.orange_stop))
                    }
                    intent.action = "STOP"
                    startService(intent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
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

    override fun onRestart() {
        super.onRestart()
        Log.d("TESTING", "onRestart - calling checkForAvailableShifts()")
        setDataForRestartState()
        binding.shiftsNum.text = getString(R.string.shifts_num, BaseApp.noOfShiftsGrabbed.toString())
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