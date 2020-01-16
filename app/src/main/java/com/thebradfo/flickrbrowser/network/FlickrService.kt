package com.thebradfo.flickrbrowser.network

import com.thebradfo.flickrbrowser.models.PhotosResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * The retrofit API layer to communicate with flickr's search api.
 */
interface FlickrService {

    @GET("?method=flickr.photos.search&format=json&nojsoncallback=1")
    fun getPhotos(
        @Query("api_key") apiKey: String,
        @Query("text") searchText: String,
        @Query("per_page") count: Int,
        @Query("page") page: Int
    ): Single<PhotosResponse>
}
