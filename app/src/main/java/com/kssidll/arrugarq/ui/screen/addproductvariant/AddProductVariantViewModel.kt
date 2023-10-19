package com.kssidll.arrugarq.ui.screen.addproductvariant

import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.shared.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddProductVariantViewModel @Inject constructor(
    private val productVariantRepository: IProductVariantRepository,
): ViewModel() {
    internal val screenState: EditProductVariantScreenState =
        EditProductVariantScreenState()

    /**
     * Tries to add a product variant to the repository
     * @param productId: Id of the product that the variant is being created for
     * @return Id of newly inserted row, null if operation failed
     */
    suspend fun addVariant(productId: Long): Long? = viewModelScope.async {
        screenState.attemptedToSubmit.value = true
        val variant =
            screenState.extractProducerOrNull(productId) ?: return@async null

        return@async productVariantRepository.insert(variant)
    }
        .await()
}