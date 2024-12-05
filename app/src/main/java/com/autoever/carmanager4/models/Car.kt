package com.autoever.carmanager4.models

class Car(
    val model:String="",
    val num:String="",
    var totalDistance: Int = 0,
    var leftFrontPre: Int = 0,
    var rightFrontPre: Int = 0,
    var leftBackPre: Int = 0,
    var rightBackPre: Int = 0,
    var temperature: Int = 0,
    var drivingRange:Int = 0,
    var isEngineOn: Boolean = false,
    var isWindowOpen: Boolean = false,
    var isLocked: Boolean = true
)