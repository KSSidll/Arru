@file:Suppress("unused")

package com.kssidll.arrugarq.domain.utils

import java.text.*
import java.util.*

fun Double.formatToCurrency(
    locale: Locale = Locale.getDefault(),
    dropDecimal: Boolean = false,
): String {
    val numberFormat = NumberFormat.getCurrencyInstance(locale)

    if (dropDecimal) numberFormat.maximumFractionDigits = 0

    return numberFormat.format(this)
}

fun Float.formatToCurrency(
    locale: Locale = Locale.getDefault(),
    dropDecimal: Boolean = false,
): String {
    val numberFormat = NumberFormat.getCurrencyInstance(locale)

    if (dropDecimal) numberFormat.maximumFractionDigits = 0

    return numberFormat.format(this)
}

fun Long.formatToCurrency(
    locale: Locale = Locale.getDefault(),
): String {
    return this.toDouble()
        .formatToCurrency(
            locale,
            dropDecimal = true
        )
}
