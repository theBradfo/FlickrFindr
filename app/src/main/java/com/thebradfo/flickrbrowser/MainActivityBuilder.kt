package com.thebradfo.flickrbrowser

import com.thebradfo.flickrbrowser.di.ActivityScope
import com.thebradfo.flickrbrowser.home.HomeModule
import com.thebradfo.flickrbrowser.search.SearchModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Dagger module for [MainActivity].
 */
@Module
abstract class MainActivityBuilder {

    @ContributesAndroidInjector(modules = [
        MainActivityModule::class,
        HomeModule::class,
        SearchModule::class])
    @ActivityScope
    abstract fun bindMainActivity(): MainActivity
}

@Module
abstract class MainActivityModule
