package com.example.subtrack.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtil {
    
    private const val ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private const val DISPLAY_FORMAT = "yyyy년 MM월 dd일"
    
    fun formatDateWithDay(isoDateString: String): String {
        val isoFormat = SimpleDateFormat(ISO_FORMAT, Locale.getDefault())
        val displayFormat = SimpleDateFormat(DISPLAY_FORMAT, Locale.getDefault())
        
        try {
            val date = isoFormat.parse(isoDateString) ?: return isoDateString
            val formattedDate = displayFormat.format(date)
            
            // 요일 추가
            val calendar = Calendar.getInstance()
            calendar.time = date
            val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> "일"
                Calendar.MONDAY -> "월"
                Calendar.TUESDAY -> "화"
                Calendar.WEDNESDAY -> "수"
                Calendar.THURSDAY -> "목"
                Calendar.FRIDAY -> "금"
                Calendar.SATURDAY -> "토"
                else -> ""
            }
            
            return "$formattedDate ($dayOfWeek)"
        } catch (e: Exception) {
            e.printStackTrace()
            return isoDateString
        }
    }
}
