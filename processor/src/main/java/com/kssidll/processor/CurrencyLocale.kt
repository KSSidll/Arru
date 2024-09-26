package com.kssidll.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import java.text.NumberFormat
import java.util.Locale

class CurrencyLocaleDataGenerator(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val fileName = "CurrencyLocaleData"
        val packageName = "com.kssidll.compiled"
        val fileContent = """
            package $packageName

            object $fileName {
                val items = listOf(
                    ${generateCachedItems()}
                )
            }
        """.trimIndent()

        try {
            codeGenerator.createNewFile(
                dependencies = Dependencies(false),
                packageName = packageName,
                fileName = fileName,
                extensionName = "kt"
            ).use {  outputStream ->
                outputStream.write(fileContent.toByteArray())
            }
        } catch (e: FileAlreadyExistsException) {
            logger.info("File $fileName.kt already exists. Skipping generation.")
        }

        return emptyList()
    }

    private fun generateCachedItems(): String {
        return NumberFormat.getAvailableLocales().map { it.toLanguageTag() }
            .groupBy { 1.0f.formatToCurrency(Locale.forLanguageTag(it)) }
            .map { (currency, tags) ->
                "Pair(\"$currency\", listOf(${tags.joinToString { "\"$it\"" }}))"
            }
            .joinToString(",\n")
    }

    private fun Float.formatToCurrency(locale: Locale): String {
        return NumberFormat.getCurrencyInstance(locale).format(this)
    }
}
