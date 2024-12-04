package com.autoever.carmanager4

import android.app.Service
import android.content.Intent
import android.location.LocationManager
import android.os.IBinder

class NavigationService : Service(){
    val manager = getSystemService(LOCATION_SERVICE) as LocationManager

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}