package com.headtech.mynoteplus.model

import com.google.firebase.firestore.PropertyName
import java.util.UUID

data class FilmNote(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val year: String = "",
    val media: String? = null,
    val episodeWatched: Int = 0,
    @get:PropertyName("finished") @set:PropertyName("finished")  // 🔥 ini penting!
    var isFinished: Boolean = false,
    val ownerUid: String = ""
)

