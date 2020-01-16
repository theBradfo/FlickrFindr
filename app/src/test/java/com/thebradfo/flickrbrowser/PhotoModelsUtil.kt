package com.thebradfo.flickrbrowser

import com.thebradfo.flickrbrowser.models.Photo
import com.thebradfo.flickrbrowser.models.PhotosCollection
import com.thebradfo.flickrbrowser.models.PhotosResponse

/**
 * A utility to provide simplified access to instantiation of Flickr data models.
 */
object PhotoModelsUtil {

    fun getPhotoResponse(photos: List<Photo>): PhotosResponse {
        val photoCollection = PhotosCollection(
            0,
            1,
            2,
            3,
            photos
        )
        return PhotosResponse(photoCollection)
    }

    fun getPhoto(id: String) = Photo(id, "", "", "", "", "")
}
