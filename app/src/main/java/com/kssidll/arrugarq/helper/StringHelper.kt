package com.kssidll.arrugarq.helper

class StringHelper {
    companion object {
        fun toFloatOrNull(value: String): Float? {
            return if (RegexHelper.isFloat(value)) {
                value.replace(
                    Regex("\\D"),
                    "."
                )
                    .toFloatOrNull()
            } else null
        }
    }
}