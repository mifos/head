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

package org.mifos.application.collectionsheet.struts.uihelpers;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.MeetingType.LOAN_INSTALLMENT;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;
import junitx.framework.StringAssert;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.util.helpers.LoanAccountView;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryGridDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerView;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.util.helpers.CustomerAccountView;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.office.business.OfficeView;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelView;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.servicefacade.ListItem;
import org.mifos.application.servicefacade.ProductDto;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class BulkEntryDisplayHelperIntegrationTest extends MifosIntegrationTestCase {

    public BulkEntryDisplayHelperIntegrationTest() throws Exception {
        super();
    }

    private CustomerBO center;

    private CustomerBO group;

    private ClientBO client;

    private AccountBO account;

    private LoanBO groupAccount;

    private LoanBO clientAccount;

    private SavingsBO centerSavingsAccount;

    private SavingsBO groupSavingsAccount;

    private SavingsBO clientSavingsAccount;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        TestObjectFactory.cleanUp(centerSavingsAccount);
        TestObjectFactory.cleanUp(groupSavingsAccount);
        TestObjectFactory.cleanUp(clientSavingsAccount);
        TestObjectFactory.cleanUp(groupAccount);
        TestObjectFactory.cleanUp(clientAccount);
        TestObjectFactory.cleanUp(account);
        TestObjectFactory.cleanUp(client);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testBuildTableHeadings() {
        LoanOfferingBO loanOffering = createLoanOfferingBO("Loan Offering", "LOAN");
        java.util.Date currentDate = new java.util.Date(System.currentTimeMillis());
        SavingsOfferingBO savingsOffering = TestObjectFactory.createSavingsProduct("Savings Offering", "SAVP",
                currentDate, RecommendedAmountUnit.COMPLETE_GROUP);

        ProductDto loanOfferingDto = new ProductDto(loanOffering.getPrdOfferingId(), loanOffering
                .getPrdOfferingShortName());
        ProductDto savingOfferingDto = new ProductDto(savingsOffering.getPrdOfferingId(), savingsOffering
                .getPrdOfferingShortName());

        List<ProductDto> loanProducts = Arrays.asList(loanOfferingDto);
        List<ProductDto> savingsProducts = Arrays.asList(savingOfferingDto);

        UserContext userContext = TestObjectFactory.getContext();
        String result = new BulkEntryDisplayHelper().buildTableHeadings(loanProducts, savingsProducts,
                userContext.getPreferredLocale()).toString();
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

    public void testGetEndTable() {
        String result = new BulkEntryDisplayHelper().getEndTable(3).toString();
        StringAssert.assertContains("</table>", result);
        StringAssert.assertContains("</tr>", result);
        StringAssert.assertContains("<tr>", result);
        StringAssert.assertContains("<td", result);
        StringAssert.assertContains("</td", result);
    }

    public void testBuildForCenterForGetMethod() throws Exception {
        CollectionSheetEntryGridDto bulkEntry = createBulkEntry();
        StringBuilder builder = new StringBuilder();

        // Assert that the extracted attendance types are the ones expected
        final String[] EXPECTED_ATTENDANCE_TYPES = { "P", "A", "AA", "L" };
        List<CustomValueListElement> attendanceTypesCustomValueList = new MasterPersistence().getCustomValueList(
                MasterConstants.ATTENDENCETYPES,
                "org.mifos.application.master.business.CustomerAttendanceType", "attendanceId")
                .getCustomValueListElements();

        List<String> attendanceTypesLookupValueList = new ArrayList<String>();
        for (CustomValueListElement attendanceTypeCustomValueListElement : attendanceTypesCustomValueList) {
            attendanceTypesLookupValueList.add(attendanceTypeCustomValueListElement.getLookUpValue());
        }
        Assert.assertEquals(Arrays.asList(EXPECTED_ATTENDANCE_TYPES), attendanceTypesLookupValueList);

        new BulkEntryDisplayHelper().buildForCenter(bulkEntry.getBulkEntryParent(), bulkEntry.getLoanProducts(),
                bulkEntry.getSavingProducts(), attendanceTypesCustomValueList, builder, Methods.get.toString(),
                TestObjectFactory.getContext());
        String result = builder.toString();

        StringAssert.assertContains("Group", result);
        StringAssert.assertContains("Client", result);
        StringAssert.assertNotContains("<option value= \"\"></option>)", result);
        StringAssert.assertContains(groupAccount.getLoanBalance().toString(), result);
        StringAssert.assertContains(clientAccount.getLoanBalance().toString(), result);
        StringAssert.assertContains("0.0", result);

        StringAssert.assertContains("enteredAmount", result);
        StringAssert.assertContains("depositAmountEntered", result);
        StringAssert.assertContains("withDrawalAmountEntered", result);
    }

    private LoanOfferingBO createLoanOfferingBO(final String prdOfferingName, final String shortName) {
        Date startDate = new Date(System.currentTimeMillis());

        MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeeting(WEEKLY, EVERY_WEEK,
                LOAN_INSTALLMENT, MONDAY));
        return TestObjectFactory.createLoanOffering(prdOfferingName, shortName, ApplicableTo.GROUPS, startDate,
                PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, InterestType.FLAT, frequency);
    }

    private CollectionSheetEntryGridDto createBulkEntry() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        Date startDate = new Date(System.currentTimeMillis());
        center = TestObjectFactory.createWeeklyFeeCenter(this.getClass().getSimpleName() + " Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(this.getClass().getSimpleName() + " Group",
                CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient(this.getClass().getSimpleName() + " Client",
                CustomerStatus.CLIENT_ACTIVE, group);
        LoanOfferingBO loanOffering1 = TestObjectFactory.createLoanOffering(startDate, meeting);
        LoanOfferingBO loanOffering2 = TestObjectFactory.createLoanOffering("Loan2345", "313f", startDate, meeting);
        groupAccount = TestObjectFactory.createLoanAccount("42423142341", group,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate, loanOffering1);
        clientAccount = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1, loanOffering2);
        java.util.Date currentDate = new java.util.Date(System.currentTimeMillis());
        SavingsOfferingBO savingsOffering1 = TestObjectFactory.createSavingsProduct("SavingPrd1", "ased", currentDate, RecommendedAmountUnit.COMPLETE_GROUP);
        SavingsOfferingBO savingsOffering2 = TestObjectFactory.createSavingsProduct("SavingPrd2", "cvdf", currentDate, RecommendedAmountUnit.COMPLETE_GROUP);
        SavingsOfferingBO savingsOffering3 = TestObjectFactory.createSavingsProduct("SavingPrd3", "zxsd", currentDate, RecommendedAmountUnit.COMPLETE_GROUP);

        centerSavingsAccount = TestObjectFactory.createSavingsAccount("43244334", center, Short.valueOf("16"),
                startDate, savingsOffering1);
        groupSavingsAccount = TestObjectFactory.createSavingsAccount("43234434", group, Short.valueOf("16"), startDate,
                savingsOffering2);
        clientSavingsAccount = TestObjectFactory.createSavingsAccount("43245434", client, Short.valueOf("16"),
                startDate, savingsOffering3);

        CollectionSheetEntryView bulkEntryParent = new CollectionSheetEntryView(getCusomerView(center), TestUtils.RUPEE);
        SavingsAccountView centerSavingsAccountView = getSavingsAccountView(centerSavingsAccount);
        centerSavingsAccountView.setDepositAmountEntered("100");
        centerSavingsAccountView.setWithDrawalAmountEntered("10");
        bulkEntryParent.addSavingsAccountDetail(centerSavingsAccountView);
        bulkEntryParent.setCustomerAccountDetails(getCustomerAccountView(center));

        CollectionSheetEntryView bulkEntryChild = new CollectionSheetEntryView(getCusomerView(group), TestUtils.RUPEE);
        LoanAccountView groupLoanAccountView = getLoanAccountView(groupAccount);
        SavingsAccountView groupSavingsAccountView = getSavingsAccountView(groupSavingsAccount);
        groupSavingsAccountView.setDepositAmountEntered("100");
        groupSavingsAccountView.setWithDrawalAmountEntered("10");
        bulkEntryChild.addLoanAccountDetails(groupLoanAccountView);
        bulkEntryChild.addSavingsAccountDetail(groupSavingsAccountView);
        bulkEntryChild.setCustomerAccountDetails(getCustomerAccountView(group));

        CollectionSheetEntryView bulkEntrySubChild = new CollectionSheetEntryView(getCusomerView(client), TestUtils.RUPEE);
        LoanAccountView clientLoanAccountView = getLoanAccountView(clientAccount);
        clientLoanAccountView.setAmountPaidAtDisbursement(0.0);
        SavingsAccountView clientSavingsAccountView = getSavingsAccountView(clientSavingsAccount);
        clientSavingsAccountView.setDepositAmountEntered("100");
        clientSavingsAccountView.setWithDrawalAmountEntered("10");
        bulkEntrySubChild.addLoanAccountDetails(clientLoanAccountView);
        bulkEntrySubChild.setAttendence(new Short("2"));
        bulkEntrySubChild.addSavingsAccountDetail(clientSavingsAccountView);
        bulkEntrySubChild.setCustomerAccountDetails(getCustomerAccountView(client));

        bulkEntryChild.addChildNode(bulkEntrySubChild);
        bulkEntryParent.addChildNode(bulkEntryChild);

        bulkEntryChild.getLoanAccountDetails().get(0).setPrdOfferingId(groupLoanAccountView.getPrdOfferingId());
        bulkEntryChild.getLoanAccountDetails().get(0).setEnteredAmount("100.0");
        bulkEntrySubChild.getLoanAccountDetails().get(0).setDisBursementAmountEntered(
                clientAccount.getLoanAmount().toString());
        bulkEntrySubChild.getLoanAccountDetails().get(0).setPrdOfferingId(clientLoanAccountView.getPrdOfferingId());

        ProductDto loanOfferingDto = new ProductDto(loanOffering1.getPrdOfferingId(), loanOffering1
                .getPrdOfferingShortName());
        ProductDto loanOfferingDto2 = new ProductDto(loanOffering2.getPrdOfferingId(), loanOffering2
                .getPrdOfferingShortName());

        List<ProductDto> loanProducts = Arrays.asList(loanOfferingDto, loanOfferingDto2);

        ProductDto savingOfferingDto = new ProductDto(savingsOffering1.getPrdOfferingId(), savingsOffering1
                .getPrdOfferingShortName());
        ProductDto savingOfferingDto2 = new ProductDto(savingsOffering2.getPrdOfferingId(), savingsOffering2
                .getPrdOfferingShortName());
        ProductDto savingOfferingDto3 = new ProductDto(savingsOffering3.getPrdOfferingId(), savingsOffering3
                .getPrdOfferingShortName());

        List<ProductDto> savingsProducts = Arrays.asList(savingOfferingDto, savingOfferingDto2, savingOfferingDto3);

        final PersonnelView loanOfficer = getPersonnelView(center.getPersonnel());
        final OfficeView officeView = null;
        final List<CustomValueListElement> attendanceTypesList = new ArrayList<CustomValueListElement>();

        CollectionSheetEntryGridDto bulkEntry = new CollectionSheetEntryGridDto(bulkEntryParent, loanOfficer,
                officeView, getPaymentTypeView(), new java.util.Date(), "324343242", new java.util.Date(),
                loanProducts, savingsProducts, attendanceTypesList);

        return bulkEntry;

    }

    private LoanBO getLoanAccount(final AccountState state, final Date startDate, final int disbursalType,
            final LoanOfferingBO loanOfferingBO) {
        return TestObjectFactory.createLoanAccountWithDisbursement("99999999999", group, state, startDate,
                loanOfferingBO, disbursalType);

    }

    private LoanAccountView getLoanAccountView(final LoanBO account) {
        LoanAccountView accountView = TestObjectFactory.getLoanAccountView(account);
        List<AccountActionDateEntity> actionDates = new ArrayList<AccountActionDateEntity>();
        actionDates.add(account.getAccountActionDate((short) 1));
        accountView.addTrxnDetails(TestObjectFactory.getBulkEntryAccountActionViews(actionDates));

        return accountView;
    }

    private SavingsAccountView getSavingsAccountView(final SavingsBO account) {
        final Integer customerId = null;
        final String savingOfferingShortName = account.getSavingsOffering().getPrdOfferingShortName();
        final Short savingOfferingId = account.getSavingsOffering().getPrdOfferingId();
        final Short savingsTypeId = account.getSavingsOffering().getSavingsType().getId();
        Short reccomendedAmountUnitId = null;
        if (account.getSavingsOffering().getRecommendedAmntUnit() != null) {
            reccomendedAmountUnitId = account.getSavingsOffering().getRecommendedAmntUnit().getId();
        }

        SavingsAccountView accountView = new SavingsAccountView(account.getAccountId(), customerId,
                savingOfferingShortName, savingOfferingId, savingsTypeId, reccomendedAmountUnitId);

        accountView.addAccountTrxnDetail(TestObjectFactory.getBulkEntryAccountActionView(account
                .getAccountActionDate((short) 1)));

        return accountView;
    }

    private CustomerView getCusomerView(final CustomerBO customer) {
        CustomerView customerView = new CustomerView();
        customerView.setCustomerId(customer.getCustomerId());
        customerView.setCustomerLevelId(customer.getCustomerLevel().getId());
        customerView.setCustomerSearchId(customer.getSearchId());
        customerView.setDisplayName(customer.getDisplayName());
        customerView.setGlobalCustNum(customer.getGlobalCustNum());
        customerView.setOfficeId(customer.getOffice().getOfficeId());
        if (null != customer.getParentCustomer()) {
            customerView.setParentCustomerId(customer.getParentCustomer().getCustomerId());
        }
        customerView.setPersonnelId(customer.getPersonnel().getPersonnelId());
        customerView.setStatusId(customer.getCustomerStatus().getId());
        return customerView;
    }

    private PersonnelView getPersonnelView(final PersonnelBO personnel) {
        PersonnelView personnelView = new PersonnelView(personnel.getPersonnelId(), personnel.getDisplayName());
        return personnelView;
    }

    private ListItem<Short> getPaymentTypeView() {
        ListItem<Short> paymentTypeView = new ListItem<Short>(Short.valueOf("1"), "displayValue");
        return paymentTypeView;
    }

    private CustomerAccountView getCustomerAccountView(final CustomerBO customer) {
        CustomerAccountView customerAccountView = new CustomerAccountView(customer.getCustomerAccount().getAccountId(), getCurrency());

        List<AccountActionDateEntity> accountAction = new ArrayList<AccountActionDateEntity>();
        accountAction.add(customer.getCustomerAccount().getAccountActionDate(Short.valueOf("1")));
        customerAccountView.setAccountActionDates(TestObjectFactory.getBulkEntryAccountActionViews(accountAction));
        customerAccountView.setCustomerAccountAmountEntered("100.0");
        customerAccountView.setValidCustomerAccountAmountEntered(true);
        return customerAccountView;
    }

}
