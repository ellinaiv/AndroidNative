package com.example.team11


data class ResponseWeather(val status:Int?, val msg:String?, val data:Array<WeatherForecast>?){
    fun isSuccess():Boolean= (status==200)
}