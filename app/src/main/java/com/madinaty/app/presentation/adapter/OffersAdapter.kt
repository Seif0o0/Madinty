package com.madinaty.app.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.madinaty.app.databinding.OfferItemBinding
import com.madinaty.app.domain.model.Offer
import javax.inject.Inject

class OffersAdapter @Inject constructor() :
    PagingDataAdapter<Offer, OffersAdapter.ViewHolder>(OffersComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    class ViewHolder private constructor(private val binding: OfferItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(offer: Offer) {
            binding.offer = offer

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    OfferItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    object OffersComparator : DiffUtil.ItemCallback<Offer>() {
        override fun areItemsTheSame(oldItem: Offer, newItem: Offer) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Offer, newItem: Offer) =
            oldItem == newItem
    }
}