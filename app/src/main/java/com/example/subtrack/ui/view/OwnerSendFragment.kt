package com.example.subtrack.ui.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.subtrack.R
import com.example.subtrack.databinding.FragmentOwnerSendBinding
import com.example.subtrack.ui.adapter.SendItemAdapter
import com.example.subtrack.viewmodel.SendViewModel
import java.util.Calendar

class OwnerSendFragment : Fragment() {
    private var _binding: FragmentOwnerSendBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SendViewModel by activityViewModels()

    private lateinit var registerAdapter: SendItemAdapter
    private lateinit var queryAdapter: SendItemAdapter

    private var isCurrentRegisterMode = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOwnerSendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDateSpinners(isRegister = true)

        // 어댑터 초기화
        registerAdapter = SendItemAdapter(isQuery = false)
        queryAdapter = SendItemAdapter(isQuery = true)

        // 기본 등록 화면 세팅
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = registerAdapter
        binding.tvNotice.visibility = View.VISIBLE
        binding.fab.visibility = View.VISIBLE

        // 탭 전환 처리
        binding.btnRegisterTab.setOnClickListener {
            if (!isCurrentRegisterMode) {
                isCurrentRegisterMode = true
                binding.recyclerView.adapter = registerAdapter
                binding.tvNotice.visibility = View.VISIBLE
                binding.fab.visibility = View.VISIBLE

                binding.btnRegisterTab.setBackgroundResource(R.drawable.button_selected)
                binding.btnRegisterTab.setTextColor(Color.BLACK)
                binding.btnQueryTab.setBackgroundResource(R.drawable.button_unselected)
                binding.btnQueryTab.setTextColor(Color.WHITE)

                setupDateSpinners(isRegister = true)
            }
        }

        binding.btnQueryTab.setOnClickListener {
            if (isCurrentRegisterMode) {
                isCurrentRegisterMode = false
                binding.recyclerView.adapter = queryAdapter
                binding.tvNotice.visibility = View.GONE
                binding.fab.visibility = View.GONE

                binding.btnRegisterTab.setBackgroundResource(R.drawable.button_unselected)
                binding.btnRegisterTab.setTextColor(Color.WHITE)
                binding.btnQueryTab.setBackgroundResource(R.drawable.button_selected)
                binding.btnQueryTab.setTextColor(Color.BLACK)

                setupDateSpinners(isRegister = false)
            }
        }

        // RecyclerView에 LiveData 적용
        viewModel.registerList.observe(viewLifecycleOwner) {
            registerAdapter.submitList(it)
        }

        viewModel.queryList.observe(viewLifecycleOwner) {
            queryAdapter.submitList(it)
        }
    }

    private fun setupDateSpinners(isRegister: Boolean) {
        val today = Calendar.getInstance()
        val tomorrow = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }

        val defaultYear = tomorrow.get(Calendar.YEAR)
        val defaultMonth = tomorrow.get(Calendar.MONTH) + 1 // 1~12
        val defaultDay = tomorrow.get(Calendar.DAY_OF_MONTH)

        // 연도 리스트 (등록은 현재~1년, 조회는 2020~내년)
        val yearList = if (isRegister) {
            (defaultYear..defaultYear + 1).toList()
        } else {
            (2020..defaultYear + 1).toList()
        }

        val yearAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, yearList.map { "${it}년" })
        binding.spinnerYear.adapter = yearAdapter
        binding.spinnerYear.setSelection(0)

        // 월 리스트 생성 함수 (등록이면 현재 달부터 시작)
        fun getMonthList(selectedYear: Int): List<Int> {
            return if (isRegister && selectedYear == defaultYear) {
                (defaultMonth..12).toList()
            } else {
                (1..12).toList()
            }
        }

        val initialMonthList = getMonthList(defaultYear)
        val monthAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, initialMonthList.map { "${it}월" })
        binding.spinnerMonth.adapter = monthAdapter

        // 초기 월 선택 (defaultMonth가 포함되어 있지 않을 경우 0번째 선택)
        val defaultMonthIndex = initialMonthList.indexOf(defaultMonth).takeIf { it != -1 } ?: 0
        binding.spinnerMonth.setSelection(defaultMonthIndex)

        // 일 리스트 초기 설정
        updateDaySpinner(defaultYear, initialMonthList[defaultMonthIndex], isRegister)

        // 연도 선택 시 → 월 & 일 재설정
        binding.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedYear = yearList[position]
                val newMonthList = getMonthList(selectedYear)

                // 월 스피너 갱신
                val monthAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, newMonthList.map { "${it}월" })
                binding.spinnerMonth.adapter = monthAdapter
                binding.spinnerMonth.setSelection(0)

                // 일도 갱신
                updateDaySpinner(selectedYear, newMonthList[0], isRegister)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // 월 선택 시 → 일 재설정
        binding.spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedYear = yearList[binding.spinnerYear.selectedItemPosition]
                val monthList = getMonthList(selectedYear)
                val selectedMonth = monthList[position]
                updateDaySpinner(selectedYear, selectedMonth, isRegister)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }


    private fun updateDaySpinner(year: Int, month: Int, isRegister: Boolean) {
        val today = Calendar.getInstance()
        val isThisMonth = (today.get(Calendar.YEAR) == year && today.get(Calendar.MONTH) + 1 == month)

        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1)
        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val startDay = if (isRegister && isThisMonth) today.get(Calendar.DAY_OF_MONTH) + 1 else 1
        val days = (startDay..maxDay).toList()

        val dayAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, days.map { "${it}일" })
        binding.spinnerDay.adapter = dayAdapter
        binding.spinnerDay.setSelection(0)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
