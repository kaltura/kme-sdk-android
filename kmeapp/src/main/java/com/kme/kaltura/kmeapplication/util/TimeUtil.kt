package com.kme.kaltura.kmeapplication.util

import java.util.*

class TimeUtil {

    fun currentDate(): Date {
        val calendar = Calendar.getInstance()
        return calendar.time
    }

    fun toDate(timestampInMillis: Long): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestampInMillis
        return calendar.time
    }

    enum class Template(private val template: String) {
        STRING_MONTH_NAME_DAY_YEAR_TIME("MMM dd, yyyy HH:mm"),
        STRING_DAY_MONTH_YEAR("d MMM, yyyy"),
        STRING_DAY_MONTH("d MMM"),
        TIME("HH:mm a");

        fun get(): String {
            return template
        }
    }

}