package com.az.hackrnd2025.serverwork

import android.util.Log
import com.az.hackrnd2025.server
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

fun refreshDBSW() {
    val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .callTimeout(60, TimeUnit.SECONDS)
        .build()

    val request = Request.Builder()
        .url("$server/refresh")
        .get()
        .build()

    // enqueue - ассинхронность
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("refreshDBSW", "Ошибка сети: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                try {
                    val responseText = it.body?.string() ?: run {
                        Log.e("refreshDBSW", "Пустой ответ от сервера")
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
                                Log.i("refreshDBSW: Success: ", message)
                            } else {
                                Log.e("refreshDBSW",message)
                            }

                            Log.i("refreshDBSW", responseText)

                        } catch (e: Exception) {
                            Log.e("refreshDBSW: ResponseText", e.message.toString())
                        }
                    }
                } catch (e: Exception) {
                    Log.e("refreshDBSW", e.message.toString())
                }
            }
        }
    })
}