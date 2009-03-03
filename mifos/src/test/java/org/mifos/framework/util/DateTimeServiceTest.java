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

package org.mifos.framework.util;


import junit.framework.Assert;
import junit.framework.JUnit4TestAdapter;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DateTimeServiceTest {
    
    private static final int ONE_SECOND = 1000;

    @Before
    public void setUp() {
        new DateTimeService().resetToCurrentSystemDateTime();
    }
    
    @After
    public void tearDown() {
        new DateTimeService().resetToCurrentSystemDateTime();
    }
    

    @Test
    public void testGetCurrentDateTime() {
        DateTimeService dateTimeService = new DateTimeService();
        DateTime systemDateTime = new DateTime(System.currentTimeMillis());
        DateTime timeServiceDateTime = new DateTime(dateTimeService.getCurrentJavaDateTime()); 
        Duration duration = new Duration(systemDateTime,timeServiceDateTime);
        // the dates returned should be equal or very close
        Assert.assertTrue(duration.getMillis() < ONE_SECOND);
    }

    @Test 
    public void testSetCurrentTime() throws InterruptedException {
        DateTimeService dateTimeService = new DateTimeService();
        DateTime someDateTime = new DateTime(2008,1,1,12,0,0,0);
        dateTimeService.setCurrentDateTime(someDateTime);
        // The date that comes back should be as set
        Assert.assertEquals(someDateTime.toLocalDate(), dateTimeService.getCurrentDateTime().toLocalDate());
        Thread.sleep(1);
        // Some time should have passed since setting the time on the dateTimeService
        Assert.assertTrue(dateTimeService.getCurrentDateTime().getMillis() > someDateTime.getMillis());
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(DateTimeServiceTest.class);
    }

}
