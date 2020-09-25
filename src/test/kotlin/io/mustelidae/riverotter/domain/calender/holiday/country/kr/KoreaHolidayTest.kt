package io.mustelidae.riverotter.domain.calender.holiday.country.kr

import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mustelidae.riverotter.config.AppEnvironment
import io.mustelidae.riverotter.domain.calender.holiday.HolidayCalender
import io.mustelidae.riverotter.domain.calender.holiday.aFixture
import io.mustelidae.riverotter.domain.calender.holiday.country.KoreaHoliday
import io.mustelidae.riverotter.domain.calender.holiday.repository.HolidayCalenderRepository
import io.mustelidae.riverotter.domain.client.korea.government.GovernmentOpenDummyClient
import org.junit.jupiter.api.Test
import java.time.DayOfWeek

internal class KoreaHolidayTest {

    private val holidayCalenderRepository: HolidayCalenderRepository = mockk()
    private val governmentOpenClient = GovernmentOpenDummyClient()
    private val appEnv = AppEnvironment().apply {
        saturdayIsHoliday = true
    }
    private val koreaHoliday = KoreaHoliday(appEnv, governmentOpenClient, holidayCalenderRepository)

    @Test
    fun getWeekend() {
        // Given
        val year = 2020
        // When

        val holidays = koreaHoliday.getWeekend(year, true)

        // Then
        holidays.size shouldBe 104
        holidays.filter { it.date.dayOfWeek == DayOfWeek.SUNDAY }.size shouldBe 52
    }

    @Test
    fun create() {
        // Given
        val year = 2020

        val slot = slot<HolidayCalender>()
        every { holidayCalenderRepository.findByYearAndCountry(year, any()) } returns null
        every { holidayCalenderRepository.save(capture(slot)) } answers { HolidayCalender.Companion.aFixture(year) }

        // When
        koreaHoliday.create(year)

        // Then
        val savedCalender = slot.captured
        savedCalender.country shouldBe koreaHoliday.localeOfCountry.country
        savedCalender.holidays.size shouldBe 116
    }
}
