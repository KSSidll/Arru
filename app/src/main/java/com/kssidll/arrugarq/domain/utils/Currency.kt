package com.kssidll.arrugarq.domain.utils

import android.icu.util.Currency
import java.text.*
import java.util.*

fun Long.formatToCurrency(
    locale: Locale = Locale.getDefault(),
): String {
    val dropAmount = Currency.getInstance(locale).defaultFractionDigits + 1
    return NumberFormat.getCurrencyInstance(locale)
        .format(this)
        .dropLast(dropAmount)
}

fun Float.formatToCurrency(
    locale: Locale = Locale.getDefault(),
    dropDecimal: Boolean = false,
): String {
    val dropAmount =
        if (dropDecimal) Currency.getInstance(Locale.getDefault()).defaultFractionDigits + 1 else 0
    return NumberFormat.getCurrencyInstance(locale)
        .format(this)
        .dropLast(dropAmount)
}
