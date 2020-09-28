package io.mustelidae.riverotter.domain.calender.api

import java.time.DayOfWeek
import io.mustelidae.riverotter.common.AvailableCountry
import io.mustelidae.riverotter.domain.calender.holiday.Holiday.Type
import io.mustelidae.riverotter.domain.calender.holiday.HolidayCalender
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

class HolidayCalenderResources {

    class Request {
        data class Crawling(
            val year: Int? = null,
            val country: String? = null
        ) {
            fun getYear(): Int = this.year ?: LocalDate.now().plusYears(1).year

            fun getLocale(): Locale? {
                return if (country.isNullOrEmpty().not())
                    AvailableCountry.getLocale(country!!)
                else
                    null
            }
        }
    }

    class Reply {

        data class YearOfCountry(
            val country: String,
            val id: String,
            val year: Int,
        )

        data class Calender(
            val id: String,
            val country: String,
            val year: Int,
            val holidays: List<Holiday>,
            val createdAt: LocalDateTime
        ) {
            companion object {
                fun from(holidayCalender: HolidayCalender): Calender {
                    val holidays = holidayCalender.holidays.map { Holiday.from(it, holidayCalender.locale) }
                    return Calender(
                        holidayCalender.id.toString(),
                        holidayCalender.locale.country,
                        holidayCalender.year,
                        holidays,
                        holidayCalender.createdAt
                    )
                }
            }
        }

        data class Holiday(
            val date: LocalDate,
            val month: Int,
            val day: Int,
            val name: String,
            val type: Type,
            val dayOfWeek: String,
            val description: String? = null
        ) {
            companion object {
                fun from(holiday: io.mustelidae.riverotter.domain.calender.holiday.Holiday, locale: Locale): Holiday {
                    return holiday.run {
                        Holiday(
                            date,
                            date.monthValue,
                            date.dayOfMonth,
                            name,
                            type,
                            date.dayOfWeek.getDisplayName(TextStyle.FULL, locale),
                            description
                        )
                    }
                }
            }
        }
    }
}