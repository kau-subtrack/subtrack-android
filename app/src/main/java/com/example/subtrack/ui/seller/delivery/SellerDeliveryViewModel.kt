package com.please.ui.seller.delivery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.please.data.models.seller.DeliveryInfo
import com.please.data.models.seller.DeliveryStatus
import com.please.data.repositories.DeliveryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SellerDeliveryViewModel @Inject constructor(
    private val repository: DeliveryRepository
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

    fun setMode(newMode: DeliveryMode) {
        if (_mode.value == newMode) return
        _mode.value = newMode
        _selectedDate.value?.let { loadDeliveries(it) }
    }

    fun onYearSelected(position: Int) {
        val year = currentYearList.getOrNull(position) ?: return
        val selectedDate = Calendar.getInstance().apply { time = _selectedDate.value ?: Date() }
        selectedDate.set(Calendar.YEAR, year)
        _selectedDate.value = selectedDate.time
        updateMonthOptions()
        updateDayOptions()
    }

    fun onMonthSelected(position: Int) {
        val month = currentMonthList.getOrNull(position) ?: return
        val selectedDate = Calendar.getInstance().apply { time = _selectedDate.value ?: Date() }
        selectedDate.set(Calendar.MONTH, month - 1)
        _selectedDate.value = selectedDate.time
        updateDayOptions()
    }

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

    fun onAddDeliveryClicked(): Date? {
        val date = _selectedDate.value ?: return null
        val cal = Calendar.getInstance().apply { time = date }
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)
        return if (isValidDateSelection(y, m, d)) date else null
    }

    private fun loadDeliveries(date: Date) {
        _deliveryList.value = repository.getDeliveriesByDate(date)
    }

    fun deleteDelivery(id: String) {
        repository.deleteDelivery(id)
        _selectedDate.value?.let { loadDeliveries(it) }
    }

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

    fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)
        return sdf.format(date)
    }
}