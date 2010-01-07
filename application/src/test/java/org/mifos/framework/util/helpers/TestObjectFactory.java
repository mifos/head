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

package org.mifos.framework.util.helpers;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountTestUtils;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.financial.business.FinancialTransactionBO;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.financial.util.helpers.ChartOfAccountsCache;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanBOIntegrationTest;
import org.mifos.application.accounts.loan.business.LoanBOTestUtils;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.loan.business.LoanTrxnDetailEntity;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountView;
import org.mifos.application.accounts.savings.business.SavingBOTestUtils;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.CustomerAccountPaymentData;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.checklist.business.AccountCheckListBO;
import org.mifos.application.checklist.business.CheckListBO;
import org.mifos.application.checklist.business.CheckListDetailEntity;
import org.mifos.application.checklist.business.CustomerCheckListBO;
import org.mifos.application.collectionsheet.business.CollSheetCustBO;
import org.mifos.application.collectionsheet.business.CollSheetLnDetailsEntity;
import org.mifos.application.collectionsheet.business.CollSheetSavingsDetailsEntity;
import org.mifos.application.collectionsheet.business.CollectionSheetBO;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryAccountFeeActionView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryCustomerAccountInstallmentView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryInstallmentView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryLoanInstallmentView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntrySavingsInstallmentView;
import org.mifos.application.customer.business.CustomerAccountBOIntegrationTest;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.customer.business.CustomerNoteEntity;
import org.mifos.application.customer.business.CustomerPositionEntity;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.persistence.CenterPersistence;
import org.mifos.application.customer.client.business.ClientAttendanceBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientDetailView;
import org.mifos.application.customer.client.business.ClientInitialSavingsOfferingEntity;
import org.mifos.application.customer.client.business.ClientNameDetailView;
import org.mifos.application.customer.client.business.NameType;
import org.mifos.application.customer.client.persistence.ClientPersistence;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.GroupTemplate;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.persistence.GroupPersistence;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.CategoryTypeEntity;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeFormulaEntity;
import org.mifos.application.fees.business.FeeFrequencyTypeEntity;
import org.mifos.application.fees.business.FeePaymentEntity;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.business.RateFeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFormula;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.HolidayPK;
import org.mifos.application.holiday.business.RepaymentRuleEntity;
import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.business.FundCodeEntity;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.helpers.OperationMode;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.application.productdefinition.business.InterestCalcTypeEntity;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.LoanOfferingTestUtils;
import org.mifos.application.productdefinition.business.PrdApplicableMasterEntity;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.productdefinition.business.PrdOfferingMeetingEntity;
import org.mifos.application.productdefinition.business.PrdStatusEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.business.RecommendedAmntUnitEntity;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.business.SavingsTypeEntity;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.struts.actionforms.LoanPrdActionForm;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.GraceType;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.application.productsmix.business.ProductMixBO;
import org.mifos.application.reports.business.ReportsBO;
import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestObjectPersistence;
import org.mifos.framework.security.authentication.EncryptionService;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;

/**
 * This class assumes that you are connected to the model database, which has
 * master data in it and also you have some default objects in it, for this you
 * can run the master data script and then the test scripts, this script has
 * statements for creating some default objects.
 * 
 * The convention followed here is that any method that starts with "get" is
 * returning an object already existing in the database, this object is not
 * meant to be modified and the method that starts with "create" creates a new
 * object inserts it into the database and returns that hence these objects are
 * meant to be cleaned up by the user.
 */
public class TestObjectFactory {

    private static TestObjectPersistence testObjectPersistence = new TestObjectPersistence();

    /**
     * Constants to make calls to
     * {@link #getNewMeeting(RecurrenceType, short, MeetingType, WeekDay)} and
     * {@link #getNewMeetingForToday(RecurrenceType, short, MeetingType)} more
     * readable.
     */
    public static final short EVERY_WEEK = 1;
    public static final short EVERY_MONTH = 1;
    public static final short EVERY_DAY = 1;
    public static final short EVERY_SECOND_WEEK = 2;
    public static final short EVERY_SECOND_MONTH = 2;

    /**
     * We supply this for the salutation in a lot of test data, but I'm not sure
     * a value of 1 really has a well-defined meaning given our master data.
     */
    public static final int SAMPLE_SALUTATION = 1;

    private static final short SAMPLE_CATEGORY = 2;

    /**
     * Corresponds to a locale we set up in latest-data.
     */
    public static final Short TEST_LOCALE = 1;

    /**
     * Set up in latest-data.
     */
    public static final Short HEAD_OFFICE = 1;

    /**
     * Set up in testdbinsertionscript.
     */
    public static final Short SAMPLE_AREA_OFFICE = 2;

    /**
     * Set up in testdbinsertionscript.
     */
    public static final Short SAMPLE_BRANCH_OFFICE = 3;

    /**
     * @return - Returns the office created by test data scripts. If the row
     *         does not already exist in the database it returns null. defaults
     *         created are 1- Head Office , 2 - Area Office , 3 - BranchOffice.
     */
    public static OfficeBO getOffice(final Short officeId) {
        return (OfficeBO) addObject(testObjectPersistence.getOffice(officeId));
    }

    public static void removeObject(PersistentObject obj) {
        if (obj != null) {
            testObjectPersistence.removeObject(obj);
            obj = null;
        }
    }

    /**
     * @return - Returns the personnel created by master data scripts. This
     *         record does not have any custom fields or roles associated with
     *         it. If the row does not already exist in the database it returns
     *         null.
     */

    public static PersonnelBO getPersonnel(final Session session, final Short personnelId) {
        return (PersonnelBO) addObject(testObjectPersistence.getPersonnel(session, personnelId));
    }

    public static PersonnelBO getPersonnel(final Short personnelId) {
        return getPersonnel(StaticHibernateUtil.getSessionTL(), personnelId);
    }

    /*
     * Create a center which includes a weekly maintenance fee of 100
     */
    public static CenterBO createCenter(final String customerName, final MeetingBO meeting) {
        return createCenter(customerName, meeting, getFees());
    }

    public static CenterBO createCenterForTestGetLoanAccounts(final String customerName, final MeetingBO meeting) {
        return createCenterForTestGetLoanAccounts(customerName, meeting, getFees());
    }

    public static CenterBO createCenter(final String customerName, final MeetingBO meeting, final Short officeId, final Short personnelId) {
        return createCenter(customerName, meeting, officeId, personnelId, getFees());
    }

    public static CenterBO createCenter(final String customerName, final MeetingBO meeting, final List<FeeView> fees) {
        return createCenter(customerName, meeting, SAMPLE_BRANCH_OFFICE, PersonnelConstants.SYSTEM_USER, fees);
    }

    public static CenterBO createCenterForTestGetLoanAccounts(final String customerName, final MeetingBO meeting, final List<FeeView> fees) {
        return createCenter(customerName, meeting, SAMPLE_BRANCH_OFFICE, PersonnelConstants.TEST_USER, fees);
    }

