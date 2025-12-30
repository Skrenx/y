package com.example.hydrant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hydrant.model.FireHydrant
import com.example.hydrant.repository.FireHydrantRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class HydrantUiState(
    val hydrants: List<FireHydrant> = emptyList(),
    val inServiceCount: Int = 0,
    val outOfServiceCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val addSuccess: Boolean = false,
    val updateSuccess: Boolean = false,
    val deleteSuccess: Boolean = false
)

class FireHydrantViewModel : ViewModel() {
    private val repository = FireHydrantRepository()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://firegrid-58bc3-default-rtdb.asia-southeast1.firebasedatabase.app/")

    private val _uiState = MutableStateFlow(HydrantUiState())
    val uiState: StateFlow<HydrantUiState> = _uiState.asStateFlow()

    fun loadHydrants(municipality: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            repository.getHydrantsByMunicipality(municipality).collect { hydrants ->
                _uiState.value = _uiState.value.copy(
                    hydrants = hydrants,
                    isLoading = false
                )
            }
        }
    }

    fun loadHydrantCounts(municipality: String) {
        viewModelScope.launch {
            repository.getHydrantCounts(municipality).collect { (inService, outOfService) ->
                _uiState.value = _uiState.value.copy(
                    inServiceCount = inService,
                    outOfServiceCount = outOfService
                )
            }
        }
    }

    fun addFireHydrant(
        hydrantName: String,
        exactLocation: String,
        latitude: String,
        longitude: String,
        typeColor: String,
        serviceStatus: String,
        remarks: String,
        municipality: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, addSuccess = false)

            val hydrant = FireHydrant(
                hydrantName = hydrantName,
                exactLocation = exactLocation,
                latitude = latitude,
                longitude = longitude,
                typeColor = typeColor,
                serviceStatus = serviceStatus,
                remarks = remarks,
                municipality = municipality,
                timestamp = System.currentTimeMillis()
            )

            val result = repository.addFireHydrant(hydrant)

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        addSuccess = true,
                        error = null
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        addSuccess = false,
                        error = e.message ?: "Failed to add hydrant"
                    )
                }
            )
        }
    }

    fun updateFireHydrant(hydrant: FireHydrant) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, updateSuccess = false)

            val result = repository.updateFireHydrant(hydrant)

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        updateSuccess = true,
                        error = null
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        updateSuccess = false,
                        error = e.message ?: "Failed to update hydrant"
                    )
                }
            )
        }
    }

    fun deleteHydrant(municipality: String, hydrantId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, deleteSuccess = false)

            val result = repository.deleteFireHydrant(municipality, hydrantId)

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        deleteSuccess = true,
                        error = null
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        deleteSuccess = false,
                        error = e.message ?: "Failed to delete hydrant"
                    )
                }
            )
        }
    }

    /**
     * Deletes a hydrant and renumbers all subsequent hydrants.
     * For example, if Id_2 is deleted, Id_3 becomes Id_2, Id_4 becomes Id_3, etc.
     * This version works with Firebase Realtime Database using fire_hydrants path.
     */
    fun deleteHydrantAndRenumber(municipality: String, hydrantId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, deleteSuccess = false)

            try {
                // Using fire_hydrants path to match your database rules
                val hydrantsRef = database.getReference("fire_hydrants")
                    .child(municipality)

                // Get the numeric part of the deleted ID (e.g., "Id_2" -> 2)
                val idWithoutPrefix = hydrantId.removePrefix("Id_")
                val deletedIdNumber: Int? = idWithoutPrefix.toIntOrNull()

                if (deletedIdNumber == null) {
                    // If ID format is different, just do simple delete
                    hydrantsRef.child(hydrantId).removeValue().await()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        deleteSuccess = true,
                        error = null
                    )
                    loadHydrants(municipality)
                    loadHydrantCounts(municipality)
                    return@launch
                }

                // First, delete the hydrant
                hydrantsRef.child(hydrantId).removeValue().await()

                // Get all hydrants to find ones with higher ID numbers
                val snapshot = hydrantsRef.get().await()

                // Create a list of hydrants that need to be renumbered
                val hydrantsToRenumber = ArrayList<Triple<String, Int, DataSnapshot>>()

                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        val docId = childSnapshot.key ?: continue
                        val idNumStr = docId.removePrefix("Id_")
                        val idNumber = idNumStr.toIntOrNull()
                        if (idNumber != null && idNumber > deletedIdNumber) {
                            hydrantsToRenumber.add(Triple(docId, idNumber, childSnapshot))
                        }
                    }
                }

                // Sort by ID number (ascending)
                hydrantsToRenumber.sortBy { it.second }

                // Renumber each hydrant (process in order from lowest to highest)
                for (triple in hydrantsToRenumber) {
                    val oldId = triple.first
                    val oldIdNumber = triple.second
                    val childSnapshot = triple.third

                    val newIdNumber = oldIdNumber - 1
                    val newId = "Id_$newIdNumber"

                    // Get the data as a map
                    val data = childSnapshot.value

                    if (data != null && data is Map<*, *>) {
                        // Create new HashMap and copy data
                        val updatedData = HashMap<String, Any?>()
                        for ((key, value) in data) {
                            if (key is String) {
                                updatedData[key] = value
                            }
                        }
                        // Update the id field
                        updatedData["id"] = newId

                        // Create new node with new ID
                        hydrantsRef.child(newId).setValue(updatedData).await()

                        // Delete old node
                        hydrantsRef.child(oldId).removeValue().await()
                    }
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    deleteSuccess = true,
                    error = null
                )

                // Reload hydrants to reflect changes
                loadHydrants(municipality)
                loadHydrantCounts(municipality)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    deleteSuccess = false,
                    error = e.message ?: "Failed to delete and renumber hydrants"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetAddSuccess() {
        _uiState.value = _uiState.value.copy(addSuccess = false)
    }

    fun resetUpdateSuccess() {
        _uiState.value = _uiState.value.copy(updateSuccess = false)
    }

    fun resetDeleteSuccess() {
        _uiState.value = _uiState.value.copy(deleteSuccess = false)
    }
}