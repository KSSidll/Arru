package com.kssidll.arrugarq.ui.screen.home.analysis


import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.*

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val itemRepository: ItemRepositorySource
): ViewModel() {
    private val mYear: MutableState<Int> = mutableStateOf(
        Calendar.getInstance()
            .get(Calendar.YEAR)
    )
    val year: Int get() = mYear.value

    private val mMonth: MutableState<Int> = mutableStateOf(
        Calendar.getInstance()
            .get(Calendar.MONTH) + 1
    )
    val month: Int get() = mMonth.value // 1 - 12

    private var mSetCategorySpendingJob: Job? = null
    private val mSetCategorySpending: MutableState<Flow<List<ItemSpentByCategory>>> =
        mutableStateOf(flowOf())
    val setCategorySpending: Flow<List<ItemSpentByCategory>> get() = mSetCategorySpending.value

    private var mCompareCategorySpendingJob: Job? = null
    private val mCompareCategorySpending: MutableState<Flow<List<ItemSpentByCategory>>> =
        mutableStateOf(flowOf())
    val compareCategorySpending: Flow<List<ItemSpentByCategory>> get() = mCompareCategorySpending.value

    private var mSetShopSpendingJob: Job? = null
    private val mSetShopSpending: MutableState<Flow<List<ItemSpentByShop>>> =
        mutableStateOf(flowOf())
    val setShopSpending: Flow<List<ItemSpentByShop>> get() = mSetShopSpending.value

    private var mCompareShopSpendingJob: Job? = null
    private val mCompareShopSpending: MutableState<Flow<List<ItemSpentByShop>>> =
        mutableStateOf(flowOf())
    val compareShopSpending: Flow<List<ItemSpentByShop>> get() = mCompareShopSpending.value

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

        mSetCategorySpendingJob?.cancel()
        mSetCategorySpendingJob = viewModelScope.launch {
            mSetCategorySpending.value = itemRepository.getCategoryTotalSpentFlowByMonth(
                year,
                month,
            )
                .cancellable()
        }

        mCompareCategorySpendingJob?.cancel()
        mCompareCategorySpendingJob = viewModelScope.launch {
            mCompareCategorySpending.value = itemRepository.getCategoryTotalSpentFlowByMonth(
                compareYear,
                compareMonth,
            )
                .cancellable()
        }

        mSetShopSpendingJob?.cancel()
        mSetShopSpendingJob = viewModelScope.launch {
            mSetShopSpending.value = itemRepository.getShopTotalSpentFlowByMonth(
                year,
                month,
            )
                .cancellable()
        }

        mCompareShopSpendingJob?.cancel()
        mCompareShopSpendingJob = viewModelScope.launch {
            mCompareShopSpending.value = itemRepository.getShopTotalSpentFlowByMonth(
                compareYear,
                compareMonth,
            )
                .cancellable()
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
