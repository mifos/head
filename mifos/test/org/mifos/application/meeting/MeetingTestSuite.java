package org.mifos.application.meeting;

import junit.framework.Test;

import org.mifos.application.meeting.business.MeetingBOTest;
import org.mifos.framework.MifosTestSuite;

public class MeetingTestSuite extends MifosTestSuite {

	public static Test suite()throws Exception{
		MeetingTestSuite testSuite = new MeetingTestSuite();
		testSuite.addTestSuite(MeetingBOTest.class);
		return testSuite;
	}
}
