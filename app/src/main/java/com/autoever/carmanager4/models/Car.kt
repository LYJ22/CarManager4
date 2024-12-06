package com.autoever.carmanager4.models

import java.io.Serializable

class Car(
    var model:String="",
    var num:String="",
    var imgName:String="",
    var totalDistance: String = "100,000",
    var leftFrontPre: String = "30",
    var rightFrontPre: String = "35",
    var leftBackPre: String = "38",
    var rightBackPre: String = "32",
    var temperature: Double = 22.0,
    var drivingRange:String = "250",
    var isEngineOn: Boolean = false,
    var isWindowOpen: Boolean = false,
    var isLocked: Boolean = true,
    var imgList: String =""
) : Serializable