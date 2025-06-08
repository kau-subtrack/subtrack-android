package com.please.ui.driver.deliver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.please.R
import com.please.data.models.driver.DeliveryList
import com.please.utils.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverDeliverFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var deliveryCountView: TextView
    private lateinit var adapter: DeliveryRequestAdapter
    private lateinit var preferenceManager: PreferenceManager
    
    // ViewModel 초기화
    private val viewModel: DriverDeliverViewModel by viewModels()
    
    // 데이터 리스트
    private val deliveryItems = mutableListOf<DeliveryList>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_driver_deliver, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            // Preference Manager 초기화
            preferenceManager = PreferenceManager(requireContext())
            
            // 뷰 초기화
            recyclerView = view.findViewById(R.id.rv_items)
            emptyView = view.findViewById(R.id.tv_empty)
            deliveryCountView = view.findViewById(R.id.tv_delivery_count)
            
            // 리사이클러뷰 설정
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            
            // ViewModel 관찰 설정
            setupObservers()
            
            // 데이터 로드
            loadData()
            
            // 어댑터 설정
            adapter = DeliveryRequestAdapter(deliveryItems) { position ->
                try {
                    // 배송 완료 버튼 클릭 처리
                    if (position >= 0 && position < deliveryItems.size) {
                        val item = deliveryItems[position]
                        val token = preferenceManager.getToken() ?: ""
                        viewModel.completeDelivery(token, item.trackingCode)
                    }
                } catch (e: Exception) {
                    // 오류 처리
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "배송 완료 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            
            recyclerView.adapter = adapter
            
            // 초기 빈 상태 확인
            updateEmptyState()
        } catch (e: Exception) {
            // 예외 처리
            e.printStackTrace()
            Toast.makeText(requireContext(), "화면 초기화 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupObservers() {
        // 배송 목록 관찰
        viewModel.deliveryList.observe(viewLifecycleOwner) { items ->
            deliveryItems.clear()
            deliveryItems.addAll(items)
            adapter.notifyDataSetChanged()
            updateEmptyState()
            updateDeliveryCount()
        }
        
        // 에러 메시지 관찰
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearErrorMessage()
            }
        }
        
        // 작업 완료 상태 관찰
        viewModel.operationCompleted.observe(viewLifecycleOwner) { completed ->
            if (completed) {
                Toast.makeText(requireContext(), "배송이 완료 처리되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun loadData() {
        try {
            val token = preferenceManager.getToken() ?: ""
            if (token.isNotEmpty()) {
                viewModel.fetchDeliveryList(token)
            } else {
                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // 예외 처리
            e.printStackTrace()
            Toast.makeText(requireContext(), "데이터 로드 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun updateEmptyState() {
        try {
            if (deliveryItems.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }
        } catch (e: Exception) {
            // 예외 처리
            e.printStackTrace()
        }
    }
    
    private fun updateDeliveryCount() {
        try {
            deliveryCountView.text = "항목 수: ${deliveryItems.size}개"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun onResume() {
        super.onResume()
        try {
            // 화면이 다시 표시될 때 데이터 갱신
            loadData()
        } catch (e: Exception) {
            // 예외 처리
            e.printStackTrace()
        }
    }
}
