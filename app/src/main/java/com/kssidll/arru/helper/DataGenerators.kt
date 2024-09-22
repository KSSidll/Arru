package com.kssidll.arru.helper

import android.annotation.SuppressLint
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

private val defaultTimeFrom: Long = Date.valueOf("2020-01-01").time
private val defaultTimeUntil: Long = Date.valueOf("2025-12-31").time
private const val defaultDateStringFormatting: String = "yyyy-MM-dd"

@SuppressLint("ConstantLocale")
private val defaultLocale: Locale = Locale.getDefault()
private const val defaultStringLength: Int = 10
private const val defaultStringLengthFrom: Int = 4
private const val defaultStringLengthUntil: Int = 12
private const val defaultStringAllowedCharacters: String = "pyfgcrlaoeuidhtnsqjkxbmwvz"
private const val defaultLongValueFrom: Long = 10000
private const val defaultLongValueUntil: Long = 100000
private const val defaultFloatDivisionFactor: Long = 100

fun generateRandomTime(
    timeFrom: Long = defaultTimeFrom,
    timeUntil: Long = defaultTimeUntil,
): Long {
    return Random.nextLong(
        from = (timeFrom / 86400000),
        until = (timeUntil / 86400000),
    ) * 86400000
}

fun generateRandomDate(
    timeFrom: Long = defaultTimeFrom,
    timeUntil: Long = defaultTimeUntil,
): Date {
    return Date(
        generateRandomTime(
            timeFrom,
            timeUntil
        )
    )
}

fun generateRandomDateString(
    timeFrom: Long = defaultTimeFrom,
    timeUntil: Long = defaultTimeUntil,
    dateFormatting: String = defaultDateStringFormatting,
    dateLocale: Locale = defaultLocale,
): String {
    return SimpleDateFormat(
        dateFormatting,
        dateLocale
    ).format(
        generateRandomDate(
            timeFrom,
            timeUntil
        )
    )
}

fun generateRandomStringValue(
    stringLength: Int = defaultStringLength,
    allowedCharacters: String = defaultStringAllowedCharacters,
): String {
    return List(stringLength) {
        allowedCharacters[Random.nextInt(allowedCharacters.length)]
    }.toCharArray()
        .concatToString()
}

fun generateRandomStringValue(
    stringLengthFrom: Int = defaultStringLengthFrom,
    stringLengthUntil: Int = defaultStringLengthUntil,
    allowedCharacters: String = defaultStringAllowedCharacters,
): String {
    return generateRandomStringValue(
        stringLength = Random.nextInt(
            stringLengthFrom,
            stringLengthUntil
        ),
        allowedCharacters = allowedCharacters,
    )
}

fun generateRandomLongValue(
    valueFrom: Long = defaultLongValueFrom,
    valueUntil: Long = defaultLongValueUntil,
): Long {
    return Random.nextLong(
        from = valueFrom,
        until = valueUntil,
    )
}

fun generateRandomFloatValue(
    valueFrom: Long = defaultLongValueFrom,
    valueUntil: Long = defaultLongValueUntil,
    divisionFactor: Long = defaultFloatDivisionFactor,
): Float {
    return Random.nextLong(
        from = valueFrom,
        until = valueUntil,
    )
        .toFloat()
        .div(divisionFactor)
}