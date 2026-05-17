package com.chorechampion.app.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

object WeekCalculator {
    
    /**
     * Gets the start of the current week based on user's configured start day
     */
    fun getWeekStart(weekStartDay: DayOfWeek = DayOfWeek.SUNDAY): Long {
        val today = LocalDate.now()
        val weekStart = today.with(TemporalAdjusters.previousOrSame(weekStartDay))
        return weekStart.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
    
    /**
     * Gets the end of the current week
     */
    fun getWeekEnd(weekStartDay: DayOfWeek = DayOfWeek.SUNDAY): Long {
        val weekStart = LocalDate.ofEpochDay(getWeekStart(weekStartDay) / (24 * 60 * 60 * 1000))
        val weekEnd = weekStart.plusDays(6).atTime(23, 59, 59)
        return weekEnd.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
    
    /**
     * Checks if a timestamp falls within the current week
     */
    fun isCurrentWeek(timestamp: Long, weekStartDay: DayOfWeek = DayOfWeek.SUNDAY): Boolean {
        val weekStart = getWeekStart(weekStartDay)
        val weekEnd = getWeekEnd(weekStartDay)
        return timestamp in weekStart..weekEnd
    }
    
    /**
     * Gets week start for a specific date
     */
    fun getWeekStartFor(date: LocalDate, weekStartDay: DayOfWeek): Long {
        val weekStart = date.with(TemporalAdjusters.previousOrSame(weekStartDay))
        return weekStart.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
