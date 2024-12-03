package com.dicoding.picodiploma.loginwithanimation.view.addstory

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.article.ArticleActivity
import com.dicoding.picodiploma.loginwithanimation.view.login.dataStore
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.view.maps.MapsActivity
import com.dicoding.picodiploma.loginwithanimation.view.save.SaveActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AddStoryActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var userPreference: UserPreference
    private lateinit var bottomNavigationView: BottomNavigationView
    private var imagePath: String? = null

    private val CAMERA_REQUEST_CODE = 1001
    private val STORAGE_PERMISSION_CODE = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)

        imageView = findViewById(R.id.imageView)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        val factory = ViewModelFactory.getInstance(this)
        addStoryViewModel = ViewModelProvider(this, factory).get(AddStoryViewModel::class.java)

        // Inisialisasi UserPreference
        userPreference = UserPreference.getInstance(dataStore)

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                imageBitmap?.let {
                    imageView.setImageBitmap(it)
                    imagePath = saveBitmapToFile(it)
                    imagePath?.let { path ->
                        val imageFile = File(path)
                        predictDisease(imageFile)
                    }
                } ?: run {
                    Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
                }
            }
        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                selectedImageUri?.let { uri ->
                    imageView.setImageURI(uri)
                    imagePath = getRealPathFromURI(uri)
                    imagePath?.let { path ->
                        val imageFile = File(path)
                        predictDisease(imageFile)
                    }
                } ?: run {
                    Toast.makeText(this, "Failed to get image", Toast.LENGTH_SHORT).show()
                }
            }
        }
        setupBottomNavigation()

        findViewById<Button>(R.id.buttonCamera).setOnClickListener {
            requestCameraPermission()
        }

        findViewById<Button>(R.id.buttonGallery).setOnClickListener {
            requestStoragePermission()
        }

        findViewById<Button>(R.id.buttonUpload).setOnClickListener {
            if (imagePath != null) {
                val imageFile = File(imagePath!!)
                predictDisease(imageFile)
            } else {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun predictDisease(imageFile: File) {
        val requestFile = RequestBody.create("image/jpeg".toMediaType(), imageFile)
        val body = MultipartBody.Part.createFormData("photo", imageFile.name, requestFile)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://capstone-443612.et.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(DiseaseApiService::class.java)
        service.predictDisease(body).enqueue(object : Callback<DiseasePredictionResponse> {
            override fun onResponse(call: Call<DiseasePredictionResponse>, response: Response<DiseasePredictionResponse>) {
                if (response.isSuccessful) {
                    val predictionResult = response.body()?.prediction
                    val intent = Intent(this@AddStoryActivity, DiseasePredictionResultActivity::class.java)
                    intent.putExtra("predictionResult", predictionResult)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@AddStoryActivity, "Failed to get prediction", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DiseasePredictionResponse>, t: Throwable) {
                Toast.makeText(this@AddStoryActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun setupBottomNavigation() {
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
                    // Already on this page
                    true
                }
                R.id.action_save -> {
                    startActivity(Intent(this, SaveActivity::class.java))
                    true
                }
                R.id.action_maps -> {
                    startActivity(Intent(this, MapsActivity::class.java))
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.selectedItemId = R.id.action_add_story
    }

    private fun saveBitmapToFile(bitmap: Bitmap): String? {
        val file = File(getExternalFilesDir(null), "captured_image.jpg")
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
        return null
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        var path: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            path = cursor.getString(columnIndex)
            cursor.close()
        }
        return path
    }

    private fun requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
            } else {
                openCamera()
            }
        } else {
            openCamera()
        }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES), STORAGE_PERMISSION_CODE)
            } else {
                openGallery()
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
            } else {
                openGallery()
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            cameraLauncher.launch(intent)
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}