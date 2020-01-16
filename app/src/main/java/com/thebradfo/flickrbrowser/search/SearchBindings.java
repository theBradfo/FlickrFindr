package com.thebradfo.flickrbrowser.search;

import com.thebradfo.flickrbrowser.di.FragmentScope;

import dagger.Binds;
import dagger.Module;

@Module
abstract class SearchBindings {

    @FragmentScope
    @Binds
    abstract SearchPresenter.SearchView bindView(SearchFragment searchFragment);
}
