package com.kssidll.arrugarq.data.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
)

