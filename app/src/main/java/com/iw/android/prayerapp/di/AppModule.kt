package com.iw.android.prayerapp.di

import android.content.Context
import androidx.databinding.ktx.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.iw.android.prayerapp.App
import com.iw.android.prayerapp.base.network.BaseApi
import com.iw.android.prayerapp.notificationService.Notification
import com.iw.android.prayerapp.utils.TinyDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.DelicateCoroutinesApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideContext(): Context{
        // Provide the application context
        // You can replace this with your actual way of obtaining the application context
        return App().applicationContext
    }

    @Singleton
    @Provides
    fun provideNotification(@ApplicationContext context: Context): Notification {
        return Notification(context)
    }

    @DelicateCoroutinesApi
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }
    @DelicateCoroutinesApi
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val builder = UnsafeOkHttpClient.unsafeOkHttpClient.newBuilder()
            .addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder().also {
                    it.addHeader("Accept", "application/json")
                }.build())
            }

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addInterceptor(logging)
        }

        return builder.build()
    }

    @DelicateCoroutinesApi
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://server.appsstaging.com:3017") // Replace with your base URL
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }


    @Provides
    fun provideTinyDB(context: Context?): TinyDB {
        return TinyDB(context)

    }

    @DelicateCoroutinesApi
    @Provides
    fun provideBaseApi(
        retrofit: Retrofit
    ): BaseApi {
        return retrofit.create(BaseApi::class.java)
    }
}