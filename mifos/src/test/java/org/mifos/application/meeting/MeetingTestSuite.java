/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
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
