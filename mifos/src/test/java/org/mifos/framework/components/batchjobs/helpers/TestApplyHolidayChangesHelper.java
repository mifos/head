package org.mifos.framework.components.batchjobs.helpers;


import java.util.Date;
import java.util.List;

import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.HolidayPK;
import org.mifos.application.holiday.business.RepaymentRuleEntity;
import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;


public class TestApplyHolidayChangesHelper extends MifosTestCase {
	public TestApplyHolidayChangesHelper() throws SystemException, ApplicationException {
        super();
    }

    private HolidayBO holidayEntity;
	private ApplyHolidayChangesHelper applyHolidayChangesHelper;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ApplyHolidayChangesTask applyHolidayChangesTask = new ApplyHolidayChangesTask();
		applyHolidayChangesHelper = (ApplyHolidayChangesHelper)applyHolidayChangesTask.getTaskHelper();
	}
	
	@Override
	public void tearDown() throws Exception {
		applyHolidayChangesHelper = null;
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	public void testExecuteAgainstAppliedHolidays() throws Exception {		
		HolidayPK holidayPK = new HolidayPK((short) 1, new Date());
		RepaymentRuleEntity entity = new HolidayPersistence().getRepaymentRule((short)1);
		holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday", entity);
		holidayEntity.setHolidayChangesAppliedFlag(YesNoFlag.YES.getValue());
		// Disable date Validation because startDate is less than today
		holidayEntity.setValidationEnabled(false);

		holidayEntity.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		////////Meat&Potato//////////
		applyHolidayChangesHelper.execute(System.currentTimeMillis());
		HibernateUtil.closeSession();
		//////////////////
		
		List<HolidayBO> holidays = new HolidayPersistence().getUnAppliedHolidays();
		
		//There should not be any UnappliedHolidays
		assertNotNull(holidays);
		assertEquals(holidays.size(), 0);
		
		TestObjectFactory.cleanUpHolidays(holidays);
		holidayEntity = null;
	}
	
	public void testExecuteAgainst_Un_AppliedHolidays() throws Exception {		
		HolidayPK holidayPK = new HolidayPK((short) 1, new Date());
		RepaymentRuleEntity entity = new HolidayPersistence().getRepaymentRule((short)1);
		holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday", entity);
		// Disable date Validation because startDate is less than today
		holidayEntity.setValidationEnabled(false);

		holidayEntity.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		////////Meat&Potato//////////
		applyHolidayChangesHelper.execute(System.currentTimeMillis());
		HibernateUtil.closeSession();
		//////////////////
		
		List<HolidayBO> holidays = new HolidayPersistence().getUnAppliedHolidays();
		
		//There should not be any UnappliedHolidays
		assertNotNull(holidays);
		assertEquals(holidays.size(), 0);
		
		TestObjectFactory.cleanUpHolidays(holidays);
		holidayEntity = null;
	}
}
