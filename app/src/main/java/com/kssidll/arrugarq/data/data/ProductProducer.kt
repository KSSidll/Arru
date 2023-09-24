package com.kssidll.arrugarq.data.data

import androidx.room.*
import me.xdrop.fuzzywuzzy.*

@Entity(
    indices = [
        Index(
            value = ["name"],
            unique = true
        )
    ]
)
data class ProductProducer(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
): IFuzzySearchable {
    constructor(
        name: String,
    ): this(
        0,
        name
    )

    override fun getFuzzyScore(query: String): Int {
        return FuzzySearch.extractOne(
            query,
            listOf(name)
        ).score
    }

}
