package com.example.subtrack.data.model
import com.google.gson.annotations.SerializedName


/// 서버에서 받는 데이터 양식들 response///
data class resUserToken(
    var message: String? = null,
    var token: String? = null
)
//@SerializedName(value = "message") var message: String,
//UserId token : ACCESS_TOKEN
//@SerializedName(value = "token") var token: String,

data class resUserRegister(
    @SerializedName(value = "success") var success: Boolean,
    @SerializedName(value = "message") var message: String,
    @SerializedName(value = "userId") var userId: Int
)


/// 클라에서 주는 데이터 양식들 request///
data class reqUserLogIn(
    var email: String? = null,
    var password: String? = null,
    var userType: String? = null
)
//@SerializedName(value = "email") var email: String,
//@SerializedName(value = "password") var password: String,
//OWNER or DRIVER
//@SerializedName(value = "userType") var userType: String

data class reqUserSignUp(
    @SerializedName(value = "email") var email: String,
    @SerializedName(value = "password") var password: String,
    @SerializedName(value = "name") var name: String,
    @SerializedName(value = "userType") var userType: String,

    //OWNER일시 추가
    @SerializedName(value = "address") var address: String? = "",
    @SerializedName(value = "detailAddress") var detailAddress: String? = "",
    @SerializedName(value = "latitude") var latitude: Float? = 0f,
    @SerializedName(value = "longitude") var longitude: Float? = 0f,

    //DRIVER일시 추가
    @SerializedName(value = "phoneNumber") var phoneNumber: String? = "",
    @SerializedName(value = "vehicleNumber") var vehicleNubmer: String? = "",
    @SerializedName(value = "regionCity") var regionCity: String? = "",
    @SerializedName(value = "regionDistrict") var regionDistrict: String? = ""

)