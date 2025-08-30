package com.kssidll.arru.domain.data

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kssidll.arru.R
import com.kssidll.arru.ui.theme.Typography

/** Generic abstraction for data and its status */
@Immutable
sealed class Field<T>(val data: T? = null, val error: FieldError? = null) {
    /** Field status signifying that it's loaded correctly and can display and use [data] */
    class Loaded<T>(data: T? = null) : Field<T>(data)

    /**
     * Field status signifying a loading process taking place, the field shouldn't be user
     * changeable in this status
     */
    class Loading<T>(data: T? = null) : Field<T>(data)

    /**
     * Field status signifying an error
     *
     * @param error [FieldError] error to set the field to
     */
    class Error<T>(error: FieldError? = null, data: T? = null) : Field<T>(data, error)

    /** @return Whether the field should be user changeable */
    fun isEnabled(): Boolean {
        return this !is Loading
    }

    /** @return Whether the field is in an error state */
    fun isError(): Boolean {
        return this is Error
    }

    /** @return Negation of [isError] */
    fun isNotError(): Boolean {
        return isError().not()
    }

    /**
     * Tries to change this field status to [Loaded]
     *
     * @return This field as [Loaded] status
     */
    fun toLoaded(): Field<T> {
        return Loaded(data)
    }

    /**
     * Tries to change this field status to [Loaded]
     *
     * @return This field as [Loaded] status or as [FieldError.NoValueError] [Error] if the data is
     *   null
     */
    fun toLoadedOrError(): Field<T> {
        return this.data?.let { Loaded(it) } ?: Error(FieldError.NoValueError)
    }

    /**
     * Changes this field status to [Loading]
     *
     * @return This field as [Loading]
     */
    fun toLoading(): Loading<T> {
        return Loading(this.data)
    }

    /**
     * Changes this field status to [Error]
     *
     * @param error Error to set the field to
     * @return This field as [Error]
     */
    fun toError(error: FieldError? = null): Error<T> {
        return Error(error, this.data)
    }
}

/** Possible [Field] errors */
@Immutable
sealed class FieldError {
    @Composable
    fun ErrorText(modifier: Modifier = Modifier) {
        Text(text = errorString(), style = Typography.bodySmall, modifier = modifier)
    }

    @Composable abstract fun errorString(): String

    /** Error signifying lack of value in the field */
    data object NoValueError : FieldError() {
        @Composable
        override fun errorString(): String {
            return stringResource(id = R.string.no_value_error_text)
        }
    }

    /** Error signifying correct, but duplicate value that can't be used */
    data object DuplicateValueError : FieldError() {
        @Composable
        override fun errorString(): String {
            return stringResource(id = R.string.duplicate_value_error_text)
        }
    }

    /** Error signifying incorrect value */
    data object InvalidValueError : FieldError() {
        @Composable
        override fun errorString(): String {
            return stringResource(id = R.string.invalid_value_error_text)
        }
    }
}
