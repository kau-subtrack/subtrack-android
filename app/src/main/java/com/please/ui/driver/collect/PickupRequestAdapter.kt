package com.please.ui.driver.collect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.please.R
import com.please.data.models.driver.PickList

class PickupRequestAdapter(
    private val items: MutableList<PickList>,
    private val onCompleteClick: (position: Int) -> Unit
) : RecyclerView.Adapter<PickupRequestAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addressText: TextView = itemView.findViewById(R.id.tv_collection_number)
        val productInfoText: TextView = itemView.findViewById(R.id.tv_product_details)
        val completeButton: TextView = itemView.findViewById(R.id.btn_collection_complete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collection_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            if (position < items.size) {
                val item = items[position]
                
                // 주소 표시
                holder.addressText.text = "${item.address}, ${item.detailAddress}"
                
                // 상품 정보 및 수거 시간대 표시
                val parcelCountText = if (item.parcelCount > 1) "${item.parcelCount}개" else "1개"
                holder.productInfoText.text = "제품명: ${item.productName} ($parcelCountText)\n수거시간: ${item.pickupTimeWindow}"
                
                // 수거 완료 버튼 설정
                holder.completeButton.setOnClickListener {
                    onCompleteClick(holder.adapterPosition)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return try {
            items.size
        } catch (e: Exception) {
            0
        }
    }
}