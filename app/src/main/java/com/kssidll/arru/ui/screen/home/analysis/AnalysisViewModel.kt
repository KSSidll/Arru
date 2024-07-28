package com.kssidll.arru.ui.screen.home.analysis


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(

): ViewModel() {
    private val mYear: MutableState<Int> = mutableIntStateOf(
        Calendar.getInstance()
            .get(Calendar.YEAR)
    )
    val year: Int get() = mYear.value

    private val mMonth: MutableState<Int> = mutableIntStateOf(
        Calendar.getInstance()
            .get(Calendar.MONTH) + 1
    )
    val month: Int get() = mMonth.value // 1 - 12

    init {
        updateData()
    }

    private fun updateData() {
        var compareYear: Int = year
        var compareMonth: Int = month

        if (compareMonth == 1) {
            compareYear -= 1
            compareMonth = 12
        } else {
            compareMonth -= 1
        }

    }

    fun monthIncrement() {
        if (month == 12) {
            mYear.value += 1
            mMonth.value = 1
        } else {
            mMonth.value += 1
        }

        updateData()
    }

    fun monthDecrement() {
        if (month == 1) {
            mYear.value -= 1
            mMonth.value = 12
        } else {
            mMonth.value -= 1
        }

        updateData()
    }

}
