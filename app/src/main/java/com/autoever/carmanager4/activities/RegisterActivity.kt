package com.autoever.carmanager4.activities

import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.autoever.carmanager4.R

// 등록 페이지
class RegisterActivity : AppCompatActivity() {

    private var isInListener = false
    private lateinit var carType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 라디오 그룹 목록
        val radioGroupList = mutableListOf<RadioGroup>(
            findViewById(R.id.radioGroupLine1),
            findViewById(R.id.radioGroupLine2),
            findViewById(R.id.radioGroupLine3),
            findViewById(R.id.radioGroupLine4),
            findViewById(R.id.radioGroupLine5),
            findViewById(R.id.radioGroupLine6),
            findViewById(R.id.radioGroupLine7),
            findViewById(R.id.radioGroupLine8),
            findViewById(R.id.radioGroupLine9),
            findViewById(R.id.radioGroupLine10),
            findViewById(R.id.radioGroupLine11),
            findViewById(R.id.radioGroupLine12),
            findViewById(R.id.radioGroupLine13),
            findViewById(R.id.radioGroupLine14),
            findViewById(R.id.radioGroupLine15),
            findViewById(R.id.radioGroupLine16),
            findViewById(R.id.radioGroupLine17),
            findViewById(R.id.radioGroupLine18),
            findViewById(R.id.radioGroupLine19)
        )


        radioGroupList.forEach { radioGroup ->
            // 각 라디오 그룹마다 changeListener 설정
            radioGroup.setOnCheckedChangeListener { line, i ->
                if (isInListener) return@setOnCheckedChangeListener // 리스너 안에서 무한루프 방지
                isInListener = true // 리스너 안에서 상태 변경 시 플래그 활성화

                carType = findViewById<RadioButton>(i).text.toString()
                Log.d("item","$carType")
                radioGroupList.forEach{ group ->
                    if(group != line) {
                        group.clearCheck()
                    }
                }

                isInListener = false
            }
        }

    }
}