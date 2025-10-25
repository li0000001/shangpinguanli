package com.expirytracker.utils

import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import java.time.LocalDate
import java.time.ZoneId

object CalendarUtils {
    fun addEventToCalendar(
        context: Context,
        productName: String,
        expiryDate: LocalDate
    ): Long? {
        try {
            val calendarId = getCalendarId(context) ?: return null

            val startMillis = expiryDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endMillis = startMillis + (24 * 60 * 60 * 1000)

            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, startMillis)
                put(CalendarContract.Events.DTEND, endMillis)
                put(CalendarContract.Events.TITLE, "【到期提醒】$productName")
                put(CalendarContract.Events.DESCRIPTION, "商品 $productName 即将到期")
                put(CalendarContract.Events.CALENDAR_ID, calendarId)
                put(CalendarContract.Events.EVENT_TIMEZONE, ZoneId.systemDefault().id)
                put(CalendarContract.Events.HAS_ALARM, 1)
            }

            val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            val eventId = uri?.lastPathSegment?.toLongOrNull()

            eventId?.let {
                addReminderToEvent(context, it)
            }

            return eventId
        } catch (e: SecurityException) {
            e.printStackTrace()
            return null
        }
    }

    private fun addReminderToEvent(context: Context, eventId: Long) {
        val reminderValues = ContentValues().apply {
            put(CalendarContract.Reminders.EVENT_ID, eventId)
            put(CalendarContract.Reminders.MINUTES, 60)
            put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        }

        context.contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues)
    }

    fun deleteEventFromCalendar(context: Context, eventId: Long): Boolean {
        try {
            val deleteUri = CalendarContract.Events.CONTENT_URI
            val selection = "${CalendarContract.Events._ID} = ?"
            val selectionArgs = arrayOf(eventId.toString())
            val deletedRows = context.contentResolver.delete(deleteUri, selection, selectionArgs)
            return deletedRows > 0
        } catch (e: SecurityException) {
            e.printStackTrace()
            return false
        }
    }

    private fun getCalendarId(context: Context): Long? {
        try {
            val projection = arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME
            )

            val cursor = context.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                null,
                null,
                null
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    val idIndex = it.getColumnIndex(CalendarContract.Calendars._ID)
                    if (idIndex >= 0) {
                        return it.getLong(idIndex)
                    }
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
        return null
    }
}