    public static CenterBO createCenter(final String customerName, final MeetingBO meeting, final Short officeId, final Short personnelId,
            final List<FeeView> fees) {
        CenterBO center;
        try {
            center = new CenterBO(TestUtils.makeUserWithLocales(), customerName, null, null, fees, null, null,
                    new OfficePersistence().getOffice(officeId), meeting, new PersonnelPersistence()
                            .getPersonnel(personnelId), new CustomerPersistence());
            new CenterPersistence().saveCenter(center);
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        addObject(center);
        return center;
    }

    public static ProductMixBO createNotAllowedProductForAProductOffering(final PrdOfferingBO prdOffering,
            final PrdOfferingBO prdOfferingNotAllowedId) {
        ProductMixBO prdmix;
        try {
            prdmix = new ProductMixBO(prdOffering, prdOfferingNotAllowedId);
            prdmix.save();
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        addObject(prdmix);
        return prdmix;
    }

    public static List<FeeView> getFees() {
        List<FeeView> fees = new ArrayList<FeeView>();
        AmountFeeBO maintenanceFee = (AmountFeeBO) createPeriodicAmountFee("Maintenance Fee", FeeCategory.ALLCUSTOMERS,
                "100", RecurrenceType.WEEKLY, Short.valueOf("1"));
        FeeView fee = new FeeView(getContext(), maintenanceFee);
        fees.add(fee);
        return fees;
    }

    public static List<FeeView> getFeesWithMakeUser() {
        List<FeeView> fees = new ArrayList<FeeView>();
        AmountFeeBO maintenanceFee = (AmountFeeBO) createPeriodicAmountFeeWithMakeUser("Maintenance Fee",
                FeeCategory.ALLCUSTOMERS, "100", RecurrenceType.WEEKLY, Short.valueOf("1"));
        FeeView fee = new FeeView(getContext(), maintenanceFee);
        fees.add(fee);
        return fees;
    }

    public static List<CustomFieldView> getCustomFields() {
        List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();
        CustomFieldView fee = new CustomFieldView(Short.valueOf("4"), "Custom", CustomFieldType.NUMERIC);
        customFields.add(fee);
        return customFields;
    }

    /**
     * This is just a helper method which returns a address object , this is
     * just a helper it does not persist any data.
     */
    public static Address getAddressHelper() {
        Address address = new Address();
        address.setLine1("line1");
        address.setCity("city");
        address.setCountry("country");
        return address;
    }

    public static GroupBO createGroupUnderCenter(final String customerName, final CustomerStatus customerStatus,
            final CustomerBO parentCustomer) {
        Short formedBy = PersonnelConstants.SYSTEM_USER;
        return createGroupUnderCenter(customerName, customerStatus, null, false, null, null, getCustomFields(),
                getFees(), formedBy, parentCustomer);
    }

    public static GroupBO createGroupUnderCenterForTestGetLoanAccountsInActiveBadStanding(final String customerName,
            final CustomerStatus customerStatus, final CustomerBO parentCustomer) {
        Short formedBy = PersonnelConstants.TEST_USER;
        return createGroupUnderCenter(customerName, customerStatus, null, false, null, null, getCustomFields(),
                getFees(), formedBy, parentCustomer);
    }

    public static GroupBO createGroupUnderCenter(final String customerName, final CustomerStatus customerStatus, final String externalId,
            final boolean trained, final Date trainedDate, final Address address, final List<CustomFieldView> customFields, final List<FeeView> fees,
            final Short formedById, final CustomerBO parentCustomer) {
        GroupBO group;
        try {
            group = new GroupBO(TestUtils.makeUserWithLocales(), customerName, customerStatus, externalId, trained,
                    trainedDate, address, customFields, fees, new PersonnelPersistence().getPersonnel(formedById),
                    parentCustomer, new GroupPersistence(), new OfficePersistence());
            new GroupPersistence().saveGroup(group);
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        addObject(group);
        return group;
    }

    public static GroupBO createGroupUnderBranch(final String customerName, final CustomerStatus customerStatus, final Short officeId,
            final MeetingBO meeting, final Short loanOfficerId) {
        Short formedBy = PersonnelConstants.SYSTEM_USER;
        return createGroupUnderBranch(customerName, customerStatus, null, false, null, null, null, getFees(), formedBy,
                officeId, meeting, loanOfficerId);
    }

    public static GroupBO createGroupUnderBranchWithMakeUser(final String customerName, final CustomerStatus customerStatus,
            final Short officeId, final MeetingBO meeting, final Short loanOfficerId) {
        Short formedBy = PersonnelConstants.SYSTEM_USER;
        return createGroupUnderBranch(customerName, customerStatus, null, false, null, null, null,
                getFeesWithMakeUser(), formedBy, officeId, meeting, loanOfficerId);
    }

    public static GroupBO createGroupUnderBranch(final String customerName, final CustomerStatus customerStatus, final String externalId,
            final boolean trained, final Date trainedDate, final Address address, final List<CustomFieldView> customFields, final List<FeeView> fees,
            final Short formedById, final Short officeId, final MeetingBO meeting, final Short loanOfficerId) {
        GroupBO group;
        PersonnelBO loanOfficer = null;
        try {
            if (loanOfficerId != null) {
                loanOfficer = new PersonnelPersistence().getPersonnel(loanOfficerId);
            }
            group = new GroupBO(TestUtils.makeUserWithLocales(), customerName, customerStatus, externalId, trained,
                    trainedDate, address, customFields, fees, new PersonnelPersistence().getPersonnel(formedById),
                    new OfficePersistence().getOffice(officeId), meeting, loanOfficer, new GroupPersistence(), new OfficePersistence());
            new GroupPersistence().saveGroup(group);
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        addObject(group);
        return group;
    }

    public static ClientBO createClient(final String customerName, final CustomerStatus status, final CustomerBO parentCustomer) {
        return createClient(customerName, status, parentCustomer, getFees(), (String) null, new Date(1222333444000L));
    }

    public static ClientBO createClient(final String customerName, final CustomerStatus status, final CustomerBO parentCustomer,
            final List<FeeView> fees, final String governmentId, final Date dateOfBirth) {
        ClientDetailView clientDetailView = new ClientDetailView(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                .valueOf("1"), Short.valueOf("41"));
        ClientNameDetailView clientNameDetailView = clientNameView(NameType.MAYBE_CLIENT, customerName);
        ClientNameDetailView spouseNameDetailView = clientNameView(NameType.SPOUSE, customerName);
        ClientBO client;
        try {
            client = new ClientBO(TestUtils.makeUserWithLocales(), customerName, status, null, null, null, null, fees,
                    null, new PersonnelPersistence().getPersonnel(PersonnelConstants.SYSTEM_USER),
                    new OfficePersistence().getOffice(SAMPLE_BRANCH_OFFICE), parentCustomer, dateOfBirth, governmentId,
                    null, null, YesNoFlag.YES.getValue(), clientNameDetailView, spouseNameDetailView, clientDetailView,
                    null);
            new ClientPersistence().saveClient(client);
        } catch (CustomerException e) {
            throw new RuntimeException(e);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
        StaticHibernateUtil.commitTransaction();
        addObject(client);
        return client;
    }

    public static ClientNameDetailView clientNameView(final NameType nameType, final String customerName) {
        return new ClientNameDetailView(nameType, SAMPLE_SALUTATION, customerName, "middle", customerName, "secondLast");
    }

    public static ClientBO createClient(final String customerName, final MeetingBO meeting, final CustomerStatus status) {
        ClientBO client;

        try {
            PersonnelBO systemUser = new PersonnelPersistence().getPersonnel(PersonnelConstants.SYSTEM_USER);
            ClientNameDetailView clientNameDetailView = new ClientNameDetailView(NameType.CLIENT, SAMPLE_SALUTATION,
                    customerName, "middle", customerName, "secondLast");
            ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(NameType.SPOUSE, SAMPLE_SALUTATION,
                    customerName, "middle", customerName, "secondLast");
            ClientDetailView clientDetailView = new ClientDetailView(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                    .valueOf("1"), Short.valueOf("41"));
            client = new ClientBO(TestUtils.makeUserWithLocales(), clientNameDetailView.getDisplayName(), status, null,
                    null, null, null, getFees(), null, systemUser, new OfficePersistence()
                            .getOffice(SAMPLE_BRANCH_OFFICE), meeting, systemUser, new Date(), null, null, null,
                    YesNoFlag.NO.getValue(), clientNameDetailView, spouseNameDetailView, clientDetailView, null);
            new ClientPersistence().saveClient(client);
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        addObject(client);
        return client;
    }

    public static ClientBO createClient(final String customerName, final CustomerStatus status, final CustomerBO parentCustomer,
            final Date startDate) {
        ClientBO client;
        Short personnel = PersonnelConstants.SYSTEM_USER;
        try {
            ClientNameDetailView clientNameDetailView = new ClientNameDetailView(NameType.MAYBE_CLIENT,
                    SAMPLE_SALUTATION, customerName, "", customerName, "");
            ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(NameType.SPOUSE, SAMPLE_SALUTATION,
                    customerName, "middle", customerName, "secondLast");
            ClientDetailView clientDetailView = new ClientDetailView(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                    .valueOf("1"), Short.valueOf("41"));

            // Add a way to create parentless clients; like clients outside
            // groups
            if (null == parentCustomer) {
                client = new ClientBO(TestUtils.makeUserWithLocales(), // UserContext
                        clientNameDetailView.getDisplayName(), // String
                        // displayName
                        status, // CustomerStatus
                        null, // String externalId
                        null, // Date mfiJoiningDate
                        null, // Address
                        null, // List<CustomFieldView> customFields
                        getFees(), // List<FeeView> fees
                        null, // List<SavingsOfferingBO> offeringsSelected
                        new PersonnelPersistence().getPersonnel(personnel), // Short
                        // formedById
                        new OfficePersistence().getOffice(SAMPLE_BRANCH_OFFICE), // Short
                        // officeId
                        null, // MeetingBO
                        null, // Short loanOfficerId
                        null, // Date dateOfBirth
                        "", // String governmentId
                        null, // Short trained
                        null, // Date trainedDate
                        YesNoFlag.NO.getValue(), // Short groupFlag
                        clientNameDetailView, // ClientNameDetailView
                        spouseNameDetailView, // ClientNameDetailView
                        clientDetailView, // ClientDetailView
                        null); // InputStream picture
            } else {
                client = new ClientBO(TestUtils.makeUserWithLocales(), clientNameDetailView.getDisplayName(), status,
                        null, null, null, null, getFees(), null, new PersonnelPersistence().getPersonnel(personnel),
                        parentCustomer.getOffice(), parentCustomer, null, null, null, null, YesNoFlag.YES.getValue(),
                        clientNameDetailView, spouseNameDetailView, clientDetailView, null);
            }

            new ClientPersistence().saveClient(client);
            StaticHibernateUtil.commitTransaction();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        addObject(client);
        return client;
    }

    public static LoanOfferingBO createLoanOffering(final String name, final ApplicableTo applicableTo, final Date startDate,
            final PrdStatus offeringStatus, final Double defLnAmnt, final Double defIntRate, final int defInstallments,
            final InterestType interestType, final boolean intDedAtDisb, final boolean princDueLastInst, final MeetingBO meeting) {
        return createLoanOffering(name, name.substring(0, 1), applicableTo, startDate, offeringStatus, defLnAmnt,
                defIntRate, defInstallments, interestType, intDedAtDisb, princDueLastInst, meeting);
    }

    public static LoanOfferingBO createLoanOffering(final String name, final ApplicableTo applicableTo, final Date startDate,
            final PrdStatus offeringStatus, final Double defLnAmnt, final Double defIntRate, final int defInstallments,
            final InterestType interestType, final MeetingBO meeting) {
        return createLoanOffering(name, name.substring(0, 1), applicableTo, startDate, offeringStatus, defLnAmnt,
                defIntRate, defInstallments, interestType, false, false, meeting);
    }

    public static LoanOfferingBO createLoanOffering(final String name, final String shortName, final ApplicableTo applicableTo,
            final Date startDate, final PrdStatus offeringStatus, final Double defLnAmnt, final Double defIntRate, final int defInstallments,
            final InterestType interestType, final boolean intDedAtDisb, final boolean princDueLastInst, final MeetingBO meeting) {
        return createLoanOffering(name, shortName, applicableTo, startDate, offeringStatus, defLnAmnt, defIntRate,
                (short) defInstallments, interestType, intDedAtDisb, princDueLastInst, meeting,
                GraceType.GRACEONALLREPAYMENTS, "1", "1");
    }

    public static LoanOfferingBO createLoanOffering(final String name, final String shortName, final ApplicableTo applicableTo,
            final Date startDate, final PrdStatus offeringStatus, final Double defLnAmnt, final Double defIntRate, final int defInstallments,
            final InterestType interestType, final MeetingBO meeting) {
        return createLoanOffering(name, shortName, applicableTo, startDate, offeringStatus, defLnAmnt, defIntRate,
                (short) defInstallments, interestType, false, false, meeting, GraceType.GRACEONALLREPAYMENTS, "1", "1");
    }

    public static LoanOfferingBO createLoanOffering(final String name, final String shortName, final ApplicableTo applicableTo,
            final Date startDate, final PrdStatus offeringStatus, final Double defLnAmnt, final Double defIntRate, final int defInstallments,
            final InterestType interestType, final MeetingBO meeting, final String loanAmtCalcType, final String calcInstallmentType) {
        return createLoanOffering(name, shortName, applicableTo, startDate, offeringStatus, defLnAmnt, defIntRate,
                (short) defInstallments, interestType, false, false, meeting, GraceType.GRACEONALLREPAYMENTS,
                loanAmtCalcType, calcInstallmentType);
    }

    public static LoanOfferingBO createLoanOffering(final Date currentTime, final MeetingBO meeting) {
        return TestObjectFactory.createLoanOffering("Loan", "L", currentTime, meeting);
    }

    public static LoanOfferingBO createLoanOffering(final String name, final String shortName, final Date currentTime, final MeetingBO meeting) {
        return TestObjectFactory.createLoanOffering(name, shortName, ApplicableTo.GROUPS, currentTime,
                PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, InterestType.FLAT, meeting, "1", "1");
    }

    /**
     * @param defLnAmnt
     *            - Loan Amount same would be set as min and max amounts
     * @param defIntRate
     *            - Interest Rate same would be set as min and max amounts
     * @param defInstallments
     *            Number of installments set as min and max amounts
     * @param offeringStatusId
     *            See {@link PrdStatus}.
     */
    public static LoanOfferingBO createLoanOffering(final String name, final String shortName, final ApplicableTo applicableTo,
            final Date startDate, final PrdStatus offeringStatus, final Double defLnAmnt, final Double defIntRate, final Short defInstallments,
            final InterestType interestType, final boolean interestDeductedAtDisbursement, final boolean principalDueInLastInstallment,
            final MeetingBO meeting, final GraceType graceType) {
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(applicableTo);
        ProductCategoryBO productCategory = TestObjectFactory.getLoanPrdCategory();
        GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(graceType);
        InterestTypesEntity interestTypes = new InterestTypesEntity(interestType);
        GLCodeEntity glCodePrincipal = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                Short.valueOf("11"));

        GLCodeEntity glCodeInterest = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                Short.valueOf("21"));
        LoanOfferingBO loanOffering;
        try {
            loanOffering = new LoanOfferingBO(getContext(), name, shortName, productCategory, prdApplicableMaster,
                    startDate, null, null, gracePeriodType, (short) 0, interestTypes, new Money(
                            TestUtils.getCurrency(), defLnAmnt.toString()), new Money(TestUtils.getCurrency(),
                            defLnAmnt.toString()), new Money(TestUtils.getCurrency(), defLnAmnt.toString()),
                    defIntRate, defIntRate, defIntRate, defInstallments, defInstallments, defInstallments, true,
                    interestDeductedAtDisbursement, principalDueInLastInstallment, new ArrayList<FundBO>(),
                    new ArrayList<FeeBO>(), meeting, glCodePrincipal, glCodeInterest);
        } catch (ProductDefinitionException e) {
            throw new RuntimeException(e);
        }

        PrdStatusEntity prdStatus = testObjectPersistence.retrievePrdStatus(offeringStatus);
        LoanOfferingTestUtils.setStatus(loanOffering, prdStatus);
        LoanOfferingTestUtils.setGracePeriodType(loanOffering, gracePeriodType);
        return (LoanOfferingBO) addObject(testObjectPersistence.persist(loanOffering));
    }

    public static LoanOfferingBO createLoanOffering(final String name, final String shortName, final ApplicableTo applicableTo,
            final Date startDate, final PrdStatus offeringStatus, final Double defLnAmnt, final Double defIntRate, final Short defInstallments,
            final InterestType interestType, final boolean interestDeductedAtDisbursement, final boolean principalDueInLastInstallment,
            final MeetingBO meeting, final GraceType graceType, final String loanAmountCalcType, final String noOfInstallCalcType) {
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(applicableTo);
        ProductCategoryBO productCategory = TestObjectFactory.getLoanPrdCategory();
        GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(graceType);
        InterestTypesEntity interestTypes = new InterestTypesEntity(interestType);
        GLCodeEntity glCodePrincipal = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                TestGeneralLedgerCode.LOANS_TO_CLIENTS);

        GLCodeEntity glCodeInterest = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                TestGeneralLedgerCode.INTEREST_ON_LOANS);
        LoanOfferingBO loanOffering;
        try {
            loanOffering = new LoanOfferingBO(getContext(), name, shortName, productCategory, prdApplicableMaster,
                    startDate, null, null, gracePeriodType, (short) 0, interestTypes, new Money(
                            TestUtils.getCurrency(), defLnAmnt.toString()), new Money(TestUtils.getCurrency(),
                            defLnAmnt.toString()), new Money(TestUtils.getCurrency(), defLnAmnt.toString()),
                    defIntRate, defIntRate, defIntRate, defInstallments, defInstallments, defInstallments, true,
                    interestDeductedAtDisbursement, principalDueInLastInstallment, new ArrayList<FundBO>(),
                    new ArrayList<FeeBO>(), meeting, glCodePrincipal, glCodeInterest, loanAmountCalcType,
                    noOfInstallCalcType);
        } catch (ProductDefinitionException e) {
            throw new RuntimeException(e);
        }

        PrdStatusEntity prdStatus = testObjectPersistence.retrievePrdStatus(offeringStatus);
        LoanOfferingTestUtils.setStatus(loanOffering, prdStatus);
        LoanOfferingTestUtils.setGracePeriodType(loanOffering, gracePeriodType);
        return (LoanOfferingBO) addObject(testObjectPersistence.persist(loanOffering));
    }

    public static LoanOfferingBO createLoanOfferingFromLastLoan(final String name, final String shortName,
            final ApplicableTo applicableTo, final Date startDate, final PrdStatus offeringStatus, final Double defIntRate,
            final InterestType interestType, final boolean interestDeductedAtDisbursement, final boolean principalDueInLastInstallment,
            final MeetingBO meeting, final GraceType graceType, final LoanPrdActionForm loanPrdActionForm) {
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(applicableTo);
        ProductCategoryBO productCategory = TestObjectFactory.getLoanPrdCategory();
        GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(graceType);
        InterestTypesEntity interestTypes = new InterestTypesEntity(interestType);
        GLCodeEntity glCodePrincipal = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                Short.valueOf("11"));

        GLCodeEntity glCodeInterest = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                Short.valueOf("21"));
        LoanOfferingBO loanOffering;
        try {
            loanOffering = new LoanOfferingBO(getContext(), name, shortName, productCategory, prdApplicableMaster,
                    startDate, null, null, gracePeriodType, (short) 0, interestTypes, defIntRate, defIntRate,
                    defIntRate, true, interestDeductedAtDisbursement, principalDueInLastInstallment,
                    new ArrayList<FundBO>(), new ArrayList<FeeBO>(), meeting, glCodePrincipal, glCodeInterest,
                    loanPrdActionForm);
        } catch (ProductDefinitionException e) {
            throw new RuntimeException(e);
        }

        PrdStatusEntity prdStatus = testObjectPersistence.retrievePrdStatus(offeringStatus);
        LoanOfferingTestUtils.setStatus(loanOffering, prdStatus);
        LoanOfferingTestUtils.setGracePeriodType(loanOffering, gracePeriodType);
        return (LoanOfferingBO) addObject(testObjectPersistence.persist(loanOffering));
    }

    public static LoanOfferingBO createCompleteLoanOfferingObject() throws Exception {
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.GROUPS);
        MeetingBO frequency = TestObjectFactory.createMeeting(getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        GLCodeEntity principalglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                TestGeneralLedgerCode.BANK_ACCOUNT_ONE);
        GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                TestGeneralLedgerCode.BANK_ACCOUNT_ONE);
        ProductCategoryBO productCategory = getLoanPrdCategory();
        InterestTypesEntity interestTypes = new InterestTypesEntity(InterestType.FLAT);
        GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(GraceType.GRACEONALLREPAYMENTS);
        List<FeeBO> fees = new ArrayList<FeeBO>();
        List<FundBO> funds = new ArrayList<FundBO>();
        FundBO fundBO = (FundBO) StaticHibernateUtil.getSessionTL().get(FundBO.class, Short.valueOf("2"));
        funds.add(fundBO);
        LoanOfferingBO loanOfferingBO = new LoanOfferingBO(getContext(), "Loan Offering", "LOAP", productCategory,
                prdApplicableMaster, DateUtils.getCurrentDateWithoutTimeStamp(), null, null, gracePeriodType,
                (short) 2, interestTypes, new Money(TestUtils.getCurrency(), "1000"), new Money(
                        TestUtils.getCurrency(), "3000"), new Money(TestUtils.getCurrency(), "2000.0"), 12.0, 2.0, 3.0,
                (short) 20, (short) 11, (short) 17, false, false, false, funds, fees, frequency, principalglCodeEntity,
                intglCodeEntity);
        loanOfferingBO.save();
        return loanOfferingBO;
    }

    public static ProductCategoryBO getLoanPrdCategory() {
        return (ProductCategoryBO) addObject(testObjectPersistence.getLoanPrdCategory());
    }

    public static LoanBO createLoanAccount(final String globalNum, final CustomerBO customer, final AccountState state, final Date startDate,
            final LoanOfferingBO offering) {
        LoanBO loan = LoanBOTestUtils.createLoanAccount(globalNum, customer, state, startDate, offering);
        try {
            loan.save();
        } catch (AccountException e) {
            throw new RuntimeException(e);
        }
        StaticHibernateUtil.commitTransaction();
        return (LoanBO) addObject(getObject(LoanBO.class, loan.getAccountId()));
    }

    public static LoanBO createBasicLoanAccount(final CustomerBO customer, final AccountState state, final Date startDate,
            final LoanOfferingBO offering) {
        LoanBO loan = LoanBOTestUtils.createBasicLoanAccount(customer, state, startDate, offering);
        try {
            loan.save();
        } catch (AccountException e) {
            throw new RuntimeException(e);
        }
        StaticHibernateUtil.commitTransaction();
        return (LoanBO) addObject(getObject(LoanBO.class, loan.getAccountId()));
    }

    public static LoanBO createIndividualLoanAccount(final String globalNum, final CustomerBO customer, final AccountState state,
            final Date startDate, final LoanOfferingBO offering) {
        LoanBO loan = LoanBOTestUtils
                .createIndividualLoanAccount(globalNum, customer, state, startDate, offering);
        try {
            loan.save();
        } catch (AccountException e) {
            throw new RuntimeException(e);
        }
        StaticHibernateUtil.commitTransaction();
        return (LoanBO) addObject(getObject(LoanBO.class, loan.getAccountId()));
    }

    public static SavingsOfferingBO createSavingsProduct(final String name, final ApplicableTo applicableTo, final Date startDate,
            final PrdStatus status, final Double recommendedAmount, final RecommendedAmountUnit unit, final Double intRate,
            final Double maxAmtWithdrawl, final Double minAmtForInt, final SavingsType savingsType, final InterestCalcType interestCalculation,
            final MeetingBO intCalcMeeting, final MeetingBO intPostMeeting) {
        return createSavingsProduct(name, name.substring(0, 1), applicableTo, startDate, status, recommendedAmount,
                unit, intRate, maxAmtWithdrawl, minAmtForInt, savingsType, interestCalculation, intCalcMeeting,
                intPostMeeting);
    }

    public static SavingsOfferingBO createSavingsProduct(final String name, final String shortName, final ApplicableTo applicableTo,
            final Date startDate, final PrdStatus status, final Double recommendedAmount, final RecommendedAmountUnit unit, final Double intRate,
            final Double maxAmtWithdrawl, final Double minAmtForInt, final SavingsType savingsType, final InterestCalcType interestCalculation,
            final MeetingBO intCalcMeeting, final MeetingBO intPostMeeting) {
        return createSavingsProduct(name, shortName, applicableTo, startDate, status, recommendedAmount, unit, intRate,
                maxAmtWithdrawl, minAmtForInt, savingsType, interestCalculation, intCalcMeeting, intPostMeeting,
                TestGeneralLedgerCode.BANK_ACCOUNT_ONE, TestGeneralLedgerCode.BANK_ACCOUNT_ONE);
    }

    public static SavingsOfferingBO createSavingsProduct(final String name, final String shortName, final ApplicableTo applicableTo,
            final Date startDate, final PrdStatus status, final Double recommendedAmount, final RecommendedAmountUnit recommendedAmountUnit,
            final Double intRate, final Double maxAmtWithdrawl, final Double minAmtForInt, final SavingsType savingsType,
            final InterestCalcType interestCalculationType, final MeetingBO intCalcMeeting, final MeetingBO intPostMeeting,
            final Short depGLCode, final Short withGLCode) {
        GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                depGLCode);
        GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                withGLCode);
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                SAMPLE_CATEGORY);

        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(applicableTo);
        SavingsTypeEntity savingsTypeEntity = new SavingsTypeEntity(savingsType);
        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(interestCalculationType);
        RecommendedAmntUnitEntity amountUnit = new RecommendedAmntUnitEntity(recommendedAmountUnit);
        SavingsOfferingBO product;
        try {
            product = new SavingsOfferingBO(TestUtils.makeUserWithLocales(), name, shortName, productCategory,
                    prdApplicableMaster, startDate, null, null, amountUnit, savingsTypeEntity, intCalType,
                    intCalcMeeting, intPostMeeting, new Money(TestUtils.getCurrency(), recommendedAmount.toString()), new Money(TestUtils.getCurrency(), maxAmtWithdrawl
                            .toString()), new Money(TestUtils.getCurrency(), minAmtForInt.toString()), intRate, depglCodeEntity, intglCodeEntity);
        } catch (ProductDefinitionException e) {
            throw new RuntimeException(e);
        } catch (SystemException e) {
            throw new RuntimeException(e);
        }

        PrdStatusEntity prdStatus = testObjectPersistence.retrievePrdStatus(status);
        product.setPrdStatus(prdStatus);
        return (SavingsOfferingBO) addObject(testObjectPersistence.persist(product));
    }

    public static SavingsOfferingBO createSavingsProduct(final String offeringName, final String shortName,
            final SavingsType savingsType, final ApplicableTo applicableTo, final Date currentDate) {
        MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return createSavingsProduct(offeringName, shortName, applicableTo, currentDate, PrdStatus.SAVINGS_ACTIVE,
                300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 24.0, 200.0, 200.0, savingsType,
                InterestCalcType.MINIMUM_BALANCE, meetingIntCalc, meetingIntPost);
    }

    public static SavingsBO createSavingsAccount(final String globalNum, final CustomerBO customer, final Short accountStateId,
            final Date startDate, final SavingsOfferingBO savingsOffering) throws Exception {
        UserContext userContext = TestUtils.makeUserWithLocales();
        MifosCurrency currency = testObjectPersistence.getCurrency();
        MeetingBO meeting = createLoanMeeting(customer.getCustomerMeeting().getMeeting());
        SavingsBO savings = new SavingsBO(userContext, savingsOffering, customer,
                AccountState.SAVINGS_PARTIAL_APPLICATION, new Money(currency, "300.0"), null);
        savings.save();
        savings.setUserContext(TestObjectFactory.getContext());
        savings.changeStatus(accountStateId, null, "");
        SavingBOTestUtils.setActivationDate(savings, new Date(System.currentTimeMillis()));
        List<Date> meetingDates = getMeetingDates(meeting, 3);
        short installment = 0;
        for (Date date : meetingDates) {
            SavingsScheduleEntity actionDate = new SavingsScheduleEntity(savings, customer, ++installment,
                    new java.sql.Date(date.getTime()), PaymentStatus.UNPAID, new Money(currency, "200.0"));
            AccountTestUtils.addAccountActionDate(actionDate, savings);
        }
        StaticHibernateUtil.commitTransaction();
        return (SavingsBO) addObject(getObject(SavingsBO.class, savings.getAccountId()));
    }

    private static List<CustomFieldView> getCustomFieldView() {
        List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();
        customFields.add(new CustomFieldView(new Short("8"), "custom field value", CustomFieldType.NONE));
        return customFields;
    }

    /**
     * Also see
     * {@link SavingsTestHelper#createSavingsAccount(SavingsOfferingBO, CustomerBO, AccountState, UserContext)}
     * which is less elaborate.
     */
    public static SavingsBO createSavingsAccount(final String globalNum, final CustomerBO customer, final AccountState state,
            final Date startDate, final SavingsOfferingBO savingsOffering, UserContext userContext) throws Exception {
        userContext = TestUtils.makeUserWithLocales();
        SavingsBO savings = new SavingsBO(userContext, savingsOffering, customer, state, savingsOffering
                .getRecommendedAmount(), getCustomFieldView());
        savings.save();
        SavingBOTestUtils.setActivationDate(savings, new Date(System.currentTimeMillis()));
        StaticHibernateUtil.commitTransaction();
        return (SavingsBO) addObject(getObject(SavingsBO.class, savings.getAccountId()));
    }

    public static MeetingBO createLoanMeeting(final MeetingBO customerMeeting) {
        MeetingBO meetingToReturn = null;
        try {
            RecurrenceType recurrenceType = RecurrenceType.fromInt(customerMeeting.getMeetingDetails()
                    .getRecurrenceType().getRecurrenceId());
            MeetingType meetingType = MeetingType.fromInt(customerMeeting.getMeetingType().getMeetingTypeId());
            Short recurAfter = customerMeeting.getMeetingDetails().getRecurAfter();

            if (recurrenceType.equals(RecurrenceType.MONTHLY)) {
                if (customerMeeting.isMonthlyOnDate()) {
                    meetingToReturn = new MeetingBO(customerMeeting.getMeetingDetails().getMeetingRecurrence()
                            .getDayNumber(), recurAfter, customerMeeting.getMeetingStartDate(), meetingType,
                            "meetingPlace");
                } else {
                    meetingToReturn = new MeetingBO(customerMeeting.getMeetingDetails().getWeekDay(), customerMeeting
                            .getMeetingDetails().getWeekRank(), recurAfter, customerMeeting.getMeetingStartDate(),
                            meetingType, "meetingPlace");
                }
            } else if (recurrenceType.equals(RecurrenceType.WEEKLY)) {
                meetingToReturn = new MeetingBO(WeekDay.getWeekDay(customerMeeting.getMeetingDetails()
                        .getMeetingRecurrence().getWeekDayValue().getValue()), recurAfter, customerMeeting
                        .getMeetingStartDate(), meetingType, "meetingPlace");
            } else {
                meetingToReturn = new MeetingBO(recurrenceType, recurAfter, customerMeeting.getMeetingStartDate(),
                        meetingType);
            }

            meetingToReturn.setMeetingPlace(customerMeeting.getMeetingPlace());
        } catch (MeetingException e) {
            throw new RuntimeException(e);
        }
        return meetingToReturn;
    }

    public static List<Date> getMeetingDates(final MeetingBO meeting, final int occurrences) {
        List<Date> dates = new ArrayList<Date>();
        try {
            dates = meeting.getAllDates(occurrences);
        } catch (MeetingException e) {
            throw new RuntimeException(e);
        }
        return dates;
    }

    public static List<Date> getAllMeetingDates(final MeetingBO meeting) {
        List<Date> dates = new ArrayList<Date>();
        try {
            dates = meeting.getAllDates(DateUtils.getLastDayOfNextYear());
        } catch (MeetingException e) {
            throw new RuntimeException(e);
        }
        return dates;
    }

    /**
     * createPeriodicAmountFee.
     * 
     * Changing {@link TestObjectFactory#getUserContext()} to
     * {@link TestUtils#makeUserWithLocales()} caused a failure in
     * {@link CustomerAccountBOIntegrationTest#testApplyPeriodicFee} (and about
     * 163 other tests).
     */
    public static FeeBO createPeriodicAmountFee(final String feeName, final FeeCategory feeCategory, final String feeAmnt,
            final RecurrenceType meetingFrequency, final Short recurAfter) {
        FeeBO fee;
        try {
            fee = createPeriodicAmountFee(feeName, feeCategory, feeAmnt, meetingFrequency, recurAfter,
                    TestObjectFactory.getUserContext());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return fee;
    }

    public static FeeBO createPeriodicAmountFeeWithMakeUser(final String feeName, final FeeCategory feeCategory, final String feeAmnt,
            final RecurrenceType meetingFrequency, final Short recurAfter) {
        FeeBO fee;
        try {
            fee = createPeriodicAmountFee(feeName, feeCategory, feeAmnt, meetingFrequency, recurAfter, TestUtils
                    .makeUserWithLocales());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return fee;
    }

    public static FeeBO createPeriodicAmountFee(final String feeName, final FeeCategory feeCategory, final String feeAmnt,
            final RecurrenceType meetingFrequency, final Short recurAfter, final UserContext userContext) {
        try {
            GLCodeEntity glCode = ChartOfAccountsCache.get("31301").getAssociatedGlcode();
            MeetingBO meeting = new MeetingBO(meetingFrequency, recurAfter, new Date(), MeetingType.PERIODIC_FEE);
            FeeBO fee = new AmountFeeBO(userContext, feeName, new CategoryTypeEntity(feeCategory),
                    new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC), glCode, getMoneyForMFICurrency(feeAmnt),
                    false, meeting);
            return (FeeBO) addObject(testObjectPersistence.createFee(fee));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /****************************
     * Begin creating a periodic rate fee
     ****************************/
    public static RateFeeBO createPeriodicRateFee(final String feeName, final FeeCategory feeCategory, final Double rate,
            final FeeFormula feeFormula, final RecurrenceType meetingFrequency, final Short recurAfter) {

        try {
            MeetingBO meeting = new MeetingBO(meetingFrequency, recurAfter, new Date(), MeetingType.PERIODIC_FEE);
            return createPeriodicRateFee(feeName, feeCategory, rate, feeFormula, meetingFrequency, recurAfter,
                    TestUtils.makeUserWithLocales(), meeting);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static RateFeeBO createPeriodicRateFee(final String feeName, final FeeCategory feeCategory, final Double rate,
            final FeeFormula feeFormula, final RecurrenceType meetingFrequency, final Short recurAfter, final UserContext userContext,
            final MeetingBO meeting) {

        try {
            MeetingBO newMeeting = new MeetingBO(meetingFrequency, recurAfter, new Date(), MeetingType.PERIODIC_FEE);
            // GLCodeEntity glCode =
            // ChartOfAccountsCache.get("31301").getAssociatedGlcode();
            GLCodeEntity glCode = new GLCodeEntity((short) 1, "31301");
            RateFeeBO fee = new RateFeeBO(userContext, feeName, new CategoryTypeEntity(feeCategory),
                    new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC), glCode, rate, new FeeFormulaEntity(
                            feeFormula), false, newMeeting);
            // [keith] I have no idea why the fee must save itself. Otherwise
            // mySQL errors crop up
            // when you try to attach the fee to a loan.
            fee.save();
            // addObject(fee);
            return fee;
            // return (RateFeeBO) addObject(testObjectPersistence.persist(fee));
            // return (RateFeeBO)
            // addObject(testObjectPersistence.createFee(fee));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /****************************
     * End creating a periodic rate fee
     ****************************/

    /**
     * createOneTimeAmountFee.
     * 
     * Changing {@link TestObjectFactory#getUserContext()} to
     * {@link TestUtils#makeUserWithLocales()} caused a failure in
     * {@link CustomerAccountBOIntegrationTest#testApplyUpfrontFee} (and other
     * tests).
     */
    public static FeeBO createOneTimeAmountFee(final String feeName, final FeeCategory feeCategory, final String feeAmnt,
            final FeePayment feePayment) {
        FeeBO fee;
        try {
            fee = createOneTimeAmountFee(feeName, feeCategory, feeAmnt, feePayment, getUserContext());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return fee;
    }

    public static FeeBO createOneTimeAmountFee(final String feeName, final FeeCategory feeCategory, final String feeAmnt,
            final FeePayment feePayment, final UserContext userContext) {
        GLCodeEntity glCode = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                TestGeneralLedgerCode.FEES);
        try {
            FeeBO fee = new AmountFeeBO(userContext, feeName, new CategoryTypeEntity(feeCategory),
                    new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME), glCode, getMoneyForMFICurrency(feeAmnt),
                    false, new FeePaymentEntity(feePayment));
            return (FeeBO) addObject(testObjectPersistence.createFee(fee));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * createOneTimeRateFee.
     * 
     * Changing {@link TestObjectFactory#getUserContext()} to
     * {@link TestUtils#makeUserWithLocales()} caused a failure in
     * {@link LoanBOIntegrationTest#testApplyUpfrontFee} (and other tests).
     */
    public static FeeBO createOneTimeRateFee(final String feeName, final FeeCategory feeCategory, final Double rate,
            final FeeFormula feeFormula, final FeePayment feePayment) {
        GLCodeEntity glCode = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                TestGeneralLedgerCode.FEES);
        FeeBO fee;
        try {
            fee = new RateFeeBO(getUserContext(), feeName, new CategoryTypeEntity(feeCategory),
                    new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME), glCode, rate,
                    new FeeFormulaEntity(feeFormula), false, new FeePaymentEntity(feePayment));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (FeeBO) addObject(testObjectPersistence.createFee(fee));
    }

    /**
     * Return a new meeting object with a meeting occurring on the day of the
     * week that the test is run. Creating a new method to fix issues with
     * meeting creation without breaking existing tests that may depend on it.
     * 
     * 
     * @param recurAfter
     *            1 means every day/week/month, 2 means every second
     *            day/week/month...
     * 
     */
    public static MeetingBO getNewWeeklyMeeting(final short recurAfter) {
        Calendar calendar = new GregorianCalendar();
        MeetingBO meeting;
        try {
            meeting = new MeetingBO(WeekDay.getWeekDay((short) calendar.get(Calendar.DAY_OF_WEEK)), recurAfter,
                    new Date(), CUSTOMER_MEETING, "meetingPlace");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return meeting;
    }

    /**
     * Return a new meeting object.
     * 
     * @param frequency
     *            DAILY, WEEKLY, MONTHLY
     * @param recurAfter
     *            1 means every day/week/month, 2 means every second
     *            day/week/month...
     * @param meetingType
     *            most commonly a CUSTOMER_MEETING
     * @param weekday
     *            MONDAY, TUESDAY...
     */
    public static MeetingBO getNewMeeting(final RecurrenceType frequency, final short recurAfter, final MeetingType meetingType,
            final WeekDay weekday) {
        MeetingBO meeting;
        try {
            meeting = new MeetingBO(frequency, recurAfter, new Date(), meetingType);
            meeting.setMeetingPlace("Loan Meeting Place");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        WeekDaysEntity weekDays = new WeekDaysEntity(weekday);
        meeting.getMeetingDetails().getMeetingRecurrence().setWeekDay(weekDays);
        return meeting;
    }

    /**
     * Return a new meeting object which occurs on today's day of the week.
     * 
     * Not recommended: New tests should call
     * {@link #getNewMeeting(RecurrenceType, short, MeetingType, WeekDay)}
     * instead to avoid bugs where the test will pass on one day but not
     * another.
     * 
     * @param frequency
     *            DAILY, WEEKLY, MONTHLY
     * @param recurAfter
     *            1 means every day/week/month, 2 means every second
     *            day/week/month...
     * @param meetingType
     *            most commonly a CUSTOMER_MEETING
     */
    public static MeetingBO getNewMeetingForToday(final RecurrenceType frequency, final short recurAfter, final MeetingType meetingType) {
        Calendar calendar = new GregorianCalendar();
        return getNewMeeting(frequency, recurAfter, meetingType, WeekDay.getWeekDay((short) calendar
                .get(Calendar.DAY_OF_WEEK)));
    }

    /**
     * Return a new meeting object that represents a weekly customer meeting
     * occurring every Monday. This is the most commonly used meeting type in
     * the unit tests.
     */
    public static MeetingBO getTypicalMeeting() {
        return getNewMeeting(RecurrenceType.WEEKLY, EVERY_WEEK, MeetingType.CUSTOMER_MEETING, WeekDay.MONDAY);
    }

    /**
     * Persist a meeting object.
     */
    public static MeetingBO createMeeting(final MeetingBO meeting) {
        return (MeetingBO) addObject(testObjectPersistence.persist(meeting));
    }

    public static void cleanUp(CustomerBO customer) {
        if (null != customer) {
            deleteCustomer(customer);
            customer = null;
        }
    }

    public static void cleanUp(List<CustomerBO> customerList) {
        if (null != customerList) {
            deleteCustomers(customerList);
            customerList = null;
        }
    }

    public static void cleanUp(FeeBO fee) {
        if (null != fee) {
            deleteFee(fee);
            fee = null;
        }
    }

    public static void cleanUp(AccountBO account) {
        if (null != account) {
            deleteAccount(account, null);
            account = null;
        }
    }

    public static void cleanUpAccount(final Integer accountId) {
        if (null != accountId) {
            Session session = StaticHibernateUtil.openSession();
            Transaction transaction = session.beginTransaction();
            AccountBO account = (AccountBO) session.get(AccountBO.class, accountId);
            deleteAccount(account, session);
            transaction.commit();
            session.close();
        }
    }

    private static void deleteFee(final FeeBO fee) {
        Session session = StaticHibernateUtil.getSessionTL();
        Transaction transaction = StaticHibernateUtil.startTransaction();
        if (fee.isPeriodic()) {
            session.delete(fee.getFeeFrequency().getFeeMeetingFrequency());
        }
        session.delete(fee);
        transaction.commit();
    }

    private static void deleteFees(final List<FeeBO> feeList) {
        Session session = StaticHibernateUtil.getSessionTL();
        for (FeeBO fee : feeList) {
            if (fee.isPeriodic()) {
                session.delete(fee.getFeeFrequency().getFeeMeetingFrequency());
            }
            session.delete(fee);
        }
    }

    private static void deleteAccountPayments(final AccountBO account) {
        Session session = StaticHibernateUtil.getSessionTL();
        for (AccountPaymentEntity accountPayment : account.getAccountPayments()) {
            if (null != accountPayment) {
                deleteAccountPayment(accountPayment, session);
            }
        }
    }

    private static void deleteAccountActionDates(final AccountBO account, final Session session) {
        AccountTypes accountType = account.getType();
        for (AccountActionDateEntity actionDates : account.getAccountActionDates()) {
            // TODO: this will never be true. Do we want to fix it or nuke it?
            if (accountType.getValue().equals(org.mifos.application.accounts.util.helpers.AccountTypes.LOAN_ACCOUNT)) {
                LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) actionDates;
                for (AccountFeesActionDetailEntity actionFees : loanScheduleEntity.getAccountFeesActionDetails()) {
                    session.delete(actionFees);
                }
            }
            // TODO: this will never be true. Do we want to fix it or nuke it?
            if (accountType.getValue()
                    .equals(org.mifos.application.accounts.util.helpers.AccountTypes.CUSTOMER_ACCOUNT)) {
                CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) actionDates;
                for (AccountFeesActionDetailEntity actionFees : customerScheduleEntity.getAccountFeesActionDetails()) {
                    session.delete(actionFees);
                }
            }
            session.delete(actionDates);
        }
    }

    private static void deleteAccountFees(final AccountBO account) {
        Session session = StaticHibernateUtil.getSessionTL();
        for (AccountFeesEntity accountFees : account.getAccountFees()) {
            session.delete(accountFees);
        }
    }

    private static void deleteSpecificAccount(final AccountBO account, final Session session) {
        if (account instanceof LoanBO) {

            LoanBO loan = (LoanBO) account;
            if (null != loan.getLoanSummary()) {
                session.delete(loan.getLoanSummary());
            }
            session.delete(account);
            loan.getLoanOffering().getLoanOfferingMeeting().setMeeting(null);
            session.delete(loan.getLoanOffering().getLoanOfferingMeeting());
            session.delete(loan.getLoanOffering());

        }
        if (account instanceof SavingsBO) {

            SavingsBO savings = (SavingsBO) account;
            session.delete(account);
            session.delete(savings.getTimePerForInstcalc());
            try {
                PrdOfferingMeetingEntity prdOfferingMeeting1 = savings.getSavingsOffering().getTimePerForInstcalc();
                prdOfferingMeeting1.setMeeting(null);
                session.delete(prdOfferingMeeting1);
            } catch (ProductDefinitionException e) {
                throw new RuntimeException(e);
            }
            try {
                PrdOfferingMeetingEntity prdOfferingMeeting2 = savings.getSavingsOffering().getFreqOfPostIntcalc();
                prdOfferingMeeting2.setMeeting(null);
                session.delete(prdOfferingMeeting2);
            } catch (ProductDefinitionException e) {
                throw new RuntimeException(e);
            }
            session.delete(savings.getSavingsOffering());
        } else {
            session.delete(account);
        }
    }

    private static void deleteAccountWithoutFee(final AccountBO account) {
        Session session = StaticHibernateUtil.getSessionTL();

        deleteAccountPayments(account);
        deleteAccountActionDates(account, session);
        deleteAccountFees(account);
        deleteSpecificAccount(account, session);
    }

    private static void deleteAccount(final AccountBO account, Session session) {
        boolean newSession = false;
        Transaction transaction = null;
        if (null == session) {
            session = StaticHibernateUtil.getSessionTL();
            transaction = StaticHibernateUtil.startTransaction();
            newSession = true;
        }

        List<FeeBO> feeList = new ArrayList<FeeBO>();
        for (AccountFeesEntity accountFees : account.getAccountFees()) {
            if (!feeList.contains(accountFees.getFees())) {
                feeList.add(accountFees.getFees());
            }
        }

        deleteAccountPayments(account);
        deleteAccountActionDates(account, session);
        deleteAccountFees(account);
        deleteSpecificAccount(account, session);
        deleteFees(feeList);

        if (newSession) {
            transaction.commit();
        }
    }

    private static void deleteAccountPayment(final AccountPaymentEntity accountPayment, final Session session) {
        Set<AccountTrxnEntity> loanTrxns = accountPayment.getAccountTrxns();
        for (AccountTrxnEntity accountTrxn : loanTrxns) {
            if (accountTrxn instanceof LoanTrxnDetailEntity) {
                LoanTrxnDetailEntity loanTrxn = (LoanTrxnDetailEntity) accountTrxn;
                for (FeesTrxnDetailEntity feesTrxn : loanTrxn.getFeesTrxnDetails()) {
                    session.delete(feesTrxn);
                }
                for (FinancialTransactionBO financialTrxn : loanTrxn.getFinancialTransactions()) {
                    session.delete(financialTrxn);
                }

                session.delete(loanTrxn);

            }
        }

        session.delete(accountPayment);
    }

    private static void deleteCustomers(final List<CustomerBO> customerList) {
        List<FeeBO> feeList = new ArrayList<FeeBO>();
        for (CustomerBO customer : customerList) {
            Session session = StaticHibernateUtil.getSessionTL();
            session.lock(customer, LockMode.UPGRADE);
            for (AccountBO account : customer.getAccounts()) {
                for (AccountFeesEntity accountFees : account.getAccountFees()) {
                    if (!feeList.contains(accountFees.getFees())) {
                        feeList.add(accountFees.getFees());
                    }
                }
            }
            Transaction transaction = StaticHibernateUtil.startTransaction();
            deleteCustomerWithoutFee(customer);
            transaction.commit();
        }
        Transaction transaction = StaticHibernateUtil.startTransaction();
        deleteFees(feeList);
        transaction.commit();
    }

    private static void deleteCustomerWithoutFee(final CustomerBO customer) {
        Session session = StaticHibernateUtil.getSessionTL();
        deleteCenterMeeting(customer);
        deleteClientAttendence(customer);
        for (AccountBO account : customer.getAccounts()) {
            if (null != account) {
                deleteAccountWithoutFee(account);
            }
        }
        session.delete(customer);
    }

    private static void deleteCustomer(CustomerBO customer) {
        Session session = StaticHibernateUtil.getSessionTL();
        StaticHibernateUtil.startTransaction();
        customer = (CustomerBO) session.load(CustomerBO.class, customer.getCustomerId());
        session.lock(customer, LockMode.NONE);
        deleteCenterMeeting(customer);
        deleteClientAttendence(customer);
        deleteCustomerNotes(customer);
        deleteClientOfferings(customer);

        List<FeeBO> feeList = new ArrayList<FeeBO>();
        for (AccountBO account : customer.getAccounts()) {
            if (null != account) {
                for (AccountFeesEntity accountFee : account.getAccountFees()) {
                    if (!feeList.contains(accountFee.getFees())) {
                        feeList.add(accountFee.getFees());
                    }
                }
                deleteAccountWithoutFee(account);
            }
        }
        session.delete(customer);
        deleteFees(feeList);
        StaticHibernateUtil.commitTransaction();
    }

    private static void deleteClientOfferings(final CustomerBO customer) {
        Session session = StaticHibernateUtil.getSessionTL();
        if (customer instanceof ClientBO) {
            Set<ClientInitialSavingsOfferingEntity> clientOfferings = ((ClientBO) customer)
                    .getOfferingsAssociatedInCreate();
            if (clientOfferings != null) {
                for (ClientInitialSavingsOfferingEntity clientOffering : clientOfferings) {
                    session.delete(clientOffering);
                }
            }
        }
    }

    private static void deleteCustomerNotes(final CustomerBO customer) {
        Session session = StaticHibernateUtil.getSessionTL();
        Set<CustomerNoteEntity> customerNotes = customer.getCustomerNotes();
        if (customerNotes != null) {
            for (CustomerNoteEntity customerNote : customerNotes) {
                session.delete(customerNote);
            }
        }

    }

    private static void deleteCenterMeeting(final CustomerBO customer) {
        Session session = StaticHibernateUtil.getSessionTL();
        if (customer instanceof CenterBO) {
            // session.delete(customer.getCustomerMeeting());
        }
    }

    public static void deleteClientAttendence(final CustomerBO customer) {
        Session session = StaticHibernateUtil.getSessionTL();
        if (customer instanceof ClientBO) {
            Set<ClientAttendanceBO> attendance = ((ClientBO) customer).getClientAttendances();
            if (attendance != null && attendance.size() > 0) {
                for (ClientAttendanceBO custAttendance : attendance) {
                    // custAttendance.setCustomer(null);
                    session.delete(custAttendance);
                }
            }
        }
    }

    // FIXME: rename to getRupees()?
    public static MifosCurrency getMFICurrency() {
        return testObjectPersistence.getCurrency();
    }

    // FIXME: rename to getRupees()?
    public static MifosCurrency getCurrency() {
        return testObjectPersistence.getCurrency();
    }

    /**
     * Convenience method where the amount is in MFI currency, and is an
     * integer.
     */
    public static Money getMoneyForMFICurrency(final int amount) {
        return new Money(TestUtils.getCurrency(), String.valueOf(amount));
    }

    public static Money getMoneyForMFICurrency(final String amount) {
        return new Money(testObjectPersistence.getCurrency(), amount);
    }

    public static void updateObject(final PersistentObject obj) {
        testObjectPersistence.update(obj);
    }

    private static UserContext userContext;

    public static UserContext getContext() {
        try {
            if (userContext == null) {
                userContext = getUserContext();
            }
            return userContext;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ActivityContext activityContext;

    public static ActivityContext getActivityContext() {
        if (activityContext == null) {
            UserContext uc = getContext();
            activityContext = new ActivityContext((short) 0, uc.getBranchId().shortValue(), uc.getId().shortValue());
        }
        return activityContext;
    }

    // FIXME: why is this here?
    private static final ThreadLocal<TestObjectsHolder> threadLocal = new ThreadLocal<TestObjectsHolder>();

    public static Object addObject(final Object obj) {
        TestObjectsHolder holder = threadLocal.get();
        if (holder == null) {
            holder = new TestObjectsHolder();
            threadLocal.set(holder);
        }
        holder.addObject(obj);

        return obj;
    }

    public static void cleanUpTestObjects() {
        TestObjectsHolder holder = threadLocal.get();
        if (holder != null) {
            holder.removeObjects();
        }

        holder = null;
        threadLocal.set(null);

    }

    /**
     * Also see {@link TestUtils#makeUser()} which should be faster (this method
     * involves several database accesses).
     */
    private static UserContext getUserContext() throws SystemException, ApplicationException {
        byte[] password = EncryptionService.getInstance().createEncryptedPassword("mifos");
        PersonnelBO personnel = getPersonnel(PersonnelConstants.SYSTEM_USER);
        personnel.setEncryptedPassword(password);
        updateObject(personnel);
        return personnel.login("mifos");
    }

    public static void flushandCloseSession() {
        testObjectPersistence.flushandCloseSession();
    }

    public static CustomerBO getCustomer(final Integer customerId) {
        return testObjectPersistence.getCustomer(customerId);
    }

    public static GroupBO getGroup(final Integer groupId) {
        return testObjectPersistence.getGroup(groupId);
    }

    public static ClientBO getClient(final Integer clientId) {
        return testObjectPersistence.getClient(clientId);
    }

    public static CenterBO getCenter(final Integer centerId) {
        return testObjectPersistence.getCenter(centerId);
    }

    public static <T> T getObject(final Class<T> clazz, final Integer pk) {
        T object = testObjectPersistence.getObject(clazz, pk);
        addObject(object);
        return object;
    }

    public static Object getObject(final Class clazz, final Short pk) {
        return addObject(testObjectPersistence.getObject(clazz, pk));
    }

    public static Object getObject(final Class clazz, final HolidayPK pk) {
        return addObject(testObjectPersistence.getObject(clazz, pk));
    }

    public static void cleanUpHolidays(List<HolidayBO> holidayList) {
        if (null != holidayList) {
            deleteHolidays(holidayList);
            holidayList = null;
        }
    }

    public static CustomerCheckListBO createCustomerChecklist(final Short customerLevel, final Short customerStatus,
            final Short checklistStatus) throws Exception {
        List<String> details = new ArrayList<String>();
        details.add("item1");
        CustomerLevelEntity customerLevelEntity = new CustomerLevelEntity(CustomerLevel.getLevel(customerLevel));
        CustomerStatusEntity customerStatusEntity = new CustomerStatusEntity(CustomerStatus.fromInt(customerStatus));
        CustomerCheckListBO customerChecklist = new CustomerCheckListBO(customerLevelEntity, customerStatusEntity,
                "productchecklist", checklistStatus, details, TEST_LOCALE, PersonnelConstants.SYSTEM_USER);
        customerChecklist.save();
        StaticHibernateUtil.commitTransaction();
        return customerChecklist;
    }

    public static AccountCheckListBO createAccountChecklist(final Short prdTypeId, final AccountState accountState,
            final Short checklistStatus) throws Exception {
        List<String> details = new ArrayList<String>();
        details.add("item1");
        ProductTypeEntity productTypeEntity = (ProductTypeEntity) StaticHibernateUtil.getSessionTL().get(
                ProductTypeEntity.class, prdTypeId);
        AccountStateEntity accountStateEntity = new AccountStateEntity(accountState);
        AccountCheckListBO accountChecklist = new AccountCheckListBO(productTypeEntity, accountStateEntity,
                "productchecklist", checklistStatus, details, TEST_LOCALE, PersonnelConstants.SYSTEM_USER);
        accountChecklist.save();
        StaticHibernateUtil.commitTransaction();
        return accountChecklist;
    }

    public static void cleanUp(CheckListBO checkListBO) {
        if (null != checkListBO) {
            deleteChecklist(checkListBO);
            checkListBO = null;
        }
    }

    public static void deleteChecklist(final CheckListBO checkListBO) {
        Session session = StaticHibernateUtil.getSessionTL();
        Transaction transaction = StaticHibernateUtil.startTransaction();

        if (checkListBO.getChecklistDetails() != null) {
            for (CheckListDetailEntity checklistDetail : checkListBO.getChecklistDetails()) {
                if (null != checklistDetail) {
                    session.delete(checklistDetail);
                }
            }
        }
        session.delete(checkListBO);
        transaction.commit();

    }

    public static HolidayBO createHoliday(final HolidayPK holidayPK, final Date holidayThruDate, final String holidayName,
            final Short repaymentRuleId, final Short localeId) throws Exception {

        RepaymentRuleEntity entity = new HolidayPersistence().getRepaymentRule(repaymentRuleId);
        HolidayBO accountHoliday = new HolidayBO(holidayPK, holidayThruDate, holidayName, entity);

        accountHoliday.save();

        StaticHibernateUtil.commitTransaction();
        return accountHoliday;
    }

    public static void cleanUp(HolidayBO holidayBO) {
        if (null != holidayBO) {
            deleteHoliday(holidayBO);
            holidayBO = null;
        }
    }

    public static void deleteHoliday(final HolidayBO holidayBO) {
        Session session = StaticHibernateUtil.getSessionTL();
        session.lock(holidayBO, LockMode.UPGRADE);
        Transaction transaction = StaticHibernateUtil.startTransaction();
        session.delete(holidayBO);
        transaction.commit();
    }

    private static void deleteHolidays(List<HolidayBO> holidayList) {
        for (HolidayBO holidayBO : holidayList) {
            deleteHoliday(holidayBO);
        }
        holidayList = null;
    }

    public static void cleanUp(ReportsCategoryBO reportsCategoryBO) {
        if (null != reportsCategoryBO) {
            deleteReportCategory(reportsCategoryBO);
            reportsCategoryBO = null;
        }
    }

    public static void deleteReportCategory(final ReportsCategoryBO reportsCategoryBO) {

        Session session = StaticHibernateUtil.getSessionTL();
        Transaction transaction = StaticHibernateUtil.startTransaction();
        session.delete(reportsCategoryBO);
        transaction.commit();
    }

    public static void cleanUp(ReportsBO reportsBO) {
        if (null != reportsBO) {
            deleteReportCategory(reportsBO);
            reportsBO = null;
        }
    }

    public static void deleteReportCategory(final ReportsBO reportsBO) {

        Session session = StaticHibernateUtil.getSessionTL();
        Transaction transaction = StaticHibernateUtil.startTransaction();
        session.delete(reportsBO);
        transaction.commit();
    }

    public static LoanBO createLoanAccountWithDisbursement(final String globalNum, final CustomerBO customer, final AccountState state,
            final Date startDate, final LoanOfferingBO loanOfering, final int disbursalType) {

        final LoanBO loan = LoanBOTestUtils.createLoanAccountWithDisbursement(customer, state, startDate,
                loanOfering, disbursalType, Short.valueOf("6"));
        
        try {
            loan.save();
        } catch (AccountException e) {
            throw new RuntimeException(e);
        }
        
        StaticHibernateUtil.commitTransaction();
        return (LoanBO) addObject(getObject(LoanBO.class, loan.getAccountId()));
    }

    private static void deleteAccountWithoutDeletetingProduct(final AccountBO account, Session session) {
        boolean newSession = false;

        Transaction transaction = null;
        if (null == session) {
            session = StaticHibernateUtil.getSessionTL();
            transaction = StaticHibernateUtil.startTransaction();
            newSession = true;
        }
        for (AccountPaymentEntity accountPayment : account.getAccountPayments()) {
            if (null != accountPayment) {
                deleteAccountPayment(accountPayment, session);
            }
        }
        for (AccountActionDateEntity actionDates : account.getAccountActionDates()) {
            if (account.getAccountType().getAccountTypeId().equals(
                    org.mifos.application.accounts.util.helpers.AccountTypes.LOAN_ACCOUNT)) {
                LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) actionDates;
                for (AccountFeesActionDetailEntity actionFees : loanScheduleEntity.getAccountFeesActionDetails()) {
                    session.delete(actionFees);
                }
            }
            if (account.getAccountType().getAccountTypeId().equals(
                    org.mifos.application.accounts.util.helpers.AccountTypes.CUSTOMER_ACCOUNT)) {
                CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) actionDates;
                for (AccountFeesActionDetailEntity actionFees : customerScheduleEntity.getAccountFeesActionDetails()) {
                    session.delete(actionFees);
                }
            }
            session.delete(actionDates);
        }

        List<FeeBO> feeList = new ArrayList<FeeBO>();
        for (AccountFeesEntity accountFees : account.getAccountFees()) {
            if (!feeList.contains(accountFees.getFees())) {
                feeList.add(accountFees.getFees());
            }
        }

        for (AccountFeesEntity accountFees : account.getAccountFees()) {
            session.delete(accountFees);
        }

        deleteFees(feeList);
        if (account instanceof LoanBO) {

            LoanBO loan = (LoanBO) account;
            if (null != loan.getLoanSummary()) {
                session.delete(loan.getLoanSummary());
            }
            session.delete(account);

        }
        session.delete(account);

        if (newSession) {
            transaction.commit();
        }
    }

    public static void cleanUpWithoutDeletingProduct(AccountBO account) {
        if (null != account) {
            deleteAccountWithoutDeletetingProduct(account, null);
            account = null;
        }
    }

    public static PaymentData getCustomerAccountPaymentDataView(final List<AccountActionDateEntity> accountActions,
            final Money totalAmount, final CustomerBO customer, final PersonnelBO personnel, final String receiptNum, final Short paymentId,
            final Date receiptDate, final Date transactionDate) {
        PaymentData paymentData = PaymentData.createPaymentData(totalAmount, personnel, paymentId, transactionDate);
        paymentData.setCustomer(customer);
        paymentData.setReceiptDate(receiptDate);
        paymentData.setReceiptNum(receiptNum);
        for (AccountActionDateEntity actionDate : accountActions) {
            CustomerAccountPaymentData customerAccountPaymentData = new CustomerAccountPaymentData(actionDate);
            paymentData.addAccountPaymentData(customerAccountPaymentData);
        }

        return paymentData;
    }

    public static CustomerAccountView getCustomerAccountView(final CustomerBO customer) throws Exception {
        CustomerAccountView customerAccountView = new CustomerAccountView(customer.getCustomerAccount().getAccountId(), getCurrency());
        List<AccountActionDateEntity> accountAction = getDueActionDatesForAccount(customer.getCustomerAccount()
                .getAccountId(), new java.sql.Date(System.currentTimeMillis()));
        customerAccountView.setAccountActionDates(getBulkEntryAccountActionViews(accountAction));
        return customerAccountView;
    }

    public static List<AccountActionDateEntity> getDueActionDatesForAccount(final Integer accountId,
            final java.sql.Date transactionDate) throws Exception {
        List<AccountActionDateEntity> dueActionDates = new CustomerPersistence().retrieveCustomerAccountActionDetails(
                accountId, transactionDate);
        for (AccountActionDateEntity accountActionDate : dueActionDates) {
            Hibernate.initialize(accountActionDate);

            if (accountActionDate instanceof LoanScheduleEntity) {
                LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDate;
                for (AccountFeesActionDetailEntity accountFeesActionDetail : loanScheduleEntity
                        .getAccountFeesActionDetails()) {
                    Hibernate.initialize(accountFeesActionDetail);
                }
            }
            if (accountActionDate instanceof CustomerScheduleEntity) {
                CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) accountActionDate;
                for (AccountFeesActionDetailEntity accountFeesActionDetail : customerScheduleEntity
                        .getAccountFeesActionDetails()) {
                    Hibernate.initialize(accountFeesActionDetail);
                }
            }
        }
        StaticHibernateUtil.closeSession();
        return dueActionDates;
    }

    public static void cleanUp(CollectionSheetBO collSheet) {
        if (null != collSheet) {
            deleteCollectionSheet(collSheet, null);
            collSheet = null;
        }
    }

    private static void deleteCollectionSheet(final CollectionSheetBO collSheet, Session session) {
        boolean newSession = false;

        Transaction transaction = null;
        if (null == session) {
            session = StaticHibernateUtil.getSessionTL();
            transaction = StaticHibernateUtil.startTransaction();
            newSession = true;
        }
        if (collSheet.getCollectionSheetCustomers() != null) {
            for (CollSheetCustBO collSheetCustomer : collSheet.getCollectionSheetCustomers()) {
                if (null != collSheetCustomer) {
                    deleteCollSheetCustomer(collSheetCustomer, session);
                }
            }
        }
        session.delete(collSheet);

        if (newSession) {
            transaction.commit();
        }
    }

    private static void deleteCollSheetCustomer(final CollSheetCustBO collSheetCustomer, final Session session) {

        if (collSheetCustomer.getCollectionSheetLoanDetails() != null) {
            for (CollSheetLnDetailsEntity collSheetLoanDetails : collSheetCustomer.getCollectionSheetLoanDetails()) {
                if (null != collSheetLoanDetails) {
                    session.delete(collSheetLoanDetails);
                }
            }
        }
        if (collSheetCustomer.getCollSheetSavingsDetails() != null) {
            for (CollSheetSavingsDetailsEntity collSheetSavingsDetails : collSheetCustomer.getCollSheetSavingsDetails()) {
                if (null != collSheetSavingsDetails) {
                    session.delete(collSheetSavingsDetails);
                }
            }
        }
        session.delete(collSheetCustomer);

    }

    public static PaymentData getLoanAccountPaymentData(final List<AccountActionDateEntity> accountActions,
            final Money totalAmount, final CustomerBO customer, final PersonnelBO personnel, final String receiptNum, final Short paymentId,
            final Date receiptDate, final Date transactionDate) {
        PaymentData paymentData = PaymentData.createPaymentData(totalAmount, personnel, paymentId, transactionDate);
        paymentData.setCustomer(customer);
        paymentData.setReceiptDate(receiptDate);
        paymentData.setReceiptNum(receiptNum);
        return paymentData;
    }

    public static LoanAccountView getLoanAccountView(final LoanBO loan) {
        final Integer customerId = null;
        return new LoanAccountView(loan.getAccountId(), customerId, loan.getLoanOffering().getPrdOfferingName(), loan
                .getLoanOffering().getPrdOfferingId(), loan.getState().getValue(), loan.getIntrestAtDisbursement(),
                loan.getLoanBalance());
    }

    public static CollectionSheetEntryInstallmentView getBulkEntryAccountActionView(
            final AccountActionDateEntity accountActionDateEntity) {
        CollectionSheetEntryInstallmentView bulkEntryAccountActionView = null;
        if (accountActionDateEntity instanceof LoanScheduleEntity) {
            LoanScheduleEntity actionDate = (LoanScheduleEntity) accountActionDateEntity;
            CollectionSheetEntryLoanInstallmentView installmentView = new CollectionSheetEntryLoanInstallmentView(
                    actionDate.getAccount().getAccountId(), actionDate.getCustomer().getCustomerId(), actionDate
                            .getInstallmentId(), actionDate.getActionDateId(), actionDate.getActionDate(), actionDate
                            .getPrincipal(), actionDate.getPrincipalPaid(), actionDate.getInterest(), actionDate
                            .getInterestPaid(), actionDate.getMiscFee(), actionDate.getMiscFeePaid(), actionDate
                            .getPenalty(), actionDate.getPenaltyPaid(), actionDate.getMiscPenalty(), actionDate
                            .getMiscPenaltyPaid(), getCurrency());
            installmentView
                    .setCollectionSheetEntryAccountFeeActions(getBulkEntryAccountFeeActionViews(accountActionDateEntity));
            bulkEntryAccountActionView = installmentView;
        } else if (accountActionDateEntity instanceof SavingsScheduleEntity) {
            SavingsScheduleEntity actionDate = (SavingsScheduleEntity) accountActionDateEntity;
            CollectionSheetEntrySavingsInstallmentView installmentView = new CollectionSheetEntrySavingsInstallmentView(
                    actionDate.getAccount().getAccountId(), actionDate.getCustomer().getCustomerId(), actionDate
                            .getInstallmentId(), actionDate.getActionDateId(), actionDate.getActionDate(), actionDate
                            .getDeposit(), actionDate.getDepositPaid());
            bulkEntryAccountActionView = installmentView;

        } else if (accountActionDateEntity instanceof CustomerScheduleEntity) {
            CustomerScheduleEntity actionDate = (CustomerScheduleEntity) accountActionDateEntity;
            CollectionSheetEntryCustomerAccountInstallmentView installmentView = new CollectionSheetEntryCustomerAccountInstallmentView(
                    actionDate.getAccount().getAccountId(), actionDate.getCustomer().getCustomerId(), actionDate
                            .getInstallmentId(), actionDate.getActionDateId(), actionDate.getActionDate(), actionDate
                            .getMiscFee(), actionDate.getMiscFeePaid(), actionDate.getMiscPenalty(), actionDate
                            .getMiscPenaltyPaid(), getCurrency());
            installmentView
                    .setCollectionSheetEntryAccountFeeActions(getBulkEntryAccountFeeActionViews(accountActionDateEntity));
            bulkEntryAccountActionView = installmentView;
        }
        return bulkEntryAccountActionView;
    }

    public static CollectionSheetEntryAccountFeeActionView getBulkEntryAccountFeeActionView(
            final AccountFeesActionDetailEntity feeAction) {
        return new CollectionSheetEntryAccountFeeActionView(feeAction.getAccountActionDate().getActionDateId(),
                feeAction.getFee().getFeeId(), feeAction.getFeeAmount(), feeAction.getFeeAmountPaid());
    }

    public static List<CollectionSheetEntryAccountFeeActionView> getBulkEntryAccountFeeActionViews(
            final AccountActionDateEntity accountActionDateEntity) {
        List<CollectionSheetEntryAccountFeeActionView> bulkEntryFeeViews = new ArrayList<CollectionSheetEntryAccountFeeActionView>();
        Set<AccountFeesActionDetailEntity> feeActions = null;
        if (accountActionDateEntity instanceof LoanScheduleEntity) {
            feeActions = ((LoanScheduleEntity) accountActionDateEntity).getAccountFeesActionDetails();
        } else if (accountActionDateEntity instanceof CustomerScheduleEntity) {
            feeActions = ((CustomerScheduleEntity) accountActionDateEntity).getAccountFeesActionDetails();
        }
        if (feeActions != null && feeActions.size() > 0) {
            for (AccountFeesActionDetailEntity accountFeesActionDetail : feeActions) {
                bulkEntryFeeViews.add(getBulkEntryAccountFeeActionView(accountFeesActionDetail));
            }
        }
        return bulkEntryFeeViews;

    }

    public static List<CollectionSheetEntryInstallmentView> getBulkEntryAccountActionViews(
            final List<AccountActionDateEntity> actionDates) {
        List<CollectionSheetEntryInstallmentView> bulkEntryActionViews = new ArrayList<CollectionSheetEntryInstallmentView>();
        if (actionDates != null && actionDates.size() > 0) {
            for (AccountActionDateEntity actionDate : actionDates) {
                bulkEntryActionViews.add(getBulkEntryAccountActionView(actionDate));
            }
        }
        return bulkEntryActionViews;

    }

    public static CustomerNoteEntity getCustomerNote(final String comment, final CustomerBO customer) {
        java.sql.Date commentDate = new java.sql.Date(System.currentTimeMillis());
        CustomerNoteEntity notes = new CustomerNoteEntity(comment, commentDate, customer.getPersonnel(), customer);
        return (CustomerNoteEntity) addObject(notes);
    }

    public static OfficeBO createOffice(final OfficeLevel level, final OfficeBO parentOffice, final String officeName, final String shortName)
            throws Exception {
        OfficeBO officeBO = new OfficeBO(TestUtils.makeUserWithLocales(), level, parentOffice, null, officeName,
                shortName, null, OperationMode.REMOTE_SERVER);
        officeBO.save();
        StaticHibernateUtil.commitTransaction();
        return (OfficeBO) addObject(officeBO);
    }

    public static void cleanUp(final OfficeBO office) {
        if (office != null) {
            Session session = StaticHibernateUtil.getSessionTL();
            Transaction transaction = StaticHibernateUtil.startTransaction();
            session.lock(office, LockMode.NONE);
            session.delete(office);
            transaction.commit();
        }
    }

    public static void removeCustomerFromPosition(final CustomerBO customer) throws CustomerException {
        if (customer != null) {
            for (CustomerPositionEntity customerPositionEntity : customer.getCustomerPositions()) {
                customerPositionEntity.setCustomer(null);
            }
            customer.update();
            StaticHibernateUtil.commitTransaction();
        }
    }

    public static void cleanUp(final PersonnelBO personnel) {
        if (personnel != null) {
            Session session = StaticHibernateUtil.getSessionTL();
            Transaction transaction = StaticHibernateUtil.startTransaction();
            session.lock(personnel, LockMode.NONE);
            session.delete(personnel);
            transaction.commit();
        }
    }

    public static PersonnelBO createPersonnel(final PersonnelLevel level, final OfficeBO office, final Integer title,
            final Short preferredLocale, final String password, final String userName, final String emailId, final List<RoleBO> personnelRoles,
            final List<CustomFieldView> customFields, final Name name, final String governmentIdNumber, final Date dob, final Integer maritalStatus,
            final Integer gender, final Date dateOfJoiningMFI, final Date dateOfJoiningBranch, final Address address) throws Exception {
        PersonnelBO personnelBO = new PersonnelBO(level, office, title, preferredLocale, password, userName, emailId,
                personnelRoles, customFields, name, governmentIdNumber, dob, maritalStatus, gender, dateOfJoiningMFI,
                dateOfJoiningBranch, address, Short.valueOf("1"));

        personnelBO.save();
        StaticHibernateUtil.commitTransaction();
        return personnelBO;
    }

    public static void simulateInvalidConnection() {
        StaticHibernateUtil.getSessionTL().close();
    }

    public static void cleanUp(final RoleBO roleBO) {
        if (roleBO != null) {
            Session session = StaticHibernateUtil.getSessionTL();
            Transaction transaction = StaticHibernateUtil.startTransaction();
            session.lock(roleBO, LockMode.NONE);
            session.delete(roleBO);
            transaction.commit();
        }
    }

    public static RoleBO createRole(final UserContext context, final String roleName, final List<ActivityEntity> activities)
            throws Exception {
        RoleBO roleBO = new RoleBO(context, roleName, activities);
        roleBO.save();
        StaticHibernateUtil.commitTransaction();
        return roleBO;
    }

    public static void cleanUp(final FundBO fundBO) {
        if (fundBO != null) {
            Session session = StaticHibernateUtil.getSessionTL();
            Transaction transaction = StaticHibernateUtil.startTransaction();
            session.lock(fundBO, LockMode.NONE);
            session.delete(fundBO);
            transaction.commit();
        }
    }

    public static FundBO createFund(final FundCodeEntity fundCode, final String fundName) throws Exception {
        FundBO fundBO = new FundBO(fundCode, fundName);
        fundBO.save();
        StaticHibernateUtil.commitTransaction();
        return fundBO;
    }

    public static GroupBO createGroupUnderBranch(final String customerName, final CustomerStatus customerStatus, final Short officeId,
            final MeetingBO meeting, final Short loanOfficerId, final List<CustomFieldView> customFields) {
        Short formedBy = new Short("1");
        return createGroupUnderBranch(customerName, customerStatus, null, false, null, null, customFields, getFees(),
                formedBy, officeId, meeting, loanOfficerId);
    }

    public static void cleanUpChangeLog() {
        Session session = StaticHibernateUtil.getSessionTL();
        StaticHibernateUtil.startTransaction();
        List<AuditLog> auditLogList = session
                .createQuery("from org.mifos.framework.components.audit.business.AuditLog").list();
        if (auditLogList != null) {
            for (Iterator<AuditLog> iter = auditLogList.iterator(); iter.hasNext();) {
                AuditLog auditLog = iter.next();
                session.delete(auditLog);
            }
        }
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
    }

    public static List<AuditLog> getChangeLog(final EntityType type, final Integer entityId) {
        return StaticHibernateUtil.getSessionTL().createQuery(
                "from org.mifos.framework.components.audit.business.AuditLog al " + "where al.entityType="
                        + type.getValue() + " and al.entityId=" + entityId).list();
    }

    public static void cleanUp(final AuditLog auditLog) {
        Session session = StaticHibernateUtil.getSessionTL();
        Transaction transaction = StaticHibernateUtil.startTransaction();
        session.lock(auditLog, LockMode.NONE);
        session.delete(auditLog);
        transaction.commit();
    }

    public static Calendar getCalendar(final Date date) {
        Calendar dateCalendar = new GregorianCalendar();
        dateCalendar.setTimeInMillis(date.getTime());
        int year = dateCalendar.get(Calendar.YEAR);
        int month = dateCalendar.get(Calendar.MONTH);
        int day = dateCalendar.get(Calendar.DAY_OF_MONTH);
        dateCalendar = new GregorianCalendar(year, month, day);
        return dateCalendar;
    }

    /**
     * Return the int corresponding to the day of the week of the date
     * parameter. Returns Calendar.SUNDAY (1), Calendar.MONDAY (2), etc.
     */
    public static int getDayForDate(final Date date) {
        Calendar dateConversionCalendar = new GregorianCalendar();
        dateConversionCalendar.setTime(date);
        return dateConversionCalendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * This method is not recommended because it calls
     * {@link #getNewMeetingForToday(RecurrenceType, short, MeetingType)}.
     */
    public static SavingsOfferingBO createSavingsProduct(final String offeringName, final String shortName, final Short depGLCode,
            final Short intGLCode, final RecommendedAmountUnit recommendedAmountUnit) {
        MeetingBO meetingIntCalc = createMeeting(getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
        MeetingBO meetingIntPost = createMeeting(getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
        return createSavingsProduct(offeringName, shortName, ApplicableTo.GROUPS, new Date(System.currentTimeMillis()),
                PrdStatus.SAVINGS_ACTIVE, 300.0, recommendedAmountUnit, 24.0, 200.0, 200.0, SavingsType.VOLUNTARY,
                InterestCalcType.MINIMUM_BALANCE, meetingIntCalc, meetingIntPost, depGLCode, intGLCode);
    }

    /**
     * This method is not recommended because it indirectly calls
     * {@link #getNewMeetingForToday(RecurrenceType, short, MeetingType)}.
     */
    public static SavingsOfferingBO createSavingsProduct(final String offeringName, final String shortName,
            final RecommendedAmountUnit recommendedAmountUnit) {
        return createSavingsProduct(offeringName, shortName, TestGeneralLedgerCode.ASSETS,
                TestGeneralLedgerCode.CASH_AND_BANK_BALANCES, recommendedAmountUnit);
    }

    public static SavingsOfferingBO createSavingsProduct(final String productName, final String shortName, final Date currentDate) {
        MeetingBO meetingIntCalc = createMeeting(getTypicalMeeting());
        MeetingBO meetingIntPost = createMeeting(getTypicalMeeting());
        return createSavingsProduct(productName, shortName, currentDate, meetingIntCalc, meetingIntPost);
    }

    public static SavingsOfferingBO createSavingsProduct(final String productName, final String shortName, final Date currentDate,
            final MeetingBO meetingIntCalc, final MeetingBO meetingIntPost) {
        return createSavingsProduct(productName, shortName, ApplicableTo.GROUPS, currentDate, PrdStatus.SAVINGS_ACTIVE,
                300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 1.2, 200.0, 200.0, SavingsType.VOLUNTARY,
                InterestCalcType.MINIMUM_BALANCE, meetingIntCalc, meetingIntPost);
    }

    public static void cleanUp(SavingsOfferingBO savingPrdBO) {
        if (null != savingPrdBO) {
            deleteSavingProduct(savingPrdBO);
            savingPrdBO = null;
        }
    }

    public static void cleanUp(MeetingBO meeting) {
        if (null != meeting) {
            deleteMeeting(meeting);
            meeting = null;
        }
    }

    public static void cleanUp(ProductMixBO prdmix) {
        if (null != prdmix) {
            deleteProductMix(prdmix);
            prdmix = null;
        }
    }

    private static void deleteProductMix(final ProductMixBO prdmix) {
        Session session = StaticHibernateUtil.getSessionTL();
        session.lock(prdmix, LockMode.UPGRADE);
        Transaction transaction = StaticHibernateUtil.startTransaction();
        session.delete(prdmix);
        transaction.commit();
    }

    private static void deleteSavingProduct(final SavingsOfferingBO savingPrdBO) {
        Session session = StaticHibernateUtil.getSessionTL();
        session.lock(savingPrdBO, LockMode.UPGRADE);
        Transaction transaction = StaticHibernateUtil.startTransaction();
        session.delete(savingPrdBO);
        transaction.commit();
    }

    private static void deleteMeeting(MeetingBO meeting) {
        Session session = StaticHibernateUtil.getSessionTL();
        Transaction transaction = StaticHibernateUtil.startTransaction();
        meeting = (MeetingBO) session.load(MeetingBO.class, meeting.getMeetingId());
        session.delete(meeting);
        transaction.commit();
    }

    public static ProductMixBO createAllowedProductsMix(final SavingsOfferingBO saving1, final SavingsOfferingBO saving2) {
        ProductMixBO prdmix;
        try {
            prdmix = new ProductMixBO(saving1, saving2);
            addObject(testObjectPersistence.persist(prdmix));
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return prdmix;
    }

    public static final int SAMPLE_BUSINESS_ACTIVITY_2 = 2;

    public static GroupBO createInstanceForTest(final UserContext userContext, final GroupTemplate template, final CenterBO center,
            final Date customerActivationDate) throws CustomerException, PersistenceException {

        GroupBO group = new GroupBO(userContext, template.getDisplayName(), template.getCustomerStatus(), template
                .getExternalId(), template.isTrained(), template.getTrainedDate(), template.getAddress(), template
                .getCustomFieldViews(), template.getFees(), new PersonnelPersistence().getPersonnel(template
                .getLoanOfficerId()), center, new GroupPersistence(), new OfficePersistence());
        group.setCustomerActivationDate(customerActivationDate);
        return group;
    }

    /*
     * Gets the test data office with office_id == 3
     */
    public static OfficeBO getBranchOffice() {
        try {
            return new OfficePersistence().getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Gets the test data user personnel_id == 1
     */
    public static PersonnelBO getSystemUser() {
        try {
            return new PersonnelPersistence().getPersonnel(PersonnelConstants.SYSTEM_USER);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Gets the test data user personnel_id == 3
     */
    public static PersonnelBO getTestUser() {
        try {
            return new PersonnelPersistence().getPersonnel(PersonnelConstants.TEST_USER);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
    }

}
