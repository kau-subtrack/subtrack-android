package com.please.ui.seller.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import com.please.data.models.seller.ChatMessage
import com.please.data.models.seller.MessageSender
import com.please.databinding.ItemChatbotMessageBinding
import com.please.databinding.ItemUserMessageBinding

class ChatAdapter(
    private val actionButtonClickListener: (String) -> Unit
) : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(ChatDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_CHATBOT = 1
        private const val VIEW_TYPE_USER = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).sender) {
            MessageSender.CHATBOT -> VIEW_TYPE_CHATBOT
            MessageSender.USER -> VIEW_TYPE_USER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CHATBOT -> {
                val binding = ItemChatbotMessageBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ChatbotMessageViewHolder(binding, actionButtonClickListener)
            }
            VIEW_TYPE_USER -> {
                val binding = ItemUserMessageBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                UserMessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is ChatbotMessageViewHolder -> holder.bind(message)
            is UserMessageViewHolder -> holder.bind(message)
        }
    }

    class ChatbotMessageViewHolder(
        private val binding: ItemChatbotMessageBinding,
        private val actionButtonClickListener: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage) {
            binding.tvMessageContent.text = message.content
            binding.tvTimestamp.text = message.getFormattedTime()

            binding.actionButtonsContainer.removeAllViews()
            if (message.actionButtons.isNotEmpty()) {
                binding.actionButtonsContainer.visibility = View.VISIBLE
                for (buttonText in message.actionButtons) {
                    val buttonView = LayoutInflater.from(binding.root.context)
                        .inflate(com.please.R.layout.item_chat_action_button, binding.actionButtonsContainer, false) as Button

                    buttonView.text = buttonText
                    buttonView.setOnClickListener { actionButtonClickListener(buttonText) }

                    val layoutParams = FlexboxLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(8, 8, 8, 8) // 버튼 간격 조정
                    }

                    binding.actionButtonsContainer.addView(buttonView, layoutParams)
                }
            } else {
                binding.actionButtonsContainer.visibility = View.GONE
            }
        }
    }

    class UserMessageViewHolder(
        private val binding: ItemUserMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage) {
            binding.tvMessageContent.text = message.content
            binding.tvTimestamp.text = message.getFormattedTime()
        }
    }

    class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}
