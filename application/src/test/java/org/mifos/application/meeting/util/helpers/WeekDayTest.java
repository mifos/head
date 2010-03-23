/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.application.meeting.util.helpers;

import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import static org.mifos.application.meeting.util.helpers.WeekDay.SATURDAY;
import static org.mifos.application.meeting.util.helpers.WeekDay.SUNDAY;
import static org.mifos.application.meeting.util.helpers.WeekDay.TUESDAY;
import static org.mifos.application.meeting.util.helpers.WeekDay.WEDNESDAY;
import junit.framework.Assert;
import junit.framework.TestCase;

import org.testng.annotations.Test;

@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class WeekDayTest extends TestCase {

    public void testNext() throws Exception {
       Assert.assertEquals(MONDAY, SUNDAY.next());
       Assert.assertEquals(WEDNESDAY, TUESDAY.next());
       Assert.assertEquals(SUNDAY, SATURDAY.next());
    }

}
