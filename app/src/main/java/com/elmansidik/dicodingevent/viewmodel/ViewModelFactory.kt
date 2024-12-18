package com.elmansidik.dicodingevent.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.elmansidik.dicodingevent.data.model_repository.SettingPreferences
import com.elmansidik.dicodingevent.data.model_repository.EventRepository
import com.elmansidik.dicodingevent.di.Injection

class ViewModelFactory private constructor(
    private val pref: SettingPreferences,
    private val eventRepository: EventRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            MainViewModel::class.java -> MainViewModel(pref, eventRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: createFactory(context).also { instance = it }
            }
        }

        // Helper function to create ViewModelFactory
        private fun createFactory(context: Context): ViewModelFactory {
            val preferences = Injection.providePreferences(context)
            val repository = Injection.provideRepository(context)
            return ViewModelFactory(preferences, repository)
        }
    }
}
