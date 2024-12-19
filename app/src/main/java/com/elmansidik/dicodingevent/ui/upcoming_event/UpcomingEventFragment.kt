package com.elmansidik.dicodingevent.ui.upcoming_event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.elmansidik.dicodingevent.R
import com.elmansidik.dicodingevent.data.response_retrofit.response.ListEventsItem
import com.elmansidik.dicodingevent.databinding.FragmentUpcomingEventBinding
import com.elmansidik.dicodingevent.utils.UiHandler
import com.elmansidik.dicodingevent.viewmodel.AdapterVerticalEvent
import com.elmansidik.dicodingevent.viewmodel.MainViewModel
import com.elmansidik.dicodingevent.viewmodel.ViewModelFactory

class UpcomingEventFragment : Fragment() {

    private var _binding: FragmentUpcomingEventBinding? = null
    private val binding get() = _binding

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var adapterVertical: AdapterVerticalEvent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingEventBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupAdapter()
        observeViewModel()

        // Memanggil getUpcomingEvent untuk memulai pengambilan data
        mainViewModel.getUpcomingEvent()

        return requireNotNull(binding?.root) { "Binding is null!" }
    }

    private fun setupRecyclerView() {
        binding?.apply {
            val layoutManager = LinearLayoutManager(requireContext())
            rvUpcomingEvent.layoutManager = layoutManager
            val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
            rvUpcomingEvent.addItemDecoration(itemDecoration)
        }
    }

    private fun setupAdapter() {
        adapterVertical = AdapterVerticalEvent(::onEventClicked)
        binding?.rvUpcomingEvent?.adapter = adapterVertical
    }

    private fun observeViewModel() {
        mainViewModel.apply {
            upcomingEvent.observe(viewLifecycleOwner, Observer { events ->
                setUpcomingEvent(events)
                clearErrorMessage()
            })

            isLoadingUpcoming.observe(viewLifecycleOwner, Observer { isLoading ->
                // Tampilkan atau sembunyikan ProgressBar berdasarkan status loading
                binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
            })

            errorMessage.observe(viewLifecycleOwner, Observer { errorMessage ->
                UiHandler.handleError(
                    isError = errorMessage != null,
                    message = errorMessage,
                    errorTextView = binding?.tvErrorMessage,
                    refreshButton = binding?.btnRefresh,
                    recyclerView = binding?.rvUpcomingEvent
                ) {
                    getUpcomingEvent()
                }
            })
        }
    }


    private fun setUpcomingEvent(events: List<ListEventsItem>) {
        if (events.isNotEmpty()) {
            adapterVertical.submitList(events)
        } else {
            showNoEventsError()
        }
    }

    private fun onEventClicked(eventId: Int?) {
        eventId?.let {
            val bundle = Bundle().apply {
                putInt("eventId", it)
            }
            findNavController().navigate(R.id.navigation_detail, bundle)
        } ?: showError("Invalid Event ID")
    }

    private fun showError(message: String) {
        binding?.apply {
            tvErrorMessage.text = message
            tvErrorMessage.visibility = View.VISIBLE
            rvUpcomingEvent.visibility = View.GONE
        }
    }

    private fun showNoEventsError() {
        showError("No upcoming events found.")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
