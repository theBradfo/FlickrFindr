package com.thebradfo.flickrbrowser.items

import com.thebradfo.flickrbrowser.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.item_search_history.view.*

/**
 * A groupie [Item] representing a historical search term.
 */
data class SearchItem(
    val searchTerm: String,
    val onClick:(searchTerm: String) -> Unit
): Item<ViewHolder>() {
    override fun getLayout(): Int = R.layout.item_search_history

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.search_term.text = searchTerm
        viewHolder.itemView.setOnClickListener { onClick(searchTerm) }
    }

}
