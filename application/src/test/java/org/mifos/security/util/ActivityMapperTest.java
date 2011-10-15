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

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.regex.Pattern;

import junit.framework.Assert;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.DateTimeService;
import org.mifos.security.rolesandpermission.persistence.LegacyRolesPermissionsDao;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ActivityMapperTest {

    Pattern allowableActionName = Pattern.compile("([a-zA-Z])+");

    @Mock
    private LegacyRolesPermissionsDao legacyRolesPermissionsDao;

    private UserContext userContext;
    private ActivityMapper activityMapper;

    @Before
    public void setUp(){
        initMocks(this);
        userContext = mock(UserContext.class);
        legacyRolesPermissionsDao = mock(LegacyRolesPermissionsDao.class);
        activityMapper = ActivityMapper.getInstance();
        activityMapper.setLegacyRolesPermissionDao(legacyRolesPermissionsDao);
    }

    @Test
    public void testNamesAcceptable() {
        for (ActionSecurity security : ActivityMapper.getInstance().getAllSecurity()) {
            String name = security.getActionName();
           Assert.assertTrue("unacceptable action name " + name, acceptableName(name));
        }
    }

    @Test
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

    @Test
    public void testAdjustmentPermittedForBackDatedPayments() {
        new DateTimeService().setCurrentDateTime(TestUtils.getDateTime(11,10,2010));
        when(legacyRolesPermissionsDao.isActivityAllowed(eq(userContext), Matchers.argThat(new ActivityContextTypeSafeMatcher()))).thenReturn(true);
        Assert.assertTrue(activityMapper.isAdjustmentPermittedForBackDatedPayments(TestUtils.getDate(10, 10, 2010),
                userContext, new Short("1"), new Short("1")));
        verify(legacyRolesPermissionsDao).isActivityAllowed(eq(userContext), Matchers.argThat(new ActivityContextTypeSafeMatcher()));
    }

    @Test
    public void testAdjustmentNotPermittedForBackDatedPayments() {
        new DateTimeService().setCurrentDateTime(TestUtils.getDateTime(11,10,2010));
        when(legacyRolesPermissionsDao.isActivityAllowed(eq(userContext), Matchers.argThat(new ActivityContextTypeSafeMatcher()))).thenReturn(false);
        Assert.assertFalse(activityMapper.isAdjustmentPermittedForBackDatedPayments(TestUtils.getDate(10, 10, 2010),
                userContext, new Short("1"), new Short("1")));
        verify(legacyRolesPermissionsDao).isActivityAllowed(eq(userContext), Matchers.argThat(new ActivityContextTypeSafeMatcher()));
    }

    @Test
    public void testAdjustmentPermittedForSameDayPayments() {
        new DateTimeService().setCurrentDateTime(TestUtils.getDateTime(10, 10, 2010));
        Assert.assertTrue(activityMapper.isAdjustmentPermittedForBackDatedPayments(TestUtils.getDate(10, 10, 2010),
                userContext, new Short("1"), new Short("1")));
        verify(legacyRolesPermissionsDao, never()).isActivityAllowed(Matchers.<UserContext>anyObject(), Matchers.<ActivityContext>anyObject());
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
