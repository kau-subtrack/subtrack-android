package com.please.ui.seller.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.please.data.models.AddressItem
import com.please.databinding.ItemAddressResultBinding

class AddressAdapter(
    private val onAddressClick: (AddressItem) -> Unit
) : ListAdapter<AddressItem, AddressAdapter.AddressViewHolder>(AddressDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AddressViewHolder(
        private val binding: ItemAddressResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onAddressClick(getItem(position))
                }
            }
        }

        fun bind(addressItem: AddressItem) {
            binding.textViewAddressTitle.text = addressItem.address
            binding.textViewAddressDetail.text = addressItem.detail
        }
    }

    private class AddressDiffCallback : DiffUtil.ItemCallback<AddressItem>() {
        override fun areItemsTheSame(oldItem: AddressItem, newItem: AddressItem): Boolean {
            return oldItem.address == newItem.address && oldItem.detail == newItem.detail
        }

        override fun areContentsTheSame(oldItem: AddressItem, newItem: AddressItem): Boolean {
            return oldItem == newItem
        }
    }
}
