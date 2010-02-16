package org.mifos.application.holiday.persistence;

import java.util.List;

import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.business.HolidayBO;

public interface HolidayDao {

    List<Holiday> findAllHolidaysThisYearAndNext();

    void save(HolidayBO holiday);

}
