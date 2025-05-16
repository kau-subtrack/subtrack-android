package com.please.ui.driver.deliver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.please.R
import com.please.data.models.driver.DeliveryRequest
import com.please.data.repositories.DriverDataRepository
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverDeliverFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var deliveryCountView: TextView
    private lateinit var adapter: DeliveryRequestAdapter
    
    // 데이터 리스트
    private val deliveryItems = mutableListOf<DeliveryRequest>()

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
            // 뷰 초기화
            recyclerView = view.findViewById(R.id.rv_items)
            emptyView = view.findViewById(R.id.tv_empty)
            deliveryCountView = view.findViewById(R.id.tv_delivery_count)
            
            // 리사이클러뷰 설정
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            
            // 데이터 로드
            loadData()
            
            // 항목 수 업데이트
            updateDeliveryCount()
            
            // 어댑터 설정
            adapter = DeliveryRequestAdapter(deliveryItems) { position ->
                try {
                    // 배송 완료 버튼 클릭 처리
                    if (position >= 0 && position < deliveryItems.size) {
                        val item = deliveryItems[position]
                        DriverDataRepository.removeDeliveryRequest(item.id)
                        deliveryItems.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        adapter.notifyItemRangeChanged(position, deliveryItems.size)
                        updateEmptyState()
                        updateDeliveryCount()
                    }
                } catch (e: Exception) {
                    // 오류 처리
                    e.printStackTrace()
                }
            }
            
            recyclerView.adapter = adapter
            
            // 초기 빈 상태 확인
            updateEmptyState()
        } catch (e: Exception) {
            // 예외 처리
            e.printStackTrace()
        }
    }
    
    private fun loadData() {
        try {
            // 데이터 리포지토리에서 데이터 가져오기
            deliveryItems.clear()
            deliveryItems.addAll(DriverDataRepository.getDeliveryRequests())
            
            // 로그로 항목 수 출력
            val itemCount = deliveryItems.size
            println("배송 요청 항목 수: $itemCount")
            // 첫 번째 항목과 마지막 항목 정보 출력
            if (itemCount > 0) {
                println("첫 번째 배송 항목: ${deliveryItems.first().trackingNumber}")
                println("마지막 배송 항목: ${deliveryItems.last().trackingNumber}")
            }
        } catch (e: Exception) {
            // 예외 처리
            e.printStackTrace()
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
            adapter.notifyDataSetChanged()
            updateEmptyState()
            updateDeliveryCount()
        } catch (e: Exception) {
            // 예외 처리
            e.printStackTrace()
        }
    }
}
