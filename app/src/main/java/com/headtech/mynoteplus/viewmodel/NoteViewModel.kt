package com.headtech.mynoteplus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.headtech.mynoteplus.model.Note
import java.util.UUID

class NoteViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _notes = mutableStateListOf<Note>()
    val notes: List<Note> get() = _notes

    fun addNote(note: Note) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val id = if (note.id.isBlank()) UUID.randomUUID().toString() else note.id
        val noteWithId = note.copy(
            id = id,
            ownerUid = uid
        )

        db.collection("users").document(uid)
            .collection("notes").document(id)
            .set(noteWithId)
    }


    fun removeNote(note: Note) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("users").document(uid)
            .collection("notes").document(note.id)
            .delete()
    }

    fun updateNote(note: Note) {
        addNote(note)
    }

    fun getNoteById(id: String, onResult: (Note?) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("users").document(uid)
            .collection("notes").document(id)
            .get()
            .addOnSuccessListener {
                val note = it.toObject(Note::class.java)
                onResult(note)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun startNoteListener() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("users").document(uid).collection("notes")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                _notes.clear()
                for (doc in snapshot.documents) {
                    doc.toObject(Note::class.java)?.let { _notes.add(it) }
                }
            }
    }
}
