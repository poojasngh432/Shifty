package com.nomimon.shifty

import androidx.appcompat.app.AppCompatActivity

class LogIn : AppCompatActivity() {
//    var databaseArtists: DatabaseReference? = null
//    var edtEmail: EditText? = null
//    var edtPassword: EditText? = null
//    var email: String? = null
//    var myModel: String? = null
//    var password: String? = null
//    var stdVersion: String? = null
//    var title: TextView? = null
//    var token: String? = null
//
//    /* access modifiers changed from: protected */
//    public override fun onCreate(bundle: Bundle?) {
//        super.onCreate(bundle)
//        setContentView(C0996R.layout.activity_logged_in as Int)
//        title = findViewById<View>(C0996R.C0999id.txtTitle) as TextView
//        edtEmail = findViewById<View>(C0996R.C0999id.edtEmail) as EditText
//        edtPassword = findViewById<View>(C0996R.C0999id.edtPassword) as EditText
//        databaseArtists = FirebaseDatabase.getInstance().getReference()
//        title!!.typeface = Typeface.createFromAsset(assets, "first.ttf")
//        try {
//            stdVersion = VersionChecker().execute(arrayOfNulls<String>(0)).get()
//        } catch (e: ExecutionException) {
//            e.printStackTrace()
//        } catch (e2: InterruptedException) {
//            e2.printStackTrace()
//        }
//        myModel = ModelChecker().getDeviceName()
//    }
//
//    fun btnLogin(view: View?) {
//        databaseArtists.push().getKey()
//        val okHttpClient = OkHttpClient()
//        val parse: MediaType = MediaType.parse("application/json")
//        if (edtEmail!!.text.length <= 0 || edtPassword!!.text.length <= 0) {
//            Toast.makeText(this, "Please Correct Enter Details", 0).show()
//            return
//        }
//        val user: User = applicationContext as User
//        user.setStdVersion(stdVersion)
//        user.setMyModel(myModel)
//        val jSONObject = JSONObject()
//        try {
//            jSONObject.put("email", edtEmail!!.text.toString())
//            jSONObject.put("password", edtPassword!!.text.toString())
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        okHttpClient.newCall(Builder().url("https://api-courier-produk.skipthedishes.com/v1/couriers/login")
//            .post(RequestBody.create(parse, jSONObject.toString()))
//            .header("app-token", "31983a5d-37b1-4390-bd1c-8184e855e5da")
//            .header("app-version", stdVersion)
//            .header("Content-Length", "0")
//            .header("app-build", "272")
//            .header("model", myModel).build())
//            .enqueue(object : Callback() {
//                fun onFailure(request: Request?, iOException: IOException) {
//                    iOException.getMessage().toString()
//                    Toast.makeText(this@LogIn.applicationContext, "Please check your details", 1)
//                        .show()
//                }
//
//                @Throws(IOException::class)
//                fun onResponse(response: Response) {
//                    try {
//                        val jSONObject = JSONObject(response.body().string())
//                        if (!jSONObject.has("token")) {
//                            runOnUiThread {
//                                Toast.makeText(this@LogIn.applicationContext, "Check Your Details", 1)
//                                    .show()
//                            }
//                            return
//                        }
//                        val string: String = jSONObject.getString("token")
//                        val string2: String = jSONObject.getString("id")
//                        val string3: String = jSONObject.getString("name")
//                        user.setToken(string)
//                        user.setUserID(string2)
//                        user.setName(string3)
//                        this@LogIn.startActivity(Intent(this@LogIn.applicationContext, subscription::class.java))
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                }
//            })
//    }
}
