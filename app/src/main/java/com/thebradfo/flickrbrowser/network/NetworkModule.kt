package com.thebradfo.flickrbrowser.network

import com.google.gson.Gson
import com.thebradfo.flickrbrowser.BuildConfig
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

/**
 * Dagger infra to support the networking layer.
 */
@Module
object NetworkModule {

    @Provides
    @JvmStatic
    @Named("ApiKey")
    fun provideApiKey(): String = BuildConfig.FLICKR_KEY

    @JvmStatic
    @Singleton
    @Provides
    @Named("BaseUri")
    fun provideBaseUri(): String = "https://www.flickr.com/services/rest/"

    @JvmStatic
    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient.Builder = OkHttpClient.Builder()

    @JvmStatic
    @Singleton
    @Provides
    fun provideGson(): Gson = Gson()

    @JvmStatic
    @Singleton
    @Provides
    fun provideFlickrService(serviceFactory: ServiceFactory): FlickrService =
        serviceFactory.api.create(FlickrService::class.java)
}
