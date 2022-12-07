package com.madinaty.app.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.madinaty.app.databinding.OfferItemBinding
import com.madinaty.app.databinding.PinOfferItemBinding
import com.madinaty.app.domain.model.Offer

class PinOffersAdapter(private val clickListener: ListItemClickListener<Offer>) :
    ListAdapter<Offer, PinOffersAdapter.ViewHolder>(PinOffersComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: PinOfferItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(offer: Offer, clickListener: ListItemClickListener<Offer>) {
            binding.offer = offer
//            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    PinOfferItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }


    object PinOffersComparator : DiffUtil.ItemCallback<Offer>() {
        override fun areItemsTheSame(oldItem: Offer, newItem: Offer) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Offer, newItem: Offer) =
            oldItem == newItem
    }
}