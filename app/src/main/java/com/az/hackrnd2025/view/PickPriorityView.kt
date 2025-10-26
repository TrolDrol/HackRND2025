package com.az.hackrnd2025.view

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.az.hackrnd2025.serverwork.putPrioritySW

@Composable
fun PickPriorityView(appContext: Context, navController: NavController) {
    val selectedPriority = remember { mutableStateOf(setOf<String>()) }

    val placeCategories = mapOf(
        "adventure" to "Активный отдых, экстрим, походы",
        "culture" to "Музеи, искусство, история",
        "relax" to "Спа, отдых, релаксация",
        "family" to "Семейный отдых, детские мероприятия",
        "gastronomy" to "Рестораны, кафе, еда",
        "nature" to "Природа, парки, заповедники",
        "shopping" to "Шоппинг, магазины"
    )

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF48B626),
            Color(0xFF0C5207)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(200.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x44FFFFFF),
                            Color(0x00FFFFFF)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(150.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x22FFFFFF),
                            Color(0x00FFFFFF)
                        )
                    )
                )
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.Center),
            elevation = CardDefaults.cardElevation(16.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Выберите 3 интересные вам категории:",
                    style = MaterialTheme.typography.bodyLarge
                )

                placeCategories.forEach { elem ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        elevation = CardDefaults.cardElevation(16.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                elem.value,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 5.dp).weight(1f)
                            )
                            Checkbox(
                                checked = selectedPriority.value.contains(elem.key),
                                onCheckedChange = { isChecked ->
                                    if (isChecked) {
                                        selectedPriority.value += elem.key
                                    } else {
                                        selectedPriority.value -= elem.key
                                    }
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        putPrioritySW(selectedPriority.value, appContext, navController)
                    },
                    content = {
                        Text(
                            "Сохранить",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = selectedPriority.value.size == 3,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color(0xFF47EC0D),
                        containerColor = Color(0xFF076E04)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 4.dp
                    )
                )


            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewPickPriorityView() {
    PickPriorityView(LocalContext.current, rememberNavController())
}