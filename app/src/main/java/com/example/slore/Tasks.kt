package com.example.slore

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.navigation.NavHostController
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlinx.coroutines.launch


@Composable
fun TasksScreen(navController: NavHostController) {
    val tasks = remember { mutableStateListOf<Task>() }
    val showDialog = remember { mutableStateOf(false) }
    val taskInput = remember { mutableStateOf("") }
    val selectedDate = remember { mutableStateOf(Date()) }
    val showDatePicker = remember { mutableStateOf(false) }

    // Fetch tasks when the component is first created
    LaunchedEffect(Unit) {
        fetchTasks(tasks)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Display list of tasks
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(tasks) { task ->
                Text(text = "${task.description} - ${task.date}")
            }
        }

        // Add button
        FloatingActionButton(
            onClick = { showDialog.value = true },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Task")
        }

        // Dialog
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Add Task") },
                text = {
                    Column {
                        TextField(
                            value = taskInput.value,
                            onValueChange = { taskInput.value = it },
                            placeholder = { Text("Your task...") }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDatePicker.value = true }
                                .border(1.dp, Color.Gray, shape = RoundedCornerShape(16.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Set alert",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                SimpleDateFormat(
                                    "MMM dd, yyyy hh:mm a",
                                    Locale.getDefault()
                                ).format(selectedDate.value),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (taskInput.value.isNotEmpty()) {
                            addTaskToFirestore(taskInput.value, selectedDate.value)
                            taskInput.value = ""
                            showDialog.value = false
                            fetchTasks(tasks)
                        }
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog.value = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showDatePicker.value) {
            DateTimePickerDialog(
                initialDate = selectedDate.value,
                onDateSelected = { newDate ->
                    selectedDate.value = newDate
                    showDatePicker.value = false
                },
                onDismiss = { showDatePicker.value = false }
            )
        }
    }
}

data class Task(val description: String, val date: Date)

fun addTaskToFirestore(taskDescription: String, taskDate: Date) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    if (userId != null) {
        val task = hashMapOf(
            "description" to taskDescription,
            "date" to taskDate
        )

        db.collection("users").document(userId)
            .collection("tasks")
            .add(task)
            .addOnSuccessListener { documentReference ->
                println("Task added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Error adding task: $e")
            }
    }
}

fun fetchTasks(tasks: MutableList<Task>) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    if (userId != null) {
        db.collection("users").document(userId)
            .collection("tasks")
            .get()
            .addOnSuccessListener { result ->
                tasks.clear()
                for (document in result) {
                    val description = document.getString("description") ?: ""
                    val date = document.getDate("date") ?: Date()
                    tasks.add(Task(description, date))
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting tasks: $exception")
            }
    }
}

@Composable
fun DatePicker(date: Date, onDateSelected: (Date) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(date) }

    Text(
        text = "Selected date: ${SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()).format(selectedDate)}",
        modifier = Modifier.clickable { showDialog = true }
    )

    if (showDialog) {
        DateTimePickerDialog(
            initialDate = selectedDate,
            onDateSelected = { newDate ->
                selectedDate = newDate
                onDateSelected(newDate)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}



//todo: in the below code eveything is good but the problem is that in the time and the date pop up the date selection and the scroll system
// is not working properly. so we have to work on that properly.

@Composable
fun DateTimePickerDialog(
    initialDate: Date,
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance().apply { time = initialDate }
    var selectedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var selectedHour by remember { mutableStateOf(calendar.get(Calendar.HOUR)) }
    var selectedMinute by remember { mutableStateOf(calendar.get(Calendar.MINUTE)) }
    var selectedAmPm by remember { mutableStateOf(if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM") }

    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    fun getDateItems(): List<String> {
        val dates = mutableListOf<String>()
        for (month in 0 until 12) {
            for (day in 1..getMaxDaysInMonth(selectedYear, month)) {
                dates.add("${monthNames[month]} $day")
            }
        }
        return dates
    }

    val dateItems = getDateItems()

    val selectedCalendar = Calendar.getInstance().apply {
        set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute)
        set(Calendar.AM_PM, if (selectedAmPm == "AM") Calendar.AM else Calendar.PM)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                SimpleDateFormat("EEEE, MMMM d, yyyy, hh:mm a", Locale.getDefault()).format(selectedCalendar.time),
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ScrollableNumberPicker(
                        items = dateItems,
                        initialValue = "${monthNames[selectedMonth]} $selectedDay",
                        onValueChange = {
                            val parts = it.split(" ")
                            selectedMonth = monthNames.indexOf(parts[0])
                            selectedDay = parts[1].toInt()
                            selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    ScrollableNumberPicker(
                        items = (1..12).toList(),
                        initialValue = selectedHour,
                        onValueChange = {
                            selectedHour = it
                            selectedCalendar.set(Calendar.HOUR, selectedHour)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    ScrollableNumberPicker(
                        items = (0..59).toList(),
                        initialValue = selectedMinute,
                        onValueChange = {
                            selectedMinute = it
                            selectedCalendar.set(Calendar.MINUTE, selectedMinute)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    ScrollableNumberPicker(
                        items = listOf("AM", "PM"),
                        initialValue = selectedAmPm,
                        onValueChange = {
                            selectedAmPm = it
                            selectedCalendar.set(Calendar.AM_PM, if (selectedAmPm == "AM") Calendar.AM else Calendar.PM)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(selectedCalendar.time)
            }) {
                Text("CONFIRM", color = Color.Green)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        }
    )
}

@Composable
fun <T> ScrollableNumberPicker(
    items: List<T>,
    initialValue: T,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = items.indexOf(initialValue))
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        state = listState,
        modifier = modifier.height(150.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(items.size) { index ->
            val isSelected = index == listState.firstVisibleItemIndex + 1
            Text(
                text = items[index].toString(),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable {
                        coroutineScope.launch {
                            listState.scrollToItem(index)
                        }
                        onValueChange(items[index])
                    },
                style = if (isSelected) {
                    MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                } else {
                    MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray
                    )
                }
            )
        }
    }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        onValueChange(items[listState.firstVisibleItemIndex])
    }
}

fun getMaxDaysInMonth(year: Int, month: Int): Int {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, 1)
    return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
}
