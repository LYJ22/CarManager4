package com.autoever.carmanager4.fragments

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.autoever.carmanager4.R
import com.autoever.carmanager4.models.Car
import com.google.firebase.firestore.FirebaseFirestore

// 제어 페이지
class ControlFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private val cars = mutableListOf<Car>()
    private var selectedCar: Car? = null
    //val car = document.toObject(Car::class.java)

    val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_control, container, false)
        addCar()
        //getCars()
        getCar()
        return view
    }

    fun addCar(){


// 데이터 객체 생성
        val car = Car(model="sonata", num="123")

// 특정 컬렉션과 문서에 저장
        firestore.collection("cars")
            .document("car1") // 문서 ID를 직접 지정
            .set(car)
            .addOnSuccessListener {
                Log.d("Firestore", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error writing document", e)
            }
    }

    fun getCars(){
        db.collection("cars")
            .get()
            .addOnSuccessListener { result ->
                cars.clear()
                for (document in result) {
                    val car = document.toObject(Car::class.java)
                    cars.add(car)
                }
                Toast.makeText(requireContext(), "차량 데이터를 불러왔습니다.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getCar() {
        firestore.collection("cars")
            .document("car1")
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val car = document.toObject(Car::class.java)
                    //Log.d("Firestore", "Car retrieved: $car")
                    Log.d("Firestore", "Car retrieved: ${car!!.model}")
                } else {
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error getting document", e)
            }
    }
}