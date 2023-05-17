package com.example.clockapp

sealed class Weather(open val temperature: Int) {
    data class Windy(override val temperature: Int) : Weather(temperature)
    data class Cloud(override val temperature: Int) : Weather(temperature)
    data class Rain(override val temperature: Int) : Weather(temperature)
    data class Snowy(override val temperature: Int) : Weather(temperature)
    data class Sunny(override val temperature: Int) : Weather(temperature)
}
