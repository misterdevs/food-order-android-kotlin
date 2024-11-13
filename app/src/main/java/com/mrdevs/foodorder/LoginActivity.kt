package com.mrdevs.foodorder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mrdevs.foodorder.api.ApiInstance
import com.mrdevs.foodorder.api.models.request.UserLoginRequest
import com.mrdevs.foodorder.api.models.response.Global
import com.mrdevs.foodorder.api.models.response.LoginResponse
import com.mrdevs.foodorder.api.services.UserService
import com.mrdevs.foodorder.sharedPreference.SharedPreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type


class LoginActivity : AppCompatActivity() {

    private val userService: UserService = ApiInstance.createService(this, UserService::class.java)
    private lateinit var sharedPreferences: SharedPreferenceManager

    private lateinit var toRegister: TextView
    private lateinit var loginButton: Button
    private lateinit var username: EditText
    private lateinit var password: EditText

    private fun init() {
        toRegister = findViewById(R.id.toRegister)
        loginButton = findViewById(R.id.loginButton)
        username = findViewById(R.id.inputUsername)
        password = findViewById(R.id.inputPassword)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sharedPreferences = SharedPreferenceManager(applicationContext)
        if (sharedPreferences.isLogin()) {
            val intent = Intent(this, DashFragmentActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        } else {
            setContentView(R.layout.activity_login)
        }

        init()

        //set input hint
        username.hint = getString(R.string.hint, getString(R.string.username))
        password.hint = getString(R.string.hint, getString(R.string.password))

        toRegister.setOnClickListener {
            val intent =
                Intent(this, RegisterActivity::class.java).putExtra("from", "loginActivity")
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            if (username.text.isEmpty()) {
                Log.e("LoginActivity/LoginRequest", "Username is empty")
                Toast.makeText(
                    this@LoginActivity,
                    "Username is empty",
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (password.text.isEmpty()) {
                Log.e("LoginActivity/LoginRequest", "Password is empty")
                Toast.makeText(
                    this@LoginActivity,
                    "Password is empty",
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (username.text.isNotEmpty() && password.text.isNotEmpty()) {
                loginRequest(UserLoginRequest(username.text.toString(), password.text.toString()))
            }

        }
    }

    private fun loginRequest(loginRequest: UserLoginRequest) {
        userService.login(loginRequest).enqueue(object : Callback<Global<LoginResponse>> {
            override fun onResponse(
                call: Call<Global<LoginResponse>>,
                response: Response<Global<LoginResponse>>
            ) {
                when {
                    response.isSuccessful -> {
                        val responseBody = response.body()
                        Log.i("LoginActivity/LoginRequest", responseBody?.data.toString())
                        when {
                            response.body() != null -> {
                                sharedPreferences.saveSpString(
                                    SharedPreferenceManager.KEYS.SP_USERNAME.name,
                                    responseBody?.data?.username!!
                                )
                                sharedPreferences.saveSpString(
                                    SharedPreferenceManager.KEYS.SP_FULL_NAME.name,
                                    responseBody.data.fullName
                                )
                                sharedPreferences.saveSpString(
                                    SharedPreferenceManager.KEYS.SP_TOKEN.name,
                                    responseBody.data.token
                                )
                                sharedPreferences.saveSpBoolean(
                                    SharedPreferenceManager.KEYS.SP_IS_LOGIN.name,
                                    true
                                )

                                Toast.makeText(
                                    this@LoginActivity,
                                    "Welcome! ${responseBody.data.fullName}",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent =
                                    Intent(this@LoginActivity, DashFragmentActivity::class.java)
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                            }
                        }
                    }

                    else -> {
                        val collectionType: Type =
                            object : TypeToken<Global<LoginResponse?>?>() {}.type
                        val responseBody: Global<LoginResponse> =
                            Gson().fromJson(response.errorBody()?.charStream(), collectionType)

                        Log.e("LoginActivity/LoginRequest", responseBody.message)
                        Toast.makeText(
                            this@LoginActivity,
                            responseBody.message,
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

            }

            override fun onFailure(call: Call<Global<LoginResponse>>, t: Throwable) {
                Log.e("LoginActivity/LoginRequest", t.message.toString())
                Toast.makeText(
                    this@LoginActivity,
                    "Internal Server Error",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }
}
