package com.example.timeline.data.remote

import com.google.firebase.auth.FirebaseAuth

class FirebaseService {
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUser() = auth.currentUser

    fun signOut() {
        auth.signOut()
    }
}
