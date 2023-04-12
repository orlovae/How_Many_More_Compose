package ru.alexandrorlov.howmanymore.model

import java.util.*

data class User(
    val sex: Sex,
    val birthday: Calendar,
    val country: String
)
