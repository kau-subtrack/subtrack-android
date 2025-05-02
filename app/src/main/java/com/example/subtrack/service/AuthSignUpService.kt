package com.example.subtrack.service
import android.util.Log
import com.example.subtrack.data.model.resUserRegister
import com.example.subtrack.data.model.reqUserSignUp
import com.example.subtrack.service.interFace.SignUpInterFace
import com.example.subtrack.service.viewInterface.SignUpView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthSignUpService {
    // 1단계
    private lateinit var signUpView: SignUpView

    fun setSignUpView(signUpView: SignUpView) {
        this.signUpView = signUpView
    }

    // 2단계
    fun signUp(user: reqUserSignUp) {
        val authService = getRetrofit().create(SignUpInterFace::class.java)

        authService.signUp(user).enqueue(object : Callback<resUserRegister> {
            override fun onResponse(call: Call<resUserRegister>, response: Response<resUserRegister>) {
                Log.d("SIGNUP/SUCCESS", response.toString())
                val resp: resUserRegister = response.body()!!

                if(resp.success) signUpView.onSignUpSuccess()
                else signUpView.onSignUpFailure()

            }

            override fun onFailure(call: Call<resUserRegister>, t: Throwable) {
                Log.d("SIGNUP/FAILURE", t.message.toString())
            }
        })
    }
}