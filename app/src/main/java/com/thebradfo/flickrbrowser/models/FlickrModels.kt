package com.thebradfo.flickrbrowser.models

import com.google.gson.annotations.SerializedName

/**
 * The raw response wrapper from the flickr api.
 */
data class PhotosResponse(
    val photos: PhotosCollection
)

/**
 * A wrapper of [Photo] objects from the response payload, containing [page] metadata.
 */
data class PhotosCollection(
    val page: Int,
    val pages: Int,
    @SerializedName("perpage") val numPerPage: Int,
    val total: Int,
    @SerializedName("photo") val photos: List<Photo>
)

/**
 * An individual Flickr photo item.
 */
data class Photo(
    val id: String,
    val owner: String,
    val secret: String,
    val server: String,
    val title: String,
    @SerializedName("ispublic") val isPublic: String
) {

    val imageUri: String
        get() = "https://farm1.staticflickr.com/$server/${id}_$secret.jpg"
}
