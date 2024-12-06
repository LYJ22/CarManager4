package com.autoever.carmanager4.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.autoever.carmanager4.R
//import com.autoever.carmanager4.adapters.ImageAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class CameraActivity : AppCompatActivity() {
    private lateinit var storageRef: StorageReference
//    private lateinit var listView: ListView
    private lateinit var imageView: ImageView
    private var selectedImageUri: Uri? = null

    private val cameraPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    private val imagePickLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { photoUri ->
                    uploadToFirebase(photoUri)
                    selectedImageUri = photoUri
                    imageView.setImageURI(photoUri)
//                    (listView.adapter as ImageAdapter).notifyDataSetChanged()
                }
            }
        }

    private val cameraCaptureLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val photoUri: Uri? = result.data?.data
                photoUri?.let {
                    uploadToFirebase(it)
                    imageView.setImageURI(it)
//                    (listView.adapter as ImageAdapter).notifyDataSetChanged()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        storageRef = FirebaseStorage.getInstance().reference
        val cameraButton: Button = findViewById(R.id.buttonCamera)
        val albumButton: Button = findViewById(R.id.buttonAlbum)
        val uploadButton: Button = findViewById(R.id.buttonUpload)
        imageView = findViewById(R.id.imageViewCar)
//        imageView.adapter = ImageAdapter(this, imageView)

        cameraButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }

        albumButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickLauncher.launch(intent)
        }

        uploadButton.setOnClickListener {
            selectedImageUri?.let {
                uploadToFirebase(it)
            } ?: Toast.makeText(this, "업로드할 사진을 선택하세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openCamera() {
        val photoUri: Uri = createImageUri() // 카메라로 찍을 이미지의 Uri를 생성
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        cameraCaptureLauncher.launch(intent)
    }

    private fun createImageUri(): Uri {
        val contentResolver = applicationContext.contentResolver
        val imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val contentValues = android.content.ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "car_image_${UUID.randomUUID()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return contentResolver.insert(imageCollection, contentValues) ?: Uri.EMPTY
    }

    private fun uploadToFirebase(photoUri: Uri) {
        val fileName = "cars/${UUID.randomUUID()}.jpg"
        val imageRef = storageRef.child(fileName)

        imageRef.putFile(photoUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    imageView.setImageURI(uri)
//                    (listView.adapter as ImageAdapter).notifyDataSetChanged()
                    saveImageUrlToDatabase(uri)
                }
            }
            .addOnFailureListener {
                // 업로드 실패 처리
                Toast.makeText(this, "사진 업로드 실패", Toast.LENGTH_SHORT).show()
            }
    }
    private fun saveImageUrlToDatabase(uri: Uri) {
        // Firebase Realtime Database나 Firestore에 URL 저장
        // 예시: Firebase Firestore 사용
        val db = FirebaseFirestore.getInstance()
        val imageData = hashMapOf(
            "imageUrl" to uri.toString()
        )

        db.collection("car_images")
            .add(imageData)
            .addOnSuccessListener {
                // 성공적으로 데이터가 저장된 후 처리
                Toast.makeText(this, "이미지 URL이 저장되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                // 데이터 저장 실패 처리
                Toast.makeText(this, "이미지 URL 저장 실패", Toast.LENGTH_SHORT).show()
            }
    }
}
