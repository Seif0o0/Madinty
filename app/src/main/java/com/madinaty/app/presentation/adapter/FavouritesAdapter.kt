package com.madinaty.app.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.madinaty.app.domain.model.Favourite
import com.madinaty.app.databinding.FavouritesItemBinding

class FavouritesAdapter(
    private val clickListener: ListItemClickListener<Favourite>,
    private val favClickListener: ListItemClickListener<String>
) :
    ListAdapter<Favourite, FavouritesAdapter.ViewHolder>(FavouritesComparator) {

    fun removeItem(favouriteId: String) {
        val currentMutableList = currentList.toMutableList()
        val favourite = currentMutableList.firstOrNull { item ->
            item.id == favouriteId
        }
        favourite?.let { item ->
            currentMutableList.remove(item)
            submitList(currentMutableList)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener, favClickListener)
    }

    class ViewHolder private constructor(private val binding: FavouritesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            favourite: Favourite,
            clickListener: ListItemClickListener<Favourite>,
            favClickListener: ListItemClickListener<String>
        ) {
            binding.favourite = favourite
            binding.clickListener = clickListener
            binding.favClickListener = favClickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    FavouritesItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

    object FavouritesComparator : DiffUtil.ItemCallback<Favourite>() {
        override fun areItemsTheSame(oldItem: Favourite, newItem: Favourite) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Favourite, newItem: Favourite) = oldItem == newItem
    }
}