package com.kssidll.arru.ui.screen.home.analysis


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ItemSpentByCategory
import com.kssidll.arru.data.data.TransactionTotalSpentByShop
import com.kssidll.arru.data.repository.CategoryRepositorySource
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.domain.data.Data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val categoryRepository: CategoryRepositorySource,
    private val shopRepository: ShopRepositorySource,
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

    private var mSetCategorySpendingJob: Job? = null
    private val mSetCategorySpending: MutableState<Flow<Data<List<ItemSpentByCategory>>>> =
        mutableStateOf(flowOf())
    val setCategorySpending: Flow<Data<List<ItemSpentByCategory>>> get() = mSetCategorySpending.value

    private var mCompareCategorySpendingJob: Job? = null
    private val mCompareCategorySpending: MutableState<Flow<Data<List<ItemSpentByCategory>>>> =
        mutableStateOf(flowOf())
    val compareCategorySpending: Flow<Data<List<ItemSpentByCategory>>> get() = mCompareCategorySpending.value

    private var mSetShopSpendingJob: Job? = null
    private val mSetShopSpending: MutableState<Flow<Data<List<TransactionTotalSpentByShop>>>> =
        mutableStateOf(flowOf())
    val setShopSpending: Flow<Data<List<TransactionTotalSpentByShop>>> get() = mSetShopSpending.value

    private var mCompareShopSpendingJob: Job? = null
    private val mCompareShopSpending: MutableState<Flow<Data<List<TransactionTotalSpentByShop>>>> =
        mutableStateOf(flowOf())
    val compareShopSpending: Flow<Data<List<TransactionTotalSpentByShop>>> get() = mCompareShopSpending.value

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
            mSetCategorySpending.value = categoryRepository.totalSpentByCategoryByMonthFlow(
                year,
                month
            )
        }

        mCompareCategorySpendingJob?.cancel()
        mCompareCategorySpendingJob = viewModelScope.launch {
            mCompareCategorySpending.value = categoryRepository.totalSpentByCategoryByMonthFlow(
                compareYear,
                compareMonth
            )
        }

        mSetShopSpendingJob?.cancel()
        mSetShopSpendingJob = viewModelScope.launch {
            mSetShopSpending.value = shopRepository.totalSpentByShopByMonthFlow(
                year,
                month
            )
        }

        mCompareShopSpendingJob?.cancel()
        mCompareShopSpendingJob = viewModelScope.launch {
            mCompareShopSpending.value = shopRepository.totalSpentByShopByMonthFlow(
                compareYear,
                compareMonth
            )
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
