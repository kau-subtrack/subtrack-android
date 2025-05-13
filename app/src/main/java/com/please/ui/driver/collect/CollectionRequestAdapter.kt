package com.please.ui.driver.collect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.please.R
import com.please.data.models.driver.CollectionRequest

class CollectionRequestAdapter(
    private val items: MutableList<CollectionRequest>,
    private val onCompleteClick: (position: Int) -> Unit
) : RecyclerView.Adapter<CollectionRequestAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val numberText: TextView = itemView.findViewById(R.id.tv_collection_number)
        val detailsText: TextView = itemView.findViewById(R.id.tv_product_details)
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
                
                holder.numberText.text = item.trackingNumber
                holder.detailsText.text = "제품명: ${item.productDetails}"
                
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
