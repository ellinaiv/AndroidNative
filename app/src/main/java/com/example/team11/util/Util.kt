package com.example.team11.util

import android.util.Log
import com.example.team11.database.dao.MetadataDao
import com.example.team11.database.entity.Place
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

//TODO("Hadde vært smooth om vi kunne refaktorert noe av dette, her er det mye kode som går igjen mange steder")
object Util {


    fun getWantedHoursForecastApi(): List<String>{
        val listTimes = arrayListOf<String>()
        val c: Calendar = GregorianCalendar()
        c.time = Date(System.currentTimeMillis())
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        val stringFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        for (hour in 0..Constants.NUMB_HOURS_FORECAST){
            listTimes.add(stringFormat.format(c.time))
            c.add(Calendar.HOUR, 1)
            Log.d("Gattering times", stringFormat.format(c.time))
        }
        return listTimes
    }

    fun getWantedDaysForecastApi(): List<String>{
        val listTimes = arrayListOf<String>()
        val c: Calendar = GregorianCalendar()
        c.time = Date(System.currentTimeMillis())
        c.set(Calendar.HOUR, 12)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        val stringFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        for (hour in 1..Constants.NUMB_DAYS_FORECAST){
            c.add(Calendar.DATE, 1)
            listTimes.add(stringFormat.format(c.time))
            Log.d("Gattering times", stringFormat.format(c.time))
        }
        return listTimes
    }


    private fun getWantedHoursForecastDb(c: Calendar, stringFormat: SimpleDateFormat): List<String>{
        val listTimes = arrayListOf<String>()
        for (hour in 0..Constants.NUMB_HOURS_FORECAST){
            listTimes.add(stringFormat.format(c.time))
            c.add(Calendar.HOUR, 1)
            Log.d("Gattering times", stringFormat.format(c.time))
        }
        Log.d("HOUR", listTimes.toString())
        return listTimes
    }

    private fun getWantedDaysForecastDb(c : Calendar, stringFormat: SimpleDateFormat): List<String>{
        val listTimes = arrayListOf<String>()
        c.set(Calendar.HOUR, 12)
        for (hour in 0 .. Constants.NUMB_DAYS_FORECAST){
            listTimes.add(stringFormat.format(c.time))
            c.add(Calendar.DATE, 1)
        }
        Log.d("DAY", listTimes.toString())
        return listTimes
    }

    fun getWantedForecastDb(hour: Boolean, currentTime: Long): List<String>{
        val c: Calendar = GregorianCalendar()
        c.time = Date(currentTime)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        if(hour) return getWantedHoursForecastDb(c, SimpleDateFormat("HH"))
        return getWantedDaysForecastDb(c, SimpleDateFormat("dd/MM"))
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

    fun getNowHourForecastDb(currentTime: Long): List<String>{
        val listTimes = arrayListOf<String>()
        val c: Calendar = GregorianCalendar()
        c.time = Date(currentTime)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        val stringFormat = SimpleDateFormat("HH")
        Log.d("Getting nowTime", stringFormat.format(c.time))
        listTimes.add(stringFormat.format(c.time))
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

    fun parseXMLPlace(response: String): List<Place>{
        val places = ArrayList<Place>()
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val xpp = factory.newPullParser()
        xpp.setInput(StringReader(response))
        var eventType = xpp.eventType

        lateinit var name: String
        lateinit var lat: String
        lateinit var long: String
        var tempWater = Int.MAX_VALUE
        var id = 0

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && xpp.name == "place") {
                for (i in 0 until xpp.attributeCount) {
                    val attrName = xpp.getAttributeName(i)
                    if (attrName != null && attrName == "id") {
                        id = xpp.getAttributeValue(i).toInt()
                    }
                }
            } else if (eventType == XmlPullParser.START_TAG && xpp.name == "name") {
                xpp.next()
                name = xpp.text
                xpp.next()
            } else if (eventType == XmlPullParser.START_TAG && xpp.name == "lat") {
                xpp.next()
                lat = xpp.text
                xpp.next()

            } else if (eventType == XmlPullParser.START_TAG && xpp.name == "long") {
                xpp.next()
                long = xpp.text
                xpp.next()
            } else if (eventType == XmlPullParser.START_TAG && xpp.name == "temp_vann") {
                tempWater = Int.MAX_VALUE
                if (xpp.next() != XmlPullParser.END_TAG) {
                    tempWater = xpp.text.toInt()
                    xpp.next()
                }
                places.add(
                    Place(
                        id,
                        name,
                        lat.toDouble(),
                        long.toDouble(),
                        tempWater,
                        false
                    )
                )
            }
            eventType = xpp.next()

        }
        return places
    }

}