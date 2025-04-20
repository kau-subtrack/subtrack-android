package com.example.subtrack.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.subtrack.R
import com.example.subtrack.data.model.SendItem

class SendItemAdapter(private val isQuery: Boolean) :
    ListAdapter<SendItem, SendItemAdapter.SendViewHolder>(diffUtil) {

    inner class SendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // 공통
        val tvTrackingNumber: TextView = view.findViewById(R.id.tvTrackingNumber)

        // query 모드
        val tvStatus: TextView? = view.findViewById(R.id.tvStatus)

        // register 모드
        val tvName: TextView? = view.findViewById(R.id.tvName)
        val tvPhone: TextView? = view.findViewById(R.id.tvPhone)
        val tvAddress: TextView? = view.findViewById(R.id.tvAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SendViewHolder {
        val layoutId = if (isQuery) R.layout.item_send_query else R.layout.item_send_register
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return SendViewHolder(view)
    }

    override fun onBindViewHolder(holder: SendViewHolder, position: Int) {
        val item = getItem(position)
        holder.tvTrackingNumber.text = item.trackingNumber

        if (isQuery) {
            holder.tvStatus?.text = item.status

            // 상태별 색상
            val color = when (item.status) {
                "배송완료" -> R.color.green
                "배송중" -> R.color.blue
                else -> android.R.color.black
            }
            holder.tvStatus?.setTextColor(ContextCompat.getColor(holder.itemView.context, color))
        } else {
            holder.tvName?.text = item.name
            holder.tvPhone?.text = "수령인 전화번호: ${item.phone}"
            holder.tvAddress?.text = "주소: ${item.address}"
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<SendItem>() {
            override fun areItemsTheSame(oldItem: SendItem, newItem: SendItem): Boolean {
                return oldItem.trackingNumber == newItem.trackingNumber
            }

            override fun areContentsTheSame(oldItem: SendItem, newItem: SendItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}

