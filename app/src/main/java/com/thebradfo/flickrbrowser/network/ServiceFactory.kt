package com.thebradfo.flickrbrowser.network

import com.google.gson.Gson
import com.thebradfo.flickrbrowser.BuildConfig
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Named

/**
 * A Factory to provide [api] access to retrofit service instances.
 */
class ServiceFactory @Inject constructor(
    @Named("BaseUri") baseUri: String,
    httpClient: OkHttpClient.Builder,
    gson: Gson
) {
    val api: Retrofit = Retrofit.Builder()
        .baseUrl(baseUri)
        .client(
            httpClient.addInterceptor {
                val request = addBaseHeaders(it.request())

                it.proceed(request)
            }.build()
        )
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private fun addBaseHeaders(
        original: Request
    ): Request =
        original.newBuilder()
            .header("X-ApplicationVersion", BuildConfig.VERSION_NAME)
            .header("User-Agent", "Android ${BuildConfig.APPLICATION_ID}")
            .method(original.method(), original.body())
            .build()

}
