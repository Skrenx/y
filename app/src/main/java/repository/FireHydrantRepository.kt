package com.example.hydrant.repository

import com.example.hydrant.model.FireHydrant
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FireHydrantRepository {
    // Use your Asia region database URL
    private val database = FirebaseDatabase.getInstance("https://firegrid-58bc3-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val hydrantsRef = database.getReference("fire_hydrants")

    // Add a new fire hydrant with custom ID: Id_1, Id_2, etc.
    suspend fun addFireHydrant(hydrant: FireHydrant): Result<String> {
        return try {
            val municipalityRef = hydrantsRef.child(hydrant.municipality)

            // Get current count to generate next ID
            val snapshot = municipalityRef.get().await()
            val currentCount = snapshot.childrenCount.toInt()
            val newId = "Id_${currentCount + 1}"

            // Create hydrant with custom ID
            val hydrantWithId = hydrant.copy(id = newId)
            municipalityRef.child(newId).setValue(hydrantWithId).await()

            Result.success(newId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get all fire hydrants for a specific municipality
    fun getHydrantsByMunicipality(municipality: String): Flow<List<FireHydrant>> = callbackFlow {
        val municipalityRef = hydrantsRef.child(municipality)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hydrants = mutableListOf<FireHydrant>()
                for (child in snapshot.children) {
                    val hydrant = child.getValue<FireHydrant>()
                    if (hydrant != null) {
                        hydrants.add(hydrant)
                    }
                }
                trySend(hydrants)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        municipalityRef.addValueEventListener(listener)
        awaitClose { municipalityRef.removeEventListener(listener) }
    }

    // Get hydrant counts for a municipality
    fun getHydrantCounts(municipality: String): Flow<Pair<Int, Int>> = callbackFlow {
        val municipalityRef = hydrantsRef.child(municipality)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var inService = 0
                var outOfService = 0

                for (child in snapshot.children) {
                    val hydrant = child.getValue<FireHydrant>()
                    when (hydrant?.serviceStatus) {
                        "In Service" -> inService++
                        "Out of Service" -> outOfService++
                    }
                }
                trySend(Pair(inService, outOfService))
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        municipalityRef.addValueEventListener(listener)
        awaitClose { municipalityRef.removeEventListener(listener) }
    }

    // Update an existing fire hydrant
    suspend fun updateFireHydrant(hydrant: FireHydrant): Result<Unit> {
        return try {
            hydrantsRef.child(hydrant.municipality)
                .child(hydrant.id)
                .setValue(hydrant)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete a fire hydrant
    suspend fun deleteFireHydrant(municipality: String, hydrantId: String): Result<Unit> {
        return try {
            hydrantsRef.child(municipality)
                .child(hydrantId)
                .removeValue()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}