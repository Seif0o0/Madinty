package com.madinaty.app.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.madinaty.app.databinding.MyPlaceItemBinding
import com.madinaty.app.domain.model.MyPlace

class MyPlacesAdapter(private val clickListener: ListItemClickListener<MyPlace>) :
    ListAdapter<MyPlace, MyPlacesAdapter.ViewHolder>(MyPlacesComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: MyPlaceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(myPlace: MyPlace, clickListener: ListItemClickListener<MyPlace>) {
            binding.myPlace = myPlace
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    MyPlaceItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

    object MyPlacesComparator : DiffUtil.ItemCallback<MyPlace>() {
        override fun areItemsTheSame(oldItem: MyPlace, newItem: MyPlace) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: MyPlace, newItem: MyPlace) = oldItem == newItem
    }
}