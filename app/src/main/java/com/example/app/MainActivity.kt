package com.example.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.app.ui.home.HomeFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_old)

        if (savedInstanceState == null) {
            // 개발 중에는 AuthManager.isLoggedIn이 항상 true를 반환하므로
            // 로그인 체크를 하지만 실제로는 항상 홈 화면으로 이동
            if (AuthManager.isLoggedIn(this)) {
                // 로그인된 상태 - 홈 화면으로 이동
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, HomeFragment())
                    .commitNow()
            } else {
                // 로그인되지 않은 상태 - 원래는 로그인 화면으로 이동해야 하지만
                // 현재는 AuthManager가 항상 true를 반환하므로 이 코드는 실행되지 않음
                // TODO: 완성본에서는 LoginFragment로 이동하도록 구현
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, HomeFragment())
                    .commitNow()
            }
        }
    }
}
