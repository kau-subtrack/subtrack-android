package com.please.ui.seller.delivery

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.please.R
import com.please.data.models.seller.DeliveryStatus
import com.please.databinding.FragmentSellerDeliveryBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import android.util.Log
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatSpinner

@AndroidEntryPoint
class SellerDeliveryFragment : Fragment() {

    private var _binding: FragmentSellerDeliveryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SellerDeliveryViewModel by viewModels()

    private lateinit var deliveryAdapter: DeliveryAdapter

    private var initializingSpinners = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerDeliveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupModeButtons()
        setupObservers()
        setupSpinnerListeners()

        if (viewModel.selectedDate.value == null) {
            initializingSpinners = true
            viewModel.initializeDefaultDate()
        }
    }

    private fun setupRecyclerView() {
        deliveryAdapter = DeliveryAdapter(
            onDeleteClick = { id -> showDeleteConfirmDialog(id) },
            getDeliveryStatusColor = { status ->
                when (status) {
                    DeliveryStatus.PENDING -> ContextCompat.getColor(requireContext(), R.color.delivery_pending)
                    DeliveryStatus.IN_PROGRESS -> ContextCompat.getColor(requireContext(), R.color.delivery_in_progress)
                    DeliveryStatus.DELIVERED -> ContextCompat.getColor(requireContext(), R.color.delivery_delivered)
                }
            },
            getDeliveryStatusText = { status ->
                when (status) {
                    DeliveryStatus.PENDING -> "배송전"
                    DeliveryStatus.IN_PROGRESS -> "배송중"
                    DeliveryStatus.DELIVERED -> "배송완료"
                }
            }
        )

        binding.rvDeliveries.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = deliveryAdapter
        }
    }

    private fun setupModeButtons() {
        binding.btnRegister.setOnClickListener {
            viewModel.setMode(SellerDeliveryViewModel.DeliveryMode.REGISTER)
        }

        binding.btnView.setOnClickListener {
            if (viewModel.mode.value != SellerDeliveryViewModel.DeliveryMode.VIEW) {
                viewModel.setMode(SellerDeliveryViewModel.DeliveryMode.VIEW)
            }
        }
    }

    private fun setupObservers() {
        viewModel.yearOptions.observe(viewLifecycleOwner) { years ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, years)
            binding.spinnerYear.adapter = adapter
        }

        viewModel.monthOptions.observe(viewLifecycleOwner) { months ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, months)
            binding.spinnerMonth.adapter = adapter
        }

        viewModel.dayOptions.observe(viewLifecycleOwner) { days ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, days)
            binding.spinnerDay.adapter = adapter
        }

        viewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            Log.d("SelectedDateCheck", "selectedDate = $date")

            if (initializingSpinners) {
                val cal = Calendar.getInstance().apply { time = date }
                binding.spinnerYear.setSelection(viewModel.getYearPosition(cal.get(Calendar.YEAR)))
                binding.spinnerMonth.setSelection(viewModel.getMonthPosition(cal.get(Calendar.MONTH) + 1))
                binding.spinnerDay.setSelection(viewModel.getDayPosition(cal.get(Calendar.DAY_OF_MONTH)))

                initializingSpinners = false
            }
        }

        viewModel.mode.observe(viewLifecycleOwner) { mode ->
            when (mode) {
                SellerDeliveryViewModel.DeliveryMode.REGISTER -> {
                    binding.btnRegister.setBackgroundResource(R.drawable.bg_button_selected)
                    binding.btnRegister.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    binding.btnView.setBackgroundResource(R.drawable.bg_button_unselected)
                    binding.btnView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    binding.fabAddDelivery.visibility = View.VISIBLE
                    binding.tvRegisterInfo.visibility = View.VISIBLE
                    deliveryAdapter.setDeleteButtonVisible(true)
                }
                SellerDeliveryViewModel.DeliveryMode.VIEW -> {
                    binding.btnView.setBackgroundResource(R.drawable.bg_button_selected)
                    binding.btnView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    binding.btnRegister.setBackgroundResource(R.drawable.bg_button_unselected)
                    binding.btnRegister.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    binding.fabAddDelivery.visibility = View.GONE
                    binding.tvRegisterInfo.visibility = View.GONE
                    deliveryAdapter.setDeleteButtonVisible(false)
                }
            }
        }

        viewModel.deliveryList.observe(viewLifecycleOwner) { deliveries ->
            deliveryAdapter.submitList(deliveries)
            binding.tvEmptyMessage.visibility = if (deliveries.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun setupSpinnerListeners() {
        binding.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.onYearSelected(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.onMonthSelected(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.onDaySelected(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.fabAddDelivery.setOnClickListener {
            viewModel.onAddDeliveryClicked()?.let {
                val action = SellerDeliveryFragmentDirections.actionSellerDeliveryFragmentToDeliveryInputFragment(it.time)
                findNavController().navigate(action)
            } ?: Toast.makeText(requireContext(), "유효하지 않은 날짜입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmDialog(deliveryId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("택배 삭제")
            .setMessage("이 택배를 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                viewModel.deleteDelivery(deliveryId)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}