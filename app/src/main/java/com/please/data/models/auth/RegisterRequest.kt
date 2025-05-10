package com.please.data.models.auth

/*
{
  "email": "test3@example.com",
  "password": "123456",
  "name": "홍길동",
  "userType": "OWNER", // 또는 "DRIVER"

  "address": "서마포구",
  "detailAddress": "임시",
  "latitude": 317.5,
  "longitude": 156.9,

  "phoneNumber": "",
  "vehicleNumber": "",
  "regionCity": "",
  "regionDistrict": ""
}
 */


data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String = "홍길동",
    val userType: UserType, //OWNER or DRIVER
    //val passwordConfirm: String,

    val address: String? = null, // 개인 주소 (자영업자)
    val detailAddress: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,

    val phoneNumber: String? = null,
    val vehicleNumber: String? = null,
    val regionCity: String? = null, // 도시 (배송기사)
    val regionDistrict: String? = null, // 구 (배송기사)

    //tmp
    //val transportLicenseFile: String? = null, // 화물 운송 자격증 파일 (배송기사)
    //val drivingExperienceFile: String? = null, // 운전 경력 증명서 파일 (배송기사)
    //val businessNumber: String? = null // 사업자 등록번호 (자영업자)
)

data class RegisterResponse(
    val success: Boolean,
    val message: String
)
