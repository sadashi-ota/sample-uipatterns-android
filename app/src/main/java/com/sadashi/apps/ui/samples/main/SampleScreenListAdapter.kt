package com.sadashi.apps.ui.samples.main

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.sadashi.apps.ui.samples.R
import com.sadashi.apps.ui.samples.extensions.inflate
import kotlinx.android.synthetic.main.item_sample_screen.view.*
import kotlin.properties.Delegates

class SampleScreenListAdapter: RecyclerView.Adapter<SampleScreenListAdapter.ViewHolder>() {
    var collection: List<SampleScreenItem> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var clickListener: (SampleScreenItem) -> Unit = { _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(parent.inflate(R.layout.item_sample_screen))

    override fun getItemCount() = collection.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = collection.getOrNull(position) ?: return
        holder.bind(item, clickListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: SampleScreenItem, clickListener: (SampleScreenItem) -> Unit) {
            itemView.name.text = item.name
            itemView.rootView.setOnClickListener {
                clickListener(item)
            }
        }

    }
}