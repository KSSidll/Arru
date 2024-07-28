package com.kssidll.arru.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kssidll.arru.data.data.TagColor
import com.kssidll.arru.data.data.TagEntity

val MIGRATION_5_6 = object: Migration(
    5,
    6
) {
    override fun migrate(db: SupportSQLiteDatabase) {

        /// Stage 1: Remove TransactionBasketItem
        /// Move transactionId to Item

        db.execSQL(
            """
            CREATE TABLE tmp_item (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                transactionId INTEGER NOT NULL,
                productId INTEGER NOT NULL,
                variantId INTEGER,
                quantity INTEGER NOT NULL,
                price INTEGER NOT NULL,
                FOREIGN KEY(transactionId) REFERENCES TransactionEntity(id) ON UPDATE CASCADE ON DELETE CASCADE,
                FOREIGN KEY(productId) REFERENCES Product(id) ON UPDATE CASCADE ON DELETE CASCADE,
                FOREIGN KEY(variantId) REFERENCES ProductVariant(id) ON UPDATE CASCADE ON DELETE CASCADE
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO tmp_item (transactionId, productId, variantId, quantity, price)
            SELECT TransactionBasketItem.transactionBasketId, productId, variantId, quantity, price
            FROM Item
            JOIN TransactionBasketItem ON Item.id = TransactionBasketItem.itemId
        """.trimIndent()
        )

        db.execSQL(
            """
            DROP TABLE Item
        """.trimIndent()
        )

        db.execSQL(
            """
            DROP TABLE TransactionBasketItem
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ItemEntity_transactionId ON tmp_item (transactionId)
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ItemEntity_productId ON tmp_item (productId)
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ItemEntity_variantId ON tmp_item (variantId)
        """.trimIndent()
        )

        /// Stage 1.5: Rename Item to ItemEntity

        db.execSQL(
            """
            ALTER TABLE tmp_item RENAME TO ItemEntity
        """.trimIndent()
        )

        /// Stage 2: Change constraints
        /// Using Restrict was bug prone and Cascade should work better with a tag system

        db.execSQL(
            """
            CREATE TABLE tmp_transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                date INTEGER NOT NULL,
                shopId INTEGER,
                totalCost INTEGER NOT NULL,
                FOREIGN KEY(shopId) REFERENCES Shop(id) ON UPDATE CASCADE ON DELETE SET NULL
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO tmp_transactions (id, date, shopId, totalCost)
            SELECT id, date, shopId, totalCost
            FROM TransactionBasket
        """.trimIndent()
        )

        db.execSQL(
            """
            DROP TABLE TransactionBasket
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_TransactionEntity_date ON tmp_transactions (date)
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_TransactionEntity_shopId ON tmp_transactions (shopId)
        """.trimIndent()
        )

        db.execSQL(
            """
            ALTER TABLE tmp_transactions RENAME TO TransactionEntity
        """.trimIndent()
        )

        /// Stage 3: Add tag tables
        /// TagEntity, ItemTagEntity, TransactionTagEntity and TagTagEntity

        db.execSQL(
            """
            CREATE TABLE TagEntity (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                colorOrdinal INTEGER NOT NULL
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE ItemTagEntity (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                itemId INTEGER NOT NULL,
                tagId INTEGER NOT NULL,
                FOREIGN KEY(itemId) REFERENCES ItemEntity(id) ON UPDATE CASCADE ON DELETE CASCADE,
                FOREIGN KEY(tagId) REFERENCES TagEntity(id) ON UPDATE CASCADE ON DELETE CASCADE
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ItemTagEntity_itemId ON ItemTagEntity (itemId)
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ItemTagEntity_tagId ON ItemTagEntity (tagId)
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE TransactionTagEntity (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                transactionId INTEGER NOT NULL,
                tagId INTEGER NOT NULL,
                FOREIGN KEY(transactionId) REFERENCES TransactionEntity(id) ON UPDATE CASCADE ON DELETE CASCADE,
                FOREIGN KEY(tagId) REFERENCES TagEntity(id) ON UPDATE CASCADE ON DELETE CASCADE
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_TransactionTagEntity_transactionId ON TransactionTagEntity (transactionId)
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_TransactionTagEntity_tagId ON TransactionTagEntity (tagId)
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE TagTagEntity (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                mainTagId INTEGER NOT NULL,
                subTagId INTEGER NOT NULL,
                FOREIGN KEY(mainTagId) REFERENCES TagEntity(id) ON UPDATE CASCADE ON DELETE CASCADE,
                FOREIGN KEY(subTagId) REFERENCES TagEntity(id) ON UPDATE CASCADE ON DELETE CASCADE
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_TagTagEntity_mainTagId ON TagTagEntity (mainTagId)
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_TagTagEntity_subTagId ON TagTagEntity (subTagId)
        """.trimIndent()
        )

        /// Stage 4: Migrate Shop data

        // Add special/system shop tag

        db.execSQL(
            """
            INSERT INTO TagEntity (id, name, colorOrdinal)
            SELECT ${TagEntity.System.SHOP.id}, '${TagEntity.System.SHOP.name}', ${TagEntity.System.SHOP.color.ordinal}
        """.trimIndent()
        )

        // Add shop tags

        db.execSQL(
            """
            INSERT INTO TagEntity (name, colorOrdinal)
            SELECT name, ${TagEntity.System.SHOP.color.ordinal}
            FROM Shop
        """.trimIndent()
        )

        // Set shop tags as subtags of system shop tag

        db.execSQL(
            """
            INSERT INTO TagTagEntity (mainTagId, subTagId)
            SELECT ${TagEntity.System.SHOP.id}, TagEntity.id
            FROM TagEntity WHERE name IN (SELECT name FROM Shop)
        """.trimIndent()
        )

        // Add shop tags to the associated transactions

        val shopTagsCursor = db.query(
            """
            SELECT Shop.id, TagEntity.id
            FROM TagEntity
            JOIN Shop ON Shop.name = TagEntity.name
        """.trimIndent()
        )

        if (shopTagsCursor.moveToFirst()) {
            do {
                val shopId = shopTagsCursor.getLong(0)
                val tagId = shopTagsCursor.getLong(1)

                db.execSQL(
                    """
                    INSERT INTO TransactionTagEntity (transactionId, tagId)
                    SELECT id, $tagId FROM TransactionEntity WHERE shopId = $shopId
                """.trimIndent()
                )
            } while (shopTagsCursor.moveToNext())
        }

        shopTagsCursor.close()

        // Remove unused tags

        db.execSQL(
            """
            DELETE FROM TagEntity
            WHERE id IN (
                SELECT TagEntity.id FROM TagEntity
                JOIN TagTagEntity ON TagTagEntity.subTagId = TagEntity.id
                LEFT JOIN TransactionTagEntity ON TransactionTagEntity.tagId = TagEntity.id
                WHERE TagTagEntity.mainTagId = ${TagEntity.System.SHOP.id}
                AND TransactionTagEntity.tagId IS NULL
            )
        """.trimIndent()
        )

        /// Stage 5: Migrate ProductVariant data

        // Add special/system variant tag

        db.execSQL(
            """
            INSERT INTO TagEntity (id, name, colorOrdinal)
            SELECT ${TagEntity.System.VARIANT.id}, '${TagEntity.System.VARIANT.name}', ${TagEntity.System.VARIANT.color.ordinal}
        """.trimIndent()
        )

        // Add variant tags

        db.execSQL(
            """
            INSERT INTO TagEntity (name, colorOrdinal)
            SELECT DISTINCT(name), ${TagEntity.System.VARIANT.color.ordinal}
            FROM ProductVariant
        """.trimIndent()
        )

        // Set variant tags as subtags of system variant tag

        db.execSQL(
            """
            INSERT INTO TagTagEntity (mainTagId, subTagId)
            SELECT ${TagEntity.System.VARIANT.id}, TagEntity.id
            FROM TagEntity WHERE name IN (SELECT name FROM ProductVariant)
            AND TagEntity.id NOT IN (
                SELECT TagEntity.id
                FROM TagEntity
                JOIN TagTagEntity
                ON TagTagEntity.subTagId = TagEntity.id
                    OR TagTagEntity.mainTagId = TagEntity.id
            )
        """.trimIndent()
        )

        // Add variant tags to the associated items

        val variantTagsCursor = db.query(
            """
            SELECT ProductVariant.id, TagEntity.id
            FROM ProductVariant
            JOIN TagEntity ON TagEntity.name = ProductVariant.name
            AND TagEntity.id IN (
                SELECT TagTagEntity.subTagId
                FROM TagTagEntity
                WHERE TagTagEntity.mainTagId = ${TagEntity.System.VARIANT.id}
            )
        """.trimIndent()
        )

        if (variantTagsCursor.moveToFirst()) {
            do {
                val variantId = variantTagsCursor.getLong(0)
                val tagId = variantTagsCursor.getLong(1)

                db.execSQL(
                    """
                    INSERT INTO ItemTagEntity (itemId, tagId)
                    SELECT id, $tagId FROM ItemEntity WHERE variantId = $variantId
                """.trimIndent()
                )
            } while (variantTagsCursor.moveToNext())
        }

        variantTagsCursor.close()

        // Remove unused tags

        db.execSQL(
            """
            DELETE FROM TagEntity
            WHERE id IN (
                SELECT TagEntity.id FROM TagEntity
                JOIN TagTagEntity ON TagTagEntity.subTagId = TagEntity.id
                LEFT JOIN ItemTagEntity ON ItemTagEntity.tagId = TagEntity.id
                WHERE TagTagEntity.mainTagId = ${TagEntity.System.VARIANT.id}
                AND ItemTagEntity.tagId IS NULL
            )
        """.trimIndent()
        )

        // Remove redundant variantId column from ItemEntity

        db.execSQL(
            """
            CREATE TABLE tmp_item (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                transactionId INTEGER NOT NULL,
                productId INTEGER NOT NULL,
                quantity INTEGER NOT NULL,
                price INTEGER NOT NULL,
                FOREIGN KEY(transactionId) REFERENCES TransactionEntity(id) ON UPDATE CASCADE ON DELETE CASCADE,
                FOREIGN KEY(productId) REFERENCES Product(id) ON UPDATE CASCADE ON DELETE CASCADE
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO tmp_item (transactionId, productId, quantity, price)
            SELECT transactionId, productId, quantity, price
            FROM ItemEntity
        """.trimIndent()
        )

        db.execSQL(
            """
            DROP TABLE ItemEntity
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ItemEntity_transactionId ON tmp_item (transactionId)
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ItemEntity_productId ON tmp_item (productId)
        """.trimIndent()
        )

        db.execSQL(
            """
            ALTER TABLE tmp_item RENAME TO ItemEntity
        """.trimIndent()
        )

        /// Stage 6: Migrate ProductProducer data

        // Add special/system producer tag

        db.execSQL(
            """
            INSERT INTO TagEntity (id, name, colorOrdinal)
            SELECT ${TagEntity.System.PRODUCER.id}, '${TagEntity.System.PRODUCER.name}', ${TagEntity.System.PRODUCER.color.ordinal}
        """.trimIndent()
        )

        // Add producer tags

        db.execSQL(
            """
            INSERT INTO TagEntity (name, colorOrdinal)
            SELECT DISTINCT(name), ${TagEntity.System.PRODUCER.color.ordinal}
            FROM ProductProducer
        """.trimIndent()
        )

        // Set producer tags as subtags of system producer tag

        db.execSQL(
            """
            INSERT INTO TagTagEntity (mainTagId, subTagId)
            SELECT ${TagEntity.System.PRODUCER.id}, TagEntity.id
            FROM TagEntity WHERE name IN (SELECT name FROM ProductProducer)
            AND TagEntity.id NOT IN (
                SELECT TagEntity.id
                FROM TagEntity
                JOIN TagTagEntity
                ON TagTagEntity.subTagId = TagEntity.id
                    OR TagTagEntity.mainTagId = TagEntity.id
            )
        """.trimIndent()
        )

        // Add producer tags to the associated items

        val producerTagsCursor = db.query(
            """
            SELECT ProductProducer.id, TagEntity.id
            FROM ProductProducer
            JOIN TagEntity ON TagEntity.name = ProductProducer.name
            AND TagEntity.id IN (
                SELECT TagTagEntity.subTagId
                FROM TagTagEntity
                WHERE TagTagEntity.mainTagId = ${TagEntity.System.PRODUCER.id}
            )
        """.trimIndent()
        )

        if (producerTagsCursor.moveToFirst()) {
            do {
                val producerId = producerTagsCursor.getLong(0)
                val tagId = producerTagsCursor.getLong(1)

                db.execSQL(
                    """
                    INSERT INTO ItemTagEntity (itemId, tagId)
                    SELECT ItemEntity.id, $tagId
                    FROM ItemEntity
                    JOIN Product ON Product.id = ItemEntity.productId
                    JOIN ProductProducer ON ProductProducer.id = Product.producerId
                    WHERE ProductProducer.id = $producerId
                """.trimIndent()
                )
            } while (producerTagsCursor.moveToNext())
        }

        producerTagsCursor.close()

        // Remove unused tags

        db.execSQL(
            """
            DELETE FROM TagEntity
            WHERE id IN (
                SELECT TagEntity.id FROM TagEntity
                JOIN TagTagEntity ON TagTagEntity.subTagId = TagEntity.id
                LEFT JOIN ItemTagEntity ON ItemTagEntity.tagId = TagEntity.id
                WHERE TagTagEntity.mainTagId = ${TagEntity.System.PRODUCER.id}
                AND ItemTagEntity.tagId IS NULL
            )
        """.trimIndent()
        )

        /// Stage 7: Migrate Product data

        // Add special/system product tag

        db.execSQL(
            """
            INSERT INTO TagEntity (id, name, colorOrdinal)
            SELECT ${TagEntity.System.PRODUCT.id}, '${TagEntity.System.PRODUCT.name}', ${TagEntity.System.PRODUCT.color.ordinal}
        """.trimIndent()
        )

        // Add product tags

        db.execSQL(
            """
            INSERT INTO TagEntity (name, colorOrdinal)
            SELECT DISTINCT(name), ${TagEntity.System.PRODUCT.color.ordinal}
            FROM Product
        """.trimIndent()
        )

        // Set product tags as subtags of system product tag

        db.execSQL(
            """
            INSERT INTO TagTagEntity (mainTagId, subTagId)
            SELECT ${TagEntity.System.PRODUCT.id}, TagEntity.id
            FROM TagEntity WHERE name IN (SELECT name FROM Product)
            AND TagEntity.id NOT IN (
                SELECT TagEntity.id
                FROM TagEntity
                JOIN TagTagEntity
                ON TagTagEntity.subTagId = TagEntity.id
                    OR TagTagEntity.mainTagId = TagEntity.id
            )
        """.trimIndent()
        )

        // Add product tags to the associated items

        val productTagsCursor = db.query(
            """
            SELECT Product.id, TagEntity.id
            FROM Product
            JOIN TagEntity ON TagEntity.name = Product.name
            AND TagEntity.id IN (
                SELECT TagTagEntity.subTagId
                FROM TagTagEntity
                WHERE TagTagEntity.mainTagId = ${TagEntity.System.PRODUCT.id}
            )
        """.trimIndent()
        )

        if (productTagsCursor.moveToFirst()) {
            do {
                val productId = productTagsCursor.getLong(0)
                val tagId = productTagsCursor.getLong(1)

                db.execSQL(
                    """
                    INSERT INTO ItemTagEntity (itemId, tagId)
                    SELECT ItemEntity.id, $tagId
                    FROM ItemEntity
                    JOIN Product ON Product.id = ItemEntity.productId
                    WHERE Product.id = $productId
                """.trimIndent()
                )
            } while (productTagsCursor.moveToNext())
        }

        productTagsCursor.close()

        // Remove unused tags

        db.execSQL(
            """
            DELETE FROM TagEntity
            WHERE id IN (
                SELECT TagEntity.id FROM TagEntity
                JOIN TagTagEntity ON TagTagEntity.subTagId = TagEntity.id
                LEFT JOIN ItemTagEntity ON ItemTagEntity.tagId = TagEntity.id
                WHERE TagTagEntity.mainTagId = ${TagEntity.System.PRODUCT.id}
                AND ItemTagEntity.tagId IS NULL
            )
        """.trimIndent()
        )

        /// Stage 8: Migrate ProductCategory data

        // Add category tags

        db.execSQL(
            """
            INSERT INTO TagEntity (name, colorOrdinal)
            SELECT DISTINCT(name), ${TagColor.Secondary.ordinal}
            FROM ProductCategory
        """.trimIndent()
        )

        // Add category tags to the associated items

        val categoryTagsCursor = db.query(
            """
            SELECT ProductCategory.id, TagEntity.id
            FROM ProductCategory
            JOIN TagEntity ON TagEntity.name = ProductCategory.name
            AND TagEntity.id NOT IN (
                SELECT TagTagEntity.subTagId
                FROM TagTagEntity
            )
        """.trimIndent()
        )

        if (categoryTagsCursor.moveToFirst()) {
            do {
                val categoryId = categoryTagsCursor.getLong(0)
                val tagId = categoryTagsCursor.getLong(1)

                db.execSQL(
                    """
                    INSERT INTO ItemTagEntity (itemId, tagId)
                    SELECT ItemEntity.id, $tagId
                    FROM ItemEntity
                    JOIN Product ON Product.id = ItemEntity.productId
                    JOIN ProductCategory ON ProductCategory.id = Product.categoryId
                    WHERE ProductCategory.id = $categoryId
                """.trimIndent()
                )
            } while (categoryTagsCursor.moveToNext())
        }

        categoryTagsCursor.close()

        // Remove unused tags

        db.execSQL(
            """
            DELETE FROM TagEntity
            WHERE id IN (
                SELECT TagEntity.id FROM TagEntity
                LEFT JOIN ItemTagEntity ON ItemTagEntity.tagId = TagEntity.id
                WHERE TagEntity.id NOT IN (
                    SELECT TagTagEntity.subTagId
                    FROM TagTagEntity
                )
                AND TagEntity.id NOT IN (
                    SELECT TagTagEntity.mainTagId
                    FROM TagTagEntity
                )
                AND ItemTagEntity.tagId IS NULL
            )
        """.trimIndent()
        )

        /// Stage 9: Update tables format

        // Remove redundant productId column from ItemEntity

        db.execSQL(
            """
            CREATE TABLE tmp_item (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                transactionId INTEGER NOT NULL,
                quantity INTEGER NOT NULL,
                price INTEGER NOT NULL,
                FOREIGN KEY(transactionId) REFERENCES TransactionEntity(id) ON UPDATE CASCADE ON DELETE CASCADE
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO tmp_item (transactionId, quantity, price)
            SELECT transactionId, quantity, price
            FROM ItemEntity
        """.trimIndent()
        )

        db.execSQL(
            """
            DROP TABLE ItemEntity
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ItemEntity_transactionId ON tmp_item (transactionId)
        """.trimIndent()
        )

        db.execSQL(
            """
            ALTER TABLE tmp_item RENAME TO ItemEntity
        """.trimIndent()
        )

        // Remove redundant shopId column from TransactionEntity

        db.execSQL(
            """
            CREATE TABLE tmp_transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                date INTEGER NOT NULL,
                totalCost INTEGER NOT NULL
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO tmp_transactions (id, date, totalCost)
            SELECT id, date, totalCost
            FROM TransactionEntity
        """.trimIndent()
        )

        db.execSQL(
            """
            DROP TABLE TransactionEntity
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_TransactionEntity_date ON tmp_transactions (date)
        """.trimIndent()
        )

        db.execSQL(
            """
            ALTER TABLE tmp_transactions RENAME TO TransactionEntity
        """.trimIndent()
        )

        /// Stage 10: Delete redundant tables

        db.execSQL(
            """
            DROP TABLE Product
        """.trimIndent()
        )

        db.execSQL(
            """
            DROP TABLE ProductAltName
        """.trimIndent()
        )

        db.execSQL(
            """
            DROP TABLE ProductVariant
        """.trimIndent()
        )

        db.execSQL(
            """
            DROP TABLE ProductCategory
        """.trimIndent()
        )

        db.execSQL(
            """
            DROP TABLE ProductCategoryAltName
        """.trimIndent()
        )

        db.execSQL(
            """
            DROP TABLE Shop
        """.trimIndent()
        )

        db.execSQL(
            """
            DROP TABLE ProductProducer
        """.trimIndent()
        )
    }
}
