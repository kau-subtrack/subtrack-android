package com.please.ui.seller.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.please.databinding.FragmentChatbotBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ChatbotFragment : Fragment() {

    private var _binding: FragmentChatbotBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatbotViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatbotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()

        // Send initial greeting when view is created
        viewModel.sendInitialGreeting()
    }

    private fun setupUI() {
        // Set current date
        val dateFormat = SimpleDateFormat("yyyy년 M월 d일 EEEE", Locale.KOREAN)
        binding.tvDate.text = dateFormat.format(Date())

        // Setup RecyclerView
        chatAdapter = ChatAdapter { buttonText ->
            // 사용자 메시지 추가
            viewModel.sendUserMessage(buttonText)

            // 챗봇 응답 처리
            viewModel.handleActionButtonClick(buttonText)
        }

        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }

        // Setup message input
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                viewModel.sendUserMessage(message)
                binding.etMessage.text.clear()
                
                // 메시지 전송 후 키보드 내리기
                binding.etMessage.clearFocus()
            }
        }

        // Setup close button
        binding.btnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Setup attachment button
        binding.btnAttach.setOnClickListener {
            Toast.makeText(context, "파일 첨부 기능은 준비 중입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            chatAdapter.submitList(messages)

            // 약간의 딜레이 후 가장 아래로 스크롤
            binding.rvMessages.postDelayed({
                binding.rvMessages.scrollToPosition(messages.size - 1)
            }, 100)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}