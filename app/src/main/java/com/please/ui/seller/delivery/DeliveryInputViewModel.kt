package com.please.ui.seller.delivery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.please.data.models.seller.DeliveryInfo
import com.please.data.models.seller.PackageSize
import com.please.data.repositories.DeliveryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DeliveryInputViewModel @Inject constructor(
    private val repository: DeliveryRepository
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
        val deliveryInfo = DeliveryInfo(
            id = UUID.randomUUID().toString(),
            productName = productName,
            recipientName = recipientName,
            recipientPhone = recipientPhone,
            address = address,
            detailAddress = _detailAddress.value,
            pickupDate = pickupDate,
            packageSize = packageSize,
            isCautionRequired = _isCautionRequired.value ?: false
        )

        // 저장
        try {
            repository.addDelivery(deliveryInfo)
            _saveResult.value = SaveResult.Success
        } catch (e: Exception) {
            _saveResult.value = SaveResult.Error("배송 정보 저장에 실패했습니다: ${e.message}")
        }
    }

    // 저장 결과 상태
    sealed class SaveResult {
        object Success : SaveResult()
        data class Error(val message: String) : SaveResult()
    }
}
