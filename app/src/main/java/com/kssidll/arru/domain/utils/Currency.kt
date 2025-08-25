package com.kssidll.arru.domain.utils

import java.text.NumberFormat
import java.util.Locale

fun Float.formatToCurrency(locale: Locale, dropDecimal: Boolean = false): String {
    val numberFormat = NumberFormat.getCurrencyInstance(locale)

    if (dropDecimal) numberFormat.maximumFractionDigits = 0

    return numberFormat.format(this)
}

fun Long.formatToCurrency(locale: Locale): String {
    return toFloat().formatToCurrency(locale, dropDecimal = true)
}
