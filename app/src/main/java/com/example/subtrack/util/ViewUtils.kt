package com.example.subtrack.util

import android.graphics.Color
import android.widget.Button
import com.example.subtrack.R

fun setUserTypeUI(
    isOwner: Boolean,
    btnOwner: Button,
    btnCourier: Button
) {
    if (isOwner) {
        btnOwner.setBackgroundResource(R.drawable.button_selected)
        btnOwner.setTextColor(Color.BLACK)
        btnCourier.setBackgroundResource(R.drawable.button_unselected)
        btnCourier.setTextColor(Color.WHITE)
    } else {
        btnOwner.setBackgroundResource(R.drawable.button_unselected)
        btnOwner.setTextColor(Color.WHITE)
        btnCourier.setBackgroundResource(R.drawable.button_selected)
        btnCourier.setTextColor(Color.BLACK)
    }
}