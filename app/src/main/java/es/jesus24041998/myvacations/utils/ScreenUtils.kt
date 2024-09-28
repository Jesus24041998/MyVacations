package es.jesus24041998.myvacations.utils

import android.content.res.Configuration


fun ellipsizeTextScreen(string: String,configuration: Configuration): String {
    val screenWidth = configuration.screenWidthDp
    val maxLength = when {
        screenWidth < 360 -> 10 // Para pantallas pequeñas
        screenWidth < 600 -> 15 // Para pantallas medianas
        else -> 20 // Para pantallas grandes
    }

    return if (string.length > maxLength) {
        string.take(maxLength) + "..."
    } else {
        string
    }
}