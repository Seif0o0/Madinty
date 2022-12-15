package com.madinaty.app.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.madinaty.app.databinding.PlaceItemBinding
import com.madinaty.app.domain.model.Place

class PlacesAdapter(
    private val clickListener: ListItemClickListener<Place>,
    private val favClickListener: ListItemClickListener<String>
) :
    ListAdapter<Place, PlacesAdapter.ViewHolder>(PlacesComparator) {

    fun updatePlace(placeId: String) {
        val currentMutableList = currentList.toMutableList()
        val place = currentMutableList.firstOrNull { item ->
            item.id == placeId
        }

        place?.let { item ->
            item.isFavourite = !item.isFavourite
            val index = currentMutableList.indexOf(item)
            currentMutableList[index] = item
            submitList(currentMutableList)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener, favClickListener)
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
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

    object PlacesComparator : DiffUtil.ItemCallback<Place>() {
        override fun areItemsTheSame(oldItem: Place, newItem: Place) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Place, newItem: Place) = oldItem == newItem
    }
}