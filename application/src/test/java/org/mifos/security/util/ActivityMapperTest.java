/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.security.util;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.DateTimeService;
import org.mifos.security.authorization.AuthorizationManager;
import org.mockito.Matchers;

import java.util.regex.Pattern;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ActivityMapperTest extends TestCase {

    Pattern allowableActionName = Pattern.compile("([a-zA-Z])+");

    private AuthorizationManager authorizationManager;
    private UserContext userContext;
    private ActivityMapper activityMapper;

    @Override
    public void setUp(){
        initMocks(this);
        authorizationManager = mock(AuthorizationManager.class);
        userContext = mock(UserContext.class);
        activityMapper = new ActivityMapper(){
            @Override
            AuthorizationManager getAuthorizationManager() {
                return authorizationManager;
            }
        };
    }

    public void testNamesAcceptable() {
        for (ActionSecurity security : ActivityMapper.getInstance().getAllSecurity()) {
            String name = security.getActionName();
           Assert.assertTrue("unacceptable action name " + name, acceptableName(name));
        }
    }

    public void testMachinery() {
       Assert.assertTrue(acceptableName("openSesame"));
        Assert.assertFalse(acceptableName("/bin/sh"));
        Assert.assertFalse(acceptableName("/openSesame"));
        Assert.assertFalse(acceptableName("open,sesame"));
        Assert.assertFalse(acceptableName("open sesame"));
        Assert.assertFalse(acceptableName("openSesame "));
        Assert.assertFalse(acceptableName(""));
        Assert.assertFalse(acceptableName(null));
    }

    public void testAdjustmentPermittedForBackDatedPayments() {
        new DateTimeService().setCurrentDateTime(TestUtils.getDateTime(11,10,2010));
        when(authorizationManager.isActivityAllowed(eq(userContext), Matchers.argThat(new ActivityContextTypeSafeMatcher()))).thenReturn(true);
        assertTrue(activityMapper.isAdjustmentPermittedForBackDatedPayments(TestUtils.getDate(10, 10, 2010),
                userContext, new Short("1"), new Short("1")));
        verify(authorizationManager).isActivityAllowed(eq(userContext), Matchers.argThat(new ActivityContextTypeSafeMatcher()));
    }

    public void testAdjustmentNotPermittedForBackDatedPayments() {
        new DateTimeService().setCurrentDateTime(TestUtils.getDateTime(11,10,2010));
        when(authorizationManager.isActivityAllowed(eq(userContext), Matchers.argThat(new ActivityContextTypeSafeMatcher()))).thenReturn(false);
        assertFalse(activityMapper.isAdjustmentPermittedForBackDatedPayments(TestUtils.getDate(10, 10, 2010),
                userContext, new Short("1"), new Short("1")));
        verify(authorizationManager).isActivityAllowed(eq(userContext), Matchers.argThat(new ActivityContextTypeSafeMatcher()));
    }

    public void testAdjustmentPermittedForSameDayPayments() {
        new DateTimeService().setCurrentDateTime(TestUtils.getDateTime(10, 10, 2010));
        assertTrue(activityMapper.isAdjustmentPermittedForBackDatedPayments(TestUtils.getDate(10, 10, 2010),
                userContext, new Short("1"), new Short("1")));
        verify(authorizationManager, never()).isActivityAllowed(Matchers.<UserContext>anyObject(), Matchers.<ActivityContext>anyObject());
    }

    private boolean acceptableName(String name) {
        return name != null && allowableActionName.matcher(name).matches();
    }

    private static class ActivityContextTypeSafeMatcher extends TypeSafeMatcher<ActivityContext> {
        @Override
        public boolean matchesSafely(ActivityContext activityContext) {
            return activityContext.getActivityId() == SecurityConstants.LOAN_ADJUST_BACK_DATED_TRXNS;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("ActivityID mismatch");
        }
    }
}
