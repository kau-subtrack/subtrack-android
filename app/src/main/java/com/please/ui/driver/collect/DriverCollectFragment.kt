package com.please.ui.driver.collect

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
import com.please.data.models.driver.PickList
import com.please.utils.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverCollectFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var itemCountView: TextView
    private lateinit var pickupAdapter: PickupRequestAdapter
    private lateinit var preferenceManager: PreferenceManager
    
    // ViewModel 초기화
    private val viewModel: DriverCollectViewModel by viewModels()
    
    // 데이터 리스트
    private val pickupItems = mutableListOf<PickList>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_driver_collect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            // Preference Manager 초기화
            preferenceManager = PreferenceManager(requireContext())
            
            // 뷰 초기화
            recyclerView = view.findViewById(R.id.rv_collection_requests)
            emptyView = view.findViewById(R.id.tv_empty_collections)
            itemCountView = view.findViewById(R.id.tv_item_count)
            
            // 리사이클러뷰 설정
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            
            // 어댑터 설정
            pickupAdapter = PickupRequestAdapter(pickupItems) { position ->
                try {
                    // 수거 완료 버튼 클릭 처리
                    if (position >= 0 && position < pickupItems.size) {
                        val item = pickupItems[position]
                        val token = preferenceManager.getToken() ?: ""
                        viewModel.completePickup(token, item.ownerId)
                    }
                } catch (e: Exception) {
                    // 오류 처리
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "수거 완료 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            
            recyclerView.adapter = pickupAdapter
            
            // ViewModel 관찰 설정
            setupObservers()
            
            // 데이터 로드
            loadData()
            
            // 초기 빈 상태 확인
            updateEmptyState()
        } catch (e: Exception) {
            // 예외 처리
            e.printStackTrace()
            Toast.makeText(requireContext(), "화면 초기화 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupObservers() {
        // 수거 목록 관찰
        viewModel.pickupList.observe(viewLifecycleOwner) { items ->
            pickupItems.clear()
            pickupItems.addAll(items)
            pickupAdapter.notifyDataSetChanged()
            updateEmptyState()
            updateItemCount()
        }
        
        // 로딩 상태 관찰
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // 로딩 상태에 따라 적절한 UI 변경
            // 프로그레스바가 있다면 여기서 표시/숨김 처리
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
                Toast.makeText(requireContext(), "수거 완료 처리가 완료되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun loadData() {
        try {
            val token = preferenceManager.getToken() ?: ""
            if (token.isNotEmpty()) {
                viewModel.fetchPickupList(token)
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
            if (pickupItems.isEmpty()) {
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
    
    private fun updateItemCount() {
        try {
            itemCountView.text = "항목 수: ${pickupItems.size}개"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun onResume() {
        super.onResume()
        try {
            // 화면이 다시 표시될 때 데이터 갱신
            loadData()
            updateEmptyState()
            updateItemCount()
        } catch (e: Exception) {
            // 예외 처리
            e.printStackTrace()
        }
    }
}
