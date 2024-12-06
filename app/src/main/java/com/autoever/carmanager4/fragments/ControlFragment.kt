package com.autoever.carmanager4.fragments

import android.animation.Animator
import android.content.Context
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.autoever.carmanager4.R
import com.autoever.carmanager4.models.Car
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 제어 페이지
class ControlFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private val cars = mutableListOf<Car>()
    private var carState: Car? = null
    private lateinit var temp:TextView
    private lateinit var power:ImageButton
    private lateinit var open:ImageButton
    private lateinit var close:ImageButton
    private lateinit var carModel:TextView
    //private lateinit var glow:Animation


    val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_control, container, false)
        val tempUp = view.findViewById<TextView>(R.id.textView5)
        val tempDown = view.findViewById<TextView>(R.id.textView4)
        temp = view.findViewById<TextView>(R.id.textView3)
        val horn = view.findViewById<ImageButton>(R.id.imageButton7)
        val windowOpen = view.findViewById<ImageButton>(R.id.imageButton5)
        val windowClose = view.findViewById<ImageButton>(R.id.imageButton8)
        power = view.findViewById(R.id.imageButton4)
        open = view.findViewById(R.id.imageButton9)
        close = view.findViewById(R.id.imageButton6)
        carModel = view.findViewById(R.id.textView21)
        //glow = AnimationUtils.loadAnimation(requireContext(), R.anim.glow_animation)

        //temp.text = "${carState!!.temperature}"
        //addCar()
        getCar()

        //temp.text = carState?.temperature?.toString() ?: "22.0"


        tempUp.setOnClickListener {
            carState!!.temperature += 0.1
            temp.text = formatTemperature(carState!!.temperature)
            updateCarState(carState!!)
        }
        tempDown.setOnClickListener {
            carState!!.temperature -= 0.1
            temp.text = formatTemperature(carState!!.temperature)
            updateCarState(carState!!)
        }
        horn.setOnClickListener{
            Toast.makeText(requireContext(), "경적 \uD83D\uDCE2~!", Toast.LENGTH_SHORT).show()
            horn.setBackgroundResource(R.drawable.glowing_button)

            // 1초 후 원래 상태로 복원
            lifecycleScope.launch {
                delay(1000) // 1초 (1000ms) 대기
                horn.setBackgroundResource(R.drawable.rounded_button) // 원래 상태로 복원
            }
        }
        windowOpen.setOnClickListener{
            Toast.makeText(requireContext(), "창문이 열립니다 \uD83D\uDD3D", Toast.LENGTH_SHORT).show()
            windowOpen.setBackgroundResource(R.drawable.glowing_button)

            // 1초 후 원래 상태로 복원
            lifecycleScope.launch {
                delay(1000) // 1초 (1000ms) 대기
                windowOpen.setBackgroundResource(R.drawable.rounded_button) // 원래 상태로 복원
            }
        }
        windowClose.setOnClickListener{
            Toast.makeText(requireContext(), "창문이 닫힙니다 \uD83D\uDD3C", Toast.LENGTH_SHORT).show()

            windowClose.setBackgroundResource(R.drawable.glowing_button)

            // 1초 후 원래 상태로 복원
            lifecycleScope.launch {
                delay(1000) // 1초 (1000ms) 대기
                windowClose.setBackgroundResource(R.drawable.rounded_button) // 원래 상태로 복원
            }
        }
        power.setOnClickListener{
            carState?.let { car ->
                car.isEngineOn = !car.isEngineOn // 시동 상태 변경
                if (car.isEngineOn) {
                    power.setBackgroundResource(R.drawable.glowing_button) // 빛나는 효과
                    //power.startAnimation(glow)
                    Toast.makeText(requireContext(), "시동 On", Toast.LENGTH_SHORT).show()
                } else {
                    power.setBackgroundResource(R.drawable.rounded_button) // 기본 상태로 복원
                    Toast.makeText(requireContext(), "시동 Off", Toast.LENGTH_SHORT).show()
                    //power.clearAnimation()
                }
                updateCarState(car) // Firestore에 상태 업데이트
            }
        }
        close.setOnClickListener{
            carState?.let { car ->
                car.isLocked = true
                close.setBackgroundResource(R.drawable.glowing_button)
                open.setBackgroundResource(R.drawable.rounded_button)
                Toast.makeText(requireContext(), "문이 잠겼습니다 \uD83D\uDD10", Toast.LENGTH_SHORT).show()
                updateCarState(car) // Firestore에 상태 업데이트
            }
        }
        open.setOnClickListener{
            carState?.let { car ->
                car.isLocked = false
                open.setBackgroundResource(R.drawable.glowing_button)
                close.setBackgroundResource(R.drawable.rounded_button)
                Toast.makeText(requireContext(), "문이 열렸습니다 \uD83D\uDD11", Toast.LENGTH_SHORT).show()

                updateCarState(car) // Firestore에 상태 업데이트
            }
        }

        return view
    }




    private fun getCar() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val carID = sharedPreferences.getString("carID", null)
        firestore.collection("cars")
            .document(carID!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val car = document.toObject(Car::class.java)
                    carState = car

                    //Log.d("Firestore", "Car retrieved: $car")
                    Log.d("Firestore", "Car retrieved: ${carState!!.model}")
                } else {
                    Log.d("Firestore", "No such document")
                }
                carModel.text = carState!!.model
                temp.text = formatTemperature(carState?.temperature ?: 22.0)
                if (carState?.isEngineOn == true) {
                    power.setBackgroundResource(R.drawable.glowing_button) // 빛나는 효과
                } else {
                    power.setBackgroundResource(R.drawable.rounded_button) // 기본 배경 제거
                }

                if(carState?.isLocked==true){
                    close.setBackgroundResource(R.drawable.glowing_button)
                }else{
                    open.setBackgroundResource(R.drawable.glowing_button)
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error getting document", e)
            }
    }


    private fun updateCarState(car: Car) {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val carID = sharedPreferences.getString("carID", null)
        firestore.collection("cars")
            .document(carID!!)
            .set(car)
            .addOnSuccessListener {
                Log.d("Firestore", "Car state updated: ${car!!.temperature}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error updating car state", e)
            }
    }

    private fun formatTemperature(temperature: Double): String {
        return String.format("%.1f", temperature) // 소수점 첫째 자리까지 표시
    }


}