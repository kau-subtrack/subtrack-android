package com.example.subtrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.subtrack.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var selectedUserType: String = "OWNER"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnOwner.setOnClickListener {
            selectedUserType = "OWNER"
            updateUI()
        }

        binding.btnCourier.setOnClickListener {
            selectedUserType = "COURIER"
            updateUI()
        }

        binding.btnCheckId.setOnClickListener {
            // TODO: 나중에 실제 API 연동
        }

        binding.btnRegister.setOnClickListener {
            // TODO: 나중에 유효성 검사 및 전송
        }
    }

    private fun updateUI() {
        val isOwner = selectedUserType == "OWNER"

        binding.etShopAddress.visibility = if (isOwner) View.VISIBLE else View.GONE
        binding.etBizNum.visibility = if (isOwner) View.VISIBLE else View.GONE

        binding.btnUploadLicense.visibility = if (isOwner) View.GONE else View.VISIBLE
        binding.btnUploadCareer.visibility = if (isOwner) View.GONE else View.VISIBLE
        binding.layoutRegion.visibility = if (isOwner) View.GONE else View.VISIBLE
        binding.tvCourierNotice.visibility = if (isOwner) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
