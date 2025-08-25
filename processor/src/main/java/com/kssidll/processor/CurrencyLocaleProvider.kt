package com.kssidll.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class LocaleDataGeneratorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return CurrencyLocaleDataGenerator(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
        )
    }
}
