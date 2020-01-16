package com.thebradfo.flickrbrowser.home

import com.thebradfo.flickrbrowser.di.FragmentScope
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

/**
 * Dagger infra to support the [HomeFragment].
 */
@Module
internal abstract class HomeModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [HomeBindings::class, HomeDependencies::class])
    internal abstract fun contributeHomeFragment(): HomeFragment
}

@Module
class HomeDependencies {

    @FragmentScope
    @Provides
    fun provideGroupAdapter(): GroupAdapter<ViewHolder> = GroupAdapter()

}

@Module
internal abstract class HomeBindings {

    @FragmentScope
    @Binds
    abstract fun bindView(homeFragment: HomeFragment): HomePresenter.HomeView

}
