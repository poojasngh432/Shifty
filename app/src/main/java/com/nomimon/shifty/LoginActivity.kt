package com.nomimon.shifty

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nomimon.shifty.databinding.ActivityMain2Binding
import com.nomimon.shifty.model.LoginResponse
import io.reactivex.disposables.Disposable
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    private lateinit var requestBodyParams: MutableMap<String, String>
    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.save.setOnClickListener {
            setPrefs()
            callLoginApi()
        }
    }

    private fun setPrefs() {
        val email = binding.email.editText?.text.toString()
        val password = binding.password.editText?.text.toString()
        BaseApp.email = email
        BaseApp.password = password
        val pref: SharedPreferences = this.getSharedPreferences(BaseApp.PREF_NAME, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString("EMAIL_PREF", email)
        editor.putString("PASSWORD_PREF", password)
        editor.apply()
    }

    private fun callLoginApi() {
        val pref: SharedPreferences = binding.root.context.getSharedPreferences(BaseApp.PREF_NAME, Context.MODE_PRIVATE)
        val editor = pref.edit()

        val jSONObject = JSONObject()
        try {
            jSONObject.put("email", BaseApp.email)
            jSONObject.put("password", BaseApp.password)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val parse: MediaType? = MediaType.parse("application/json")
        val requestBody2 = RequestBody.create(parse, jSONObject.toString())

        val call: Call<LoginResponse> = BaseApp.apiInterface.login(requestBody2)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val id = response.body()?.id
                val name = response.body()?.name
                val token = response.body()?.token
                if (id != null) {
                    BaseApp.userId = id
                }
                if (name != null) {
                    BaseApp.userName = name
                }
                if (token != null) {
                    BaseApp.userToken = token
                }
                editor.putString("ID_PREF", id)
                editor.putString("NAME_PREF", name)
                editor.putString("TOKEN_PREF", token)
                if (!id.isNullOrEmpty() && !token.isNullOrEmpty()) {
                    editor.putBoolean("isLoggedIn", true)
                    Toast.makeText(this@LoginActivity, "Api call success", Toast.LENGTH_SHORT).show()
                    val intent =  Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    editor.putBoolean("isLoggedIn", false)
                    Toast.makeText(this@LoginActivity, "Check your details and try again", Toast.LENGTH_SHORT).show()
                }
                editor.apply()
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                editor.putBoolean("isLoggedIn", false)
                Toast.makeText(this@LoginActivity, "Api call failure. Check your details", Toast.LENGTH_SHORT).show()
            }
        })

    }
}