package com.madinaty.app.presentation.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.madinaty.app.databinding.PickImageItemBinding
import com.madinaty.app.databinding.PickedImageItemBinding
import com.madinaty.app.presentation.adapter.PickedImagesAdapter.Const.pickImageViewType
import com.madinaty.app.presentation.adapter.PickedImagesAdapter.Const.pickedImageViewType

class PickedImagesAdapter(
    private val clickListener: ListItemClickListener<Uri>,
    private val addImageClickListener: ListItemClickListener<Unit>
) : ListAdapter<Uri, RecyclerView.ViewHolder>(PickedImagesComparator) {

    private object Const {
        const val pickImageViewType = 0
        const val pickedImageViewType = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) pickImageViewType else pickedImageViewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == pickImageViewType) AddImageViewHolder.from(parent) else ViewHolder.from(
            parent
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == pickImageViewType) {
            (holder as AddImageViewHolder).bind(addImageClickListener)
        } else {
            (holder as ViewHolder).bind(getItem(position), clickListener)
        }
    }

    class AddImageViewHolder private constructor(private val binding: PickImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: ListItemClickListener<Unit>) {
            binding.setUnit(Unit)
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): AddImageViewHolder {
                return AddImageViewHolder(
                    PickImageItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

    class ViewHolder private constructor(private val binding: PickedImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            image: Uri, clickListener: ListItemClickListener<Uri>
        ) {
            binding.image = image
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    PickedImageItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

    object PickedImagesComparator : DiffUtil.ItemCallback<Uri>() {
        override fun areItemsTheSame(oldItem: Uri, newItem: Uri) = oldItem == newItem

        override fun areContentsTheSame(oldItem: Uri, newItem: Uri) = oldItem == newItem

    }

}