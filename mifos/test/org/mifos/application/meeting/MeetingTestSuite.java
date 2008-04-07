package org.mifos.application.meeting;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.meeting.business.MeetingBOTest;
import org.mifos.application.meeting.business.service.MeetingBusinessServiceTest;
import org.mifos.application.meeting.persistence.MeetingPersistenceTest;
import org.mifos.application.meeting.struts.action.MeetingActionTest;
import org.mifos.application.meeting.util.helpers.MeetingHelperTest;

public class MeetingTestSuite extends TestSuite {

	public static Test suite()throws Exception{
		MeetingTestSuite testSuite = new MeetingTestSuite();
		testSuite.addTestSuite(MeetingBOTest.class);
		testSuite.addTestSuite(MeetingPersistenceTest.class);
		testSuite.addTestSuite(MeetingBusinessServiceTest.class);
		testSuite.addTestSuite(MeetingActionTest.class);
		testSuite.addTestSuite(MeetingHelperTest.class);
		return testSuite;
	}
}
