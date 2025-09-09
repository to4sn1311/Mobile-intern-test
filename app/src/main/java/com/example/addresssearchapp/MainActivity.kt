package com.example.addresssearchapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.addresssearchapp.data.network.NetworkModule
import com.example.addresssearchapp.data.repository.LocationRepository
import com.example.addresssearchapp.presentation.adapter.LocationAdapter
import com.example.addresssearchapp.presentation.viewmodel.AddressSearchViewModel
import com.example.addresssearchapp.presentation.viewmodel.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var rvLocations: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: View
    private lateinit var tvError: TextView

    private lateinit var locationAdapter: LocationAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    
    // Google Maps API key - replace with your actual key
    private val googleMapsApiKey = "YOUR_API_KEY"
    
    // For location permission request
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions.entries.all { it.value }
        if (locationGranted) {
            // Permission granted, we can get location now
            Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private val viewModel: AddressSearchViewModel by viewModels {
        ViewModelFactory(LocationRepository(NetworkModule.locationApiService))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initViews()
        setupRecyclerView()
        setupSearch()
        observeViewModel()
        
        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        // Check location permissions
        checkLocationPermission()
    }
    
    private fun checkLocationPermission() {
        when {
            // Check if permission is already granted
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is granted, we can get location
            }
            
            // Should show rationale
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                Toast.makeText(
                    this,
                    "Location permission is needed to show directions",
                    Toast.LENGTH_LONG
                ).show()
                requestLocationPermission()
            }
            
            // Request permission directly
            else -> {
                requestLocationPermission()
            }
        }
    }
    
    private fun requestLocationPermission() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    private fun initViews() {
        etSearch = findViewById(R.id.et_search)
        rvLocations = findViewById(R.id.rv_locations)
        progressBar = findViewById(R.id.progress_bar)
        emptyState = findViewById(R.id.empty_state)
        tvError = findViewById(R.id.tv_error)
    }

    private fun setupRecyclerView() {
        locationAdapter = LocationAdapter { location ->
            openInGoogleMaps(location.latitude, location.longitude, location.address)
        }

        rvLocations.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = locationAdapter
        }
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                viewModel.searchAddresses(query)
                locationAdapter.updateSearchQuery(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(this) { locations ->
            locationAdapter.submitList(locations)
            updateUIState(locations.isEmpty(), false)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                tvError.visibility = View.GONE
            }
        }

        viewModel.error.observe(this) { error ->
            if (error != null) {
                tvError.text = error
                tvError.visibility = View.VISIBLE
                updateUIState(isEmpty = true, hasError = true)
            } else {
                tvError.visibility = View.GONE
            }
        }

        viewModel.currentQuery.observe(this) { query ->
            updateUIState(
                isEmpty = locationAdapter.itemCount == 0,
                hasError = tvError.visibility == View.VISIBLE,
                hasQuery = query.isNotEmpty()
            )
        }
    }

    private fun updateUIState(isEmpty: Boolean, hasError: Boolean, hasQuery: Boolean = true) {
        when {
            hasError -> {
                emptyState.visibility = View.GONE
                rvLocations.visibility = View.GONE
            }
            isEmpty && hasQuery -> {
                emptyState.visibility = View.GONE
                rvLocations.visibility = View.VISIBLE
            }
            isEmpty && !hasQuery -> {
                emptyState.visibility = View.VISIBLE
                rvLocations.visibility = View.GONE
            }
            else -> {
                emptyState.visibility = View.GONE
                rvLocations.visibility = View.VISIBLE
            }
        }
    }

    private fun getCurrentLocation(callback: (Location?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission if not granted
            requestLocationPermission()
            callback(null)
            return
        }
        
        // Get current location with high accuracy
        val cancellationToken = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationToken.token)
            .addOnSuccessListener { location ->
                callback(location)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
                callback(null)
            }
    }
    
    private fun openInGoogleMaps(latitude: Double, longitude: Double, address: String) {
        // Get current location first
        getCurrentLocation { currentLocation ->
            if (currentLocation == null) {
                // If we can't get current location, fall back to simple navigation
                openSimpleNavigation(latitude, longitude, address)
                return@getCurrentLocation
            }
            
            try {
                // Use Google Directions API via web intent
                val origin = "${currentLocation.latitude},${currentLocation.longitude}"
                val destination = "$latitude,$longitude"
                
                // Open Google Maps with directions
                val uri = Uri.parse(
                    "https://www.google.com/maps/dir/?api=1" +
                    "&origin=$origin" +
                    "&destination=$destination" +
                    "&travelmode=driving"
                )
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
                
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                // Fall back to simple navigation if there's an error
                openSimpleNavigation(latitude, longitude, address)
            }
        }
    }
    
    private fun openSimpleNavigation(latitude: Double, longitude: Double, address: String) {
        try {
            // Create a directions URI from current location to destination
            // Format: "google.navigation:q=latitude,longitude&mode=d"
            val uri = Uri.parse("google.navigation:q=$latitude,$longitude&mode=d")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
            }

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                // Fallback to web version
                val encodedAddress = Uri.encode(address)
                val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude&travelmode=driving")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                startActivity(webIntent)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to open maps for directions", Toast.LENGTH_SHORT).show()
        }
    }
}