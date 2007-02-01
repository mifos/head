package org.mifos.application.holiday.business;

import java.util.Date;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestHolidayBO extends MifosTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testAddHoliday() throws Exception {
		HolidayPK holidayPK = new HolidayPK((short)1, new Date());
		HolidayBO holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday",
				(short) 1, (short) 1, "Same Day");// the last string has no effect.
		
		holidayEntity.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		holidayEntity = (HolidayBO) TestObjectFactory.getObject(
				HolidayBO.class, holidayEntity.getHolidayPK());

		assertEquals("Test Holiday", holidayEntity.getHolidayName());
		
		TestObjectFactory.cleanUp(holidayEntity);
	}
}
