package com.kssidll.arru.helper

sealed class RegexHelper {
    companion object {
        /**
         * Checks whether [value] is a representation of digits optionally divided by some other
         * character
         *
         * @param value Value to check
         * @param decimalPoints Optional max amount of digits after the non digit character, null
         *   means no limit
         * @return True if [value] is a representation of digits optionally divided by some other
         *   character, False otherwise
         */
        fun isFloat(value: String, decimalPoints: Int? = null): Boolean {
            if (decimalPoints == null) {
                return value.matches(Regex("\\d+?\\D?\\d*"))
            }

            return value.matches(Regex("\\d+?\\D?\\d{0,${decimalPoints}}"))
        }
    }
}
