package ru.alexandrorlov.howmanymore.model

data class Screen(
    val height: Int,
    val width: Int,
    val topBar: Bar,
    val bottomBar: Bar
) {
    private val heightDraw = height - topBar.height - bottomBar.height

    private val weightDraw = width - topBar.width - bottomBar.width
}
