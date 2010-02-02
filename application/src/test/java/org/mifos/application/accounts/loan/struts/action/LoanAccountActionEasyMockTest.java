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

package org.mifos.application.accounts.loan.struts.action;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.configuration.business.service.ConfigurationBusinessService;
import org.mifos.application.customer.client.business.service.ClientBusinessService;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeFrequencyEntity;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.business.service.FeeBusinessService;
import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingDetailsEntity;
import org.mifos.application.meeting.business.RecurrenceTypeEntity;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdOfferingMeetingEntity;
import org.mifos.application.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.application.productdefinition.business.service.LoanProductService;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;

/*
 * This is a stand alone unit test class which uses Easymock to 
 * test a methods on LoanAccountAction without hitting the database.
 * There is a related integration test class LoanAccountActionEasyMockTest
 * which uses the "old style" of testing which hits the database.
 */
public class LoanAccountActionEasyMockTest extends TestCase {

    static Short shortOne = Short.valueOf((short) 1);

    public void testGetDefaultAndAdditionalFees() throws Exception {
        ConfigurationBusinessService mockConfigurationBusinessService = createMock(ConfigurationBusinessService.class);
        LoanBusinessService mockLoanBusinessService = createMock(LoanBusinessService.class);
        GlimLoanUpdater mockGlimLoanUpdater = createMock(GlimLoanUpdater.class);
        FeeBusinessService mockFeeBusinessService = createMock(FeeBusinessService.class);
        LoanPrdBusinessService mockLoanPrdBusinessService = createMock(LoanPrdBusinessService.class);
        ClientBusinessService mockClientBusinessService = createMock(ClientBusinessService.class);
        MasterDataService mockMasterDataService = createMock(MasterDataService.class);
        ConfigurationPersistence mockConfigurationPersistence = createMock(ConfigurationPersistence.class);
        AccountBusinessService mockAccountBusinessService = createMock(AccountBusinessService.class);

        LoanOfferingBO mockLoanOfferingBO = createMock(LoanOfferingBO.class);
        expect(mockLoanPrdBusinessService.getLoanOffering(shortOne)).andReturn(mockLoanOfferingBO);

        LoanAccountAction loanAccountAction = new LoanAccountAction(mockConfigurationBusinessService,
                mockLoanBusinessService, mockGlimLoanUpdater, mockFeeBusinessService, mockLoanPrdBusinessService,
                mockClientBusinessService, mockMasterDataService, mockConfigurationPersistence,
                new LoanProductService(mockLoanPrdBusinessService, mockFeeBusinessService), mockAccountBusinessService);

        List<FeeView> defaultFees = new ArrayList<FeeView>();
        List<FeeView> additionalFees = new ArrayList<FeeView>();

        MeetingBO mockMeetingBO = createMockMeeting();

        PrdOfferingMeetingEntity mockPrdOfferingMeetingEntity = createMock(PrdOfferingMeetingEntity.class);
        expect(mockPrdOfferingMeetingEntity.getMeeting()).andReturn(mockMeetingBO).anyTimes();

        AmountFeeBO mockUpfrontFee = createMockAmountFeeBO((short) 1, "Fee1", "10", false, null);
        AmountFeeBO mockPeriodicMatchingFee = createMockAmountFeeBO((short) 2, "Fee2", "10", true, mockMeetingBO);
        AmountFeeBO mockPeriodicNonMatchingFee = createMockAmountFeeBO((short) 3, "Fee3", "10", true, mockMeetingBO);

        List<FeeBO> fees = new ArrayList<FeeBO>();
        fees.add(mockUpfrontFee);
        fees.add(mockPeriodicMatchingFee);
        fees.add(mockPeriodicNonMatchingFee);

        expect(mockLoanOfferingBO.isFeePresent(mockUpfrontFee)).andReturn(true);
        expect(mockLoanOfferingBO.isFeePresent(mockPeriodicMatchingFee)).andReturn(true);
        expect(mockLoanOfferingBO.isFeePresent(mockPeriodicNonMatchingFee)).andReturn(false);
        expect(mockLoanOfferingBO.getLoanOfferingMeeting()).andReturn(mockPrdOfferingMeetingEntity).anyTimes();

        expect(mockFeeBusinessService.getAllApplicableFeesForLoanCreation()).andReturn(fees);
        replay(mockFeeBusinessService, mockUpfrontFee, mockPeriodicMatchingFee, mockPeriodicNonMatchingFee,
                mockLoanPrdBusinessService, mockLoanOfferingBO, mockPrdOfferingMeetingEntity);

        UserContext mockUserContext = createUserContext();

        loanAccountAction.getDefaultAndAdditionalFees((short) 1, mockUserContext, defaultFees, additionalFees);
        Assert.assertEquals(2, defaultFees.size());
        Assert.assertEquals(defaultFees.get(0).getFeeIdValue(), mockUpfrontFee.getFeeId());
        Assert.assertEquals(defaultFees.get(1).getFeeIdValue(), mockPeriodicMatchingFee.getFeeId());
        Assert.assertEquals(1, additionalFees.size());
        Assert.assertEquals(additionalFees.get(0).getFeeIdValue(), mockPeriodicNonMatchingFee.getFeeId());
    }

