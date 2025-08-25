package com.kssidll.arru.domain

// TODO Remove when reading application supported locales is possible on API < 34
// for same reason [Locale] doesn't work as intended
/**
 * Application supported locales
 *
 * @param tag Language tag of the locale
 */
enum class AppLocale(val tag: String) {
    PL("pl-PL"),
    EN_US("en-US"),
    TR("tr-TR"),
}
