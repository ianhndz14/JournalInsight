package edu.uprb.journalinsight.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // 10.0.2.2 is the Android emulator alias for localhost on the host PC
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val journalService: JournalApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JournalApiService::class.java)
    }
}
