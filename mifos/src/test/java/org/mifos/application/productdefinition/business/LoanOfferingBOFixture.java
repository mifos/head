package org.mifos.application.productdefinition.business;

import static org.mifos.application.meeting.util.helpers.MeetingType.LOAN_INSTALLMENT;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.sql.Date;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanOfferingBOFixture {
	
	public static LoanOfferingBO createLoanOfferingBO(String prdOfferingName,
			String shortName) {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeeting(WEEKLY, EVERY_WEEK, LOAN_INSTALLMENT, MONDAY));
		return TestObjectFactory.createLoanOffering(prdOfferingName, shortName,
				ApplicableTo.GROUPS, startDate, PrdStatus.LOAN_ACTIVE, 300.0,
				1.2, 3, InterestType.FLAT, frequency);
	}
}
