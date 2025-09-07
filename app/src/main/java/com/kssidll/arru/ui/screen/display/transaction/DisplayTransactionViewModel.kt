package com.kssidll.arru.ui.screen.display.transaction

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.domain.data.data.Transaction
import com.kssidll.arru.domain.usecase.data.GetTransactionEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable data class DisplayTransactionUiState(val transaction: Transaction? = null)

@Immutable
sealed class DisplayTransactionEvent {
    data object NavigateBack : DisplayTransactionEvent()

    data class NavigateDisplayProduct(val productId: Long) : DisplayTransactionEvent()

    data class NavigateDisplayProductCategory(val productCategoryId: Long) :
        DisplayTransactionEvent()

    data class NavigateEditProductCategory(val productCategoryId: Long) : DisplayTransactionEvent()

    data class NavigateDisplayProductProducer(val productProducerId: Long) :
        DisplayTransactionEvent()

    data class NavigateEditProductProducer(val productProducerId: Long) : DisplayTransactionEvent()

    data object NavigateDisplayShop : DisplayTransactionEvent()

    data object NavigateEditShop : DisplayTransactionEvent()

    data object NavigateAddItem : DisplayTransactionEvent()

    data class NavigateEditItem(val itemId: Long) : DisplayTransactionEvent()

    data object NavigateEditTransaction : DisplayTransactionEvent()
}

@HiltViewModel
class DisplayTransactionViewModel
@Inject
constructor(
    private val getTransactionEntityUseCase: GetTransactionEntityUseCase,
    private val getTransactionUseCase: GetTransactionUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DisplayTransactionUiState())
    val uiState = _uiState.asStateFlow()

    private var job: Job? = null

    private var _transactionId: Long? = null

    suspend fun checkExists(id: Long) =
        viewModelScope
            .async {
                return@async getTransactionEntityUseCase(id).first() != null
            }
            .await()

    fun updateState(transactionId: Long) =
        viewModelScope.launch {
            val transaction = getTransactionEntityUseCase(transactionId).first() ?: return@launch
            _transactionId = transaction.id

            job?.cancel()
            job =
                viewModelScope.launch {
                    getTransactionUseCase(transactionId).collectLatest {
                        _uiState.update { currentState -> currentState.copy(transaction = it) }
                    }
                }
        }
}
