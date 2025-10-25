package com.expirytracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val productionDate: LocalDate,
    val shelfLifeDays: Int,
    val expiryDate: LocalDate,
    val calendarEventId: Long? = null
) {
    fun isExpired(): Boolean {
        return LocalDate.now().isAfter(expiryDate)
    }

    fun daysUntilExpiry(): Long {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expiryDate)
    }
}
