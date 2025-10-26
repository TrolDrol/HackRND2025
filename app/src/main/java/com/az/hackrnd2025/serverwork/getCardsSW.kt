package com.az.hackrnd2025.serverwork

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.az.hackrnd2025.server
import com.az.hackrnd2025.view.LocationCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

fun getCardsSW(email: String, context: Context, pushCards: (List<LocationCard>) -> Unit) {
    val client = OkHttpClient.Builder()
        .connectTimeout(1000, TimeUnit.SECONDS)
        .writeTimeout(1000, TimeUnit.SECONDS)
        .readTimeout(1000, TimeUnit.SECONDS)
        .callTimeout(1000, TimeUnit.SECONDS)
        .build()
    println("1")
    val jsonBody = """
        {
            "email": "$email"
        }
    """.trimIndent()

    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull() ?: run {
        Log.e("putPrioritySW", "Error .toMediaTypeOrNull()")
        return
    }

    val requestBody = jsonBody.toRequestBody(mediaType)

    val request = Request.Builder()
        .url("$server/data_trans")
        .post(requestBody)
        .build()
    println("2")
    // enqueue - ассинхронность
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("getCardsSW", "Ошибка сети: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                try {
                    val responseText = it.body?.string() ?: run {
                        Log.e("getCardsSW", "Пустой ответ от сервера")
                        return
                    }

                    // Ответ в главном потоке
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            // JSON ответ
                            val response = Json.decodeFromString<CardsResponse>(responseText)

                            if (response.success) {
                                pushCards(response.recommended_places)
                                context.getSharedPreferences("User", MODE_PRIVATE).edit().apply {
                                    putBoolean("CardsSaving", true)
                                }
                                Log.i("getCardsSW: Success: ", response.user_preferences.toString())
                            } else {
                                Log.e("getCardsSW", response.user_preferences.toString())
                            }

                            Log.i("getCardsSW", responseText)

                        } catch (e: Exception) {
                            Log.e("getCardsSW: ResponseText", e.message.toString())
                        }
                    }
                } catch (e: Exception) {
                    Log.e("getCardsSW", e.message.toString())
                }
            }
        }
    })
}

@Serializable
data class CardsResponse (
    var success: Boolean,
    var user_preferences: List<String>,
    var recommended_places: List<LocationCard>
)