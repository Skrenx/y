package com.example.hydrant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hydrant.model.FireHydrant
import com.example.hydrant.repository.FireHydrantRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
    val deleteSuccess: Boolean = false,
    val scrollIndex: Int = 0,
    val scrollOffset: Int = 0,
    val lastAddedHydrantName: String = ""
)

class FireHydrantViewModel : ViewModel() {
    private val repository = FireHydrantRepository()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://firegrid-58bc3-default-rtdb.asia-southeast1.firebasedatabase.app/")

    private val _uiState = MutableStateFlow(HydrantUiState())
    val uiState: StateFlow<HydrantUiState> = _uiState.asStateFlow()

    // Real-time listener tracking for loadHydrants (single municipality)
    private var hydrantListener: ValueEventListener? = null
    private var hydrantRef: DatabaseReference? = null

    // Real-time listener tracking for loadAllHydrants (all municipalities)
    private val allHydrantListeners = mutableMapOf<String, ValueEventListener>()
    private val allHydrantRefs = mutableMapOf<String, DatabaseReference>()
    // Cache of per-municipality hydrant lists, merged into allHydrants
    private val allHydrantsCache = mutableMapOf<String, List<FireHydrant>>()

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

    /**
     * Save the current scroll position
     */
    fun saveScrollPosition(index: Int, offset: Int) {
        _uiState.value = _uiState.value.copy(
            scrollIndex = index,
            scrollOffset = offset
        )
    }

    /**
     * Reset scroll position (optional, can be called when needed)
     */
    fun resetScrollPosition() {
        _uiState.value = _uiState.value.copy(
            scrollIndex = 0,
            scrollOffset = 0
        )
    }

    fun loadHydrants(municipality: String) {
        // Remove previous listener if municipality changed
        hydrantListener?.let { hydrantRef?.removeEventListener(it) }

        _uiState.value = _uiState.value.copy(isLoading = true)

        val ref = database.getReference("fire_hydrants").child(municipality)
        hydrantRef = ref

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hydrants = snapshot.children.mapNotNull { child ->
                    val hydrant = child.getValue(FireHydrant::class.java)
                    if (hydrant != null && hydrant.id.isEmpty()) {
                        hydrant.copy(id = child.key ?: "")
                    } else {
                        hydrant
                    }
                }.sortedBy { extractHydrantNumber(it.id) }

                _uiState.value = _uiState.value.copy(
                    hydrants = hydrants,
                    isLoading = false,
                    error = null
                )
            }

            override fun onCancelled(error: DatabaseError) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message
                )
            }
        }

        hydrantListener = listener
        ref.addValueEventListener(listener) // real-time: fires on every remote change
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

    fun reloadAllHydrants() {
        // With real-time listeners, this is now a no-op —
        // allHydrants updates automatically via addValueEventListener.
        // Kept for backward compatibility so MainActivity.kt call still compiles.
    }

    fun loadAllHydrants() {
        if (allHydrantListeners.isNotEmpty()) return // already listening

        val municipalities = listOf(
            "Alaminos", "Bay", "Biñan", "Cabuyao", "Calamba", "Calauan",
            "Cavinti", "Famy", "Kalayaan", "Liliw", "Los Baños", "Luisiana",
            "Lumban", "Mabitac", "Magdalena", "Majayjay", "Nagcarlan", "Paete",
            "Pagsanjan", "Pakil", "Pangil", "Pila", "Rizal", "San Pablo", "San Pedro",
            "Santa Cruz", "Santa Maria", "Santa Rosa", "Siniloan", "Victoria"
        )

        _uiState.value = _uiState.value.copy(isLoading = true)

        for (municipality in municipalities) {
            allHydrantsCache[municipality] = emptyList() // seed so municipality is always in cache
            val ref = database.getReference("fire_hydrants").child(municipality)
            allHydrantRefs[municipality] = ref

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val hydrants = snapshot.children.mapNotNull { child ->
                        val hydrant = child.getValue(FireHydrant::class.java)
                        if (hydrant != null && hydrant.id.isEmpty()) {
                            hydrant.copy(id = child.key ?: "")
                        } else {
                            hydrant
                        }
                    }
                    // Update only this municipality's slice in the cache
                    allHydrantsCache[municipality] = hydrants

                    // Merge all municipalities and push to UI
                    val merged = allHydrantsCache.values.flatten()
                        .sortedWith(compareBy({ it.municipality }, { extractHydrantNumber(it.id) }))

                    _uiState.value = _uiState.value.copy(
                        allHydrants = merged,
                        isLoading = false,
                        error = null
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
            }

            allHydrantListeners[municipality] = listener
            ref.addValueEventListener(listener) // real-time per municipality
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
                onSuccess = { generatedName ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        addSuccess = true,
                        error = null,
                        lastAddedHydrantName = generatedName
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

    override fun onCleared() {
        super.onCleared()
        // Clean up all real-time listeners to prevent memory leaks
        hydrantListener?.let { hydrantRef?.removeEventListener(it) }
        allHydrantListeners.forEach { (municipality, listener) ->
            allHydrantRefs[municipality]?.removeEventListener(listener)
        }
        allHydrantListeners.clear()
        allHydrantRefs.clear()
        allHydrantsCache.clear()
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

    fun updateLocalHydrantStatus(hydrantId: String, newStatus: String) {
        val updatedList = _uiState.value.hydrants.map {
            if (it.id == hydrantId) it.copy(serviceStatus = newStatus) else it
        }
        _uiState.value = _uiState.value.copy(hydrants = updatedList)
    }
}