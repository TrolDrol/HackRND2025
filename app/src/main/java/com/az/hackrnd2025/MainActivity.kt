package com.az.hackrnd2025

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.az.hackrnd2025.serverwork.refreshDBSW
import com.az.hackrnd2025.view.AuthorizationView
import com.az.hackrnd2025.view.CardsView
import com.az.hackrnd2025.view.CardsViewModel
import com.az.hackrnd2025.view.PickPriorityView
import com.az.hackrnd2025.view.SignInView

const val server: String = "http://138.124.14.227:80"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CardsViewModel = viewModel()
            //refreshDBSW()
            val sharedPreferences = applicationContext.getSharedPreferences("User", MODE_PRIVATE)
            val login = sharedPreferences.getBoolean("Login", false)
            val priority = sharedPreferences.getString("Priority", "")
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination =
                    if (login && priority!!.isNotEmpty())
                        Ways.CARDS.name
                    else if (login)
                        Ways.PRIORITY.name
                    else
                        Ways.AUTORIZATION.name
            ) {
                composable(Ways.AUTORIZATION.name) {
                    AuthorizationView(applicationContext, navController)
                }
                composable(Ways.SIGNIN.name) {
                    SignInView(applicationContext, navController)
                }
                composable(Ways.PRIORITY.name) {
                    PickPriorityView(applicationContext, navController)
                }
                composable(Ways.CARDS.name) {
                    CardsView()
                }
            }
        }
    }
}

enum class Ways {
    AUTORIZATION,
    SIGNIN,
    PRIORITY,
    CARDS
}