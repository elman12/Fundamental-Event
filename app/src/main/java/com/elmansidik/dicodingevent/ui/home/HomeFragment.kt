package com.elmansidik.dicodingevent.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elmansidik.dicodingevent.R
import com.elmansidik.dicodingevent.data.response_retrofit.response.ListEventsItem
import com.elmansidik.dicodingevent.databinding.FragmentHomeBinding
import com.elmansidik.dicodingevent.utils.UiHandler.handleError
import com.elmansidik.dicodingevent.utils.UiHandler.showLoading
import com.elmansidik.dicodingevent.viewmodel.AdapterHorizontalEvent
import com.elmansidik.dicodingevent.viewmodel.AdapterVerticalEvent
import com.elmansidik.dicodingevent.viewmodel.MainViewModel
import com.elmansidik.dicodingevent.viewmodel.ViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var adapterVertical: AdapterVerticalEvent
    private lateinit var adapterHorizontal: AdapterHorizontalEvent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerViews()
        setupAdapters()
        observeViewModel()

        return requireNotNull(binding?.root) { "Binding is null!" }
    }

    private fun setupRecyclerViews() {
        binding?.apply {
            setupRecyclerView(rvUpcomingEvent, LinearLayoutManager.HORIZONTAL)
            setupRecyclerView(rvFinishedEvent, LinearLayoutManager.VERTICAL)
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, orientation: Int) {
        val layoutManager = LinearLayoutManager(requireContext(), orientation, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), layoutManager.orientation))
    }

    private fun setupAdapters() {
        adapterVertical = AdapterVerticalEvent { eventId ->
            val bundle = Bundle().apply {
                if (eventId != null) {
                    putInt("eventId", eventId)
                }
            }
            findNavController().navigate(R.id.navigation_detail, bundle)
        }

        adapterHorizontal = AdapterHorizontalEvent { eventId ->
            val bundle = Bundle().apply {
                if (eventId != null) {
                    putInt("eventId", eventId)
                }
            }
            findNavController().navigate(R.id.navigation_detail, bundle)
        }

        binding?.apply {
            rvUpcomingEvent.adapter = adapterHorizontal
            rvFinishedEvent.adapter = adapterVertical
        }

    }

    private fun observeViewModel() {
        binding?.apply {
            mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                Log.d("HomeFragment", "Loading state: $isLoading")
                showLoading(isLoading, progressBar)
            }

            mainViewModel.upcomingEvent.observe(viewLifecycleOwner) { listItems ->
                setUpcomingEvent(listItems)
                mainViewModel.clearErrorMessage()
            }

            mainViewModel.finishedEvent.observe(viewLifecycleOwner) { listItems ->
                setFinishedEvent(listItems)
                mainViewModel.clearErrorMessage()
            }

            mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                handleError(
                    isError = errorMessage != null,
                    message = errorMessage,
                    errorTextView = tvErrorMessage,
                    refreshButton = btnRefresh,
                    recyclerView = rvFinishedEvent
                ) {
                    mainViewModel.getUpcomingEvent()
                    mainViewModel.getFinishedEvent()
                }
            }
        }
    }


    private fun setUpcomingEvent(listUpcomingEvent: List<ListEventsItem>) {
        val limitedList = listUpcomingEvent.take(5)
        adapterHorizontal.submitList(limitedList)
    }

    private fun setFinishedEvent(listFinishedEvent: List<ListEventsItem>) {
        val limitedList = listFinishedEvent.takeLast(15)
        adapterVertical.submitList(limitedList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
