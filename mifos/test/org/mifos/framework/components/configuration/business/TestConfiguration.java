package org.mifos.framework.components.configuration.business;

import java.util.List;

import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.config.ClientRules;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

/**
 * Most of this class is a remnant of per-office configuration, which <a
 * href="http://article.gmane.org/gmane.comp.finance.mifos.devel/3498">is
 * deprecated and may be removed</a> (-Adam 22-JAN-2008).
 */
public class TestConfiguration extends MifosTestCase{
	private Configuration configuration ;
	
	@Override
	protected void setUp() throws Exception {
		configuration = Configuration.getInstance();
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		HibernateUtil.closeSession();
	}
	
	public void testSystemConfiguration()throws Exception{
		SystemConfiguration systemConfig = configuration.getSystemConfig();
		assertNotNull(systemConfig);
		assertNotNull(systemConfig.getCurrency());
		assertNotNull(systemConfig.getMifosTimeZone());
		assertNotNull(systemConfig.getSessionTimeOut());
		assertEquals(Short.valueOf("1"),systemConfig.getMFILocaleId());
	}
	
	public void testHeadOfficeConfiguration()throws Exception{
		OfficeBO headOffice = new OfficePersistence().getHeadOffice();
		OfficeConfig officeConfig = configuration.getOfficeConfig(headOffice.getOfficeId());
		assertForCustomerConfig(officeConfig.getCustomerConfig());
		assertForAccountConfig(officeConfig.getAccountConfig());
		assertForMeetingConfig(officeConfig.getMeetingConfig());
	}

	public void testAreaOfficeConfiguration()throws Exception{
		OfficeBO areaOffice = new OfficePersistence().getOffice(
			TestObjectFactory.SAMPLE_AREA_OFFICE);
		OfficeConfig officeConfig = configuration.getOfficeConfig(
			areaOffice.getOfficeId());
		assertForCustomerConfig(officeConfig.getCustomerConfig());
		assertForAccountConfig(officeConfig.getAccountConfig());
		assertForMeetingConfig(officeConfig.getMeetingConfig());
	}

	public void testBranchOfficeConfiguration()throws Exception{
		OfficeBO branchOffice = new OfficePersistence().getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
		OfficeConfig officeConfig = configuration.getOfficeConfig(branchOffice.getOfficeId());
		assertForCustomerConfig(officeConfig.getCustomerConfig());
		assertForAccountConfig(officeConfig.getAccountConfig());
		assertForMeetingConfig(officeConfig.getMeetingConfig());
	}

	private void assertForCustomerConfig(CustomerConfig customerConfig) throws Exception {
		assertEquals(true, ClientRules.getCenterHierarchyExists().booleanValue());
		assertEquals(true,ClientRules.getClientCanExistOutsideGroup().booleanValue());
		assertEquals(true,ClientRules.getGroupCanApplyLoans().booleanValue());
	}
	
	private void assertForAccountConfig(AccountConfig accountConfig){
		assertEquals(Short.valueOf("10"),accountConfig.getLatenessDays());
		assertEquals(Short.valueOf("30"),accountConfig.getDormancyDays());
		assertEquals(true,accountConfig.isBackDatedTxnAllowed());
	}

	private void assertForMeetingConfig(MeetingConfig meetingConfig){
		assertEquals(Short.valueOf("2"),meetingConfig.getFiscalWeekStartDay());
		assertEquals("same_day",meetingConfig.getSchTypeForMeetingOnHoliday());
		assertEquals(Short.valueOf("30"),meetingConfig.getDaysForCalDefinition());
		List<Short> weekOffs = meetingConfig.getWeekOffDays();
		assertEquals(weekOffs.size(), 0);

		
	}
}
