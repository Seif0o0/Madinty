package com.madinaty.app.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.madinaty.app.databinding.AttachmentItemBinding
import com.madinaty.app.domain.model.Attachment

class AttachmentsAdapter(private val clickListener: ListItemClickListener<Attachment>) :
    ListAdapter<Attachment, AttachmentsAdapter.ViewHolder>(AttachmentsComparator) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: AttachmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attachment: Attachment, clickListener: ListItemClickListener<Attachment>) {
            binding.attachment = attachment
//            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    AttachmentItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    object AttachmentsComparator : DiffUtil.ItemCallback<Attachment>() {
        override fun areItemsTheSame(oldItem: Attachment, newItem: Attachment) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Attachment, newItem: Attachment) =
            oldItem == newItem
    }
}