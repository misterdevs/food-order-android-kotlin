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
import com.mrdevs.foodorder.api.models.request.UserRegisterRequest
import com.mrdevs.foodorder.api.models.response.Global
import com.mrdevs.foodorder.api.models.response.LoginResponse
import com.mrdevs.foodorder.api.models.response.UserResponse
import com.mrdevs.foodorder.api.services.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type


class RegisterActivity : AppCompatActivity() {

    private val userService: UserService = ApiInstance.createService(this,UserService::class.java)

    private lateinit var toLogin: TextView
    private lateinit var username: EditText
    private lateinit var fullName: EditText
    private lateinit var password: EditText
    private lateinit var confirmationPassword: EditText
    private lateinit var registerButton: Button

    private fun init() {
        toLogin = findViewById(R.id.toLogin)
        username = findViewById(R.id.inputUsername)
        fullName = findViewById(R.id.inputFullName)
        password = findViewById(R.id.inputPassword)
        confirmationPassword = findViewById(R.id.inputConfirmationPassword)
        registerButton = findViewById(R.id.registerButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        init()

        //set input hint
        username.hint = getString(R.string.hint, getString(R.string.username))
        fullName.hint = getString(R.string.hint, getString(R.string.fullName))
        password.hint = getString(R.string.hint, getString(R.string.password))
        confirmationPassword.hint =
            resources.getString(R.string.hint, getString(R.string.confirmationPassword))

        toLogin.setOnClickListener {
            if (intent.getStringExtra("from").equals("loginActivity")) {
                super.onBackPressedDispatcher.onBackPressed()
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

        }

        registerButton.setOnClickListener {
            if (username.text.isEmpty()) {
                Log.e("RegisterActivity/RegisterRequest", "Username is empty")
                Toast.makeText(
                    this@RegisterActivity,
                    "Username is empty",
                    Toast.LENGTH_SHORT
                ).show()

            }
            if (fullName.text.isEmpty()) {
                Log.e("RegisterActivity/RegisterRequest", "Full Name is empty")
                Toast.makeText(
                    this@RegisterActivity,
                    "Full Name is empty",
                    Toast.LENGTH_SHORT
                ).show()

            }
            if (password.text.isEmpty()) {
                Log.e("RegisterActivity/RegisterRequest", "Password is empty")
                Toast.makeText(
                    this@RegisterActivity,
                    "Password is empty",
                    Toast.LENGTH_SHORT
                ).show()

            }
            if (confirmationPassword.text.isEmpty()) {
                Log.e("RegisterActivity/RegisterRequest", "Confirmation password is empty")
                Toast.makeText(
                    this@RegisterActivity,
                    "Confirmation password is empty",
                    Toast.LENGTH_SHORT
                ).show()

            }

            if (password.text.toString() != confirmationPassword.text.toString()) {
                Log.e("RegisterActivity/RegisterRequest", "Confirmation password does not match")
                Toast.makeText(
                    this@RegisterActivity,
                    "Confirmation password does not match",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (username.text.isNotEmpty() && fullName.text.isNotEmpty() && password.text.isNotEmpty() && confirmationPassword.text.isNotEmpty()) {
                registerRequest(
                    UserRegisterRequest(
                        username.text.toString(),
                        fullName.text.toString(),
                        password.text.toString(),
                        confirmationPassword.text.toString()
                    )
                )
            }


        }


    }

    private fun registerRequest(registerRequest: UserRegisterRequest) {

        userService.register(registerRequest).enqueue(object : Callback<Global<UserResponse>> {
            override fun onResponse(
                call: Call<Global<UserResponse>>,
                response: Response<Global<UserResponse>>
            ) {
                when {
                    response.isSuccessful -> {
                        val responseBody = response.body()
                        Log.i(
                            "RegisterActivity/RegisterRequest",
                            "${responseBody?.data?.username} successfully registered"
                        )
                        when {
                            response.body() != null -> {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "${responseBody?.data?.username} successfully registered",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent =
                                    Intent(this@RegisterActivity, LoginActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }

                    else -> {
                        val collectionType: Type =
                            object : TypeToken<Global<LoginResponse?>?>() {}.type
                        val responseBody: Global<LoginResponse> =
                            Gson().fromJson(response.errorBody()?.charStream(), collectionType)

                        Log.e("RegisterActivity/RegisterRequest", responseBody.message)
                        Toast.makeText(
                            this@RegisterActivity,
                            responseBody.message,
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
            }

            override fun onFailure(call: Call<Global<UserResponse>>, t: Throwable) {
                Log.e("RegisterActivity/RegisterRequest", t.message.toString())
                Toast.makeText(
                    this@RegisterActivity,
                    "Internal Server Error",
                    Toast.LENGTH_SHORT
                ).show()
            }


        })

    }
}