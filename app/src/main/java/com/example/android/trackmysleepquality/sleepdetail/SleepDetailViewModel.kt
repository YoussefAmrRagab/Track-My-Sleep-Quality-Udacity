package com.example.android.trackmysleepquality.sleepdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepNight

class SleepDetailViewModel(night: SleepNight) : ViewModel() {

    private val _night = MutableLiveData<SleepNight>()
    val night: LiveData<SleepNight>
        get() = _night

    init {
        _night.value = night
    }

    fun onClose() {
        _night.value = null
    }

}