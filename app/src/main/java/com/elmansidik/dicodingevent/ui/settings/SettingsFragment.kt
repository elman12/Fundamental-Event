package com.elmansidik.dicodingevent.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.elmansidik.dicodingevent.data.response_retrofit.response.ListEventsItem
import com.elmansidik.dicodingevent.databinding.FragmentSettingsBinding
import com.elmansidik.dicodingevent.utils.DailyReminderWorker
import com.elmansidik.dicodingevent.viewmodel.MainViewModel
import com.elmansidik.dicodingevent.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var workManager: WorkManager
    private lateinit var periodicWorkRequest: PeriodicWorkRequest

    // Permission launcher for notification
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(
                    requireContext(),
                    "Notifications permission granted",
                    Toast.LENGTH_SHORT
                ).show()
                setupDailyReminder() // Only setup reminder after permission is granted
            } else {
                Toast.makeText(
                    requireContext(),
                    "Notifications permission rejected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        workManager = WorkManager.getInstance(requireContext())

        // Check for notification permission
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        // Observing dark mode setting
        mainViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding?.switchDarkMode?.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding?.switchDarkMode?.isChecked = false
            }
        }

        // Dark mode switch listener
        binding?.switchDarkMode?.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            mainViewModel.saveThemeSetting(isChecked)
        }

        return requireNotNull(binding?.root) { "Binding is null!" }
    }

    // Setup daily reminder logic
    private fun setupDailyReminder() {
        mainViewModel.getUpcomingEvent()
        mainViewModel.upcomingEvent.observe(viewLifecycleOwner) { listItems ->
            if (listItems.isNotEmpty()) {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                // Filter valid events
                val validEvents = listItems.filter { event ->
                    event.beginTime?.let {
                        val eventDate = sdf.parse(it)?.time ?: 0
                        eventDate >= System.currentTimeMillis()
                    } ?: false
                }

                if (validEvents.isNotEmpty()) {
                    val nearestEvent = validEvents.minByOrNull { event ->
                        event.beginTime?.let { sdf.parse(it)?.time } ?: Long.MAX_VALUE
                    }

                    nearestEvent?.let { event ->
                        scheduleDailyReminder(event)
                    }
                }
            }
        }
    }

    // Schedule a daily reminder worker
    private fun scheduleDailyReminder(event: ListEventsItem) {
        val data = Data.Builder()
            .putString("event_name", event.name)
            .putString("event_time", event.beginTime)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        periodicWorkRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        workManager.enqueue(periodicWorkRequest)
    }

    // Cancel any scheduled daily reminders
    private fun cancelDailyReminder() {
        if (this::periodicWorkRequest.isInitialized) {
            workManager.cancelWorkById(periodicWorkRequest.id)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
