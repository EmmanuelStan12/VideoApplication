package com.codedev.videoapp.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AutoCompleteItem(
    @PrimaryKey val id: Int? = null,
    val text: String
)

