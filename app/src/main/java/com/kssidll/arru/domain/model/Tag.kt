package com.kssidll.arru.domain.model

import android.util.Log
import androidx.compose.runtime.Composable
import com.kssidll.arru.data.data.ColorPair
import com.kssidll.arru.data.data.TagColor
import com.kssidll.arru.data.data.TagEntity
import com.kssidll.arru.data.data.TagEntity.Companion.TAG
import com.kssidll.arru.helper.generateRandomLongValue
import com.kssidll.arru.helper.generateRandomStringValue

@Composable
fun TagEntity.asTag(): Tag {
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
        color = color?.asColor() ?: TagColor.getByOrdinal(0)!!
            .asColor(),
        childrenTags = emptyList(),
    )
}

data class Tag(
    val id: Long,
    val name: String,
    val color: ColorPair,
    val childrenTags: List<Tag>
) {
    companion object {
        @Composable
        fun generate(): Tag {
            return Tag(
                id = generateRandomLongValue(),
                name = generateRandomStringValue(),
                color = TagColor.random()
                    .asColor(),
                childrenTags = emptyList(),
            )
        }

        @Composable
        fun generateList(): List<Tag> {
            return List(10) {
                this.generate()
            }.plus(
                Tag(
                    id = TagEntity.System.SHOP.id,
                    name = TagEntity.System.SHOP.name,
                    color = TagEntity.System.SHOP.color.asColor(),
                    childrenTags = List(6) {
                        this.generate()
                    }
                )
            )
                .plus(
                    Tag(
                        id = TagEntity.System.PRODUCT.id,
                        name = TagEntity.System.PRODUCT.name,
                        color = TagEntity.System.PRODUCT.color.asColor(),
                        childrenTags = List(6) {
                            this.generate()
                        }
                    )
                )
                .plus(
                    Tag(
                        id = TagEntity.System.PRODUCER.id,
                        name = TagEntity.System.PRODUCER.name,
                        color = TagEntity.System.PRODUCER.color.asColor(),
                        childrenTags = List(6) {
                            this.generate()
                        }
                    )
                )
                .plus(
                    Tag(
                        id = TagEntity.System.VARIANT.id,
                        name = TagEntity.System.VARIANT.name,
                        color = TagEntity.System.VARIANT.color.asColor(),
                        childrenTags = List(6) {
                            this.generate()
                        }
                    )
                )
        }
    }
}