package com.please.ui.seller.delivery

import android.util.Log
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

        val initialDate = when (mode) {
            DeliveryMode.REGISTER -> calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time
            DeliveryMode.VIEW -> calendar.time
        }

        _selectedDate.value = initialDate
        updateDateOptionsForMode(mode)

        loadDeliveries(initialDate)
    }

    //등록, 조회 변경 시 리스트 재조회
    fun setMode(newMode: DeliveryMode) {
        if (_mode.value == newMode) return
        _mode.value = newMode
        _selectedDate.value?.let { loadDeliveries(it) }
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
    }

    fun getYearPosition(year: Int): Int = currentYearList.indexOf(year).coerceAtLeast(0)
    fun getMonthPosition(month: Int): Int = currentMonthList.indexOf(month).coerceAtLeast(0)
    fun getDayPosition(day: Int): Int = currentDayList.indexOf(day).coerceAtLeast(0)

    private fun updateDateOptionsForMode(mode: DeliveryMode) {
        val now = Calendar.getInstance()
        val currentYear = now.get(Calendar.YEAR)

        currentYearList = when (mode) {
            DeliveryMode.REGISTER -> listOf(currentYear, currentYear + 1)
            DeliveryMode.VIEW -> listOf(currentYear - 1, currentYear, currentYear + 1)
        }
        _yearOptions.value = currentYearList.map { "${it}년" }

        updateMonthOptions()
        updateDayOptions()
    }

    fun updateMonthOptions() {
        val now = Calendar.getInstance()
        val currentYear = now.get(Calendar.YEAR)
        val currentMonth = now.get(Calendar.MONTH) + 1

        val selected = Calendar.getInstance().apply { time = _selectedDate.value ?: Date() }
        val selectedYear = selected.get(Calendar.YEAR)
        val mode = _mode.value ?: DeliveryMode.REGISTER

        currentMonthList = when (mode) {
            DeliveryMode.REGISTER -> {
                if (selectedYear == currentYear) (currentMonth..12).toList()
                else (1..12).toList()
            }
            DeliveryMode.VIEW -> (1..12).toList()
        }
        _monthOptions.value = currentMonthList.map { "${it}월" }
    }

    // 전체 날짜 수정
    fun updateDayOptions() {
        val now = Calendar.getInstance()
        val currentYear = now.get(Calendar.YEAR)
        val currentMonth = now.get(Calendar.MONTH) + 1
        val currentDay = now.get(Calendar.DAY_OF_MONTH)

        val selected = Calendar.getInstance().apply { time = _selectedDate.value ?: Date() }
        val year = selected.get(Calendar.YEAR)
        val month = selected.get(Calendar.MONTH) + 1

        val lastDay = selected.getActualMaximum(Calendar.DAY_OF_MONTH)
        val mode = _mode.value ?: DeliveryMode.REGISTER

        val startDay = when (mode) {
            DeliveryMode.REGISTER -> {
                if (year == currentYear && month == currentMonth) currentDay + 1 else 1
            }
            DeliveryMode.VIEW -> 1
        }

        currentDayList = (startDay..lastDay).toList()
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
                val minDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }
                val maxDate = Calendar.getInstance().apply {
                    set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1)
                    set(Calendar.MONTH, 11)
                    set(Calendar.DAY_OF_MONTH, 31)
                }
                !selected.before(minDate) && !selected.after(maxDate)
            }
            DeliveryMode.VIEW -> {
                val minDate = Calendar.getInstance().apply { add(Calendar.YEAR, -1); set(Calendar.MONTH, 0); set(Calendar.DAY_OF_MONTH, 1) }
                val maxDate = Calendar.getInstance().apply { add(Calendar.YEAR, 1); set(Calendar.MONTH, 11); set(Calendar.DAY_OF_MONTH, 31) }
                !selected.before(minDate) && !selected.after(maxDate)
            }
            else -> false
        }
    }

    //추가 관련
    fun onAddDeliveryClicked(): Date? {
        val date = _selectedDate.value ?: return null
        val cal = Calendar.getInstance().apply { time = date }
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)
        return if (isValidDateSelection(y, m, d)) date else null
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
                    _deliveryList.value =  repository.jsonDelivery(response.body()!!)// + repository.getDeliveriesByDate(date) //repository.getDeliveriesByDate(date) //메모리 내 리스트 추가 조회.
                    //Log.d("Delivery/ListAll", _deliveryList.value.toString())
                } else {
                    Log.d("Delivery/ERROR" , "배송 내역이 없습니다.")
                }
            } catch (e: Exception) {
                Log.d("Delivery/ERROR" , e.message.toString())
            }
        }
    }

    fun deleteDelivery(id: String) {
        repository.deleteDelivery(id)
        _selectedDate.value?.let { loadDeliveries(it) }
    }

    // 등록 추가
    fun addDelivery(trackingNumber: String, contactPhone: String, address: String) {
        val pickupDate = _selectedDate.value ?: Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }.time

        val deliveryInfo = DeliveryInfo(
            id = UUID.randomUUID().toString(),
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
    }

    // 날짜 포맷
    fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)
        return sdf.format(date)
    }
}