package com.madinaty.app.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.madinaty.app.databinding.DepartmentChildItemBinding
import com.madinaty.app.domain.model.DepartmentChild

class DepartmentChildsAdapter(private val clickListener: ListItemClickListener<DepartmentChild>) :
    ListAdapter<DepartmentChild, DepartmentChildsAdapter.ViewHolder>(DepartmentChildsComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            getItem(position),
            clickListener
        )
    }

    class ViewHolder private constructor(private val binding: DepartmentChildItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            departmentChild: DepartmentChild,
            clickListener: ListItemClickListener<DepartmentChild>
        ) {
            binding.departmentChild = departmentChild
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    DepartmentChildItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    object DepartmentChildsComparator : DiffUtil.ItemCallback<DepartmentChild>() {
        override fun areItemsTheSame(oldItem: DepartmentChild, newItem: DepartmentChild) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: DepartmentChild, newItem: DepartmentChild) =
            oldItem == newItem
    }
}