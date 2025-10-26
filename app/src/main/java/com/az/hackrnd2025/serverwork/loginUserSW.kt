package com.az.hackrnd2025.serverwork

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.core.content.edit
import androidx.navigation.NavController
import com.az.hackrnd2025.R
import com.az.hackrnd2025.Ways
import com.az.hackrnd2025.server
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

fun loginUserSW(
    email: String,
    password: String,
    appContext: Context,
    navController: NavController
) {
    val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .callTimeout(60, TimeUnit.SECONDS)
        .build()

    val jsonBody = """
        {
            "email": "$email",
            "password": "$password"
        }
    """.trimIndent()

    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull() ?: run {
        Log.e("loginUserSW", "Error toMediaTypeOrNull")
        return
    }

    val requestBody = jsonBody.toRequestBody(mediaType)

    val request = Request.Builder()
        .url("$server/login")
        .post(requestBody)
        .build()
    println("0")
    // enqueue - ассинхронность
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("loginUserSW", "Ошибка сети: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                try {
                    val responseText = it.body?.string() ?: run {
                        Log.e("loginUserSW", "Пустой ответ от сервера")
                        return
                    }

                    // Ответ в главном потоке
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            // JSON ответ
                            val jsonObject = JSONObject(responseText)
                            println("1")
                            val success = jsonObject.optBoolean("success", false)
                            val message = jsonObject.optString("message", "")
                            val name = jsonObject.optString("name", "")
                            val surname = jsonObject.optString("surname", "")
                            println("2")
                            if (success) {
                                Log.i("loginUserSW", message)
                                appContext.applicationContext.getSharedPreferences(
                                    "User",
                                    MODE_PRIVATE
                                ).edit() {
                                    putBoolean("Login", true)
                                    putString("Email", email)
                                    putString("Name", name)
                                    putString("Surname", surname)
                                }
                                navController.navigate(Ways.PRIORITY.name)
                            } else {
                                Log.e("loginUserSW", message)
                                navController.navigate(Ways.AUTORIZATION.name)
                            }

                            Log.i("loginUserSW: ResponseText", responseText)

                        } catch (e: Exception) {
                            Log.e("loginUserSW", e.message.toString())
                        }
                    }
                } catch (e: Exception) {
                    Log.e("loginUserSW", e.message.toString())
                }
            }
        }
    })
}