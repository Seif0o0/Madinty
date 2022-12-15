package com.madinaty.app.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.madinaty.app.databinding.PlaceItemBinding
import com.madinaty.app.domain.model.Place

class PagingPlacesAdapter(
    private var clickListener: ListItemClickListener<Place>,
    private val favClickListener: ListItemClickListener<String>
) : PagingDataAdapter<Place, PagingPlacesAdapter.ViewHolder>(PlacesComparator) {

    fun updatePlace(placeId: String) {
        val place = snapshot().firstOrNull { snapShotPlace ->
            placeId == snapShotPlace?.id
        }

        if (place != null) {
            place.isFavourite = !place.isFavourite
            notifyItemChanged(snapshot().indexOf(place), Unit)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener, favClickListener)
    }

    class ViewHolder private constructor(private val binding: PlaceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            place: Place,
            clickListener: ListItemClickListener<Place>,
            favClickListener: ListItemClickListener<String>
        ) {
            binding.place = place
            binding.clickListener = clickListener
            binding.favClickListener = favClickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    PlaceItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    object PlacesComparator : DiffUtil.ItemCallback<Place>() {
        override fun areContentsTheSame(oldItem: Place, newItem: Place) = oldItem == newItem
        override fun areItemsTheSame(oldItem: Place, newItem: Place) = oldItem.id == newItem.id
    }
}