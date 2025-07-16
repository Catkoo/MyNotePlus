package com.headtech.mynoteplus.model

import java.util.UUID

data class Note(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val ownerUid: String = ""
)

