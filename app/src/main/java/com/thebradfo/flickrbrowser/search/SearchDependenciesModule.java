package com.thebradfo.flickrbrowser.search;

import com.thebradfo.flickrbrowser.di.FragmentScope;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.ViewHolder;

import dagger.Module;
import dagger.Provides;

@Module
public class SearchDependenciesModule {

    @FragmentScope
    @Provides
    GroupAdapter<ViewHolder> provideGroupAdapter() {
        return new GroupAdapter<>();
    }
}
