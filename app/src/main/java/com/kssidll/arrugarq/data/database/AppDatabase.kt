package com.kssidll.arrugarq.data.database

import android.content.*
import androidx.room.*
import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.di.module.*
import java.io.*

@Database(
    version = 2,
    entities = [
        Item::class,
        Product::class,
        ProductAltName::class,
        ProductVariant::class,
        ProductCategory::class,
        ProductCategoryAltName::class,
        Shop::class,
        ProductProducer::class,
    ],
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2
        ),
    ]
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getItemDao(): ItemDao
    abstract fun getProductDao(): ProductDao
    abstract fun getVariantDao(): VariantDao
    abstract fun getCategoryDao(): CategoryDao
    abstract fun getShopDao(): ShopDao
    abstract fun getProducerDao(): ProducerDao

    companion object {
        /**
         * @param context app context
         * @param name name of the database or absolute path if not internal location, defaults to internal location with [DATABASE_NAME] name
         * @return [RoomDatabase.Builder] of [AppDatabase] for [name]
         */
        private fun builder(
            context: Context,
            name: String = DATABASE_NAME
        ): Builder<AppDatabase> {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                name
            )
        }

        /**
         * @param context app context
         * @return [AppDatabase] created in internal location with [DATABASE_NAME] name, doesn't ensure database file creation
         */
        fun buildInternal(context: Context): AppDatabase {
            return builder(context).createFromAsset("database/arrugarq.db")
                .build()
        }

        /**
         * @param context app context
         * @return [AppDatabase] created in external [Context.externalDbPath] location, doesn't ensure database file creation
         */
        fun buildExternal(context: Context): AppDatabase {
            return builder(
                context,
                context.externalDbPath()
            ).build()
        }

        /**
         * moves database files from [fromDbFile] parent directory to [toDbFile] parent directory
         * @param fromDbFile absolute path to db file from whose parent directory to move database files
         * @param toDbFile absolute path to db file to whose parent directory to move database files
         */
        fun move(
            fromDbFile: File,
            toDbFile: File
        ) {
            val fromDbWalFile = File("${fromDbFile.path}-wal")
            val fromDbShmFile = File("${fromDbFile.path}-shm")

            val toDbWalFile = File("${toDbFile.path}-wal")
            val toDbShmFile = File("${toDbFile.path}-shm")

            if (toDbWalFile.exists()) toDbWalFile.delete()
            if (toDbShmFile.exists()) toDbShmFile.delete()

            fromDbFile.copyTo(
                toDbFile,
                true
            )
            fromDbFile.delete()

            if (fromDbWalFile.exists()) {
                fromDbWalFile.copyTo(
                    toDbWalFile,
                    true
                )
                fromDbWalFile.delete()
            }

            if (fromDbShmFile.exists()) {
                fromDbShmFile.copyTo(
                    toDbShmFile,
                    true
                )
                fromDbShmFile.delete()
            }
        }

        /**
         * moves the database files from external to internal location
         * @param context app context
         */
        fun moveExternalToInternal(context: Context) {
            val externalDbFile = File(context.externalDbPath())
            val internalDbFile = context.internalDbFile()

            move(
                externalDbFile,
                internalDbFile
            )
        }

        /**
         * moves the database files from internal to external location
         * @param context app context
         */
        fun moveInternalToExternal(context: Context) {
            val externalDbFile = File(context.externalDbPath())
            val internalDbFile = context.internalDbFile()

            move(
                internalDbFile,
                externalDbFile
            )
        }
    }
}
