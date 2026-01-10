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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class HydrantUiState(
    val hydrants: List<FireHydrant> = emptyList(),
    val allHydrants: List<FireHydrant> = emptyList(),
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

    /**
     * Helper function to extract numeric ID from hydrant ID string.
     * Handles formats like "Id_1", "Id_01", "Id_001", etc.
     */
    private fun extractHydrantNumber(hydrantId: String): Int {
        return try {
            val numberPart = hydrantId
                .replace("Id_", "", ignoreCase = true)
                .replace("Hydrant Id_", "", ignoreCase = true)
                .trim()
            numberPart.toIntOrNull() ?: Int.MAX_VALUE
        } catch (e: Exception) {
            Int.MAX_VALUE
        }
    }

    /**
     * Generates a zero-padded ID like Id_01, Id_02, ... Id_10, Id_99, Id_100
     * Uses 2 digits for 1-99, 3 digits for 100-999, etc.
     */
    private fun generateZeroPaddedId(number: Int): String {
        return when {
            number < 100 -> "Id_${number.toString().padStart(2, '0')}"  // Id_01 to Id_99
            number < 1000 -> "Id_${number.toString().padStart(3, '0')}" // Id_100 to Id_999
            else -> "Id_${number.toString().padStart(4, '0')}"          // Id_1000+
        }
    }

    /**
     * Generates a formatted timestamp string like "January 10, 2026 at 3:33:09 PM UTC+8"
     */
    private fun getFormattedTimestamp(): String {
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy 'at' h:mm:ss a 'UTC+8'", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Manila") // UTC+8
        return dateFormat.format(Date())
    }

    fun loadHydrants(municipality: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            repository.getHydrantsByMunicipality(municipality).collect { hydrants ->
                // Repository already sorts numerically, but we can sort again for safety
                val sortedHydrants = hydrants.sortedBy { extractHydrantNumber(it.id) }
                _uiState.value = _uiState.value.copy(
                    hydrants = sortedHydrants,
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

    fun loadAllHydrants() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val municipalities = listOf(
                    "Alaminos", "Bay", "Biñan", "Cabuyao", "Calamba", "Calauan",
                    "Cavinti", "Famy", "Kalayaan", "Liliw", "Los Baños", "Luisiana",
                    "Lumban", "Mabitac", "Magdalena", "Majayjay", "Nagcarlan", "Paete",
                    "Pagsanjan", "Pakil", "Pangil", "Rizal", "San Pablo", "San Pedro",
                    "Santa Cruz", "Santa Maria", "Santa Rosa", "Siniloan", "Victoria"
                )

                val allHydrants = mutableListOf<FireHydrant>()

                for (municipality in municipalities) {
                    try {
                        val hydrantsRef = database.getReference("fire_hydrants").child(municipality)
                        val snapshot = hydrantsRef.get().await()

                        if (snapshot.exists()) {
                            for (childSnapshot in snapshot.children) {
                                val hydrant = childSnapshot.getValue(FireHydrant::class.java)
                                if (hydrant != null) {
                                    val hydrantWithId = if (hydrant.id.isEmpty()) {
                                        hydrant.copy(id = childSnapshot.key ?: "")
                                    } else {
                                        hydrant
                                    }
                                    allHydrants.add(hydrantWithId)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // Sort all hydrants by municipality first, then by numeric ID
                val sortedAllHydrants = allHydrants.sortedWith(
                    compareBy(
                        { it.municipality },
                        { extractHydrantNumber(it.id) }
                    )
                )

                _uiState.value = _uiState.value.copy(
                    allHydrants = sortedAllHydrants,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load all hydrants"
                )
            }
        }
    }

    // Updated: hydrantName parameter is now ignored - name is auto-generated in repository
    fun addFireHydrant(
        hydrantName: String = "", // This is now ignored, kept for compatibility
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
                hydrantName = "", // Will be auto-generated in repository
                exactLocation = exactLocation,
                latitude = latitude,
                longitude = longitude,
                typeColor = typeColor,
                serviceStatus = serviceStatus,
                remarks = remarks,
                municipality = municipality,
                lastUpdated = "" // Will be set in repository
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

    fun deleteHydrantAndRenumber(municipality: String, hydrantId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, deleteSuccess = false)

            try {
                val hydrantsRef = database.getReference("fire_hydrants")
                    .child(municipality)

                // Extract the number from the ID (handles both Id_1 and Id_01 formats)
                val deletedIdNumber = extractHydrantNumber(hydrantId)

                if (deletedIdNumber == Int.MAX_VALUE || deletedIdNumber == 0) {
                    // Invalid ID format, just delete without renumbering
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

                // Delete the hydrant
                hydrantsRef.child(hydrantId).removeValue().await()

                // Get all remaining hydrants
                val snapshot = hydrantsRef.get().await()

                val hydrantsToRenumber = ArrayList<Triple<String, Int, DataSnapshot>>()

                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        val docId = childSnapshot.key ?: continue
                        val idNumber = extractHydrantNumber(docId)
                        if (idNumber != Int.MAX_VALUE && idNumber > deletedIdNumber) {
                            hydrantsToRenumber.add(Triple(docId, idNumber, childSnapshot))
                        }
                    }
                }

                // Sort by numeric ID to ensure proper renumbering order
                hydrantsToRenumber.sortBy { it.second }

                // Renumber hydrants with zero-padded IDs
                for (triple in hydrantsToRenumber) {
                    val oldId = triple.first
                    val oldIdNumber = triple.second
                    val childSnapshot = triple.third

                    val newIdNumber = oldIdNumber - 1
                    val newId = generateZeroPaddedId(newIdNumber)

                    // Auto-generate new hydrant name based on new ID
                    val newHydrantName = "Hydrant $newId"

                    // Get formatted timestamp for the update
                    val formattedTimestamp = getFormattedTimestamp()

                    val data = childSnapshot.value

                    if (data != null && data is Map<*, *>) {
                        val updatedData = HashMap<String, Any?>()
                        for ((key, value) in data) {
                            if (key is String) {
                                updatedData[key] = value
                            }
                        }
                        updatedData["id"] = newId
                        updatedData["hydrantName"] = newHydrantName
                        updatedData["lastUpdated"] = formattedTimestamp

                        // Create new entry with zero-padded ID
                        hydrantsRef.child(newId).setValue(updatedData).await()
                        // Delete old entry
                        hydrantsRef.child(oldId).removeValue().await()
                    }
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    deleteSuccess = true,
                    error = null
                )

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