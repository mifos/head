package org.mifos.application.bulkentry.struts.uihelpers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import junitx.framework.StringAssert;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class BulkEntryDisplayHelperTest extends MifosTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testBuildTableHeadings() {
		LoanOfferingBO loanOffering = createLoanOfferingBO("Loan Offering",
				"LOAN");
		SavingsOfferingBO savingsOffering = createSavingsOfferingBO(
				"Savings Offering", "SAVP");
		List<PrdOfferingBO> loanProducts = new ArrayList<PrdOfferingBO>();
		List<PrdOfferingBO> savingsProducts = new ArrayList<PrdOfferingBO>();
		loanProducts.add(loanOffering);
		savingsProducts.add(savingsOffering);

		String result = new BulkEntryDisplayHelper().buildTableHeadings(
				loanProducts, savingsProducts).toString();
		StringAssert.assertContains("LOAN", result);
		StringAssert.assertContains("SAVP", result);

		StringAssert.assertContains("Due/Collections", result);
		StringAssert.assertContains("Issues/Withdrawals", result);
		StringAssert.assertContains("Client Name", result);
		StringAssert.assertContains("A/C Collections", result);
		StringAssert.assertContains("Attn", result);

		TestObjectFactory.removeObject(loanOffering);
		TestObjectFactory.removeObject(savingsOffering);
	}

	private LoanOfferingBO createLoanOfferingBO(String prdOfferingName,
			String shortName) {
		MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 1, 2));
		return TestObjectFactory.createLoanOffering(prdOfferingName, shortName,
				Short.valueOf("2"), new Date(System.currentTimeMillis()), Short
						.valueOf("1"), 300.0, 1.2, Short.valueOf("3"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("0"), Short.valueOf("1"), frequency);
	}

	private SavingsOfferingBO createSavingsOfferingBO(String prdOfferingName,
			String shortName) {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createSavingsOffering(prdOfferingName,
				shortName, (short) 1, new Date(System.currentTimeMillis()),
				(short) 2, 300.0, (short) 1, 1.2, 200.0, 200.0, (short) 2,
				(short) 1, meetingIntCalc, meetingIntPost);
	}
}
