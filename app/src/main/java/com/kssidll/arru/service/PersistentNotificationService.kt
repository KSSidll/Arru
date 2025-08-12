package com.kssidll.arru.service

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import com.kssidll.arru.APPLICATION_NAME
import com.kssidll.arru.MainActivity
import com.kssidll.arru.R
import com.kssidll.arru.helper.checkPermission
import com.kssidll.arru.helper.getLocalizedString
import dagger.hilt.android.AndroidEntryPoint


enum class PersistentNotificationServiceActions {
    START,
    STOP
}

@AndroidEntryPoint
class PersistentNotificationService: Service() {

    private fun registerLocaleChangeReceiver() {
        val filter = IntentFilter(Intent.ACTION_LOCALE_CHANGED)
        registerReceiver(localeChangeReceiver, filter)
    }

    private val localeChangeReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_LOCALE_CHANGED) {
                updateNotificationChannel()
                updateNotification()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        Log.d(
            TAG,
            "onStartCommand: Executed with startId: $startId"
        )

        if (intent != null) {
            when (intent.action) {
                PersistentNotificationServiceActions.START.name -> startAction()
                PersistentNotificationServiceActions.STOP.name  -> stopAction()
                else                                            -> {
                    Log.e(
                        TAG,
                        "onStartCommand: No action in the received intent"
                    )
                }
            }
        } else {
            Log.e(
                TAG,
                "onStartCommand: No intent"
            )
        }

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        Log.d(
            TAG,
            "onCreate: Service created"
        )

        // Initialize receivers
        registerLocaleChangeReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(
            TAG,
            "onDestroy: Service destroyed"
        )

        unregisterReceiver(localeChangeReceiver)
    }

    private fun init() {
        createNotificationChannel()

        ServiceCompat.startForeground(
            this,
            SERVICE_NOTIFICATION_ID,
            createNotification(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            } else {
                0
            }
        )
    }

    private fun startAction() {
        Log.d(
            TAG,
            "startAction: Attempting to start"
        )

        // early return if already started
        if (getServiceState(SERVICE_NAME) == ServiceState.STARTED) {
            Log.d(
                TAG,
                "startAction: Service already set as running"
            )
            return
        }

        // set as started
        setServiceState(
            SERVICE_NAME,
            ServiceState.STARTED
        )

        Log.d(
            TAG,
            "startAction: Started"
        )

        init()
    }

    private fun stopAction() {
        Log.d(
            TAG,
            "stopAction: Stopping the service"
        )

        stop()
    }

    private fun stop() {
        Log.d(
            TAG,
            "stop: Stopping the service"
        )

        setServiceState(
            SERVICE_NAME,
            ServiceState.STOPPED
        )

        try {
            ServiceCompat.stopForeground(
                this,
                ServiceCompat.STOP_FOREGROUND_REMOVE
            )

            stopSelf()
        } catch (e: Exception) {
            Log.d(
                TAG,
                "stop: Service stopped without being started: ${e.message}"
            )
        }
    }

    /**
     * Creates the notification channel for the service
     *
     * Has to be called before any notifications can be posted
     */
    private fun createNotificationChannel() {
        Log.d(
            TAG,
            "createNotificationChannel: creating"
        )

        val notificationManager = NotificationManagerCompat.from(applicationContext)

        notificationManager.createNotificationChannel(makeNotificationChannel())
    }

    /**
     * Updates the notification channel for the service
     *
     * Changes the name and the description to current locale
     */
    private fun updateNotificationChannel() {
        Log.d(
            TAG,
            "updateNotificationChannel: updating"
        )

        val notificationManager = NotificationManagerCompat.from(applicationContext)

        notificationManager.createNotificationChannel(makeNotificationChannel())
    }

    /**
     * @return notification channel for the service
     */
    private fun makeNotificationChannel(): NotificationChannelCompat {
        Log.d(
            TAG,
            "makeNotificationChannel: making"
        )

        return NotificationChannelCompat.Builder(
            NOTIFICATION_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_LOW
        )
            .setName(getLocalizedString(R.string.service_persistent_notification_name))
            .setDescription(getLocalizedString(R.string.service_persistent_notification_description))
            .setLightsEnabled(false)
            .setVibrationEnabled(false)
            .build()
    }

    /**
     * Creates the initial notification for the service
     *
     * [createNotificationChannel] has to be called before any notifications can be posted
     * @return the initial notification
     */
    private fun createNotification(): Notification {
        Log.d(
            TAG,
            "createNotification: creating"
        )

        val pendingIntent: PendingIntent = Intent(
            this,
            MainActivity::class.java
        ).let { notificationIntent ->
            PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }

        val builder = NotificationCompat.Builder(
            applicationContext,
            NOTIFICATION_CHANNEL_ID
        )

        return builder
            .setContentTitle("$APPLICATION_NAME - ${getLocalizedString(R.string.persistent_notification_add_transaction)}")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(BitmapFactory.decodeResource(applicationContext.resources, R.mipmap.ic_launcher_round))
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }

    /**
     * Updates the notification for the service
     *
     * [createNotificationChannel] has to be called before any notifications can be posted
     */
    private fun updateNotification() {
        Log.d(
            TAG,
            "updateNotification: checking for permisson"
        )

        if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) || checkPermission(
                this,
                Permissions.NOTIFICATIONS
            )
        ) {
            Log.d(
                TAG,
                "updateNotification: updating"
            )

            val notification = createNotification()

            NotificationManagerCompat.from(this)
                .notify(
                    SERVICE_NOTIFICATION_ID,
                    notification
                )
        } else {
            Log.d(
                TAG,
                "updateNotification: no permission"
            )
        }
    }

    object Permissions {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        const val NOTIFICATIONS = Manifest.permission.POST_NOTIFICATIONS

        val ALL = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(NOTIFICATIONS)
            }
        }.toTypedArray()

        /**
         * Checks whether service required permissions are granted
         * @param context application context
         * @return whether permissions are granted
         */
        fun check(context: Context): Boolean {
            return ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) || checkPermission(
                context,
                NOTIFICATIONS
            ))
        }
    }

    companion object {
        const val TAG = "PER_NOTIF_SERVICE"
        const val SERVICE_NAME = TAG
        const val SERVICE_NOTIFICATION_ID = 2
        const val NOTIFICATION_CHANNEL_ID = "Persistent Notification"

        /**
         * Helper function to start the service
         * @param context context
         */
        fun start(context: Context) {
            Intent(
                context, PersistentNotificationService::class.java
            ).also { intent ->
                intent.action = PersistentNotificationServiceActions.START.name

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            }
        }

        /**
         * Helper function to stop the service
         * @param context context
         */
        fun stop(context: Context) {
            Intent(
                context, PersistentNotificationService::class.java
            ).also {
                it.action = PersistentNotificationServiceActions.STOP.name

                context.startService(it)
            }
        }
    }
}
