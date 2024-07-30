package com.kssidll.arru.data.data

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlin.random.Random

data class ColorPair(
    val containerColor: Color,
    val contentColor: Color
)

enum class TagColor {
    // **THIS WORKS ON ORDINALS**
    // **NEVER CHANGE THE ORDER OF DECLARATION**

    /**
     * [TagColor] Primary material theme tag color
     * @since version 3.0.0
     */
    Primary,

    /**
     * [TagColor] Secondary material theme tag color
     * @since version 3.0.0
     */
    Secondary,

    /**
     * [TagColor] Tertiary material theme tag color
     * @since version 3.0.0
     */
    Tertiary,

    ;

    @Composable
    fun asColor(): ColorPair {
        return when (this) {
            Primary -> {
                ColorPair(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }

            Secondary -> {
                ColorPair(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            }

            Tertiary -> {
                ColorPair(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                )
            }
        }
    }

    companion object {
        private val idMap = TagColor.entries.associateBy { it.ordinal }
        fun getByOrdinal(ordinal: Int) = idMap[ordinal]

        fun random(): TagColor {
            return this.getByOrdinal(
                Random.nextInt(
                    from = TagColor.idMap.keys.first(),
                    until = TagColor.idMap.keys.last() + 1,
                )
            )!!
        }
    }
}

@Entity
data class TagEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val colorOrdinal: Int,
) {
    constructor(
        name: String,
        color: TagColor = TagColor.getByOrdinal(0)!!,
    ): this(
        0,
        name = name,
        colorOrdinal = color.ordinal
    )

    companion object {
        const val TAG = "TAG_ENTITY"
    }

    /**
     * Special/system [TagEntity] reserved by the app to be able to provide additional functionality
     *
     * @param id **immutable**, id reference to the database [TagEntity], starts at 9999 and goes down to 0
     * @param color **mutable, can be changed by user**, [TagColor] the tag will be displayed in
     */
    enum class System(
        val id: Long,
        val color: TagColor
    ) {
        // **NEVER CHANGE ANY OF THOSE ITEMS, YOU CAN ONLY ADD NEW ONES**

        /**
         * [TagEntity] shop:* group tag
         * @since version 3.0.0
         */
        SHOP(
            9999,
            TagColor.Primary
        ),

        /**
         * [TagEntity] variant:* group tag
         * @since version 3.0.0
         */
        VARIANT(
            9998,
            TagColor.Secondary
        ),

        /**
         * [TagEntity] producer:* group tag
         * @since version 3.0.0
         */
        PRODUCER(
            9997,
            TagColor.Secondary
        ),

        /**
         * [TagEntity] product:* group tag
         * @since version 3.0.0
         */
        PRODUCT(
            9996,
            TagColor.Tertiary
        ),
    }
}

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["mainTagId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["subTagId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
    ]
)
data class TagTagEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(index = true) val mainTagId: Long,
    @ColumnInfo(index = true) val subTagId: Long,
)