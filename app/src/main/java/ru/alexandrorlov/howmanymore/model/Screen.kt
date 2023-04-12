package ru.alexandrorlov.howmanymore.model

data class Screen(
    val height: Int = 0,
    val width: Int = 0,
    val density: Int = 0,
    val topBar: Bar,
    val bottomBar: Bar
) {
    private val heightDraw = height - topBar.height - bottomBar.height

    private val weightDraw = width - topBar.width - bottomBar.width
}
