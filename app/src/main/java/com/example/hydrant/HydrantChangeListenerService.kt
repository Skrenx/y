package com.example.hydrant

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HydrantChangeListenerService : Service() {

    private var hydrantChangeListener: ListenerRegistration? = null
    private var fireIncidentListener: ListenerRegistration? = null
    private val firestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate() {
        super.onCreate()
        startListeningForChanges()
        startListeningForFireIncidents() // NEW: Listen for fire incident reports
    }

    private fun startListeningForChanges() {
        val currentUserEmail = auth.currentUser?.email?.lowercase() ?: return

        // Listen for new hydrant changes
        hydrantChangeListener = firestore.collection("hydrant_changes")
            .whereEqualTo("notified", false)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("HydrantListener", "Listen failed", error)
                    return@addSnapshotListener
                }

                snapshots?.documentChanges?.forEach { change ->
                    if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                        val doc = change.document
                        val performedBy = doc.getString("performedBy")?.lowercase() ?: ""

                        // Only notify if change was made by someone else
                        if (performedBy != currentUserEmail) {
                            val changeType = doc.getString("changeType") ?: ""
                            val hydrantId = doc.getString("hydrantId") ?: ""
                            val hydrantName = doc.getString("hydrantName") ?: ""
                            val newStatus = doc.getString("newStatus") ?: ""
                            val municipality = doc.getString("municipality") ?: ""

                            // Check if current user is admin
                            firestore.collection("admins")
                                .document(currentUserEmail)
                                .get()
                                .addOnSuccessListener { adminDoc ->
                                    if (adminDoc.exists()) {
                                        // Send notification to this admin
                                        when (changeType) {
                                            "updated" -> {
                                                showHydrantUpdateNotification(
                                                    context = this,
                                                    hydrantId = hydrantId,
                                                    hydrantName = hydrantName,
                                                    newStatus = newStatus,
                                                    municipality = municipality
                                                )
                                            }
                                            "added" -> {
                                                showHydrantAddedNotification(
                                                    context = this,
                                                    municipality = municipality
                                                )
                                            }
                                            "deleted" -> {
                                                showHydrantDeletedNotification(
                                                    context = this,
                                                    hydrantId = hydrantId,
                                                    municipality = municipality
                                                )
                                            }
                                        }

                                        // Mark as notified for this user
                                        doc.reference.update("notified", true)
                                    }
                                }
                        } else {
                            // Mark as notified if it was performed by current user
                            doc.reference.update("notified", true)
                        }
                    }
                }
            }
    }

    // NEW FUNCTION: Listen for fire incident reports
    private fun startListeningForFireIncidents() {
        val currentUserEmail = auth.currentUser?.email?.lowercase() ?: return

        // Listen for new fire incident reports
        fireIncidentListener = firestore.collection("fire_incident_reports")
            .whereEqualTo("notified", false)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("FireIncidentListener", "Listen failed", error)
                    return@addSnapshotListener
                }

                snapshots?.documentChanges?.forEach { change ->
                    if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                        val doc = change.document

                        // Check if current user is admin
                        firestore.collection("admins")
                            .document(currentUserEmail)
                            .get()
                            .addOnSuccessListener { adminDoc ->
                                if (adminDoc.exists()) {
                                    // Send emergency notification to admin
                                    val reporterName = doc.getString("reporterName") ?: "Unknown"
                                    val incidentLocation = doc.getString("incidentLocation") ?: "Unknown location"
                                    val description = doc.getString("description") ?: ""

                                    Log.d("FireIncidentListener", "Sending notification to admin: $currentUserEmail")

                                    showFireIncidentNotification(
                                        context = this,
                                        reporterName = reporterName,
                                        incidentLocation = incidentLocation,
                                        description = description
                                    )

                                    // Mark as notified
                                    doc.reference.update("notified", true)
                                }
                            }
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        hydrantChangeListener?.remove()
        fireIncidentListener?.remove() // NEW: Remove fire incident listener
    }

    override fun onBind(intent: Intent?): IBinder? = null
}