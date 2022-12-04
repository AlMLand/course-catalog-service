package com.AlMLand.dto.enums.converter

import com.AlMLand.dto.enums.Category
import com.AlMLand.dto.enums.Category.*
import javax.persistence.AttributeConverter
import javax.persistence.Converter

private const val D = "D"
private const val Q = "Q"
private const val M = "M"

@Converter(autoApply = true)
class CategoryConverter : AttributeConverter<Category, String> {
    override fun convertToDatabaseColumn(entityAttribute: Category?): String =
        when (entityAttribute) {
            DEVELOPMENT -> D
            QA -> Q
            MANAGEMENT -> M
            else -> throw IllegalArgumentException("The entity attribute $entityAttribute is not available")
        }

    override fun convertToEntityAttribute(dbDataAttribute: String?): Category =
        when (dbDataAttribute) {
            D -> DEVELOPMENT
            Q -> QA
            M -> MANAGEMENT
            else -> throw IllegalArgumentException("The database attribute $dbDataAttribute is not available")
        }
}