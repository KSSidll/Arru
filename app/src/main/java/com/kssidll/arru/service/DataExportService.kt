package com.kssidll.arru.service

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.IntentCompat
import com.kssidll.arru.APPLICATION_NAME
import com.kssidll.arru.MainActivity
import com.kssidll.arru.R
import com.kssidll.arru.broadcast.DataExportServiceStopActionReceiver
import com.kssidll.arru.data.database.exportDataAsRawCsv
import com.kssidll.arru.data.repository.*
import com.kssidll.arru.helper.checkPermission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Possible actions that the [DataExportService] can perform
 */
enum class DataExportServiceActions {
    START_EXPORT_CSV_RAW,
    STOP
}

@AndroidEntryPoint
class DataExportService: Service() {

    private lateinit var serviceJob: Job
    private lateinit var serviceScope: CoroutineScope

    @Inject
    lateinit var categoryRepository: CategoryRepositorySource
    @Inject
    lateinit var itemRepository: ItemRepositorySource

    @Inject
    lateinit var producerRepository: ProducerRepositorySource

    @Inject
    lateinit var productRepository: ProductRepositorySource

    @Inject
    lateinit var shopRepository: ShopRepositorySource

    @Inject
    lateinit var transactionRepository: TransactionBasketRepositorySource

    @Inject
    lateinit var variantRepository: VariantRepositorySource

    /**
     * How much data is there to export
     */
    private var totalDataSize = 0

    /**
     * How much data has already been exported
     */
    private var exportedDataSize = 0

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
                DataExportServiceActions.START_EXPORT_CSV_RAW.name -> startExportCsvRawAction(intent)
                DataExportServiceActions.STOP.name -> stopAction(intent)
                else -> Log.e(
                    TAG,
                    "onStartCommand: No action in the received intent"
                )
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

        // Initialize the scope
        serviceJob = Job()
        serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(
            TAG,
            "onDestroy: Service destroyed"
        )

