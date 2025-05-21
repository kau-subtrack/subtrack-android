package com.please.data.repositories

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.please.data.api.AuthApiService
import com.please.data.models.auth.LoginRequest
import com.please.data.models.auth.LoginResponse
import com.please.data.models.auth.UserType
import com.google.android.gms.maps.model.LatLng
import com.please.data.api.GoogleMapApi
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleMapRepository @Inject constructor(
    private val googleApi: GoogleMapApi
){
    /*
    suspend fun login(id: String, password: String, userType: UserType): Response<LoginResponse> {
        //return apiService.login(LoginRequest(id, password, userType))
    }
     */

    suspend fun searchAddress(address: String) : LocationResult {
        val apiKey = "AIzaSyBbf7RzN2iMRMo_XsFRU00bH6JX4GziByg" // 하드코딩된 apikey_gme
        Log.d("MAP/SEARCH", address)
        //TODO request자체에서 문제가 있음. ㅇㅇ.
        val response = googleApi.getLatLngFromAddress(address, apiKey)

        if (response.status == "OK" && response.results.isNotEmpty()) {
            val location = response.results[0].geometry.location
            val latLng = LatLng(location.lat, location.lng)
            Log.d("MAP/SEARCH", address + "\n" + location + "\n"+ latLng)
            return LocationResult.Success(latLng, response.results[0].formatted_address)
        } else {
            return LocationResult.Error("주소를 찾을 수 없습니다.")
        }
    }

    //api key가져오기
    fun getMetaDataValue(context: Context, key: String): String? {
        return try {
            val appInfo = context.packageManager
                .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            appInfo.metaData?.getString(key)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

}

sealed class LocationResult {
    data class Success(val latLng: LatLng, val address: String) : LocationResult()
    data class Error(val message: String) : LocationResult()
}