    private UserContext createUserContext() {
        UserContext mockUserContext = createMock(UserContext.class);
        Locale local = new Locale("EN");
        expect(mockUserContext.getLocaleId()).andReturn(shortOne).anyTimes();
        expect(mockUserContext.getPreferredLocale()).andReturn(local).anyTimes();
        replay(mockUserContext);

        return mockUserContext;
    }

    private AmountFeeBO createMockAmountFeeBO(final Short feeId, final String feeName, final String feeAmountString, final boolean isPeriodic,
            final MeetingBO meeting) {
        Money feeAmount = new Money(new MifosCurrency((short) 1, "INR", Float.valueOf(1), (short) 1, "INR"), feeAmountString);

        AmountFeeBO mockFee = createMock(AmountFeeBO.class);
        expect(mockFee.getFeeId()).andReturn((short) 3).anyTimes();
        expect(mockFee.getFeeName()).andReturn("Fee3");
        expect(mockFee.getFeeType()).andReturn(RateAmountFlag.AMOUNT).anyTimes();
        expect(mockFee.getFeeAmount()).andReturn(feeAmount).anyTimes();
        expect(mockFee.isPeriodic()).andReturn(isPeriodic).anyTimes();
        if (isPeriodic) {
            FeeFrequencyEntity mockFeeFrequencyEntity = createMock(FeeFrequencyEntity.class);
            expect(mockFeeFrequencyEntity.getFeeMeetingFrequency()).andReturn(meeting).anyTimes();
            expect(mockFee.getFeeFrequency()).andReturn(mockFeeFrequencyEntity).anyTimes();
            replay(mockFeeFrequencyEntity);
        }

        return mockFee;
    }

    private MeetingBO createMockMeeting() {
        MeetingBO mockMeetingBO = createMock(MeetingBO.class);
        MeetingDetailsEntity mockMeetingDetailsEntity = createMock(MeetingDetailsEntity.class);
        RecurrenceTypeEntity mockRecurrenceTypeEntity = createMock(RecurrenceTypeEntity.class);

        expect(mockMeetingBO.getMeetingDetails()).andReturn(mockMeetingDetailsEntity).anyTimes();
        expect(mockMeetingBO.isWeekly()).andReturn(true).anyTimes();
        expect(mockMeetingBO.isMonthly()).andReturn(false).anyTimes();
        expect(mockRecurrenceTypeEntity.getRecurrenceId()).andReturn(shortOne).anyTimes();

        expect(mockMeetingDetailsEntity.getRecurAfter()).andReturn(shortOne).anyTimes();
        expect(mockMeetingDetailsEntity.getRecurrenceType()).andReturn(mockRecurrenceTypeEntity).anyTimes();

        replay(mockMeetingBO, mockMeetingDetailsEntity, mockRecurrenceTypeEntity);

        return mockMeetingBO;
    }
}
