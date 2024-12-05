package com.autoever.carmanager4.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.autoever.carmanager4.R
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Handler(Looper.getMainLooper()).postDelayed({
            // 앱에 저장된 Car 문서ID 가져오기
            val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val carID = sharedPreferences.getString("carID", "") ?: ""
            val intent = if (carID.isEmpty()) {
                // 저장된 carID 없음: 처음 앱을 실행하거나 차량 삭제 했을 때
                Intent(this, RegisterActivity::class.java)
            } else {
                // 저장된 carID 있음
                Intent(this, MainActivity::class.java)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }, 1500)
    }
}