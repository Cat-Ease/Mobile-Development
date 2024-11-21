package com.dicoding.picodiploma.loginwithanimation.view.maps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMapsBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.data.repository.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiService
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.view.login.dataStore
import com.dicoding.picodiploma.loginwithanimation.view.addstory.AddStoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.article.ArticleActivity
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.view.save.SaveActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var searchInput: AutoCompleteTextView
    private lateinit var searchButton: Button
    private lateinit var placesClient: PlacesClient
    private val viewModel: MapsViewModel by viewModels {
        ViewModelFactory(
            userRepository = Injection.provideRepository(this),
            storyRepository = StoryRepository(ApiService.create())
        )
    }

    private val locationPermissionRequestCode = 1
    private val suggestions = mutableListOf<String>() // List untuk menyimpan saran lokasi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        }
        placesClient = Places.createClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        searchInput = findViewById(R.id.search_input)
        searchButton = findViewById(R.id.search_button)

        // Setup adapter untuk AutoCompleteTextView
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestions)
        searchInput.setAdapter(adapter)

        searchInput.setOnItemClickListener { parent, view, position, id ->
            val selectedLocation = parent.getItemAtPosition(position) as String
            searchLocation(selectedLocation)
        }

        searchButton.setOnClickListener {
            val locationName = searchInput.text.toString()
            searchLocation(locationName)
        }

        setupBottomNavigation()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.action_maps

        // Listener untuk mengupdate saran saat mengetik
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tidak perlu diimplementasikan
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.isNotEmpty()) {
                    findAutocompletePredictions(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Tidak perlu diimplementasikan
            }
        })
    }

    private fun findAutocompletePredictions(query: String) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            suggestions.clear() // Clear previous suggestions
            for (prediction in response.autocompletePredictions) {
                suggestions.add(prediction.getPrimaryText(null).toString())
            }
            // Notify the adapter of the change
            (searchInput.adapter as ArrayAdapter<*>).notifyDataSetChanged()
        }.addOnFailureListener { exception ->
            Log.e("MapsActivity", "Error finding autocomplete predictions: ${exception.message}")
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.action_article -> {
                    startActivity(Intent(this, ArticleActivity::class.java))
                    true
                }
                R.id.action_add_story -> {
                    startActivity(Intent(this, AddStoryActivity::class.java))
                    true
                }
                R.id.action_save -> {
                    startActivity(Intent(this, SaveActivity::class.java))
                    true
                }
                R.id.action_maps -> true
                else -> false
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        checkLocationPermission()

        val indonesiaLatLng = LatLng(-6.2088, 106.8456)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(indonesiaLatLng, 5f))

        val userPreference = UserPreference.getInstance(dataStore)
        lifecycleScope.launch {
            userPreference.getSession().collectLatest { userModel ->
                val token = userModel.token
                val bearerToken = "Bearer $token"
                viewModel.fetchStoriesWithLocation(bearerToken)
            }
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionRequestCode)
        } else {
            getMyLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionRequestCode) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getMyLocation()
            } else {
                Log.e("MapsActivity", "Location permission denied")
            }
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                    findNearbyPetServices(userLatLng)
                }
            }.addOnFailureListener { exception ->
                Log.e("MapsActivity", "Failed to get location: ${exception.message}")
            }
        }
    }

    private fun searchLocation(locationName: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>? = geocoder.getFromLocationName(locationName, 1)

        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            val latLng = LatLng(address.latitude, address.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

            mMap.addMarker(
                com.google.android.gms.maps.model.MarkerOptions()
                    .position(latLng)
                    .title(locationName)
            )
        } else {
            Log.e("MapsActivity", "Location not found")
        }
    }

    private fun findNearbyPetServices(location: LatLng) {
        val placesClient = Places.createClient(this)

        val request = FindCurrentPlaceRequest.newInstance(listOf(Place.Field.NAME, Place.Field.LAT_LNG))

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        placesClient.findCurrentPlace(request).addOnSuccessListener { response ->
            for (placeLikelihood in response.placeLikelihoods) {
                val place = placeLikelihood.place
                val markerOptions = com.google.android.gms.maps.model.MarkerOptions()
                    .position(place.latLng!!)
                    .title(place.name)

                mMap.addMarker(markerOptions)
                suggestions.add(place.name) // Menambahkan nama tempat ke daftar saran
            }
        }.addOnFailureListener { exception ->
            Log.e("MapsActivity", "Place not found: ${exception.message}")
        }
    }

    private fun setMapStyle() {
        // Implementasi untuk mengatur gaya peta
    }
}