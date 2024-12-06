package com.autoever.carmanager4.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.autoever.carmanager4.R
import com.autoever.carmanager4.models.Car
import com.google.firebase.firestore.FirebaseFirestore

// 상태 페이지
class StatusFragment : Fragment() {
    private var carState: Car? = null
    private lateinit var carModel: TextView
    private lateinit var totalDis:TextView
    private lateinit var drivingDis:TextView
    private lateinit var leftFront:TextView
    private lateinit var rightFront:TextView
    private lateinit var leftBack:TextView
    private lateinit var rightBack:TextView

    val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_status, container, false)

        carModel = view.findViewById(R.id.textView21)
        totalDis = view.findViewById(R.id.textView14)
        drivingDis = view.findViewById(R.id.textView12)
        leftBack = view.findViewById(R.id.textView19)
        leftFront = view.findViewById(R.id.textView17)
        rightBack = view.findViewById(R.id.textView20)
        rightFront = view.findViewById(R.id.textView18)


        getCar()
        // Inflate the layout for this fragment
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
                totalDis.text = carState!!.totalDistance+"km"
                drivingDis.text = carState!!.drivingRange+"km"
                leftBack.text = carState!!.leftBackPre
                leftFront.text = carState!!.leftFrontPre
                rightBack.text = carState!!.rightBackPre
                rightFront.text= carState!!.rightFrontPre





            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error getting document", e)
            }
    }
}