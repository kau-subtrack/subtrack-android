package com.please.ui.seller.chat

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.please.data.models.seller.ChatMessage
import com.please.data.models.seller.MessageSender
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatbotViewModel @Inject constructor() : ViewModel() {

    private val _messages = MutableLiveData<List<ChatMessage>>(emptyList())
    val messages: LiveData<List<ChatMessage>> = _messages

    private val handler = Handler(Looper.getMainLooper())

    fun sendInitialGreeting() {
        val initialMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = "안녕하세요! SubTrack 고객센터입니다. 😊\n무엇을 도와드릴까요?",
            sender = MessageSender.CHATBOT,
            timestamp = Date(),
            actionButtons = listOf(
                "구독제 추천",
                "수거 주소 변경",
                "포인트 문의",
                "발송 등록 안내",
                "구독제 선택 팁"
            )
        )
        addMessage(initialMessage)
    }

    fun sendUserMessage(content: String) {
        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = content,
            sender = MessageSender.USER,
            timestamp = Date()
        )
        addMessage(userMessage)

        handler.postDelayed({
            provideChatbotResponse(content)
        }, 400)
    }

    fun handleActionButtonClick(buttonText: String) {
        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = buttonText,
            sender = MessageSender.USER,
            timestamp = Date()
        )
        addMessage(userMessage)

        handler.postDelayed({
            val response = when (buttonText) {
                "구독제 추천" -> ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = "현재 저희 SubTrack에서는 다양한 구독제를 제공하고 있습니다. 매장 규모와 배송량에 따라 최적의 구독제를 추천해 드리겠습니다. 어떤 종류의 매장을 운영 중이신가요?",
                    sender = MessageSender.CHATBOT,
                    timestamp = Date(),
                    actionButtons = listOf("소규모 매장", "중규모 매장", "대규모 매장")
                )
                "수거 주소 변경" -> ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = "네, 수거 주소 변경 도와드리겠습니다. 변경하실 주소를 알려주시면 바로 변경해드리도록 하겠습니다. 🏠",
                    sender = MessageSender.CHATBOT,
                    timestamp = Date(),
                    actionButtons = listOf("구독제 추천", "포인트 문의", "발송 등록 안내")
                )
                "포인트 문의" -> ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = "포인트 관련 문의 도와드리겠습니다. 현재 보유하신 포인트는 300P입니다. 추가 문의사항 있으신가요? 💰",
                    sender = MessageSender.CHATBOT,
                    timestamp = Date(),
                    actionButtons = listOf("포인트 사용법", "포인트 적립 방법", "포인트 소멸 기간")
                )
                "발송 등록 안내" -> ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = "발송 등록은 [발송] 메뉴에서 쉽게 하실 수 있습니다. 발송 등록 버튼을 클릭하여 고객 정보와 배송 정보를 입력하시면 됩니다. 특별한 요청사항이 있으신가요?",
                    sender = MessageSender.CHATBOT,
                    timestamp = Date(),
                    actionButtons = listOf("발송 취소 방법", "발송 추적 방법", "발송 요금 문의")
                )
                "소규모 매장" -> ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = "소규모 매장을 운영하시는군요. 월 배송량이 50건 미만인 소규모 매장에는 'Lite' 또는 'Lite Plus' 구독제를 추천해 드립니다. 기본 요금은 월 300,000원이며, 300 포인트가 제공됩니다. 다른 구독제도 살펴보시겠어요?",
                    sender = MessageSender.CHATBOT,
                    timestamp = Date(),
                    actionButtons = listOf("Lite 구독제 상세 정보", "다른 구독제 보기", "구독 신청 방법")
                )
                "중규모 매장" -> ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = "중규모 매장을 운영하시는군요. 월 배송량이 50~200건인 중규모 매장에는 'Standard' 또는 'Standard Plus' 구독제를 추천해 드립니다. 기본 요금은 월 500,000원이며, 500 포인트가 제공됩니다. 다른 구독제도 살펴보시겠어요?",
                    sender = MessageSender.CHATBOT,
                    timestamp = Date(),
                    actionButtons = listOf("Standard 구독제 상세 정보", "다른 구독제 보기", "구독 신청 방법")
                )
                "대규모 매장" -> ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = "대규모 매장을 운영하시는군요. 월 배송량이 200건 이상인 대규모 매장에는 'Premium' 또는 'Premium Plus' 구독제를 추천해 드립니다. 기본 요금은 월 950,000원이며, 1,000 포인트가 제공됩니다. 다른 구독제도 살펴보시겠어요?",
                    sender = MessageSender.CHATBOT,
                    timestamp = Date(),
                    actionButtons = listOf("Premium 구독제 상세 정보", "다른 구독제 보기", "구독 신청 방법")
                )
                else -> ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = "\"$buttonText\"에 대한 답변 도와드리겠습니다. 추가 문의사항 있으신가요?",
                    sender = MessageSender.CHATBOT,
                    timestamp = Date(),
                    actionButtons = listOf("구독제 추천", "수거 주소 변경", "포인트 문의")
                )
            }

            addMessage(response)
        }, 400)
    }

    private fun provideChatbotResponse(userQuery: String) {
        val response = when {
            userQuery.contains("안녕") -> ChatMessage(
                id = UUID.randomUUID().toString(),
                content = "안녕하세요! 무엇을 도와드릴까요?",
                sender = MessageSender.CHATBOT,
                timestamp = Date(),
                actionButtons = listOf("구독제 추천", "수거 주소 변경", "포인트 문의")
            )
            userQuery.contains("수거") || userQuery.contains("일정") -> ChatMessage(
                id = UUID.randomUUID().toString(),
                content = "수거 일정 관련 문의를 도와드리겠습니다. 원하시는 날짜로 변경 가능합니다. 언제로 변경을 원하시나요?",
                sender = MessageSender.CHATBOT,
                timestamp = Date(),
                actionButtons = listOf("내일로 변경", "이번 주 내로 변경", "다음 주로 변경")
            )
            userQuery.contains("구독") || userQuery.contains("요금") -> ChatMessage(
                id = UUID.randomUUID().toString(),
                content = "구독제 관련 문의를 도와드리겠습니다. 현재 Lite, Standard, Premium 세 가지 구독제를 제공하고 있습니다. 어떤 정보가 필요하신가요?",
                sender = MessageSender.CHATBOT,
                timestamp = Date(),
                actionButtons = listOf("구독제 가격 비교", "구독 변경 방법", "구독 취소 방법")
            )
            userQuery.contains("포인트") || userQuery.contains("적립") -> ChatMessage(
                id = UUID.randomUUID().toString(),
                content = "포인트 관련 문의를 도와드리겠습니다. 현재 300포인트를 보유하고 계십니다. 포인트는 배송 비용 결제, 구독 요금 할인 등에 사용하실 수 있습니다.",
                sender = MessageSender.CHATBOT,
                timestamp = Date(),
                actionButtons = listOf("포인트 사용처", "포인트 적립 방법", "포인트 소멸 기간")
            )
            else -> ChatMessage(
                id = UUID.randomUUID().toString(),
                content = "죄송합니다. 질문을 이해하지 못했습니다. 아래 주제 중 하나를 선택해 주세요.",
                sender = MessageSender.CHATBOT,
                timestamp = Date(),
                actionButtons = listOf("구독제 추천", "수거 주소 변경", "포인트 문의", "발송 등록 안내")
            )
        }

        addMessage(response)
    }

    private fun addMessage(message: ChatMessage) {
        val currentList = _messages.value.orEmpty().toMutableList()
        currentList.add(message)
        _messages.postValue(currentList)
    }
}

