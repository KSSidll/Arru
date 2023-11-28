package com.kssidll.arrugarq.helper

class StringHelper {
    companion object {
        fun toDoubleOrNull(value: String): Double? {
            return if (RegexHelper.isFloat(value)) {
                value.replace(
                    Regex("\\D"),
                    "."
                )
                    .toDoubleOrNull()
            } else null
        }
    }
}