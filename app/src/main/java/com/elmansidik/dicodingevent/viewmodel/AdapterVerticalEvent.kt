package com.elmansidik.dicodingevent.viewmodel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elmansidik.dicodingevent.data.response_retrofit.response.ListEventsItem
import com.elmansidik.dicodingevent.databinding.CardItemVerticalBinding
import com.elmansidik.dicodingevent.utils.ListEventsDiffUtil

class AdapterVerticalEvent(private val onItemClick: ((Int?) -> Unit)? = null) :
    ListAdapter<ListEventsItem, AdapterVerticalEvent.VerticalEventViewHolder>(ListEventsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalEventViewHolder {
        val binding = CardItemVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VerticalEventViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: VerticalEventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class VerticalEventViewHolder(
        private val binding: CardItemVerticalBinding,
        private val onItemClick: ((Int?) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        // Bind data event ke tampilan
        fun bind(event: ListEventsItem) {
            binding.apply {
                // Mengatur data di UI
                titleEvent.text = event.name
                descriptionEvent.text = event.summary

                // Menggunakan Glide untuk memuat gambar
                Glide.with(imageEvent.context)
                    .load(event.imageLogo)
                    .into(imageEvent)

                // Menambahkan listener klik pada root view
                root.setOnClickListener {
                    onItemClick?.invoke(event.id)
                }
            }
        }
    }
}
