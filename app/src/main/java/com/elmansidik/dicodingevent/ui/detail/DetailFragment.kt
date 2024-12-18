package com.elmansidik.dicodingevent.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.elmansidik.dicodingevent.R
import com.elmansidik.dicodingevent.data.local.database.FavoriteEvent
import com.elmansidik.dicodingevent.databinding.FragmentDetailBinding
import com.elmansidik.dicodingevent.viewmodel.MainViewModel
import com.elmansidik.dicodingevent.viewmodel.ViewModelFactory

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private var eventId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        eventId = arguments?.getInt("eventId")

        eventId?.let { id ->
            mainViewModel.getDetailEvent(id)
            observeFavoriteEvent(id)
        }

        observeEventDetails()
        observeLoadingState()
        observeErrorMessages()

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        return requireNotNull(binding?.root) { "Binding is null!" }
    }

    private fun observeFavoriteEvent(eventId: Int) {
        mainViewModel.getFavoriteEventById(eventId).observe(viewLifecycleOwner) { favoriteEvent ->
            binding?.fabLove?.setImageResource(
                if (favoriteEvent == null) R.drawable.ic_favorite else R.drawable.ic_favorite_filled
            )
            binding?.fabLove?.setOnClickListener {
                handleFavoriteClick(favoriteEvent)
            }
        }
    }

    private fun handleFavoriteClick(favoriteEvent: FavoriteEvent?) {
        val currentEvent = mainViewModel.detailEvent.value
        currentEvent?.let { event ->
            if (favoriteEvent == null) {
                val favorite = FavoriteEvent(
                    eventId = event.id,
                    name = event.name,
                    description = event.summary,
                    image = event.imageLogo
                )
                mainViewModel.insertFavoriteEvent(favorite)
            } else {
                mainViewModel.deleteFavoriteEvent(favoriteEvent)
            }
        }
    }

    private fun observeEventDetails() {
        mainViewModel.detailEvent.observe(viewLifecycleOwner) { event ->
            binding?.apply {
                tvEventName.text = event.name
                tvOwnerName.text = event.ownerName
                tvEventTime.text = getString(R.string.event_time, event.beginTime)
                val remainingQuota = event.quota?.minus(event.registrants ?: 0) ?: 0
                tvQuota.text = getString(R.string.quota_remaining, remainingQuota)

                tvDescription.text = event.description?.let {
                    HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
                }

                btnEventLink.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                    startActivity(intent)
                }

                ivMediaCover?.let {
                    Glide.with(this@DetailFragment)
                        .load(event.mediaCover)
                        .into(it)
                }
            }
        }
    }

    private fun observeLoadingState() {
        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun observeErrorMessages() {
        mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                showErrorState(errorMessage)
            } else {
                hideErrorState()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showErrorState(errorMessage: String) {
        binding?.apply {
            handlingLayout.visibility = View.VISIBLE
            tvErrorMessage.text = errorMessage
            btnRefresh.visibility = View.VISIBLE
            btnRefresh.setOnClickListener {
                eventId?.let { id ->
                    mainViewModel.getDetailEvent(id)
                }
            }
        }
    }

    private fun hideErrorState() {
        binding?.handlingLayout?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
