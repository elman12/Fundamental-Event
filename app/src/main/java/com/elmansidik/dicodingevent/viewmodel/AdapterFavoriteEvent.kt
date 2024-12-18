package com.elmansidik.dicodingevent.viewmodel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elmansidik.dicodingevent.data.local.database.FavoriteEvent
import com.elmansidik.dicodingevent.databinding.CardItemVerticalBinding

class AdapterFavoriteEvent(private val onItemClick: ((Int?) -> Unit)? = null) :
    ListAdapter<FavoriteEvent, AdapterFavoriteEvent.FavoriteEventViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteEventViewHolder {
        val binding =
            CardItemVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteEventViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: FavoriteEventViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
    }

    class FavoriteEventViewHolder(
        private val binding: CardItemVerticalBinding,
        private val onItemClick: ((Int?) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        // Function to bind the event to the view
        fun bind(event: FavoriteEvent) {
            binding.apply {
                // Set event data to the views
                titleEvent.text = event.name
                descriptionEvent.text = event.description

                // Load image using Glide
                Glide.with(imageEvent.context)
                    .load(event.image)
                    .into(imageEvent)

                // Set click listener for root view
                root.setOnClickListener {
                    onItemClick?.invoke(event.eventId)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<FavoriteEvent> =
            object : DiffUtil.ItemCallback<FavoriteEvent>() {

                // Compare items based on a unique identifier (id)
                override fun areItemsTheSame(oldItem: FavoriteEvent, newItem: FavoriteEvent): Boolean {
                    return oldItem.id == newItem.id
                }

                // Compare the contents of the items
                override fun areContentsTheSame(oldItem: FavoriteEvent, newItem: FavoriteEvent): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
