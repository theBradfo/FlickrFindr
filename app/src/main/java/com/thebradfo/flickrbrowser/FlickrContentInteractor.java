package com.thebradfo.flickrbrowser;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.thebradfo.flickrbrowser.models.Photo;
import com.thebradfo.flickrbrowser.network.FlickrService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.Scheduler;
import io.reactivex.Single;

/**
 * An MVP-style interactor responsible for communicating with the network layer and providing
 * search data from the backing network layer.
 */
@Singleton
public class FlickrContentInteractor {

    @VisibleForTesting
    static final String DEFAULT_SEARCH_TERM = "new england patriots";

    @VisibleForTesting
    static final int COUNT = 25;

    private final FlickrService flickrService;
    private final String apiKey;
    private final Scheduler ioScheduler;

    @Inject
    public FlickrContentInteractor(
            final FlickrService flickrService,
            @Named("ApiKey") final String apiKey,
            @Named("IoScheduler") final Scheduler ioScheduler) {
        this.flickrService = flickrService;
        this.apiKey = apiKey;
        this.ioScheduler = ioScheduler;
    }

    /**
     * An api to populate the "home" screen of content, with the backing constant search term.
     * @param page The page number to return results for.
     * @return A single stream of List of Photos matching the search criteria and page.
     */
    public Single<List<Photo>> getHomePhotos(int page) {
        return getPhotos(DEFAULT_SEARCH_TERM, page);
    }

    /**
     * Provides a list of Photo models from the backing retrofit flickrService.
     * @param searchTerm The search text to return results for.
     * @param page The page number to search for.
     * @return A single stream of List of Photos matching the search criteria and page.
     */
    public Single<List<Photo>> getPhotos(@NonNull final String searchTerm, int page) {
        return flickrService.getPhotos(apiKey, searchTerm, COUNT, page)
                .subscribeOn(ioScheduler)
                .map(photosResponse -> photosResponse.getPhotos().getPhotos());
    }
}
