package com.please.ui.seller.delivery

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.please.data.models.seller.DeliveryInfo
import com.please.data.models.seller.DeliveryStatus
import com.please.data.repositories.AuthRepository
import com.please.data.repositories.DeliveryRepository
import com.please.data.repositories.SellerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Random
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SellerDeliveryViewModel @Inject constructor(
    private val repository: DeliveryRepository,
    private val repository_sell: SellerRepository
) : ViewModel() {

    enum class DeliveryMode {
        REGISTER, VIEW
    }

    private val _mode = MutableLiveData(DeliveryMode.REGISTER)
    val mode: LiveData<DeliveryMode> = _mode

    private val _selectedDate = MutableLiveData<Date>()
    val selectedDate: LiveData<Date> = _selectedDate

    private val _yearOptions = MutableLiveData<List<String>>()
    val yearOptions: LiveData<List<String>> = _yearOptions

    private val _monthOptions = MutableLiveData<List<String>>()
    val monthOptions: LiveData<List<String>> = _monthOptions

    private val _dayOptions = MutableLiveData<List<String>>()
    val dayOptions: LiveData<List<String>> = _dayOptions

    private val _deliveryList = MutableLiveData<List<DeliveryInfo>>()
    val deliveryList: LiveData<List<DeliveryInfo>> = _deliveryList

    private var currentYearList = listOf<Int>()
    private var currentMonthList = listOf<Int>()
    private var currentDayList = listOf<Int>()


    val token = AuthRepository.AppState.userToken ?: "none"

    //초기 날짜 세팅 후 리스트 조회
    fun initializeDefaultDate() {
        if (_selectedDate.value != null) return

        val mode = _mode.value ?: DeliveryMode.REGISTER
        val calendar = Calendar.getInstance()

        // 등록모드와 조회모드 모두 오늘 날짜를 기본값으로 설정
        val initialDate = calendar.time
        _selectedDate.value = initialDate
        updateDateOptionsForMode(mode)

        //loadDeliveries(initialDate)

        //loadTwoDays()
    }

    //등록, 조회 변경 시 리스트 재조회 및 날짜 리셋
    fun setMode(newMode: DeliveryMode) {
        if (_mode.value == newMode) return
        
        _mode.value = newMode
        
        // 모드가 변경되면 항상 오늘 날짜로 재설정
        val today = Calendar.getInstance().time
        _selectedDate.value = today
        
        // 모드에 맞게 날짜 옵션 업데이트
        updateDateOptionsForMode(newMode)
        
        // 새로운 날짜로 배송 목록 로드
        //loadDeliveries(today)

        //등록일시.
        //loadTwoDays()
    }

    // 연도
    fun onYearSelected(position: Int) {
        val year = currentYearList.getOrNull(position) ?: return
        val selectedDate = Calendar.getInstance().apply { time = _selectedDate.value ?: Date() }
        selectedDate.set(Calendar.YEAR, year)
        _selectedDate.value = selectedDate.time
        updateMonthOptions()
        updateDayOptions()
    }

    // 월
    fun onMonthSelected(position: Int) {
        val month = currentMonthList.getOrNull(position) ?: return
        val selectedDate = Calendar.getInstance().apply { time = _selectedDate.value ?: Date() }
        selectedDate.set(Calendar.MONTH, month - 1)
        _selectedDate.value = selectedDate.time
        updateDayOptions()
    }

    // 일
    fun onDaySelected(position: Int) {
        val day = currentDayList.getOrNull(position) ?: return
        val selectedDate = Calendar.getInstance().apply { time = _selectedDate.value ?: Date() }
        selectedDate.set(Calendar.DAY_OF_MONTH, day)
        _selectedDate.value = selectedDate.time
        loadDeliveries(selectedDate.time)
        loadTwoDays()
    }

    fun getYearPosition(year: Int): Int = currentYearList.indexOf(year).coerceAtLeast(0)
    fun getMonthPosition(month: Int): Int = currentMonthList.indexOf(month).coerceAtLeast(0)
    fun getDayPosition(day: Int): Int = currentDayList.indexOf(day).coerceAtLeast(0)

    private fun updateDateOptionsForMode(mode: DeliveryMode) {
        val now = Calendar.getInstance()
        val currentYear = now.get(Calendar.YEAR)

        currentYearList = when (mode) {
            DeliveryMode.REGISTER -> listOf(currentYear) // 등록 모드에서는 오늘 연도만 표시
            DeliveryMode.VIEW -> listOf(currentYear - 1, currentYear, currentYear + 1) // 조회 모드에서는 작년부터 내년까지
        }
        _yearOptions.value = currentYearList.map { "${it}년" }

        updateMonthOptions()
        updateDayOptions()
    }

    fun updateMonthOptions() {
        val now = Calendar.getInstance()
        val currentMonth = now.get(Calendar.MONTH) + 1

        currentMonthList = when (_mode.value ?: DeliveryMode.REGISTER) {
            DeliveryMode.REGISTER -> {
                // 등록 모드에서는 현재 월만 선택 가능
                listOf(currentMonth)
            }
            DeliveryMode.VIEW -> {
                // 조회 모드에서는 모든 월 선택 가능
                (1..12).toList()
            }
        }
        _monthOptions.value = currentMonthList.map { "${it}월" }
    }


    // 전체 날짜 수정
    fun updateDayOptions() {
        val now = Calendar.getInstance()
        val currentDay = now.get(Calendar.DAY_OF_MONTH)

        val selected = Calendar.getInstance().apply { time = _selectedDate.value ?: Date() }
        val lastDay = selected.getActualMaximum(Calendar.DAY_OF_MONTH)
        val mode = _mode.value ?: DeliveryMode.REGISTER

        currentDayList = when (mode) {
            DeliveryMode.REGISTER -> {
                // 등록 모드에서는 오늘 날짜만 선택 가능
                listOf(currentDay)
            }
            DeliveryMode.VIEW -> {
                // 조회 모드에서는 월의 모든 날짜 선택 가능
                (1..lastDay).toList()
            }
        }
        
        _dayOptions.value = currentDayList.map { "${it}일" }
    }

    // 날짜 선택 제한
    fun isValidDateSelection(year: Int, month: Int, day: Int): Boolean {
        val cal = Calendar.getInstance()
        val selected = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
        }

        return when (_mode.value) {
            DeliveryMode.REGISTER -> {
                // 등록 모드에서는 오늘 날짜만 유효
                val today = Calendar.getInstance()
                selected.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                selected.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                selected.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
            }
            DeliveryMode.VIEW -> {
                // 조회 모드에서는 과거 1년부터 미래 1년까지 선택 가능
                val minDate = Calendar.getInstance().apply { add(Calendar.YEAR, -1); set(Calendar.MONTH, 0); set(Calendar.DAY_OF_MONTH, 1) }
                val maxDate = Calendar.getInstance().apply { add(Calendar.YEAR, 1); set(Calendar.MONTH, 11); set(Calendar.DAY_OF_MONTH, 31) }
                !selected.before(minDate) && !selected.after(maxDate)
            }
            else -> false
        }
    }

    //추가 관련
    fun onAddDeliveryClicked(): Date? {
        // 등록 모드에서는 항상 오늘 날짜만 리턴
        return Calendar.getInstance().time
    }

    // !!! 날짜 기반 리스트 가져오기 = 실제 사용값 _deliveryList. 고정 변수값 _repository내 return값.
    private fun loadDeliveries(date: Date) {
        //해당 날짜 리스트 조회(서버 연동)
        viewModelScope.launch {
            try {
                val response = repository_sell.shipmentDateView(token, date)
                Log.d("Delivery/List", date.toString())
                Log.d("Delivery/List", response.body().toString())

                //성공시 리스트 생성 양식 기입
                if (response.isSuccessful && response.body() != null) {
                    //뷰에 보일 리스트 간추리기  / 해당 날짜 리스트 조회(서버 연동)
                    _deliveryList.value = repository.jsonDelivery(response.body()!!)// + repository.getDeliveriesByDate(date) //repository.getDeliveriesByDate(date) //메모리 내 리스트 추가 조회.
                    //Log.d("Delivery/ListAll", _deliveryList.value.toString())
                } else {
                    Log.d("Delivery/ERROR" , "배송 내역이 없습니다.")
                }
            } catch (e: Exception) {
                Log.d("Delivery/ERROR" , e.message.toString())
            }
        }
    }

    fun deleteDelivery(code: String?) {
        if(code == null) Log.d("Delivery/Delete" , "삭제 요청에 실패했습니다")

        viewModelScope.launch {
            try {
                val response = repository.deleteDelivery(token, code!!)

                if (response.isSuccessful && response.body() != null) {
                    //현재 list 리로딩
                    _selectedDate.value?.let { loadDeliveries(it) }
                    Log.d("Delivery/Delete" , "삭제 요청이 정상 처리되었습니다.")
                } else {
                    Log.d("Delivery/ERROR" , "삭제 요청에 실패했습니다")
                }
            } catch (e: Exception) {
                Log.d("Delivery/ERROR" , e.message.toString())
            }
        }
    }

    // 등록 추가
    fun addDelivery(trackingNumber: String, contactPhone: String, address: String) {
        val pickupDate = _selectedDate.value ?: Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }.time

        val deliveryInfo = DeliveryInfo(
            id = Random().nextInt(1000000), // 임시 고유 ID
            productName = "임시 제품명",
            recipientName = "수령인",
            recipientPhone = contactPhone,
            address = address,
            pickupDate = pickupDate,
            packageSize = com.please.data.models.seller.PackageSize.MEDIUM,
            status = DeliveryStatus.PENDING,
            trackingNumber = trackingNumber
        )

        repository.addDelivery(deliveryInfo)
        loadDeliveries(pickupDate)
        loadTwoDays()
    }

    // 등록 조회시, 오늘내일로 등록된 내역 확인.
    fun loadTwoDays(){
        if (_mode.value == DeliveryMode.REGISTER) {
            //오늘 내역 저장
            val todayList = _deliveryList.value ?: emptyList()

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, 1)  // 하루 더하기
            val tomorrow = calendar.time
            loadDeliveries(tomorrow)
            val tomorrowList = _deliveryList.value ?: emptyList()

            //합친결과값반환
            _deliveryList.value = todayList + tomorrowList
        }
    }

    // 날짜 포맷
    fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)
        return sdf.format(date)
    }
}