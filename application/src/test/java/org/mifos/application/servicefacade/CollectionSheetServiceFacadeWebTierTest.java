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
package org.mifos.application.servicefacade;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.collectionsheet.struts.actionforms.BulkEntryActionForm;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.customers.business.CustomerDto;
import org.mifos.customers.office.business.OfficeDetailsDto;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelDto;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test {@link CollectionSheetServiceFacadeWebTier}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CollectionSheetServiceFacadeWebTierTest {

    // system under test (SUT)
    private CollectionSheetServiceFacadeWebTier collectionSheetServiceFacadeWebTier;

    // create test-doubles for all depended-on-components (DOC)s
    @Mock
    private OfficePersistence officePersistence;

    @Mock
    private MasterPersistence masterPersistence;

    @Mock
    private PersonnelPersistence personnelPersistence;

    @Mock
    private CustomerPersistence customerPersistence;

    @Mock
    private CollectionSheetService collectionSheetService;

    @Mock
    private PaymentTypeEntity masterDataEntity;

    @Mock
    private CollectionSheetDtoTranslator collectionSheetTranslator;

    private UserContext userContext;
    private BulkEntryActionForm collectionSheetForm;
    private static final Short defaultCurrencyId = Short.valueOf("2");
    private static MifosCurrency defaultCurrency;

    @BeforeClass
    public static void setupMifosLoggerDueToUseOfStaticClientRules() {
        defaultCurrency = new MifosCurrency(defaultCurrencyId, null, null, null);
        Money.setDefaultCurrency(defaultCurrency);
    }

    @Before
    public void setupSUTAndInjectMocksAsDependencies() {

        userContext = new UserContext();
        userContext.setBranchId(Short.valueOf("1"));
        userContext.setId(Short.valueOf("1"));
        userContext.setLevel(PersonnelLevel.LOAN_OFFICER);

        collectionSheetForm = new BulkEntryActionForm();
        collectionSheetForm.setOfficeId("2");
        collectionSheetForm.setLoanOfficerId("2");
        collectionSheetForm.setCustomerId("2");
        collectionSheetForm.setPaymentId("2");

        collectionSheetServiceFacadeWebTier = new CollectionSheetServiceFacadeWebTier(officePersistence,
                masterPersistence, personnelPersistence, customerPersistence, collectionSheetService, collectionSheetTranslator);
    }

    @Test
    public void shouldConvertMasterDataEntitiesToListItemsAndPopulateDtoWithPaymentTypes() throws Exception {

        // setup
        final Short paymentTypeId = Short.valueOf("2");
        final String paymentTypeName = "type1";
        final ListItem<Short> paymentType1 = new ListItem<Short>(paymentTypeId, paymentTypeName);

        final List<PaymentTypeEntity> paymentTypeEntities = new ArrayList<PaymentTypeEntity>();
        paymentTypeEntities.add(masterDataEntity);

        // stubbing
        when(masterDataEntity.getId()).thenReturn(paymentTypeId);
        when(masterDataEntity.getName()).thenReturn(paymentTypeName);
        when(masterPersistence.retrieveMasterEntities(PaymentTypeEntity.class, Short.valueOf("1"))).thenReturn(
                paymentTypeEntities);

        // exercise test
        CollectionSheetEntryFormDto formDto = collectionSheetServiceFacadeWebTier
                .loadAllActiveBranchesAndSubsequentDataIfApplicable(userContext);

        // verification
        assertThat(formDto.getPaymentTypesList(), hasItem(paymentType1));
    }

    @Test
    public void shouldPopulateDtoWithActiveBranches() throws Exception {

        // setup
        final Short branchId = userContext.getBranchId();
        final Short branchId2 = Short.valueOf("2");
        final Short levelId = OfficeLevel.BRANCHOFFICE.getValue();

        // we typcially don't try to mock/stub value objects (DTO) as they have
        // no behaviour so just use as you would in production code.
        OfficeDetailsDto officeStub1 = new OfficeDetailsDto(branchId, "branchName1", levelId, Integer.valueOf(1));
        OfficeDetailsDto officeStub2 = new OfficeDetailsDto(branchId2, "branchName2", levelId, Integer.valueOf(1));
        List<OfficeDetailsDto> activeOffices = Arrays.asList(officeStub1, officeStub2);

        // stub interaction with DAO/Persistence layer.
        when(officePersistence.getActiveOffices(branchId)).thenReturn(activeOffices);

        // exercise test
        CollectionSheetEntryFormDto formDto = collectionSheetServiceFacadeWebTier
                .loadAllActiveBranchesAndSubsequentDataIfApplicable(userContext);

        // verification
        assertThat(formDto.getActiveBranchesList(), is(activeOffices));
    }

    @Test
    public void shouldPopulateDtoWithLoanOfficersWhenOnlyOneActiveBranchExists() throws Exception {

        // setup
        final Short branchId = userContext.getBranchId();
        final Short levelId = OfficeLevel.BRANCHOFFICE.getValue();
        OfficeDetailsDto officeStub1 = new OfficeDetailsDto(branchId, "branchName1", levelId, Integer.valueOf(1));
        List<OfficeDetailsDto> onlyOneActiveBranch = Arrays.asList(officeStub1);

        final PersonnelDto loanOfficer1 = new PersonnelDto(Short.valueOf("1"), "LoanOfficer1");
        final PersonnelDto loanOfficer2 = new PersonnelDto(Short.valueOf("2"), "LoanOfficer2");
        List<PersonnelDto> loanOfficers = Arrays.asList(loanOfficer1, loanOfficer2);

        // stub interaction with DAO/Persistence layer.
        when(officePersistence.getActiveOffices(branchId)).thenReturn(onlyOneActiveBranch);
        when(
                personnelPersistence.getActiveLoanOfficersInBranch(PersonnelConstants.LOAN_OFFICER, branchId,
                        userContext.getId(), userContext.getLevelId())).thenReturn(loanOfficers);

        // exercise test
        CollectionSheetEntryFormDto formDto = collectionSheetServiceFacadeWebTier
                .loadAllActiveBranchesAndSubsequentDataIfApplicable(userContext);

        // verification
        assertThat(formDto.getLoanOfficerList(), is(loanOfficers));
    }

    @Test
    public void shouldPopulateDtoWithCustomersWhenOnlyOneActiveBranchAndOneLoanOfficerExists() throws Exception {

        // setup
        final Short branchId = userContext.getBranchId();
        final Short levelId = OfficeLevel.BRANCHOFFICE.getValue();
        OfficeDetailsDto officeStub1 = new OfficeDetailsDto(branchId, "branchName1", levelId, Integer.valueOf(1));
        List<OfficeDetailsDto> onlyOneActiveBranch = Arrays.asList(officeStub1);

        final PersonnelDto loanOfficer1 = new PersonnelDto(Short.valueOf("1"), "LoanOfficer1");
        List<PersonnelDto> onlyOneActiveLoanOfficer = Arrays.asList(loanOfficer1);

        final CustomerDto customer1 = new CustomerDto();
        List<CustomerDto> customers = Arrays.asList(customer1);

        // stub interaction with DAO/Persistence layer.
        when(officePersistence.getActiveOffices(branchId)).thenReturn(onlyOneActiveBranch);
        when(
                personnelPersistence.getActiveLoanOfficersInBranch(PersonnelConstants.LOAN_OFFICER, branchId,
                        userContext.getId(), userContext.getLevelId())).thenReturn(onlyOneActiveLoanOfficer);
        when(
                customerPersistence.getActiveParentList(loanOfficer1.getPersonnelId(), CustomerLevel.CENTER.getValue(),
                        branchId)).thenReturn(customers);

        // exercise test
        CollectionSheetEntryFormDto formDto = collectionSheetServiceFacadeWebTier
                .loadAllActiveBranchesAndSubsequentDataIfApplicable(userContext);

        // verification
        assertThat(formDto.getCustomerList(), is(customers));
    }

    @Test
    public void shouldPopulateDtoWithValueToForceFormNotToBeRefreshedWhenAllDropdownListDataIsFetched()
            throws Exception {

        // setup
        final Short branchId = userContext.getBranchId();
        final Short levelId = OfficeLevel.BRANCHOFFICE.getValue();
        OfficeDetailsDto officeStub1 = new OfficeDetailsDto(branchId, "branchName1", levelId, Integer.valueOf(1));
        List<OfficeDetailsDto> onlyOneActiveBranch = Arrays.asList(officeStub1);

        final PersonnelDto loanOfficer1 = new PersonnelDto(Short.valueOf("1"), "LoanOfficer1");
        List<PersonnelDto> onlyOneActiveLoanOfficer = Arrays.asList(loanOfficer1);

        final CustomerDto customer1 = new CustomerDto();
        List<CustomerDto> customers = Arrays.asList(customer1);

        // stub interaction with DAO/Persistence layer.
        when(officePersistence.getActiveOffices(branchId)).thenReturn(onlyOneActiveBranch);
        when(
                personnelPersistence.getActiveLoanOfficersInBranch(PersonnelConstants.LOAN_OFFICER, branchId,
                        userContext.getId(), userContext.getLevelId())).thenReturn(onlyOneActiveLoanOfficer);
        when(
                customerPersistence.getActiveParentList(loanOfficer1.getPersonnelId(), CustomerLevel.CENTER.getValue(),
                        branchId)).thenReturn(customers);

        // exercise test
        CollectionSheetEntryFormDto formDto = collectionSheetServiceFacadeWebTier
                .loadAllActiveBranchesAndSubsequentDataIfApplicable(userContext);

        // verification
        assertThat(formDto.getReloadFormAutomatically(), is(Constants.NO));
    }

    @Test
    public void shouldPopulateDtoWithValueToForceFormToRefreshedWhenMoreDropdownDataNeedsToBeFetched() throws Exception {

        // setup
        final Short branchId = userContext.getBranchId();
        final Short branchId2 = Short.valueOf("2");
        final Short levelId = OfficeLevel.BRANCHOFFICE.getValue();

        OfficeDetailsDto officeStub1 = new OfficeDetailsDto(branchId, "branchName1", levelId, Integer.valueOf(1));
        OfficeDetailsDto officeStub2 = new OfficeDetailsDto(branchId2, "branchName2", levelId, Integer.valueOf(1));
        List<OfficeDetailsDto> activeOffices = Arrays.asList(officeStub1, officeStub2);

        // stub interaction with DAO/Persistence layer.
        when(officePersistence.getActiveOffices(branchId)).thenReturn(activeOffices);

        // exercise test
        CollectionSheetEntryFormDto formDto = collectionSheetServiceFacadeWebTier
                .loadAllActiveBranchesAndSubsequentDataIfApplicable(userContext);

        // verification
        assertThat(formDto.getReloadFormAutomatically(), is(Constants.YES));
    }

    @Test
    public void shouldPopulateDtoWithValueToDisallowBackDatedTransactions() throws Exception {

        // exercise test
        CollectionSheetEntryFormDto formDto = collectionSheetServiceFacadeWebTier
                .loadAllActiveBranchesAndSubsequentDataIfApplicable(userContext);

        // verification
        assertThat(formDto.getBackDatedTransactionAllowed(), is(Constants.NO));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPopulateDtoWithLoanOfficersAndPreviouslyPopulatedDataWithDto() throws Exception {

        // setup
        final Short branchId = userContext.getBranchId();
        final Short levelId = OfficeLevel.BRANCHOFFICE.getValue();

        final OfficeDetailsDto officeStub1 = new OfficeDetailsDto(branchId, "branchName1", levelId, Integer.valueOf(1));
        final List<OfficeDetailsDto> onlyOneActiveBranch = Arrays.asList(officeStub1);

        final ListItem<Short> paymentType1 = new ListItem<Short>(Short.valueOf("1"), "paymentType1");

        final CollectionSheetEntryFormDto previousCollectionSheetFormDto = new CollectionSheetEntryFormDto(
                onlyOneActiveBranch, Arrays.<ListItem<Short>> asList(paymentType1), new ArrayList<PersonnelDto>(),
                new ArrayList<CustomerDto>(), Constants.YES, Constants.YES, Constants.YES);

        final PersonnelDto loanOfficer1 = new PersonnelDto(Short.valueOf("1"), "LoanOfficer1");
        final PersonnelDto loanOfficer2 = new PersonnelDto(Short.valueOf("2"), "LoanOfficer2");
        final List<PersonnelDto> loanOfficers = Arrays.asList(loanOfficer1, loanOfficer2);

        // stub interaction with DAO/Persistence layer.
        when(
                personnelPersistence.getActiveLoanOfficersInBranch(PersonnelConstants.LOAN_OFFICER, branchId,
                        userContext.getId(), userContext.getLevelId())).thenReturn(loanOfficers);

        // exercise test
        CollectionSheetEntryFormDto formDto = collectionSheetServiceFacadeWebTier.loadLoanOfficersForBranch(branchId,
                userContext, previousCollectionSheetFormDto);

        // verification
        assertThat(formDto.getLoanOfficerList(), is(loanOfficers));

        // assert rest of data comes from previousDto
        assertThat(formDto.getActiveBranchesList(), is(previousCollectionSheetFormDto.getActiveBranchesList()));
    }

    @Test
    public void shouldPopulateDtoWithLatestMeetingDateWhenBackDatedTransactionsAreAllowed() throws Exception {

        // setup
        final Integer customerId = Integer.valueOf(3);
        final CustomerDto customer1 = new CustomerDto();
        customer1.setCustomerId(customerId);
        final List<CustomerDto> customers = Arrays.asList(customer1);

        final Date expectedMeetingDateAsJavaDate = new DateTime().plusDays(2).toDate();
        final java.sql.Date expectedMeetingDateAsSqlDate = new java.sql.Date(expectedMeetingDateAsJavaDate.getTime());

        final CollectionSheetEntryFormDto previousCollectionSheetFormDto = new CollectionSheetEntryFormDto(
                new ArrayList<OfficeDetailsDto>(), new ArrayList<ListItem<Short>>(), new ArrayList<PersonnelDto>(),
                customers, Constants.YES, Constants.YES, Constants.YES);

        when(customerPersistence.getLastMeetingDateForCustomer(customerId)).thenReturn(expectedMeetingDateAsSqlDate);

        // NOTE: Backdated transactions is checked through static
        // AccountingRules so can't set up for now

        // exercise test
        CollectionSheetEntryFormDto formDto = collectionSheetServiceFacadeWebTier.loadMeetingDateForCustomer(
                customerId, previousCollectionSheetFormDto);

        // verification
        assertThat(formDto.getMeetingDate(), is(expectedMeetingDateAsJavaDate));

        // assert rest of data comes from previousDto
        assertThat(formDto.getCustomerList(), is(customers));
    }
}
