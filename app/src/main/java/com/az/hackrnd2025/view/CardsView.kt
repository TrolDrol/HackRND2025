package com.az.hackrnd2025.view

import android.content.Context.MODE_PRIVATE
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.az.hackrnd2025.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsView(viewModel: CardsViewModel = viewModel()) {
    val cardsSaving = LocalContext.current.getSharedPreferences("User", MODE_PRIVATE).getBoolean("CardsSaving", false)

    val state = viewModel.state.value

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF48B626),
            Color(0xFF0C5207)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Мои места",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                )
                        },
                actions = {
                    if (!state.isAddingMode) {
                        IconButton(
                            onClick = { viewModel.startAddingMode() },
                            content = {
                                Icon(Icons.Default.Add, contentDescription = "Добавить")
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(0xFF0C5207),
                                contentColor = Color.White
                            ))

                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        content = { padding ->
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
            }
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                if (state.availableCards.isEmpty())
                    DownloadingView()
                else if (state.isAddingMode) {
                    AddCardsScreen(
                        viewModel
                    )
                } else {
                    SelectedCardsScreen(
                        viewModel
                    )
                }
            }
        },
        floatingActionButton = {
            val context = LocalContext.current
            FloatingActionButton(
                onClick = {
                    viewModel.clear()
                    viewModel.loadCardsFromDB(context)
                          },
                content = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Refresh DB"
                    )
                },
                contentColor = Color.White,
                containerColor = Color(0xFF076E04)
            )
        }
    )

}

@Composable
fun SelectedCardsScreen(
    viewModel: CardsViewModel
) {
    val cards = viewModel.state.value.selectedCards
    Column(modifier = Modifier.fillMaxSize()) {
        if (cards.isEmpty()) {
            EmptyState(viewModel)
        } else {
            CardsList(
                cards,
                { viewModel.removeCard(it) },
                Modifier.weight(1f)
            )
        }

        // Показать информацию о маршруте, если карточек больше 1
        if (cards.size > 1) {
            RouteInfo(cards, viewModel)
        }
    }
}

@Composable
fun EmptyState(viewModel: CardsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Нет выбранных мест",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.startAddingMode()},
            content = {
                Text(
                    "Добавить первое место",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = Color(0xFF076E04)
            )
        )
    }
}

@Composable
fun CardsList(
    cards: List<LocationCard>,
    onRemoveCard: (LocationCard) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(cards.size) { card ->
            LocationCardItem(
                cards[card],
                onRemove = { onRemoveCard(cards[card]) }
            )
        }
    }
}

@Composable
fun LocationCardItem(
    card: LocationCard,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = card.name,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Координаты: ${card.latitude}, ${card.longitude}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = card.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun AddCardsScreen(
    viewModel: CardsViewModel
) {
    val availableCards = viewModel.state.value.availableCards
    val selectedCards = viewModel.state.value.selectedCards

    val availableToAdd = availableCards.filter { it !in selectedCards }

    Column(modifier = Modifier.fillMaxSize()) {
        // Заголовок
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Выберите места",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )
            TextButton(onClick = { viewModel.cancelAddingMode() }) {
                Text(
                    "Готово",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        }

        // Список доступных карточек
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(availableToAdd.size) { card ->
                AvailableCardItem(
                    availableToAdd[card],
                    { viewModel.addCard(availableToAdd[card]) }
                )
            }
        }
    }
}

@Composable
fun AvailableCardItem(
    card: LocationCard,
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.elevatedCardElevation(2.dp)
    ) {
        Column(
            Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = card.name,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = card.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onAdd,
                content = {
                    Text(
                        "Добавить",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color(0xFF076E04)
                )
            )
        }
    }
}

@Composable
fun RouteInfo(cards: List<LocationCard>,
              viewModel: CardsViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Оптимальный маршрут",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Показать порядок посещения
            cards.forEachIndexed { index, card ->
                Text(
                    "${index + 1}. ${card.name}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Показать расстояние между первым и последним
            if (cards.size >= 2) {
                Spacer(modifier = Modifier.height(8.dp))
                val distance = viewModel.calculateDistance(cards.first(), cards.last())
                Text(
                    "Расстояние между первым и последним: ${"%.2f".format(distance * 111)} км",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun DownloadingView() {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AdvancedCatLoadingAnimation(
            catPainter = painterResource(id = R.drawable.cat),
            ballPainter = painterResource(id = R.drawable.ball)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Загрузка...",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White
        )
        Text(
            "Простите, я ещё не подобрал вам места",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
        )
        Text(
            "У меня лапки",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewCardsView() {
    CardsView()
}

