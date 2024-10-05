package es.jesus24041998.myvacations.utils

import android.os.Build
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

fun dateTimeToMillis(day: Int, month: Int, year: Int) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDate.of(year, month, day).atStartOfDay(ZoneId.systemDefault()).toInstant()
            .toEpochMilli()
    } else {
        Date(year, month, day).time
    }

fun millisToDateTime(millis: Long) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toString()
    } else {
        Date(millis).toString()
    }