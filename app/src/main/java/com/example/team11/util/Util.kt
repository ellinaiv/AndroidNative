package com.example.team11.util

import android.util.Log
import com.example.team11.database.dao.MetadataDao
import com.example.team11.database.entity.WeatherForecastDb
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

//TODO("Hadde vært smooth om vi kunne refaktorert noe av dette, her er det mye kode som går igjen mange steder")
object Util {


    fun getWantedHoursForecastApi(): List<String>{
        var listTimes = arrayListOf<String>()
        val c: Calendar = GregorianCalendar()
        c.time = Date(System.currentTimeMillis())
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        val stringFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        for (hour in 0..DbConstants.NUMB_HOURS_FORECAST){
            listTimes.add(stringFormat.format(c.time))
            c.add(Calendar.HOUR, 1)
            Log.d("Gattering times", stringFormat.format(c.time))
        }
        return listTimes
    }

    fun getWantedDaysForecastApi(): List<String>{
        var listTimes = arrayListOf<String>()
        val c: Calendar = GregorianCalendar()
        c.time = Date(System.currentTimeMillis())
        c.set(Calendar.HOUR, 12)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        val stringFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        for (hour in 1..DbConstants.NUMB_DAYS_FORECAST){
            c.add(Calendar.DATE, 1)
            listTimes.add(stringFormat.format(c.time))
            Log.d("Gattering times", stringFormat.format(c.time))
        }
        return listTimes
    }

    fun formatToHoursTime(time: String): String{
        val parser =  SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val dateTime = parser.parse(time)
        val formatter = SimpleDateFormat("HH")
        return formatter.format(dateTime)
    }

    fun formatToDaysTime(time: String): String{
        val parser =  SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val dateTime = parser.parse(time)
        val formatter = SimpleDateFormat("dd/MM")
        return formatter.format(dateTime)
    }

    fun getNowHourForecastDb(): List<String>{
        var listTimes = arrayListOf<String>()
        val c: Calendar = GregorianCalendar()
        c.time = Date(System.currentTimeMillis())
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        val stringFormat = SimpleDateFormat("HH")
        Log.d("Getting nowTime", stringFormat.format(c.time))
        listTimes.add(stringFormat.format(c.time))
        return listTimes
    }

    fun getWantedHoursForecastDb(): List<String>{
        var listTimes = arrayListOf<String>()
        val c: Calendar = GregorianCalendar()
        c.time = Date(System.currentTimeMillis())
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        val stringFormat = SimpleDateFormat("HH")
        for (hour in 0..DbConstants.NUMB_HOURS_FORECAST){
            listTimes.add(stringFormat.format(c.time))
            c.add(Calendar.HOUR, 1)
            Log.d("Gattering times", stringFormat.format(c.time))
        }
        return listTimes
    }
    fun getWantedDaysForecastDb(): List<String>{
        var listTimes = arrayListOf<String>()
        val c: Calendar = GregorianCalendar()
        c.time = Date(System.currentTimeMillis())
        c.set(Calendar.HOUR, 12)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        val stringFormat = SimpleDateFormat("dd/MM")
        for (hour in 0..DbConstants.NUMB_DAYS_FORECAST){
            c.add(Calendar.DATE, 1)
            listTimes.add(stringFormat.format(c.time))
            Log.d("Gattering times", stringFormat.format(c.time))
        }
        return listTimes
    }

    fun shouldFetch(metadataDao: MetadataDao, nameDatabase: String, timeout: Int, timeUnit: TimeUnit): Boolean{
        val dateLastFetched = metadataDao.getDateLastCached(nameDatabase)
        val now = System.currentTimeMillis()
        val timeoutMilli = timeUnit.toMillis(timeout.toLong())
        Log.d("tagDatabase", "dateLastFetched: $dateLastFetched")

        if(dateLastFetched.equals(0)){
            return true
        }
        if (now - dateLastFetched > timeoutMilli) {
            Log.d("tagDatabse", "Now: $now, dateLastFetcged: $dateLastFetched, timeoutMilli: $timeoutMilli")
            return true
        }
        return false

    }
}