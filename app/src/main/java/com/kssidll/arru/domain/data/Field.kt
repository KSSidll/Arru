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
sealed class Field<T>(val data: T, val error: FieldError? = null) {
    /** Field status signifying that it's loaded correctly and can display and use [data] */
    class Loaded<T>(data: T) : Field<T>(data)

    /**
     * Field status signifying a loading process taking place, the field shouldn't be user
     * changeable in this status
     */
    class Loading<T>(data: T) : Field<T>(data)

    /**
     * Field status signifying an error
     *
     * @param error [FieldError] error to set the field to
     */
    class Error<T>(error: FieldError, data: T) : Field<T>(data, error)

    /** @return Whether the field is loading */
    fun isLoading(): Boolean {
        return this !is Loading
    }

    /** @return Whether the field is in an error state */
    fun isError(): Boolean {
        return this is Error
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
     * Changes this field status to [Loading]
     *
     * @return This field as [Loading]
     */
    fun toLoading(): Loading<T> {
        return Loading(data)
    }

    /**
     * Changes this field status to [Error]
     *
     * @param error Error to set the field to
     * @return This field as [Error]
     */
    fun toError(error: FieldError): Error<T> {
        return Error(error, data)
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
