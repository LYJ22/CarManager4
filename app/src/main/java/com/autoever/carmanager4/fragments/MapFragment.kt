package com.autoever.carmanager4.fragments

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.autoever.carmanager4.R
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons
import java.lang.Exception

// 차량 위치 확인(지도) 페이지
class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var naverMap: NaverMap
    private lateinit var mapView: MapView
    private lateinit var locationManager: LocationManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        mapView = view.findViewById<MapView>(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        return view
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        val marker = Marker()
        marker.icon = MarkerIcons.YELLOW
        marker.position = LatLng(37.480206, 126.881674)
        marker.map = naverMap
        // 카메라 업데이트
        var cameraUpdate = CameraUpdate.scrollTo(marker.position)
        naverMap.moveCamera(cameraUpdate)
        cameraUpdate = CameraUpdate.zoomTo(16.0)
        naverMap.moveCamera(cameraUpdate)

    }

}