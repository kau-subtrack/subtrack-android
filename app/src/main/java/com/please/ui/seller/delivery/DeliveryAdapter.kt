package com.please.ui.seller.delivery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.please.data.models.seller.DeliveryInfo
import com.please.data.models.seller.DeliveryStatus
import com.please.databinding.ItemDeliveryBinding

class DeliveryAdapter(
    private val onDeleteClick: (String) -> Unit,
    private val getDeliveryStatusColor: (DeliveryStatus) -> Int,
    private val getDeliveryStatusText: (DeliveryStatus) -> String
) : ListAdapter<DeliveryInfo, DeliveryAdapter.DeliveryViewHolder>(DIFF_CALLBACK) {

    private var isDeleteButtonVisible = true

    fun setDeleteButtonVisible(visible: Boolean) {
        isDeleteButtonVisible = visible
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val binding = ItemDeliveryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DeliveryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        val delivery = getItem(position)
        holder.bind(delivery, isDeleteButtonVisible)
    }

    inner class DeliveryViewHolder(
        private val binding: ItemDeliveryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        //받은 deliveryInfo 양식으로 리사이클 아이템 생성
        fun bind(delivery: DeliveryInfo, isDeleteButtonVisible: Boolean) {
            val trackingNumber = delivery.trackingNumber ?: ""
            binding.tvTrackingNumber.text = trackingNumber
            
            // 배송 상태 관련 UI (조회 모드에서 표시)
            if (!isDeleteButtonVisible) {
                binding.tvDeliveryStatus.apply {
                    text = getDeliveryStatusText(delivery.status)
                    setTextColor(getDeliveryStatusColor(delivery.status))
                    visibility = View.VISIBLE
                }
                binding.ivDeliveryArrow.visibility = View.VISIBLE
                binding.btnDeleteDelivery.visibility = View.GONE
            } else {
                // 배송 정보 관련 UI (등록 모드에서 표시)
                binding.tvDeliveryStatus.visibility = View.GONE
                binding.ivDeliveryArrow.visibility = View.GONE
                
                // 제품명 및 수령인 정보 표시 (등록 모드)
                binding.tvProductInfo.text = "${delivery.productName} - ${delivery.recipientName}"
                binding.tvProductInfo.visibility = View.VISIBLE
                
                binding.tvContactPhone.text = "연락처: ${delivery.recipientPhone}"
                binding.tvContactPhone.visibility = View.VISIBLE
                
                binding.tvAddress.text = "주소: ${delivery.address}"
                binding.tvAddress.visibility = View.VISIBLE
                
                // 삭제 버튼
                binding.btnDeleteDelivery.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        onDeleteClick(delivery.id)
                    }
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DeliveryInfo>() {
            override fun areItemsTheSame(oldItem: DeliveryInfo, newItem: DeliveryInfo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DeliveryInfo, newItem: DeliveryInfo): Boolean {
                return oldItem == newItem
            }
        }
    }
}
