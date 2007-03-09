package org.mifos.application.holiday.business;

import java.util.Date;

import org.joda.time.DateMidnight;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
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
	
	/* Do we need an update script like
	   UPDATE HOLIDAY SET HOLIDAY_THRU_DATE = HOLIDAY_FROM_DATE WHERE 
       HOLIDAY_THRU_DATE is NULL;
       ?  Doing this for save and update will fix it for future cases.
	 */
	public void testSaveSuppliesThruDate() throws Exception {
		long startDate = new DateMidnight(2003, 1, 26).getMillis();
		HolidayBO holiday =
			new HolidayBO(
				new HolidayPK((short)1, new Date(startDate)),
				null,
				"Test Day",
				TestObjectFactory.TEST_LOCALE,
				RepaymentRuleTypes.SAME_DAY.getValue(),
				null
			);
		holiday.save();
		assertEquals(startDate, holiday.getHolidayThruDate().getTime());
	}

	public void testUpdateSuppliesThruDate() throws Exception {
		long startDate = new DateMidnight(2003, 1, 26).getMillis();
		HolidayBO holiday =
			new HolidayBO(
				new HolidayPK((short)1, new Date(startDate)),
				null,
				"Test Day",
				TestObjectFactory.TEST_LOCALE,
				RepaymentRuleTypes.SAME_DAY.getValue(),
				null
			);
		holiday.update(holiday.getHolidayPK(), null, "New Name");
		assertEquals(startDate, holiday.getHolidayThruDate().getTime());
	}

	/*public void testUpdateForNullHolidayName() throws Exception {
		accountHoliday = TestObjectFactory.createCustomerHoliday(
				CustomerLevel.CENTER.getValue(), CustomerStatus.CENTER_ACTIVE
						.getValue(), HolidayConstants.STATUS_ACTIVE);
		try {
			holidayEntity.update(customerHoliday.getCustomerLevel(),
					customerHoliday.getCustomerStatus(), null,
					customerHoliday.getHolidayStatus(),
					getHolidayDetails(), (short) 1, (short) 1);
			fail();
		} catch (HolidayException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();
		customerHoliday = (CustomerHolidayBO) TestObjectFactory.getObject(
				CustomerHolidayBO.class, customerHoliday.getHolidayId());
	}

	public void testUpdateForNullHolidayDetails() throws Exception {
		customerHoliday = TestObjectFactory.createCustomerHoliday(
				CustomerLevel.CENTER.getValue(), CustomerStatus.CENTER_ACTIVE
						.getValue(), HolidayConstants.STATUS_ACTIVE);
		try {
			customerHoliday.update(customerHoliday.getCustomerLevel(),
					customerHoliday.getCustomerStatus(), customerHoliday
							.getHolidayName(), customerHoliday
							.getHolidayStatus(), null, (short) 1, (short) 1);
			fail();
		} catch (HolidayException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();
		customerHoliday = (CustomerHolidayBO) TestObjectFactory.getObject(
				CustomerHolidayBO.class, customerHoliday.getHolidayId());
	}

	public void testUpdateForEmptyHolidayDetails() throws Exception {
		customerHoliday = TestObjectFactory.createCustomerHoliday(
				CustomerLevel.CENTER.getValue(), CustomerStatus.CENTER_ACTIVE
						.getValue(), HolidayConstants.STATUS_ACTIVE);
		try {
			customerHoliday.update(customerHoliday.getCustomerLevel(),
					customerHoliday.getCustomerStatus(), customerHoliday
							.getHolidayName(), customerHoliday
							.getHolidayStatus(), new ArrayList<String>(),
					(short) 1, (short) 1);
			fail();
		} catch (HolidayException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();
		customerHoliday = (CustomerHolidayBO) TestObjectFactory.getObject(
				CustomerHolidayBO.class, customerHoliday.getHolidayId());
	}

	public void testUpdateCustomerHoliday() throws Exception {
		customerHoliday = TestObjectFactory.createCustomerHoliday(
				CustomerLevel.CENTER.getValue(), CustomerStatus.CENTER_ACTIVE
						.getValue(), HolidayConstants.STATUS_ACTIVE);
		HibernateUtil.closeSession();
		customerHoliday = (CustomerHolidayBO) TestObjectFactory.getObject(
				CustomerHolidayBO.class, customerHoliday.getHolidayId());

		customerHoliday.update(customerHoliday.getCustomerLevel(),
				customerHoliday.getCustomerStatus(), "Customer Holiday",
				HolidayConstants.STATUS_INACTIVE, getHolidayDetails(),
				(short) 1, (short) 1);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		customerHoliday = (CustomerHolidayBO) TestObjectFactory.getObject(
				CustomerHolidayBO.class, customerHoliday.getHolidayId());

		assertEquals("Customer Holiday", customerHoliday.getHolidayName());
		assertEquals(HolidayConstants.STATUS_INACTIVE, customerHoliday
				.getHolidayStatus());
		assertEquals(3, customerHoliday.getHolidayDetails().size());
		assertEquals(HolidayType.CUSTOMER_CHECKLIST, customerHoliday
				.getHolidayType());
	}

	public void testUpdateAccountHoliday() throws Exception {
		accountHoliday = TestObjectFactory.createAccountHoliday(
				ProductType.LOAN.getValue(),
				AccountState.LOANACC_ACTIVEINGOODSTANDING,
				HolidayConstants.STATUS_ACTIVE);
		HibernateUtil.closeSession();
		accountHoliday = (AccountHolidayBO) TestObjectFactory.getObject(
				AccountHolidayBO.class, accountHoliday.getHolidayId());

		accountHoliday.update(accountHoliday.getProductTypeEntity(),
				accountHoliday.getAccountStateEntity(), "Account Holiday",
				HolidayConstants.STATUS_INACTIVE, getHolidayDetails(),
				(short) 1, (short) 1);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		accountHoliday = (AccountHolidayBO) TestObjectFactory.getObject(
				AccountHolidayBO.class, accountHoliday.getHolidayId());

		assertEquals("Account Holiday", accountHoliday.getHolidayName());
		assertEquals(HolidayConstants.STATUS_INACTIVE, accountHoliday
				.getHolidayStatus());
		assertEquals(3, accountHoliday.getHolidayDetails().size());
		assertEquals(HolidayType.ACCOUNT_CHECKLIST, accountHoliday
				.getHolidayType());
	}

	public void testSaveInValidConnection() throws HolidayException {
		ProductTypeEntity productTypeEntity = (ProductTypeEntity) TestObjectFactory
				.getObject(ProductTypeEntity.class, (short) 2);
		AccountStateEntity accountStateEntity = new AccountStateEntity(
				AccountState.SAVINGS_ACC_PARTIALAPPLICATION);
		AccountHolidayBO accountHoliday = new AccountHolidayBO(productTypeEntity,
				accountStateEntity, "Account Holiday", Short.valueOf("1"),
				getHolidayDetails(), Short.valueOf("1"), (short) 1);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		TestObjectFactory.simulateInvalidConnection();
		try {
			accountHoliday.save();
			fail();
		} catch (HolidayException e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}
	}
	
	public void testCreateHolidayExceptionForCustomer() throws Exception {
		CustomerLevelEntity customerLevelEntity = new CustomerLevelEntity(
				CustomerLevel.CENTER);
		CustomerStatusEntity customerStatusEntity = new CustomerStatusEntity(
				CustomerStatus.CENTER_ACTIVE);
		try {
			customerHoliday = new CustomerHolidayBO(customerLevelEntity,
					customerStatusEntity, null,
					HolidayConstants.STATUS_ACTIVE, getHolidayDetails(),
					(short) 1, (short) 1);
			fail();
		} catch (HolidayException ce) {
			assertTrue(true);
		}
	}
	
	public void testCreateHolidayExceptionForCustomerZeroDetails() throws Exception {
		CustomerLevelEntity customerLevelEntity = new CustomerLevelEntity(
				CustomerLevel.CENTER);
		CustomerStatusEntity customerStatusEntity = new CustomerStatusEntity(
				CustomerStatus.CENTER_ACTIVE);
		try {
			customerHoliday = new CustomerHolidayBO(customerLevelEntity,
					customerStatusEntity, null,
					HolidayConstants.STATUS_ACTIVE, new ArrayList<String>(),
					(short) 1, (short) 1);
			fail();
		} catch (HolidayException ce) {
			assertTrue(true);
		}
	}



	private List<String> getHolidayDetails() {
		List<String> details = new ArrayList();
		details.add("new detail1");
		details.add("new detail2");
		details.add("new detail3");
		return details;
	}*/
}
