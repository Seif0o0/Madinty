package com.madinaty.app.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.madinaty.app.databinding.RegionItemBinding
import com.madinaty.app.domain.model.Region

class RegionsAdapter(
    private val clickListener: ListItemClickListener<Region>
) : PagingDataAdapter<Region, RegionsAdapter.ViewHolder>(RegionsComparator) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    class ViewHolder private constructor(private val binding: RegionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(region: Region, clickListener: ListItemClickListener<Region>) {
            binding.region = region
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    RegionItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    object RegionsComparator : DiffUtil.ItemCallback<Region>() {
        override fun areContentsTheSame(oldItem: Region, newItem: Region) = oldItem == newItem
        override fun areItemsTheSame(oldItem: Region, newItem: Region) = oldItem.id == newItem.id
    }

}