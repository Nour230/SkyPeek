package com.example.skypeek.composablescreens.alert

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.skypeek.MainActivity
import com.example.skypeek.R
import com.example.skypeek.data.models.AlarmPojo
import com.example.skypeek.services.NotificationService
import com.example.skypeek.ui.theme.loyalBlue
import com.example.skypeek.ui.theme.secBlue
import com.example.skypeek.ui.theme.white
import com.example.skypeek.worker.scheduleNotification
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDetailsScreen(
    isNAV: MutableState<Boolean>,
    isFAB: MutableState<Boolean>,
    onDismiss: () -> Unit,
    viewModel: AlarmViewModel
) {
    isNAV.value = false
    isFAB.value = false
    val context = LocalContext.current
    // State for dialogs
    val showDatePicker = remember { mutableStateOf(false) }
    val showTimeStartPicker = remember { mutableStateOf(false) }
    val showTimeEndPicker = remember { mutableStateOf(false) }
    val showTimeError = remember { mutableStateOf(false) }

    // Calculate today's date at midnight
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    // Picker states with date restrictions
    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = today,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= today
            }

            @ExperimentalMaterial3Api
            override fun isSelectableYear(year: Int): Boolean {
                return year >= Calendar.getInstance().get(Calendar.YEAR)
            }
        }
    )

    val startTimePickerState = rememberTimePickerState(is24Hour = false)
    val endTimePickerState = rememberTimePickerState(is24Hour = false)

    // Selected values
    val selectedDateString = stringResource(R.string.select_date)
    val selectedTimeString = stringResource(R.string.select_time)
    val selectedTimeEndString = stringResource(R.string.select_end_time)
    val selectedDate = remember { mutableStateOf(selectedDateString) }
    val selectedStartTime = remember { mutableStateOf(selectedTimeString) }
    val selectedEndtTime = remember { mutableStateOf(selectedTimeEndString) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
    ) {
        // Main content card
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val coroutineScope = rememberCoroutineScope()

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(0.46f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = stringResource(R.string.new_alarm),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = secBlue
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))
                // Show error if time is in past
                if (showTimeError.value) {
                    Text(
                        text = "Please select a future time",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                // Date and time pickers
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Date picker card
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker.value = true },
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.calendar),
                                    contentDescription = "Date",
                                    tint = secBlue,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = stringResource(R.string.date),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Text(
                                text = selectedDate.value,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = if (selectedDate.value == stringResource(R.string.select_date)) {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }

                    // Time picker card
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTimeStartPicker.value = true },
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.alarm),
                                    contentDescription = "Time",
                                    tint = secBlue,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = stringResource(R.string.time),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Text(
                                text = selectedStartTime.value,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = if (selectedStartTime.value == stringResource(R.string.select_time)) {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }

                    // Time picker card
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTimeEndPicker.value = true },
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.alarm),
                                    contentDescription = "End Time",
                                    tint = secBlue,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = stringResource(R.string.end_time),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Text(
                                text = selectedEndtTime.value,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = if (selectedEndtTime.value == stringResource(R.string.select_end_time)) {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    Button(
                        onClick = {
                            val calendar = Calendar.getInstance()

                            // Set date if selected
                            datePickerState.selectedDateMillis?.let {
                                calendar.timeInMillis = it
                            }

                            // Parse and set time
                            val startTime = selectedStartTime.value.split("[: ]".toRegex())
                            if (startTime.size == 3) {
                                var hour = startTime[0].toInt()
                                val minute = startTime[1].toInt()
                                val amPm = startTime[2]

                                // Convert to 24-hour format
                                hour = when {
                                    amPm.equals("PM", true) && hour != 12 -> hour + 12
                                    amPm.equals("AM", true) && hour == 12 -> 0
                                    else -> hour
                                }

                                calendar.set(Calendar.HOUR_OF_DAY, hour)
                                calendar.set(Calendar.MINUTE, minute)
                                calendar.set(Calendar.SECOND, 0)
                                calendar.set(Calendar.MILLISECOND, 0)
                            }
                            val alarm = AlarmPojo(
                                selectedStartTime.value, selectedDate.value
                            )
                            viewModel.indertAlarm(alarm)
                            // Schedule notification
                            scheduleNotification(calendar, context)

                            // Start the foreground service
                            NotificationService.startService(context)
                              // After saving, re-hide the navigation bar
                            (context as MainActivity).hideSystemUI()

                            coroutineScope.launch { onDismiss() }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = (selectedDate.value != stringResource(R.string.select_date)
                                && selectedStartTime.value != stringResource(R.string.select_time)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = loyalBlue,
                            contentColor = white
                        )
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }


        // Date Picker Dialog
        if (showDatePicker.value) {
            Dialog(onDismissRequest = { showDatePicker.value = false }) {
                Surface(
                    modifier = Modifier
                        .width(440.dp)
                        .clip(RoundedCornerShape(28.dp)),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column {
                        DatePicker(state = datePickerState)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { showDatePicker.value = false }
                            ) {
                                Text(stringResource(R.string.cancel))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(
                                onClick = {
                                    showDatePicker.value = false
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        val date = Date(millis)
                                        val formatter =
                                            SimpleDateFormat("EEE, MMM d", Locale.getDefault())
                                        selectedDate.value = formatter.format(date)
                                    }
                                }
                            ) {
                                Text(
                                    text = stringResource(R.string.ok),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Time Picker Dialog
        if (showTimeStartPicker.value) {
            Dialog(onDismissRequest = { showTimeStartPicker.value = false }) {
                Surface(
                    modifier = Modifier
                        .width(340.dp)
                        .clip(RoundedCornerShape(28.dp)),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TimePicker(state = startTimePickerState)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { showTimeStartPicker.value = false }
                            ) {
                                Text(stringResource(R.string.cancel))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(
                                onClick = {
                                    showTimeStartPicker.value = false
                                    val hour = startTimePickerState.hour
                                    val minute = startTimePickerState.minute
                                    val amPm = if (hour < 12) "AM" else "PM"
                                    val displayHour = if (hour % 12 == 0) 12 else hour % 12
                                    selectedStartTime.value =
                                        "$displayHour:${minute.toString().padStart(2, '0')} $amPm"
                                }
                            ) {
                                Text(stringResource(R.string.ok), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
        // End Time Picker Dialog
        if (showTimeEndPicker.value) {
            Dialog(onDismissRequest = { showTimeEndPicker.value = false }) {
                Surface(
                    modifier = Modifier
                        .width(340.dp)
                        .clip(RoundedCornerShape(28.dp)),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TimePicker(state = endTimePickerState)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { showTimeEndPicker.value = false }
                            ) {
                                Text(stringResource(R.string.cancel))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(
                                onClick = {
                                    showTimeEndPicker.value = false
                                    val hour = endTimePickerState.hour
                                    val minute = endTimePickerState.minute
                                    val amPm = if (hour < 12) "AM" else "PM"
                                    val displayHour = if (hour % 12 == 0) 12 else hour % 12
                                    selectedEndtTime.value =
                                        "$displayHour:${minute.toString().padStart(2, '0')} $amPm"
                                }
                            ) {
                                Text(stringResource(R.string.ok), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
