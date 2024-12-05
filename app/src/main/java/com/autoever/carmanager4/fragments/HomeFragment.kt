package com.autoever.carmanager4.fragments

import android.Manifest
import android.content.Context
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
import androidx.core.app.ActivityCompat
import com.autoever.carmanager4.R
import com.autoever.carmanager4.WeatherData
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import retrofit2.Response

// 홈 페이지
class HomeFragment : Fragment() {
    companion object {
        const val API_KEY = "2a2099f35655e311d8c4c26d95d2db9b"
        const val WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather"
        const val MIN_TIME: Long = 5000
        const val MIN_DISTANCE: Float = 1000F
        const val WEATHER_REQUEST: Int = 102
    }

    private lateinit var weatherState: TextView
    private lateinit var temperature: TextView
    private lateinit var weatherIcon: ImageView
    private lateinit var weatherTip: TextView
    private lateinit var mLocationManager: LocationManager
    private lateinit var mLocationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        weatherState = view.findViewById(R.id.weather_tv)
        temperature = view.findViewById(R.id.temperature_tv)
        weatherIcon = view.findViewById(R.id.weather_ic)
        return view
    }

    override fun onResume() {
        super.onResume()
        getWeatherInCurrentLocation()
    }

    private fun getWeatherInCurrentLocation() {
        mLocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mLocationListener = LocationListener { p0 ->
            val params: RequestParams = RequestParams()
            params.put("lat",p0.latitude)
            params.put("lon",p0.longitude)
            params.put("appid",Companion.API_KEY)
            doNetworking(params)
        }
        if (ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION), WEATHER_REQUEST)
            return
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener)
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME, MIN_DISTANCE, mLocationListener)
    }
    private fun doNetworking(params: RequestParams) {
        var client = AsyncHttpClient()

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

}