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
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.DiseaseApiService
import com.dicoding.picodiploma.loginwithanimation.view.article.ArticleActivity
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.view.maps.MapsActivity
import com.dicoding.picodiploma.loginwithanimation.view.save.SaveActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AddStoryActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private var imagePath: String? = null

    private val CAMERA_REQUEST_CODE = 1001
    private val STORAGE_PERMISSION_CODE = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)

        imageView = findViewById(R.id.imageView)

        // Mengatur launcher untuk kamera
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                imageBitmap?.let {
                    imageView.setImageBitmap(it)
                    imagePath = saveBitmapToFile(it) // Simpan gambar ke file
                    imagePath?.let { path ->
                        val imageFile = File(path)
                        predictDisease(imageFile) // Panggil fungsi untuk memprediksi penyakit
                    }
                } ?: run {
                    Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Mengatur launcher untuk galeri
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                selectedImageUri?.let { uri ->
                    imageView.setImageURI(uri)
                    imagePath = getRealPathFromURI(uri)
                    imagePath?.let { path ->
                        val imageFile = File(path)
                        predictDisease(imageFile) // Panggil fungsi untuk memprediksi penyakit
                    }
                } ?: run {
                    Toast.makeText(this, "Failed to get image", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Mengatur listener untuk tombol kamera
        findViewById<Button>(R.id.buttonCamera).setOnClickListener {
            requestCameraPermission()
        }

        // Mengatur listener untuk tombol galeri
        findViewById<Button>(R.id.buttonGallery).setOnClickListener {
            requestStoragePermission()
        }

        // Mengatur listener untuk tombol upload
        findViewById<Button>(R.id.buttonUpload).setOnClickListener {
            if (imagePath != null) {
                val imageFile = File(imagePath!!)
                predictDisease(imageFile) // Panggil fungsi untuk memprediksi penyakit
            } else {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
            }
        }

        setupBottomNavigation()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.action_add_story
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
    }

    private fun predictDisease(imageFile: File) {
        // Membuat RequestBody untuk gambar
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile)
        // Menggunakan key "image" sesuai dengan yang diharapkan oleh API
        val body = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

        // Membuat Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl("https://flask-app-785296543353.asia-southeast2.run.app/") // URL API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(DiseaseApiService::class.java)

        // Menggunakan coroutine untuk memanggil fungsi suspend
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.predictDisease(body)
                if (response.isSuccessful) {
                    val predictionResult = response.body()
                    runOnUiThread {
                        // Intent ke DiseasePredictionResultActivity
                        val intent = Intent(this@AddStoryActivity, DiseasePredictionResultActivity::class.java)
                        intent.putExtra("predictedLabel", predictionResult?.predicted_label)
                        intent.putExtra("accuracy", predictionResult?.accuracy)
                        intent.putExtra("articleContent", predictionResult?.article?.content)
                        intent.putExtra("articleLink", predictionResult?.article?.link)
                        intent.putExtra("medicationLink", predictionResult?.article?.medication_link)
                        intent.putExtra("articleTitle", predictionResult?.article?.title)
                        intent.putExtra("imageUrl", predictionResult?.image_url)
                        intent.putExtra("imageUrl", imagePath) // Kirimkan path gambar
                        startActivity(intent) // Memulai activity hasil prediksi
                    }
                } else {
                    // Menangani kesalahan respons
                    val errorCode = response.code()
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    runOnUiThread {
                        Toast.makeText(this@AddStoryActivity, "Failed to get prediction: $errorCode - ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Menangani kesalahan jaringan atau kesalahan lainnya
                runOnUiThread {
                    Toast.makeText(this@AddStoryActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
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