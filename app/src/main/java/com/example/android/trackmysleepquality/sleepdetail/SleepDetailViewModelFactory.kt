package com.example.android.trackmysleepquality.sleepdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.trackmysleepquality.database.SleepNight

class SleepDetailViewModelFactory(private val night: SleepNight) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SleepDetailViewModel::class.java)) {
            return SleepDetailViewModel(night) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}