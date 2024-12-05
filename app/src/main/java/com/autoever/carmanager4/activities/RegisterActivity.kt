package com.autoever.carmanager4.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.autoever.carmanager4.R
import com.autoever.carmanager4.models.Car
import com.google.firebase.firestore.FirebaseFirestore

// 등록 페이지
class RegisterActivity : AppCompatActivity() {

    private var isInListener = false
    private var carType: String = "캐스퍼 일레트릭"

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

        val editTextCarNumber = findViewById<EditText>(R.id.editTextCarNumber)
        val textViewApply = findViewById<TextView>(R.id.textViewApply)

        textViewApply.setOnClickListener {
            val car = Car()
            car.model = carType
            val carNum = editTextCarNumber.text.toString()
            if(carNum.isBlank()){
                Toast.makeText(this, "차 번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else{
                car.num = carNum
                saveCarInfo(car)
            }
        }
    }

    fun saveCarInfo(car: Car){
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("cars")
            .document("car1")
            .set(car)
            .addOnSuccessListener {
                // 메인 화면으로 이동. 메인에서 뒤로 가기 할 때 등록 페이지 안 나옴.
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "차 등록에 실패했습니다", Toast.LENGTH_SHORT).show()
            }
    }
}