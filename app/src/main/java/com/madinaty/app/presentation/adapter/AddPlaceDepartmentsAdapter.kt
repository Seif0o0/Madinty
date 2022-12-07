package com.madinaty.app.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.madinaty.app.databinding.AddPlaceDepartmentItemBinding
import com.madinaty.app.databinding.DepartmentItemBinding
import com.madinaty.app.domain.model.Department
import javax.inject.Inject

class AddPlaceDepartmentsAdapter(
    private var clickListener: ListItemClickListener<Department>
) : PagingDataAdapter<Department, AddPlaceDepartmentsAdapter.ViewHolder>(DepartmentsComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    class ViewHolder private constructor(private val binding: AddPlaceDepartmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(department: Department, clickListener: ListItemClickListener<Department>) {
            binding.department = department
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    AddPlaceDepartmentItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }


    object DepartmentsComparator : DiffUtil.ItemCallback<Department>() {
        override fun areItemsTheSame(oldItem: Department, newItem: Department) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Department, newItem: Department) =
            oldItem == newItem
    }
}