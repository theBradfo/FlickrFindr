package com.thebradfo.flickrbrowser.search;

import com.thebradfo.flickrbrowser.di.FragmentScope;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Dagger infra for the SearchFragment.
 */
@Module
public abstract class SearchModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = {SearchDependenciesModule.class, SearchBindings.class})
    abstract SearchFragment contributeSearchFragment();
}
