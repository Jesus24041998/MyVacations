package es.jesus24041998.myvacations.utils

import java.util.Currency
import java.util.Locale

val monedasMasFamosas = listOf(
    "EUR",
    "USD",
    "JPY",
    "GBP",
    "AUD",
    "CAD",
    "CHF",
    "CNY",
    "INR",
    "BRL"
)

fun getSymbol(currencyCode: String): String = Currency.getInstance(currencyCode).getSymbol(Locale.US)
