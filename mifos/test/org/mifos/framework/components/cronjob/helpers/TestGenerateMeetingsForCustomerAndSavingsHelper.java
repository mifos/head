package org.mifos.framework.components.cronjob.helpers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.valueobjects.AccountActionDate;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.cronjobs.helpers.GenerateMeetingsForCustomerAndSavingsHelper;
import org.mifos.framework.components.repaymentschedule.MeetingScheduleHelper;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestGenerateMeetingsForCustomerAndSavingsHelper extends MifosTestCase {
	CustomerBO center = null;
	CustomerBO group = null;

	public void testExecute() throws Exception {
			MeetingBO meeting = TestObjectFactory
					.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4,
							2));
			Date startDate = new Date(System.currentTimeMillis());
			
			
			
			
			center = TestObjectFactory.createCenter("center1", Short
					.valueOf("13"), "1.4", meeting, startDate);
	        group=TestObjectFactory.createGroup("Group",Short.valueOf("9"),"1.1.1",center,startDate);
			
	        SavingsTestHelper SavingsTestHelper = new SavingsTestHelper();
	        
	        SavingsOfferingBO savingsOfferingBO=SavingsTestHelper.createSavingsOffering();
	        savingsOfferingBO.getRecommendedAmntUnit().setRecommendedAmntUnitId(Short.valueOf("2"));
	        SavingsBO savingsBO = SavingsTestHelper.createSavingsAccount(savingsOfferingBO,group,Short.valueOf("16"),TestObjectFactory.getUserContext() );
	        TestObjectFactory.updateObject(savingsBO);
	        
	        
	        HibernateUtil.commitTransaction();
	        
	        AccountActionDateEntity lastYearLastInstallment = new AccountPersistanceService().getLastInstallment(savingsBO.getAccountId());
	        
	        Thread.sleep(1000);
			
			new GenerateMeetingsForCustomerAndSavingsHelper().execute(Calendar
					.getInstance().getTimeInMillis());
			TestObjectFactory.updateObject(center);
			
			TestObjectFactory.updateObject(group);
			TestObjectFactory.updateObject(savingsBO);
			TestObjectFactory.flushandCloseSession();
			center = (CustomerBO) HibernateUtil.getSessionTL().get(
					CustomerBO.class, center.getCustomerId());
			
			savingsBO = (SavingsBO) HibernateUtil.getSessionTL().get(
					SavingsBO.class, savingsBO.getAccountId());
			
			MeetingBO meetingBO = center.getCustomerMeeting().getMeeting();
			meetingBO.setMeetingStartDate(DateUtils
					.getFistDayOfNextYear(Calendar.getInstance()));
			List<Date> meetingDates = MeetingScheduleHelper.getSchedulerObject(
					meetingBO).getAllDates();
			Date date = center.getCustomerAccount().getAccountActionDate(
					Short.valueOf("4")).getActionDate();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTime(meetingDates.get(0));
			assertEquals(0, new GregorianCalendar(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE),
					0, 0, 0).compareTo(new GregorianCalendar(calendar2
					.get(Calendar.YEAR), calendar2.get(Calendar.MONTH),
					calendar2.get(Calendar.DATE), 0, 0, 0)));
			
			Integer installmetId = lastYearLastInstallment.getInstallmentId().intValue()+(short)1;
			Date FirstSavingInstallmetDate = savingsBO.getAccountActionDate(Short.valueOf(installmetId.shortValue())).getActionDate();
			Calendar calendar3 = Calendar.getInstance();
			calendar3.setTime(FirstSavingInstallmetDate);
			assertEquals(0, new GregorianCalendar(calendar3.get(Calendar.YEAR),
					calendar3.get(Calendar.MONTH), calendar3.get(Calendar.DATE),
					0, 0, 0).compareTo(new GregorianCalendar(calendar2
					.get(Calendar.YEAR), calendar2.get(Calendar.MONTH),
					calendar2.get(Calendar.DATE), 0, 0, 0)));

		TestObjectFactory.cleanUp(savingsBO);	
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
	}
	private SavingsOfferingBO createSavingsOffering(String offeringName) {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createSavingsOffering(offeringName, Short
				.valueOf("2"), new Date(System.currentTimeMillis()), Short
				.valueOf("2"), 300.0, Short.valueOf("1"), 1.2, 200.0, 200.0,
				Short.valueOf("2"), Short.valueOf("1"), meetingIntCalc,
				meetingIntPost);
	}
}
