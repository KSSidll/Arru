package com.kssidll.arru.data.data

import java.io.File


/**
 * An abstraction representing a database backup file
 */
data class DatabaseBackup(
    val file: File,
    val name: String,
    val time: Long,
    val totalTransactions: Int,
    val totalSpending: Long,
    val locked: Boolean,

    val hasLockMarkInName: Boolean,
) {
    companion object {
        private const val FILE_NAME_SEPARATOR: Char = '_'
        private const val FILE_PREFIX: String =
            "bak${FILE_NAME_SEPARATOR}arru${FILE_NAME_SEPARATOR}db"

        /**
         * Create name for a database with set [time] and [totalTransactions]
         *
         * The created name does not include the .db extension
         * @param time interpreted as database backup creation time
         * @param totalTransactions interpreted as the amount of transactions in the database
         * @param totalSpending interpreted as the total spent from the transactions in the database
         * @param locked interpreted as whether the backup is locked or not
         * @return name of the database stamped with [time] and [totalTransactions]
         */
        fun makeName(
            time: Long,
            totalTransactions: Int,
            totalSpending: Long,
            locked: Boolean,
        ): String {
            return StringBuilder().apply {
                append(FILE_PREFIX)
                append(FILE_NAME_SEPARATOR)
                append(totalTransactions.toString())
                append(FILE_NAME_SEPARATOR)
                append(totalSpending.toString())
                append(FILE_NAME_SEPARATOR)
                append(time.toString())
                append(FILE_NAME_SEPARATOR)
                append(locked.toString())
            }
                .toString()
        }

        /**
         * Create a [DatabaseBackup] from a database backup [File]
         * @param file [File] representing the database backup to create the [DatabaseBackup] from
         * @return [DatabaseBackup] representing [file]
         * @exception IllegalStateException when parsing fails
         */
        fun fromFile(file: File): DatabaseBackup {
            if (file.extension != "db") error("tried to create a database file representation from a file that isn't a database")
            val name = file.nameWithoutExtension

            return DatabaseBackup(
                file = file,
                name = name,
                time = timeFromName(name)
                    ?: error("couldn't parse time from name, naming schema must have changed without updating parsing logic"),
                totalTransactions = transactionsFromName(name)
                    ?: error("couldn't parse transactions from name, naming schema must have changed without updating parsing logic"),
                totalSpending = spendingFromName(name)
                    ?: error("couldn't parse spending from name, naming schema must have changed without updating parsing logic"),
                locked = lockedFromName(name) == true,
                hasLockMarkInName = lockedFromName(name) != null
            )
        }

        private fun transactionsFromName(name: String): Int? {
            return name
                .split(FILE_NAME_SEPARATOR)
                .getOrNull(3)
                ?.toIntOrNull()
        }

        private fun spendingFromName(name: String): Long? {
            return name
                .split(FILE_NAME_SEPARATOR)
                .getOrNull(4)
                ?.toLongOrNull()
        }

        private fun timeFromName(name: String): Long? {
            return name
                .split(FILE_NAME_SEPARATOR)
                .getOrNull(5)
                ?.toLongOrNull()
        }

        private fun lockedFromName(name: String): Boolean? {
            return name
                .split(FILE_NAME_SEPARATOR)
                .getOrNull(6)
                ?.toBooleanStrictOrNull()
        }
    }
}