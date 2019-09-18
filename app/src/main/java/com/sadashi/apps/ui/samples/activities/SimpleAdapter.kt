package com.sadashi.apps.ui.samples.activities

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.sadashi.apps.ui.samples.R
import com.sadashi.apps.ui.samples.extensions.inflate
import kotlinx.android.synthetic.main.item_simple.view.*
import kotlin.properties.Delegates

data class SimpleItem(val message: String)

class SimpleAdapter: RecyclerView.Adapter<SimpleAdapter.ViewHolder>() {
    var collection: List<SimpleItem> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var clickListener: (SimpleItem) -> Unit = { _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(parent.inflate(R.layout.item_simple))

    override fun getItemCount() = collection.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = collection.getOrNull(position) ?: return
        holder.bind(item, clickListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: SimpleItem, clickListener: (SimpleItem) -> Unit) {
            itemView.message.text = item.message
            itemView.rootView.setOnClickListener {
                clickListener(item)
            }
        }
    }
}