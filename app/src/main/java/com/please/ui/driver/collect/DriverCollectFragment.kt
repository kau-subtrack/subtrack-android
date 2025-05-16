package com.please.ui.driver.collect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.please.R
import com.please.data.models.driver.CollectionRequest
import com.please.data.repositories.DriverDataRepository
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverCollectFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var itemCountView: TextView
    private lateinit var adapter: CollectionRequestAdapter
    
    // 데이터 리스트
    private val collectionItems = mutableListOf<CollectionRequest>()

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
            // 뷰 초기화
            recyclerView = view.findViewById(R.id.rv_collection_requests)
            emptyView = view.findViewById(R.id.tv_empty_collections)
            itemCountView = view.findViewById(R.id.tv_item_count)
            
            // 리사이클러뷰 설정
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            
            // 데이터 로드
            loadData()
            
            // 항목 수 업데이트
            updateItemCount()
            
            // 어댑터 설정
            adapter = CollectionRequestAdapter(collectionItems) { position ->
                try {
                    // 수거 완료 버튼 클릭 처리
                    if (position >= 0 && position < collectionItems.size) {
                        val item = collectionItems[position]
                        DriverDataRepository.removeCollectionRequest(item.id)
                        collectionItems.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        adapter.notifyItemRangeChanged(position, collectionItems.size)
                        updateEmptyState()
                        updateItemCount()
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
            collectionItems.clear()
            collectionItems.addAll(DriverDataRepository.getCollectionRequests())
            
            // 로그로 항목 수 출력
            val itemCount = collectionItems.size
            println("수거 요청 항목 수: $itemCount")
            // 첫 번째 항목과 마지막 항목 정보 출력
            if (itemCount > 0) {
                println("첫 번째 수거 항목: ${collectionItems.first().trackingNumber}")
                println("마지막 수거 항목: ${collectionItems.last().trackingNumber}")
            }
        } catch (e: Exception) {
            // 예외 처리
            e.printStackTrace()
        }
    }
    
    private fun updateEmptyState() {
        try {
            if (collectionItems.isEmpty()) {
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
            itemCountView.text = "항목 수: ${collectionItems.size}개"
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
            updateItemCount()
        } catch (e: Exception) {
            // 예외 처리
            e.printStackTrace()
        }
    }
}
