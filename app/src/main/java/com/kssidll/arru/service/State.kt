package com.kssidll.arru.service

import android.app.ActivityManager
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat

/**
 * Helper function to remove the need to define the [Context.getSharedPreferences] mode as the only
 * non deprecated mode is [Context.MODE_PRIVATE]
 *
 * @return [SharedPreferences]
 */
fun Context.getSharedPreferences(name: String): SharedPreferences {
    return getSharedPreferences(name, Context.MODE_PRIVATE)
}

/** Possible states that a service can be in */
enum class ServiceState {
    STOPPED,
    STARTED,
}

/** Transforms the name of the service into its state key used in [SharedPreferences] */
private fun serviceNameToStateKey(serviceName: String): String = "${serviceName}_STATE"

/**
 * Sets the service state to provided [state]
 *
 * @param serviceName name of the service
 * @param state state to set the service to
 */
fun Context.setServiceState(serviceName: String, state: ServiceState) {
    getSharedPreferences(serviceName).edit().let {
        it.putString(serviceNameToStateKey(serviceName), state.name)
        it.commit()
    }
}

/**
 * Gets the service state of the service with provided [serviceName]
 *
 * @param serviceName name of the service
 * @return [ServiceState] of the service
 */
fun Context.getServiceState(serviceName: String): ServiceState {
    val value =
        getSharedPreferences(serviceName)
            .getString(
                serviceNameToStateKey(serviceName),
                ServiceState.STOPPED.name,
            )!! // it's impossible for this to be null, but kotlin LSP thinks otherwise? i'm
    // confused

    return ServiceState.valueOf(value)
}

/**
 * Gets the service state of the service with provided [serviceClass]
 *
 * Checks the system instead of the shared preferences so it can't be listened on and isn't
 * efficient, it also relies on a deprecated method that exists only for compatibility
 *
 * @param serviceClass class of the service
 * @return [ServiceState] of the service
 */
// we use the deprecated method for the purpose that it's backwards compatible for
@Suppress("DEPRECATION")
fun Context.getServiceStateCold(serviceClass: Class<*>): ServiceState {
    val manager = ContextCompat.getSystemService(this, ActivityManager::class.java)
    for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return ServiceState.STARTED
        }
    }
    return ServiceState.STOPPED
}
