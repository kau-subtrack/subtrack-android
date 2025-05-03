package com.please.data.models.auth

data class RegisterRequest(
    val id: String,
    val password: String,
    val passwordConfirm: String,
    val userType: UserType,
    val businessNumber: String? = null, // 사업자 등록번호 (자영업자)
    val address: String? = null, // 개인 주소 (자영업자)
    val city: String? = null, // 도시 (배송기사)
    val district: String? = null, // 구 (배송기사)
    val transportLicenseFile: String? = null, // 화물 운송 자격증 파일 (배송기사)
    val drivingExperienceFile: String? = null // 운전 경력 증명서 파일 (배송기사)
)

data class RegisterResponse(
    val success: Boolean,
    val message: String
)
