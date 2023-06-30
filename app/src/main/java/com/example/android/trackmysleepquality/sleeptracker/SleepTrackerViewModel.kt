/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
    val database: SleepDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val tonight = MutableLiveData<SleepNight?>()

    val nights = database.getAllNight()
    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()

    private val _showSnackBar = MutableLiveData(false)
    val showSnackBar: LiveData<Boolean>
        get() = _showSnackBar

    val navigateToSleepQuality: LiveData<SleepNight>
        get() = _navigateToSleepQuality

    val isTracking = tonight.map {
        it != null
    }

    val isNoRecord = nights.map {
        it.isEmpty()
    }

    init {
        initializeNight()
    }

    private fun initializeNight() {
        viewModelScope.launch {
            tonight.value = getNightFromDatabase()
        }
    }

    private suspend fun getNightFromDatabase(): SleepNight? {
        return withContext(Dispatchers.IO) {
            var night = database.getTonight()
            if (night?.startTimeMilli != night?.endTimeMilli) {
                // Let the coroutine get tonight from the database.
                // If the start and end times are the not the same, meaning, the night has already been completed, return null
                night = null
            }
            return@withContext night
        }
    }


    fun onStartTracking() {
        viewModelScope.launch {
            val night = SleepNight()
            insert(night)
            tonight.value = getNightFromDatabase()
        }
    }

    private suspend fun insert(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insert(night)
        }
    }

    fun onStopTracking() {
        viewModelScope.launch {
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
            _navigateToSleepQuality.value = oldNight
        }
    }

    private suspend fun update(oldNight: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(oldNight)
        }
    }

    fun onClear() {
        viewModelScope.launch {
            clear()
            tonight.value = null
            _showSnackBar.value = true
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }

}

