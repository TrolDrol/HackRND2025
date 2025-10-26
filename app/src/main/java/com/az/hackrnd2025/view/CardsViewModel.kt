package com.az.hackrnd2025.view

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.az.hackrnd2025.serverwork.getCardsSW
import kotlinx.serialization.Serializable
import kotlin.math.sqrt

@Serializable
data class LocationCard(
    var name: String,
    var description: String,
    var latitude: Double,
    var longitude: Double,
    var address: String,
    var category: String
)

data class CardsScreenState(
    val availableCards: List<LocationCard> = emptyList(),
    val selectedCards: List<LocationCard> = emptyList(),
    val isAddingMode: Boolean = false
)

class CardsViewModel : ViewModel() {
    private val _state = mutableStateOf(CardsScreenState())
    val state: MutableState<CardsScreenState> = _state

    init {
        val availableCards = listOf(
            LocationCard("Парк Горького", "Красивый центральный парк", 55.7289, 37.6014, "", ""),
            LocationCard("Красная площадь", "Главная площадь Москвы", 55.7539, 37.6208, "", ""),
            LocationCard("ВДНХ", "Выставка достижений народного хозяйства", 55.8263, 37.6313, "", ""),
            LocationCard("Арбат", "Пешеходная улица в центре", 55.7500, 37.5833, "", ""),
            LocationCard("Зарядье", "Современный парк у Кремля", 55.7512, 37.6290, "", "")
        )
        _state.value = _state.value.copy(availableCards = availableCards)
    }

    fun loadCardsFromDB(context: Context) {
        val email = context
            .getSharedPreferences("User", MODE_PRIVATE)
            .getString("Email", "")
        println(email)
        getCardsSW(
            email!!,
            context,
        ) { list: List<LocationCard> ->
            state.value = state.value.copy(
                availableCards = list
            )
        }
    }

    fun clear() {
        _state.value = _state.value.copy(
            availableCards = listOf(),
            selectedCards = listOf(),
            isAddingMode = false
        )
    }

    fun addCard(card: LocationCard) {
        val currentSelected = _state.value.selectedCards.toMutableList()
        currentSelected.add(card)

        val sortedCards = sortCardsByOptimalRoute(currentSelected)
        _state.value = _state.value.copy(
            selectedCards = sortedCards,
            isAddingMode = false
        )
    }

    fun removeCard(card: LocationCard) {
        val updatedCards = _state.value.selectedCards.toMutableList().apply {
            remove(card)
        }
        _state.value = _state.value.copy(selectedCards = updatedCards)
    }

    fun startAddingMode() {
        _state.value = _state.value.copy(isAddingMode = true)
    }

    fun cancelAddingMode() {
        _state.value = _state.value.copy(isAddingMode = false)
    }

    private fun sortCardsByOptimalRoute(cards: List<LocationCard>): List<LocationCard> {
        if (cards.size <= 2) return cards

        // Алгоритм поиска оптимального маршрута (упрощенная версия)
        return findOptimalRoute(cards)
    }

    private fun findOptimalRoute(cards: List<LocationCard>): List<LocationCard> {
        // Используем жадный алгоритм для нахождения приблизительно оптимального маршрута
        val remaining = cards.toMutableList()
        val route = mutableListOf<LocationCard>()

        // Начинаем с первой карточки
        route.add(remaining.removeAt(0))

        while (remaining.isNotEmpty()) {
            val lastCard = route.last()
            val nearestIndex = findNearestCardIndex(lastCard, remaining)
            route.add(remaining.removeAt(nearestIndex))
        }

        return route
    }

    private fun findNearestCardIndex(from: LocationCard, to: List<LocationCard>): Int {
        var minDistance = Double.MAX_VALUE
        var nearestIndex = 0

        to.forEachIndexed { index, card ->
            val distance = calculateDistance(from, card)
            if (distance < minDistance) {
                minDistance = distance.toDouble()
                nearestIndex = index
            }
        }

        return nearestIndex
    }

    internal fun calculateDistance(card1: LocationCard, card2: LocationCard): Double {
        // Упрощенное вычисление расстояния (в реальном приложении используйте более точные методы)
        val latDiff = card1.latitude - card2.latitude
        val lonDiff = card1.longitude - card2.longitude
        return sqrt(latDiff * latDiff + lonDiff * lonDiff)
    }
}