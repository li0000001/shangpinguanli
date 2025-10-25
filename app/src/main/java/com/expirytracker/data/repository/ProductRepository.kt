package com.expirytracker.data.repository

import android.content.Context
import com.expirytracker.data.database.ProductDao
import com.expirytracker.data.model.Product
import com.expirytracker.utils.CalendarUtils
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val productDao: ProductDao,
    private val context: Context
) {
    fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()

    suspend fun getProductById(id: Long): Product? = productDao.getProductById(id)

    suspend fun insertProduct(product: Product): Long {
        val eventId = CalendarUtils.addEventToCalendar(
            context,
            product.name,
            product.expiryDate
        )
        
        val productWithEventId = if (eventId != null) {
            product.copy(calendarEventId = eventId)
        } else {
            product
        }
        
        return productDao.insertProduct(productWithEventId)
    }

    suspend fun updateProduct(product: Product) {
        if (product.calendarEventId != null) {
            CalendarUtils.deleteEventFromCalendar(context, product.calendarEventId)
        }
        
        val eventId = CalendarUtils.addEventToCalendar(
            context,
            product.name,
            product.expiryDate
        )
        
        val updatedProduct = product.copy(calendarEventId = eventId)
        productDao.updateProduct(updatedProduct)
    }

    suspend fun deleteProduct(product: Product) {
        if (product.calendarEventId != null) {
            CalendarUtils.deleteEventFromCalendar(context, product.calendarEventId)
        }
        productDao.deleteProduct(product)
    }
}
