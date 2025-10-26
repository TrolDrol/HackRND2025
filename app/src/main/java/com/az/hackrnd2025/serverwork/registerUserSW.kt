package com.az.hackrnd2025.serverwork

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.core.content.edit
import androidx.navigation.NavController
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

fun registerUserSW(
    name: String,
    surname: String,
    dateOfBurst: String,
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
            "name": "$name",
            "surname": "$surname",
            "email": "$email",
            "password": "$password",
            "date_of_birth": "$dateOfBurst"
        }
    """.trimIndent()

    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull() ?: run {
        Log.e("registerUserSW", "Error toMediaTypeOrNull")
        return
    }

    val requestBody = jsonBody.toRequestBody(mediaType)

    val request = Request.Builder()
        .url("$server/register")
        .post(requestBody)
        .build()

    // enqueue - ассинхронность
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("registerUserSW", "Ошибка сети: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                try {
                    val responseText = it.body?.string() ?: run {
                        Log.e("registerUserSW", "Пустой ответ от сервера")
                        return
                    }

                    // Ответ в главном потоке
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            // JSON ответ
                            val jsonObject = JSONObject(responseText)
                            val success = jsonObject.optBoolean("success", false)
                            val message = jsonObject.optString("message", "")

                            if (success) {
                                Log.i("registerUserSW", message)
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
                                Log.e("registerUserSW", message)
                                navController.navigate(Ways.AUTORIZATION.name)
                            }

                            Log.i("registerUserSW: ResponseText", responseText)

                        } catch (e: Exception) {
                            Log.e("registerUserSW", e.message.toString())
                        }
                    }
                } catch (e: Exception) {
                    Log.e("registerUserSW", e.message.toString())
                }
            }
        }
    })
}