        serviceJob.cancel()
    }

    private fun startExportCsvRawAction(intent: Intent) {
        Log.d(
            TAG,
            "startExportCsvRawAction: Attempting to start"
        )

        // early return if already started
        if (getServiceState(SERVICE_NAME) == ServiceState.STARTED) {
            Log.d(
                TAG,
                "startExportCsvRawAction: Service already set as running"
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
            "startExportCsvRawAction: Started"
        )

        createNotificationChannel()

        ServiceCompat.startForeground(
            this,
            SERVICE_NOTIFICATION_ID,
            createNotification(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            } else {
                0
            }
        )

        val uri = IntentCompat.getParcelableExtra(
            intent,
            URI_ID_KEY,
            Uri::class.java
        )

        serviceScope.launch {
            if (uri != null) {
                exportDataAsRawCsv(
                    context = applicationContext,
                    uri = uri,
                    categoryRepository = categoryRepository,
                    itemRepository = itemRepository,
                    producerRepository = producerRepository,
                    productRepository = productRepository,
                    shopRepository = shopRepository,
                    transactionRepository = transactionRepository,
                    variantRepository = variantRepository,
                    onMaxProgressChange = {
                        totalDataSize = it
                        updateNotification(false)
                    },
                    onProgressChange = {
                        exportedDataSize = it
                        updateNotification(false)
                    },
                    onFinished = {
                        updateNotification(true)
                    }
                )
            } else {
                Log.d(
                    TAG,
                    "startExportCsvRawAction: Didn't receive uri"
                )
            }

            stop(false)
        }
    }

    private fun stopAction(intent: Intent) {
        Log.d(
            TAG,
            "stopAction: Stopping the service"
        )

        val forced = intent.extras?.getBoolean(
            FORCED_STOP_KEY,
            false
        )!!

        stop(forced)
    }

    private fun stop(forced: Boolean) {
        Log.d(
            TAG,
            "stop: Stopping the service with forced = $forced"
        )

        setServiceState(
            SERVICE_NAME,
            ServiceState.STOPPED
        )

        try {
            if (forced) ServiceCompat.stopForeground(
                this,
                ServiceCompat.STOP_FOREGROUND_REMOVE
            )
            else ServiceCompat.stopForeground(
                this,
                ServiceCompat.STOP_FOREGROUND_DETACH
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

        val channel = NotificationChannelCompat.Builder(
            NOTIFICATION_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_LOW
        )
            .setName("Data Export") // TODO localization
            .setDescription("Data Export") // TODO localization
            .setLightsEnabled(false)
            .setVibrationEnabled(false)
            .build()

        notificationManager.createNotificationChannel(channel)
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

        // Stop data export action button
        val stopServiceIntent = Intent(
            this,
            DataExportServiceStopActionReceiver::class.java
        )

        stopServiceIntent.putExtra(
            FORCED_STOP_KEY,
            true
        )

        val stopServicePendingIntent: PendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            stopServiceIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopServiceAction = NotificationCompat.Action.Builder(
            R.drawable.ic_launcher_foreground, // TODO change to icon
            "Cancel",
            stopServicePendingIntent
        )
            .build()

        return builder
            .setContentTitle(APPLICATION_NAME)
            .setContentText("Exporting Data: $exportedDataSize/$totalDataSize") // TODO localization
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .addAction(stopServiceAction)
            .setProgress(
                totalDataSize,
                exportedDataSize,
                totalDataSize == 0
            )
            .build()
    }

    /**
     * Creates the finished notification for the service
     *
     * [createNotificationChannel] has to be called before any notifications can be posted
     * @return the finished notification
     */
    private fun createFinishedNotification(): Notification {
        Log.d(
            TAG,
            "createFinishedNotification: creating"
        )

        return NotificationCompat.Builder(
            this,
            NOTIFICATION_CHANNEL_ID
        )
            .setContentTitle(APPLICATION_NAME)
            .setContentText("Finished Exporting Data") // TODO localization
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setSilent(true)
            .setAutoCancel(true)
            .setTimeoutAfter(10000)
            .setProgress(
                0,
                0,
                false
            )
            .build()
    }

    /**
     * Updates the notification for the service
     *
     * [createNotificationChannel] has to be called before any notifications can be posted
     * @param isFinished whether the service has finished, posts finished notification if true
     */
    private fun updateNotification(isFinished: Boolean = false) {
        Log.d(
            TAG,
            "updateNotification: checking for permisson"
        )

        if (checkPermission(
                this,
                Permissions.NOTIFICATIONS
            )
        ) {
            Log.d(
                TAG,
                "updateNotification: updating with isFinished = $isFinished"
            )

            val notification =
                if (isFinished) {
                    createFinishedNotification()
                } else {
                    createNotification()
                }

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
        const val TAG = "DATA_EXPORT_SERVICE"
        const val SERVICE_NAME = TAG
        const val SERVICE_NOTIFICATION_ID = 1
        const val FORCED_STOP_KEY = "${TAG}_FORCEDSTOP"
        const val NOTIFICATION_CHANNEL_ID = "Data export"
        private const val URI_ID_KEY = "${TAG}_URI"

        /**
         * Helper function to start the service with raw csv export
         * @param context context
         * @param uri Uri of the directory to save the files into
         */
        fun startExportCsvRaw(
            context: Context,
            uri: Uri,
        ) {
            Intent(
                context,
                DataExportService::class.java
            ).also { intent ->
                intent.action = DataExportServiceActions.START_EXPORT_CSV_RAW.name

                intent.putExtra(
                    URI_ID_KEY,
                    uri
                )

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
         * @param forced whether the stop is forced by user, will detach notification if true
         */
        fun stop(
            context: Context,
            forced: Boolean = false
        ) {
            Intent(
                context,
                DataExportService::class.java
            ).also {
                it.action = DataExportServiceActions.STOP.name

                it.putExtra(
                    FORCED_STOP_KEY,
                    forced
                )

                context.startService(it)
            }
        }
    }
}