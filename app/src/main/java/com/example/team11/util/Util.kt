package com.example.team11.util

import android.util.Log
import com.example.team11.database.dao.MetadataDao
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object Util {
    fun getForecastTimesHours(): List<String>{
        var listTimer = arrayListOf<String>()
        val c: Calendar = GregorianCalendar()
        c.time = Date(System.currentTimeMillis())
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        val stringFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        for (hour in 1..DbConstants.NUMB_HOURS_FORECAST){
            c.add(Calendar.HOUR, 1)
            listTimer.add(stringFormat.format(c.time))
            Log.d("Gattering times", stringFormat.format(c.time))
        }
        return listTimer
    }

    fun getForecastTimesDays(): List<String>{
        var listTimer = arrayListOf<String>()
        val c: Calendar = GregorianCalendar()
        c.time = Date(System.currentTimeMillis())
        c.set(Calendar.HOUR, 12)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        val stringFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        for (hour in 1..DbConstants.NUMB_HOURS_FORECAST){
            c.add(Calendar.DATE, 1)
            listTimer.add(stringFormat.format(c.time))
            Log.d("Gattering times", stringFormat.format(c.time))
        }
        return listTimer
    }

    fun shouldFetch(metadataDao: MetadataDao, nameDatabase: String, timeout: Int, timeUnit: TimeUnit): Boolean{
        val dateLastFetched = metadataDao.getDateLastCached(nameDatabase)
        val now = System.currentTimeMillis()
        val timeoutMilli = timeUnit.toMillis(timeout.toLong())
        Log.d("tagDatabase", "dateLastFetched: $dateLastFetched")

        if(dateLastFetched == null){
            return true
        }
        if (now - dateLastFetched > timeoutMilli) {
            Log.d("tagDatabse", "Now: $now, dateLastFetcged: $dateLastFetched, timeoutMilli: $timeoutMilli")
            return true
        }
        return false

    }
}