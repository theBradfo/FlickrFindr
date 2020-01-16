package com.thebradfo.flickrbrowser.items

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.thebradfo.flickrbrowser.R
import com.thebradfo.flickrbrowser.models.Photo
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.item_flickr_image.view.*

/**
 * A generic groupie [Item] to host an individual photo result from Flickr.
 */
data class ImageItem(
    val photo: Photo,
    val onClick: (photoUri: String, description: String?) -> Unit
): Item<ViewHolder>() {
    override fun getLayout(): Int = R.layout.item_flickr_image

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {

            Glide.with(context)
                .load(photo.imageUri)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .placeholder(R.drawable.ic_image_24px)
                .error(R.drawable.ic_broken_image_24px)
                .fitCenter()
                .into(image_item)

            item_card_view.setOnClickListener { onClick(photo.imageUri, photo.title) }

            description.text = photo.title
        }
    }

    override fun getId(): Long = photo.id.toLong()
}
