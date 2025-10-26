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

fun putPrioritySW(set: Set<String>, appContext: Context, navController: NavController) {
    val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .callTimeout(60, TimeUnit.SECONDS)
        .build()

    val preferences = set.joinToString(" ") + " "

    val email = appContext.getSharedPreferences("User", MODE_PRIVATE).getString("Email", "")
    val jsonBody = """
        {
            "email": "$email",
            "preferences": "$preferences"
        }
    """.trimIndent()

    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull() ?: run {
        Log.e("putPrioritySW", "Error .toMediaTypeOrNull()")
        return
    }

    val requestBody = jsonBody.toRequestBody(mediaType)

    val request = Request.Builder()
        .url("$server/priority")
        .post(requestBody)
        .build()

    // enqueue - ассинхронность
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("putPrioritySW", "Ошибка сети: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                try {
                    val responseText = it.body?.string() ?: run {
                        Log.e("putPrioritySW", "Пустой ответ от сервера")
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
                                Log.i("putPrioritySW", message)
                                appContext.getSharedPreferences(
                                    "User",
                                    MODE_PRIVATE
                                ).edit() {
                                    putString("Priority", preferences)
                                }
                                navController.navigate(Ways.CARDS.name)
                            } else {
                                navController.navigate(Ways.PRIORITY.name)
                                Log.e("putPrioritySW",message)
                            }

                            Log.i("putPrioritySW", responseText)

                        } catch (e: Exception) {
                            Log.e("putPrioritySW: ResponseText", e.message.toString())
                        }
                    }
                } catch (e: Exception) {
                    Log.e("putPrioritySW", e.message.toString())
                }
            }
        }
    })
}