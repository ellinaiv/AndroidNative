package com.example.team11.api

import com.example.team11.database.entity.WeatherForecast
import com.example.team11.valueObjects.OceanForecast
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Klasse som henter data fra https://in2000-apiproxy.ifi.uio.no/weatherapi/
 * @Return serviceApiInterface som inneholder de ulike metodene for å hente ulik data
 */
object ApiClient {

    private val API_BASE_URL = "https://in2000-apiproxy.ifi.uio.no/weatherapi/"

    private var servicesApiInterface: ServicesApiInterface?=null

    fun build(): ServicesApiInterface?{
        val builder: Retrofit.Builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

        val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
        httpClient.addInterceptor(interceptor())

        val retrofit: Retrofit = builder.client(httpClient.build()).build()
        servicesApiInterface = retrofit.create(
            ServicesApiInterface::class.java)

        return servicesApiInterface as ServicesApiInterface
    }

    /**
     * Metode for http-logging, logger http-resultat, feilkoder og alle kall til API
     */
    private fun interceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level=HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    /**
     * Interface for de ulike metodene for å hente ulik data
     */
    interface ServicesApiInterface{

        @GET("oceanforecast/0.9/.json?")
        fun getSeaSpeed(@Query("lat") lat: Double, @Query("lon") lon: Double): Call<OceanForecast>

        @GET("locationforecast/2.0/.json?")
        fun getWeather(@Query("lat") lat: Double, @Query("lon") lon: Double): Call<WeatherForecast>
    }
}
