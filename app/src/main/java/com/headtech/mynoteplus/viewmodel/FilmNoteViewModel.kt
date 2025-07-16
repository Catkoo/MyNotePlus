package com.headtech.mynoteplus.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.headtech.mynoteplus.model.FilmNote
import java.util.UUID

class FilmNoteViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _filmNotes = mutableStateListOf<FilmNote>()
    val filmNotes: List<FilmNote> get() = _filmNotes

    fun addFilmNote(note: FilmNote) {
        val id = if (note.id.isBlank()) UUID.randomUUID().toString() else note.id
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // 💡 Pastikan ownerUid benar diisi ulang (agar tidak null saat update)
        val noteWithId = note.copy(id = id, ownerUid = uid)

        db.collection("users").document(uid)
            .collection("film_notes").document(id)
            .set(noteWithId)
    }

    fun updateFilmNote(note: FilmNote) {
        addFilmNote(note) // Karena .set() akan otomatis update jika dokumen sudah ada
    }

    fun deleteFilmNote(note: FilmNote) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("users").document(uid)
            .collection("film_notes").document(note.id)
            .delete()
    }

    fun getFilmNoteById(id: String, onResult: (FilmNote?) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("users").document(uid)
            .collection("film_notes").document(id)
            .get()
            .addOnSuccessListener {
                val note = it.toObject(FilmNote::class.java)
                onResult(note)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun startFilmNoteListener() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("users").document(uid)
            .collection("film_notes")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                _filmNotes.clear()
                for (doc in snapshot.documents) {
                    val film = doc.toObject(FilmNote::class.java)

                    // 🔍 Tambahkan ini untuk debug di Logcat
                    println("🔥 ${film?.title} -> isFinished: ${film?.isFinished}")

                    film?.let { _filmNotes.add(it) }
                }
            }
    }
}
