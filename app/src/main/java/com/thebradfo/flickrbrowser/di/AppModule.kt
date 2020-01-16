package com.thebradfo.flickrbrowser.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.thebradfo.flickrbrowser.FlickrBrowserApplication
import com.thebradfo.flickrbrowser.MainActivityBuilder
import com.thebradfo.flickrbrowser.getFlickrBrowserPrefs
import com.thebradfo.flickrbrowser.network.NetworkModule
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Named
import javax.inject.Singleton

/**
 * Dagger [Module] to provide RX [Scheduler] instances.
 */
@Module
object RxModule {

    @JvmStatic
    @Singleton
    @Provides
    @Named("MainScheduler")
    fun provideMainScheduler(): Scheduler = AndroidSchedulers.mainThread()

    @JvmStatic
    @Singleton
    @Provides
    @Named("IoScheduler")
    fun provideIoScheduler(): Scheduler = Schedulers.io()

    @JvmStatic
    @Singleton
    @Provides
    @Named("ComputationScheduler")
    fun provideComputationScheduler(): Scheduler = Schedulers.computation()
}

/**
 * Dagger [Module] to provide context-specific app dependencies.
 */
@Module
object AppModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences =
        context.getFlickrBrowserPrefs()
}

/**
 * Dagger [Module] to bind top-level generic application-scoped dependencies.
 */
@Module
abstract class AppBindings {
    @Binds
    abstract fun bindApplication(application: FlickrBrowserApplication): Application

    @Binds
    abstract fun bindContext(application: FlickrBrowserApplication): Context
}

/**
 * Dagger application component for [FlickrBrowserApplication].
 */
@Singleton
@Component(
        modules = [
            // necessary for dagger android:
            AndroidInjectionModule::class,

            // singleton-hosting things
            AppBindings::class,
            AppModule::class,
            NetworkModule::class,
            RxModule::class,

            // activity modules
            MainActivityBuilder::class
        ]
)
interface AppComponent : AndroidInjector<FlickrBrowserApplication> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<FlickrBrowserApplication>()
}
