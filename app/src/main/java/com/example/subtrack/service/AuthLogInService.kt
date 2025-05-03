package com.example.subtrack.service
import android.util.Log
import com.example.subtrack.data.model.reqUserLogIn
import com.example.subtrack.data.model.resUserToken
import com.example.subtrack.service.interFace.LogInInterFace
import com.example.subtrack.service.viewInterface.LogInView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthLogInService {
    // 1단계
    private lateinit var logInView: LogInView

    fun setLogInView(logInView: LogInView) {
        this.logInView = logInView
    }

    // 2단계
    fun logIn(user: reqUserLogIn) {
        val authService = getRetrofit().create(LogInInterFace::class.java)

        authService.logIn(user).enqueue(object : Callback<resUserToken> {
            override fun onResponse(call: Call<resUserToken>, response: Response<resUserToken>) {
                Log.d("LOGIN/SUCCESS", user.toString())
                Log.d("LOGIN/SUCCESS", response.toString())
                Log.d("LOGIN/SUCCESS", response.body().toString())
                //val resp: resUserToken = response.body()!!
                val resp = response.body()
                if (resp != null) {
                    Log.d("LOGIN", "$resp")
                    logInView.onLogInSuccess()
                }
                else {
                    Log.d("LOGIN/ERROR", "Response body is null")
                    logInView.onLogInFailure()
                }
            }

            override fun onFailure(call: Call<resUserToken>, t: Throwable) {
                Log.d("LOGIN/FAILURE", t.message.toString())
                logInView.onLogInFailure()
            }
        })
    }
}