package com.example.skypeek.composablescreens.alert

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.skypeek.composablescreens.fav.FavViewModel
import com.example.skypeek.data.models.AlarmPojo
import com.example.skypeek.data.models.ResponseStateAlarm
import com.example.skypeek.data.repository.WeatherRepository
import com.example.skypeek.utiles.parseTimeToMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import androidx.core.content.edit

class AlarmViewModel(private val repo: WeatherRepository) : ViewModel() {
    private val _alarmList = MutableStateFlow<ResponseStateAlarm>(ResponseStateAlarm.Loading)
    val alarmList = _alarmList.asStateFlow()

    private val mutableError = MutableSharedFlow<String>()
    val error = mutableError.asSharedFlow()

    private val _isDeleted = MutableSharedFlow<String>()
    val isDelete = _isDeleted.asSharedFlow()

    init {
        gerAlarmList()
    }

    fun gerAlarmList() {
        viewModelScope.launch {
            try {
                val response = repo.getAllAlarms()
                response.catch { ex ->
                    _alarmList.value = ResponseStateAlarm.Error(ex)
                    mutableError.emit(ex.message.toString())
                }.collect {
                    _alarmList.value = ResponseStateAlarm.Success(it)
                }
            } catch (e: Exception) {
                _alarmList.value = ResponseStateAlarm.Error(e)
            }
        }
    }


    fun deleteAlarmRoom(alarm: AlarmPojo, context: Context) {
        val prefs = context.getSharedPreferences("notifications", Context.MODE_PRIVATE)
        val timeInMillis = prefs.getLong("${alarm.time}_${alarm.date}", -1)

        if (timeInMillis != -1L) {
            Log.i("TAG", "deleteAlarmRoom: $timeInMillis")

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    repo.deleteAlarm(alarm)
                    _isDeleted.emit("item deleted from favorite")

                    WorkManager.getInstance(context)
                        .cancelUniqueWork("notification_$timeInMillis")

                    // Remove from SharedPreferences
                    prefs.edit() { remove("${alarm.time}_${alarm.date}") }
                } catch (e: Exception) {
                    _alarmList.value = ResponseStateAlarm.Error(e)
                }
            }
        } else {
            Log.e("TAG", "deleteAlarmRoom: No matching scheduled notification found.")
        }
    }


    fun indertAlarm(alarm: AlarmPojo) {
        viewModelScope.launch {
            try {
                repo.insertAlarm(alarm)
            } catch (e: Exception) {
                mutableError.emit(e.message.toString())
            }
        }
    }
}


class AlarmFactory(val repo: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlarmViewModel(repo) as T
    }
}