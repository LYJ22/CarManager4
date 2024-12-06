package com.autoever.carmanager4.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.location.Location
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.autoever.carmanager4.R
import com.autoever.carmanager4.WeatherData
import com.autoever.carmanager4.activities.CameraActivity
import com.autoever.carmanager4.activities.MainActivity
import com.autoever.carmanager4.activities.RegisterActivity
import com.autoever.carmanager4.models.Car
import com.google.firebase.firestore.FirebaseFirestore
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

// 홈 페이지
class HomeFragment : Fragment() {
    companion object {
        const val API_KEY = "2a2099f35655e311d8c4c26d95d2db9b"
        const val WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather"
        const val MIN_TIME: Long = 5000
        const val MIN_DISTANCE: Float = 1000F
        const val WEATHER_REQUEST: Int = 102
    }
    private var carState: Car? = null
    private lateinit var weatherState: TextView
    private lateinit var temperature: TextView
    private lateinit var weatherIcon: ImageView
    private lateinit var mLocationManager: LocationManager
    private lateinit var mLocationListener: LocationListener
    private lateinit var carModelTextView: TextView
    private lateinit var carImageView: ImageView
    private lateinit var carNumTextView: TextView
    private lateinit var moreTextView: Button
    private lateinit var textViewRemove: TextView
    //차량 제어
    private lateinit var power: ImageButton
    private lateinit var open: ImageButton
    private lateinit var close: ImageButton
    private val firestore =FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        weatherState = view.findViewById(R.id.weather_tv)
        temperature = view.findViewById(R.id.temperature_tv)
        weatherIcon = view.findViewById(R.id.weather_ic)
        carModelTextView = view.findViewById(R.id.carModelTextView)
        carImageView = view.findViewById(R.id.carImageView)
        carNumTextView = view.findViewById(R.id.carNumTextView)
        moreTextView = view.findViewById(R.id.viewButton)
        moreTextView.setOnClickListener{
           val intent = Intent(requireContext(), CameraActivity::class.java)
            startActivity(intent)
        }
        textViewRemove = view.findViewById(R.id.textViewRemove)
        //차량제어버튼
        open = view.findViewById(R.id.imageButton5)
        close = view.findViewById(R.id.imageButton6)
        power = view.findViewById(R.id.imageButton4)


        setOnClickListener()
        fetchCarInfo()

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

    override fun onResume() {
        super.onResume()
        requestLocationUpdates()
    }

    //carID로 정보 가져오기
    private fun fetchCarInfo() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val carID = sharedPreferences.getString("carID", null)
        firestore.collection("cars").document(carID!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val car = document.toObject(Car::class.java)
                    carState=car
                    carModelTextView.text = car?.model ?: "모델 정보 없음"
//                    carImageView.setImageResource()
                    val resourceId = requireContext().resources.getIdentifier(car?.imgName, "drawable", requireContext().packageName)
                    carImageView.setImageResource(resourceId)
                    carNumTextView.text = car?.num ?: "차 번호 정보 없음"
                } else {
                    Toast.makeText(context, "차 정보가 없습니다", Toast.LENGTH_SHORT).show()
                }
                //시동 on, off 상태 표시
                if (carState?.isEngineOn == true) {
                    power.setBackgroundResource(R.drawable.glowing_button) // 빛나는 효과
                } else {
                    power.setBackgroundResource(R.drawable.rounded_button) // 기본 배경 제거
                }
                // door lock 여부 표시
                if(carState?.isLocked==true){
                    close.setBackgroundResource(R.drawable.glowing_button)
                }else{
                    open.setBackgroundResource(R.drawable.glowing_button)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "차 정보를 불러오는 데 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun requestLocationUpdates() {
        mLocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mLocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val params = RequestParams().apply {
                    put("lat", location.latitude)
                    put("lon", location.longitude+248.67466165)
                    put("appid", API_KEY)
                }
                doNetworking(params)
                Log.d("Location", "Latitude: ${location.latitude}, Longitude: ${location.longitude}")
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                WEATHER_REQUEST
            )
            return
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener)
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener)
    }


    private fun doNetworking(params: RequestParams) {
        var client = AsyncHttpClient()
        val urlWithParams = WEATHER_URL + "?" + params.toString()
        Log.d("Weather API Call", urlWithParams) // 로그 추가
        client.get(WEATHER_URL, params, object: JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                response: JSONObject?
            ) {
                val weatherData =  WeatherData().fromJson(response)
                if (weatherData != null) {
                    updateWeather(weatherData)
                }
            }
        })
    }

    private fun updateWeather(weather: WeatherData) {
        temperature.setText(weather.tempString+" ℃")
        weatherState.setText(weather.weatherType)
        val resourceID = resources.getIdentifier(weather.icon, "drawable", activity?.packageName)
        weatherIcon.setImageResource(resourceID)
    }

    override fun onPause() {
        super.onPause()
        if (mLocationManager!=null) {
            mLocationManager.removeUpdates(mLocationListener)
        }
    }

    fun setOnClickListener() {
        textViewRemove.setOnClickListener {
            // 앱에 저장돼 있는 carID 삭제
            val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("carID")
            editor.commit()

            // 차량등록 액티비티로 이동
            val intent = Intent(requireContext(), RegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }
    //차량 db 변경
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

}