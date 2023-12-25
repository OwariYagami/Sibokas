package com.overdevx.sibokas_xml.data

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    //private const val BASE_URL = "http://192.168.129.85:8000/api/" // Gantilah dengan base URL API Anda
    private const val BASE_URL = "http://192.168.43.114:8000/api/" // gantilah dengan base url
    //private const val BASE_URL = "http://192.168.14.85:8000/api/"

     val retrofit: ApiService by lazy {
       val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }

    fun BuildingApiService(header: String?, token: String?): ApiService {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val original: Request = chain.request()
                val requestBuilder: Request.Builder = original.newBuilder()

                // Menambahkan header jika disertakan
                header?.let {
                    requestBuilder.header("Your_Header_Name", it)
                }

                // Menambahkan token jika disertakan
                token?.let {
                    requestBuilder.header("Authorization", "Bearer $it")
                }

                val request: Request = requestBuilder.build()
                chain.proceed(request)
            })

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}