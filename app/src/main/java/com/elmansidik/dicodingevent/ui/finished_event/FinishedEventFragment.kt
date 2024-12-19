package com.elmansidik.dicodingevent.ui.finished_event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.elmansidik.dicodingevent.R
import com.elmansidik.dicodingevent.data.response_retrofit.response.ListEventsItem
import com.elmansidik.dicodingevent.databinding.FragmentFinishedEventBinding
import com.elmansidik.dicodingevent.viewmodel.AdapterVerticalEvent
import com.elmansidik.dicodingevent.viewmodel.MainViewModel
import com.elmansidik.dicodingevent.viewmodel.ViewModelFactory


class FinishedEventFragment : Fragment() {

    private var _binding: FragmentFinishedEventBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var adapterVertical: AdapterVerticalEvent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedEventBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeViewModel()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.apply {
            rvFinishedEvent.layoutManager = LinearLayoutManager(requireContext())
            rvFinishedEvent.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))

            adapterVertical = AdapterVerticalEvent { eventId ->
                navigateToEventDetail(eventId)
            }
            rvFinishedEvent.adapter = adapterVertical
        }
    }

    private fun observeViewModel() {
        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        mainViewModel.finishedEvent.observe(viewLifecycleOwner) { listItems ->
            updateEventList(listItems)
        }

        mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                showErrorState(errorMessage)
            }
        }
    }

    // Pastikan Anda memanggil getAllFinishedEvents() di tempat yang tepat
    private fun fetchFinishedEvents() {
        mainViewModel.getAllFinishedEvents() // Memanggil fungsi untuk mengambil data
    }


    private fun navigateToEventDetail(eventId: Int?) {
        val bundle = Bundle().apply {
            eventId?.let { putInt("eventId", it) }
        }
        findNavController().navigate(R.id.navigation_detail, bundle)
    }

    private fun updateEventList(listItems: List<ListEventsItem>) {
        adapterVertical.submitList(listItems)  // Pastikan list yang dikirim adalah tipe yang benar
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showErrorState(errorMessage: String) {
        binding.apply {
            tvErrorMessage.visibility = View.VISIBLE
            tvErrorMessage.text = errorMessage
            btnRefresh.visibility = View.VISIBLE

            btnRefresh.setOnClickListener {
                mainViewModel.getAllFinishedEvents() // Retry fetching the events
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
