package com.iw.android.prayerapp.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.iw.android.prayerapp.App
import com.iw.android.prayerapp.notificationService.Notification
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.DelicateCoroutinesApi
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideContext(): Context {
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

}

