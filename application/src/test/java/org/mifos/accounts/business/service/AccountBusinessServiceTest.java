package org.mifos.accounts.business.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountBusinessServiceTest {
    @Mock
    private ActivityMapper activityMapper;
    @Mock
    UserContext userContext;
    private AccountBusinessService accountBusinessService;
    Short recordOfficeId = new Short("1");
    Short recordLoanOfficer = new Short("1");

    @Before
    public void setUp() throws Exception {
        accountBusinessService = new AccountBusinessService() {
            @Override
            ActivityMapper getActivityMapper() {
                return activityMapper;
            }
        };
    }

    @Test
    public void shouldGrantPermissionForDifferentDayAdjustments() {
        Date lastPaymentDate = TestUtils.getDate(10, 10, 2010);
        when(activityMapper.isAdjustmentPermittedForBackDatedPayments(lastPaymentDate, userContext, recordOfficeId, recordLoanOfficer)).thenReturn(true);
        try {
            accountBusinessService.checkPermissionForAdjustmentOnBackDatedPayments(lastPaymentDate, userContext, recordOfficeId, recordLoanOfficer);
        } catch (ServiceException e) {
            Assert.fail("Should not have thrown exception when back dated adjustments are permitted");
        }
        verify(activityMapper, times(1)).isAdjustmentPermittedForBackDatedPayments(lastPaymentDate, userContext, recordOfficeId, recordLoanOfficer);
    }

    @Test
    public void shouldNotGrantPermissionForDifferentDayAdjustmentsIfNotPermitted() {
        Date lastPaymentDate = TestUtils.getDate(10, 10, 2010);
        when(activityMapper.isAdjustmentPermittedForBackDatedPayments(lastPaymentDate, userContext,recordOfficeId, recordLoanOfficer)).thenReturn(false);
        try {
            accountBusinessService.checkPermissionForAdjustmentOnBackDatedPayments(lastPaymentDate, userContext,recordOfficeId, recordLoanOfficer);
            Assert.fail("Should have thrown exception when back dated adjustments are not permitted");
        } catch (ServiceException e) {
            assertThat(e.getKey(), is(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED));
        }
        verify(activityMapper, times(1)).isAdjustmentPermittedForBackDatedPayments(lastPaymentDate, userContext,recordOfficeId, recordLoanOfficer);
    }
}
