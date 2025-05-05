package com.example.subtrack.service.viewInterface

import android.content.Context

interface LogInView {
    fun onLogInSuccess()
    fun onLogInFailure()
    fun getContext(): Context?
}