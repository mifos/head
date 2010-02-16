package org.mifos.application.holiday.persistence;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.joda.time.DateTime;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class HolidayDaoHibernate implements HolidayDao {

    private final GenericDao genericDao;

    public HolidayDaoHibernate(final GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    @Override
    public List<Holiday> findAllHolidaysThisYearAndNext() {
        
        DateTime today = new DateTime();
        
        List<HolidayBO> holidaysThisYear = findAllHolidaysForYear(today.getYear());
        List<HolidayBO> holidaysNextYear = findAllHolidaysForYear(today.plusYears(1).getYear());
        
        List<Holiday> orderedHolidays = new ArrayList<Holiday>(holidaysThisYear);
        orderedHolidays.addAll(holidaysNextYear);
        
        return orderedHolidays;
    }
    
    @SuppressWarnings("unchecked")
    private List<HolidayBO> findAllHolidaysForYear(final int year) {

        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("en", "GB"));
        isoDateFormat.setLenient(false);
        
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        try {
            queryParameters.put("START_OF_YEAR", isoDateFormat.parse(year + "-01-01"));
            queryParameters.put("END_OF_YEAR", isoDateFormat.parse(year + "-12-31"));
        } catch (ParseException e) {
            throw new MifosRuntimeException(e);
        }

        return (List<HolidayBO>) genericDao.executeNamedQuery(NamedQueryConstants.GET_HOLIDAYS, queryParameters);
    }


    @Override
    public void save(final HolidayBO holiday) {
        // FIXME - keithw - transaction code will move up towards service layer.
        try {
            StaticHibernateUtil.startTransaction();
            genericDao.createOrUpdate(holiday);
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }
}
