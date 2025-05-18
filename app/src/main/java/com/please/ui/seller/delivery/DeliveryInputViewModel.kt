package com.please.ui.seller.delivery

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.please.data.models.seller.DeliveryInfo
import com.please.data.models.seller.PackageSize
import com.please.data.models.seller.RegisterDelivery
import com.please.data.models.seller.RegisterDeliveryResponse
import com.please.data.repositories.AuthRepository
import com.please.data.repositories.DeliveryRepository
import com.please.data.repositories.SellerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import java.util.*

@HiltViewModel
class DeliveryInputViewModel @Inject constructor(
    private val repository: DeliveryRepository,
    private val repository_sell: SellerRepository
) : ViewModel() {

    // 입력 필드 상태
    private val _productName = MutableLiveData<String>()
    val productName: LiveData<String> = _productName

    private val _recipientName = MutableLiveData<String>()
    val recipientName: LiveData<String> = _recipientName

    private val _recipientPhone = MutableLiveData<String>()
    val recipientPhone: LiveData<String> = _recipientPhone

    private val _address = MutableLiveData<String>()
    val address: LiveData<String> = _address

    private val _detailAddress = MutableLiveData<String>()
    val detailAddress: LiveData<String> = _detailAddress

    private val _selectedPackageSize = MutableLiveData<PackageSize>(PackageSize.MEDIUM)
    val selectedPackageSize: LiveData<PackageSize> = _selectedPackageSize

    private val _isCautionRequired = MutableLiveData<Boolean>(false)
    val isCautionRequired: LiveData<Boolean> = _isCautionRequired

    private val _pickupDate = MutableLiveData<Date>()
    val pickupDate: LiveData<Date> = _pickupDate

    private val _registerResult = MutableLiveData<RegisterDeliveryResponse>()
    val registerResult: LiveData<RegisterDeliveryResponse> = _registerResult

    // 저장 성공 여부
    private val _saveResult = MutableLiveData<SaveResult>()
    val saveResult: LiveData<SaveResult> = _saveResult

    // 주소 검색 완료 여부
    private val _addressSearchComplete = MutableLiveData<Boolean>(false)
    val addressSearchComplete: LiveData<Boolean> = _addressSearchComplete

    // 입력값 업데이트 메서드들
    fun setProductName(name: String) {
        _productName.value = name
    }

    fun setRecipientName(name: String) {
        _recipientName.value = name
    }

    fun setRecipientPhone(phone: String) {
        _recipientPhone.value = phone
    }

    fun setAddress(address: String) {
        _address.value = address
        _addressSearchComplete.value = address.isNotEmpty()
    }

    fun setDetailAddress(address: String) {
        _detailAddress.value = address
    }

    fun setPackageSize(size: PackageSize) {
        _selectedPackageSize.value = size
    }

    fun toggleCautionRequired() {
        _isCautionRequired.value = _isCautionRequired.value?.not() ?: false
    }

    fun setCautionRequired(required: Boolean) {
        _isCautionRequired.value = required
    }

    fun setPickupDate(date: Date) {
        _pickupDate.value = date
    }

    // 배송 정보 저장
    fun saveDeliveryInfo() {
        val token = AuthRepository.AppState.userToken ?: "none"

        // 필수 입력값 확인
        val productName = _productName.value
        val recipientName = _recipientName.value
        val recipientPhone = _recipientPhone.value
        val address = _address.value
        val packageSize = _selectedPackageSize.value
        val pickupDate = _pickupDate.value

        if (productName.isNullOrEmpty() || 
            recipientName.isNullOrEmpty() || 
            recipientPhone.isNullOrEmpty() ||
            address.isNullOrEmpty() ||
            packageSize == null ||
            pickupDate == null
        ) {
            _saveResult.value = SaveResult.Error("필수 정보를 모두 입력해주세요.")
            return
        }

        // 배송 정보 생성
        val deliveryInfo = RegisterDelivery(
            productName = productName,
            recipientName = recipientName,
            recipientPhone = recipientPhone,
            recipientAddr = address,
            detailAddress = _detailAddress.value,
            size = packageSize,
            caution = isCautionRequired.value ?: false,
            pickupScheduledDate = formatDateForMySQL(pickupDate)
        //isCautionRequired = _isCautionRequired.value ?: false
        )

        //저장한 방식으로 백엔드 서버에 갱신.
        viewModelScope.launch {
            try {
                val response = repository_sell.registerShipment(token, deliveryInfo)
                Log.d("Delivery/Register", deliveryInfo.toString())
                Log.d("Delivery/Register", response.body().toString())

                //성공시 리스트 생성 양식 기입
                if (response.isSuccessful && response.body() != null && response.body()?.status == true) {
                    //여기서 response 반환. 어디다가 쓰느냐라... 현재 지정 없음.
                    Log.d("Delivery/Regiter", "배송 등록이 완료되었습니다.")
                    _registerResult.value = response.body()!!
                    _saveResult.value = SaveResult.Success
                    // 로컬 저장 - 임의 제거
                    //repository.addDelivery(deliveryInfo)
                } else {
                    Log.d("Delivery/Register" , "배송 내역이 없습니다.")
                }
            } catch (e: Exception) {
                _saveResult.value = SaveResult.Error("배송 정보 저장에 실패했습니다: ${e.message}")
                Log.d("Delivery/Register" , e.message.toString())
            }
        }
    }

    // 저장 결과 상태
    sealed class SaveResult {
        object Success : SaveResult()
        data class Error(val message: String) : SaveResult()
    }

    fun formatDateForMySQL(date: Date): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC") // 필요 시 'Asia/Seoul' 등으로 변경
        return formatter.format(date)
    }

}
