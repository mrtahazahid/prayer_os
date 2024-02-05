package com.iw.android.prayerapp.di

import android.content.Context
import com.iw.android.prayerapp.App
import com.iw.android.prayerapp.utils.TinyDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideContext(): Context{
        // Provide the application context
        // You can replace this with your actual way of obtaining the application context
        return App().applicationContext
    }

    @Provides
    fun provideTinyDB(context: Context?): TinyDB {
        return TinyDB(context)

    }
}