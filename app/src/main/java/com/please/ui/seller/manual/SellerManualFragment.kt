package com.please.ui.seller.manual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.please.databinding.FragmentSellerManualBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellerManualFragment : Fragment() {

    private var _binding: FragmentSellerManualBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerManualBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 필요한 경우 여기에 추가적인 설정 코드를 넣을 수 있습니다.
        // 예: 전화번호 클릭 이벤트, 특정 섹션 자세히 보기 등
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}