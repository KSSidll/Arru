package com.kssidll.arru.ui.screen.modify.productvariant.addproductvariant

import android.util.Log
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.repository.ProductVariantRepositorySource
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.GetProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertProductVariantEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertProductVariantEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.productvariant.ModifyProductVariantViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first

// TODO refactor uiState Event UseCase

@HiltViewModel
class AddProductVariantViewModel
@Inject
constructor(
    override val variantRepository: ProductVariantRepositorySource,
    private val getProductEntityUseCase: GetProductEntityUseCase,
    private val insertProductVariantEntityUseCase: InsertProductVariantEntityUseCase,
) : ModifyProductVariantViewModel() {
    private var mProduct: ProductEntity? = null

    init {
        screenState.isVariantGlobal.value = Field.Loaded(false)
    }

    suspend fun checkExists(id: Long): Boolean {
        mProduct = getProductEntityUseCase(id).first()
        return mProduct != null
    }

    suspend fun addVariant(): Long? {
        return mProduct?.let { product ->
            screenState.attemptedToSubmit.value = true
            val isGlobal = screenState.isVariantGlobal.value.data ?: false

            val result =
                insertProductVariantEntityUseCase(
                    name = screenState.name.value.data,
                    productId = if (isGlobal) null else product.id,
                )

            return@let when (result) {
                is InsertProductVariantEntityUseCaseResult.Error -> {
                    result.errors.forEach {
                        when (it) {
                            InsertProductVariantEntityUseCaseResult.ProductIdInvalid -> {
                                Log.e(
                                    "ModifyProductVariant",
                                    "Insert invalid product `${product.id}`",
                                )
                            }
                            InsertProductVariantEntityUseCaseResult.NameDuplicateValue -> {
                                screenState.name.apply {
                                    value = value.toError(FieldError.DuplicateValueError)
                                }
                            }
                            InsertProductVariantEntityUseCaseResult.NameNoValue -> {
                                screenState.name.apply {
                                    value = value.toError(FieldError.NoValueError)
                                }
                            }
                        }
                    }

                    null
                }
                is InsertProductVariantEntityUseCaseResult.Success -> {
                    result.id
                }
            }
        }
    }
}
