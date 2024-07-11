package com.kssidll.arru.data.data

import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

enum class TagColor {
    // **THIS WORKS ON ORDINALS**
    // **NEVER CHANGE THE ORDER OF DECLARATION**
    Primary,
    Secondary,

    ;

    @Composable
    fun asColor(): Color {
        return when (this) {
            Primary -> {
                MaterialTheme.colorScheme.primary
            }

            Secondary -> {
                MaterialTheme.colorScheme.secondary
            }
        }
    }

    companion object {
        private val idMap = TagColor.entries.associateBy { it.ordinal }
        fun getByOrdinal(ordinal: Int) = idMap[ordinal]
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

    fun asTag(): Tag {
        val color = TagColor.getByOrdinal(colorOrdinal)

        if (color == null) {
            Log.e(
                TAG,
                "No color with ordinal ${colorOrdinal}. The color saved in the database doesn't exist"
            )
        }

        return Tag(
            id = id,
            name = name,
            color = color ?: TagColor.getByOrdinal(0)!!,
        )
    }

    companion object {
        const val TAG = "TAG_ENTITY"
    }

    /**
     * Special/system [TagEntity] reserved by the app to be able to provide additional functionality
     *
     * @param id **immutable**, id reference to the database [TagEntity], starts at 9999 and goes down to 0
     * @param color **mutable, can be changed by user**, [TagColor] the tag will be displayed in
     */
    enum class System(val id: Long, val color: TagColor) {
        // **NEVER CHANGE ANY OF THOSE ITEMS, YOU CAN ONLY ADD NEW ONES**

        /**
         * [TagEntity] shop:* group tag
         * @since version 3.0.0
         */
        SHOP(9999, TagColor.Primary),

        /**
         * [TagEntity] variant:* group tag
         * @since version 3.0.0
         */
        VARIANT(9998, TagColor.Secondary),

        /**
         * [TagEntity] producer:* group tag
         * @since version 3.0.0
         */
        PRODUCER(9997, TagColor.Secondary),
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

data class Tag(
    val id: Long,
    val name: String,
    val color: TagColor,
)