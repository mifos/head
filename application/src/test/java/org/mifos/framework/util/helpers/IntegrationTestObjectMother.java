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

package org.mifos.framework.util.helpers;

import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.fund.persistence.FundDao;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.PrdOfferingBO;
import org.mifos.accounts.productdefinition.business.ProductTypeEntity;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.productdefinition.persistence.SavingsProductDao;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.service.HolidayService;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.master.business.FundCodeEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.clientportfolio.newloan.applicationservice.CreateLoanAccount;
import org.mifos.clientportfolio.newloan.applicationservice.LoanAccountCashFlow;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerLevelEntity;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.checklist.business.AccountCheckListBO;
import org.mifos.customers.checklist.business.CustomerCheckListBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.domain.builders.OfficeBuilder;
import org.mifos.domain.builders.SavingsProductBuilder;
import org.mifos.dto.domain.HolidayDetails;
import org.mifos.dto.screen.LoanCreationResultDto;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.security.util.UserContext;

/**
 *
 */
public class IntegrationTestObjectMother {

    private static final String INTEGRATION_TEST_DATA_MISSING_MESSAGE = "This should exist within database for integration tests but does not.";

    // user
    private static final Short DEFAULT_INTEGRATION_TEST_USER = PersonnelConstants.TEST_USER;
    private static PersonnelBO testUser = null;
    private static PersonnelBO systemUser = null;

    // office
    public static final Short SAMPLE_BRANCH_OFFICE = 3;
    private static OfficeBO sampleBranchOffice = null;

    // serviceFacades
    private static final LoanAccountServiceFacade loanAccountServiceFacade = ApplicationContextProvider.getBean(LoanAccountServiceFacade.class);
    
    // DAO's for fetching existing data within database
    private static final OfficePersistence officePersistence = new OfficePersistence();
    private static final OfficeDao officeDao = ApplicationContextProvider.getBean(OfficeDao.class);
    private static final HolidayDao holidayDao = ApplicationContextProvider.getBean(HolidayDao.class);
    private static final FundDao fundDao = ApplicationContextProvider.getBean(FundDao.class);
    private static final SavingsDao savingsDao = ApplicationContextProvider.getBean(SavingsDao.class);
    private static final LoanProductDao loanProductDao = ApplicationContextProvider.getBean(LoanProductDao.class);
    private static final SavingsProductDao savingsProductDao = ApplicationContextProvider.getBean(SavingsProductDao.class);
    private static final CustomerDao customerDao = ApplicationContextProvider.getBean(CustomerDao.class);
    private static final LoanDao loanDao = ApplicationContextProvider.getBean(LoanDao.class);
    private static final PersonnelDao personnelDao = ApplicationContextProvider.getBean(PersonnelDao.class);
    private static final CustomerPersistence customerPersistence = new CustomerPersistence();

    private static final CustomerService customerService = ApplicationContextProvider.getBean(CustomerService.class);
    private static final HolidayService holidayService = ApplicationContextProvider.getBean(HolidayService.class);
    private static final GenericDao genericDao = ApplicationContextProvider.getBean(GenericDao.class);

    public static OfficeBO sampleBranchOffice() {
        if (sampleBranchOffice == null) {
            try {
                sampleBranchOffice = officePersistence.getOffice(SAMPLE_BRANCH_OFFICE);
                StaticHibernateUtil.flushAndClearSession();
            } catch (PersistenceException e) {
                throw new IllegalStateException("Office with id [" + SAMPLE_BRANCH_OFFICE + "]. "
                        + INTEGRATION_TEST_DATA_MISSING_MESSAGE);
            }
        }
        return sampleBranchOffice;
    }

    public static PersonnelBO testUser() {
        if (testUser == null) {
            try {
                testUser = personnelDao.findPersonnelById(DEFAULT_INTEGRATION_TEST_USER);
            } catch (Exception e) {
                throw new IllegalStateException("PersonnelBO with id [" + DEFAULT_INTEGRATION_TEST_USER + "]. "
                        + INTEGRATION_TEST_DATA_MISSING_MESSAGE);
            }
        }

        return testUser;
    }

    public static PersonnelBO systemUser() {
        if (systemUser == null) {
            try {
                systemUser = personnelDao.findPersonnelById(PersonnelConstants.SYSTEM_USER);
            } catch (Exception e) {
                throw new IllegalStateException("PersonnelBO with id [" + PersonnelConstants.SYSTEM_USER + "]. "
                        + INTEGRATION_TEST_DATA_MISSING_MESSAGE);
            }
        }

        return systemUser;
    }

    public static MeetingBO getMeeting(Integer meetingId) throws PersistenceException {
        return customerPersistence.getPersistentObject(MeetingBO.class, meetingId);
    }

    public static void saveMeeting(final MeetingBO meeting) {
        try {
            customerPersistence.createOrUpdate(meeting);
            StaticHibernateUtil.flushSession();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveCustomer(final CustomerBO center) {
        try {
//            StaticHibernateUtil.startTransaction();
            customerPersistence.saveCustomer(center);
            StaticHibernateUtil.flushSession();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //    public static void cleanCustomerHierarchyWithMultipleClientsMeetingsAndFees(final ClientBO activeClient,
//            final ClientBO inActiveClient, final GroupBO group, final CustomerBO center, final MeetingBO weeklyMeeting) {
//        try {
//            TestObjectFactory.cleanUp(activeClient);
//            TestObjectFactory.cleanUp(inActiveClient);
//            group = null;
//            center = null;
//            TestObjectFactory.cleanUp(weeklyMeeting);
//        } finally {
//            StaticHibernateUtil.flushSession();
//        }
//    }

    public static void cleanCustomerHierarchyWithMeeting(final ClientBO client, final GroupBO group,
            final CustomerBO center, final MeetingBO weeklyMeeting) {
        try {
//            client = null;
//            group = null;
//            center = null;
//            TestObjectFactory.cleanUp(weeklyMeeting);
        } finally {
            StaticHibernateUtil.flushSession();
        }
    }

    public static void saveSavingsProductAndAssociatedSavingsAccounts(final SavingsOfferingBO savingsProduct,
            final SavingsBO... relatedSavingsAccounts) {
        try {
            customerPersistence.createOrUpdate(savingsProduct);
            for (SavingsBO savingAccount : relatedSavingsAccounts) {
                customerPersistence.createOrUpdate(savingAccount);
            }
            StaticHibernateUtil.flushSession();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void cleanSavingsProductAndAssociatedSavingsAccounts(final SavingsBO... savingsAccount) {

        try {
//            for (SavingsBO savingsBO : savingsAccount) {
//                savingsBO = null;
//            }
        } finally {
            StaticHibernateUtil.flushSession();
        }
    }

    public static void saveLoanProducts(final LoanOfferingBO... loanProducts) {
        try {
            for (LoanOfferingBO loanProduct : loanProducts) {
                customerPersistence.createOrUpdate(loanProduct);
            }
            StaticHibernateUtil.flushSession();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveSavingsProducts(final SavingsOfferingBO... savingsProducts) {
        try {
            for (SavingsOfferingBO savingsProduct : savingsProducts) {
                savingsProductDao.save(savingsProduct);
            }
            StaticHibernateUtil.flushSession();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void createSavingsProduct(SavingsProductBuilder savingsProductBuilder) {
        SavingsOfferingBO savingsProduct = savingsProductBuilder.buildForIntegrationTests();
        try {
            StaticHibernateUtil.startTransaction();
            savingsProductDao.save(savingsProduct);
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new RuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public static void createCenter(CenterBO center, MeetingBO meeting) {
        UserContext userContext = TestUtils.makeUser();
        center.setUserContext(userContext);

        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();

        customerService.createCenter(center, meeting, accountFees);
        StaticHibernateUtil.flushAndClearSession();
    }

    public static void createCenter(CenterBO center, MeetingBO meeting, AmountFeeBO fee) {

        UserContext userContext = TestUtils.makeUser();
        center.setUserContext(userContext);

        AccountFeesEntity accountFee = new AccountFeesEntity(null, fee, fee.getFeeAmount().getAmountDoubleValue());

        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();
        accountFees.add(accountFee);

            customerService.createCenter(center, meeting, accountFees);
    }

    public static void createCenter(CenterBO center, MeetingBO meeting, AmountFeeBO...fees) {

        UserContext userContext = TestUtils.makeUser();
        center.setUserContext(userContext);

        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();
        for (AmountFeeBO fee : fees) {
            AccountFeesEntity accountFee = new AccountFeesEntity(null, fee, fee.getFeeAmount().getAmountDoubleValue());
            accountFees.add(accountFee);
        }

        customerService.createCenter(center, meeting, accountFees);
    }

    public static void createGroup(GroupBO group, MeetingBO meeting) {

        UserContext userContext = TestUtils.makeUser();
        group.setUserContext(userContext);

        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();

        try {
            customerService.createGroup(group, meeting, accountFees);
        } catch (CustomerException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createGroup(GroupBO group, MeetingBO meeting, AmountFeeBO fee) throws CustomerException {

        UserContext userContext = TestUtils.makeUser();
        group.setUserContext(userContext);

        AccountFeesEntity accountFee = new AccountFeesEntity(null, fee, fee.getFeeAmount().getAmountDoubleValue());

        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();
        accountFees.add(accountFee);

        customerService.createGroup(group, meeting, accountFees);
    }

    public static void createClient(ClientBO client, MeetingBO meeting) throws CustomerException {
        UserContext userContext = TestUtils.makeUser();
        client.setUserContext(userContext);

        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();
        List<SavingsOfferingBO> selectedOfferings = new ArrayList<SavingsOfferingBO>();

        customerService.createClient(client, meeting, accountFees, selectedOfferings);
    }

    public static void createClient(ClientBO client, MeetingBO meeting, SavingsOfferingBO savingsProduct) throws CustomerException {
        UserContext userContext = TestUtils.makeUser();
        client.setUserContext(userContext);

        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();
        List<SavingsOfferingBO> selectedOfferings = new ArrayList<SavingsOfferingBO>();
        selectedOfferings.add(savingsProduct);

        customerService.createClient(client, meeting, accountFees, selectedOfferings);
    }

    public static void createClient(ClientBO client, MeetingBO meeting, AmountFeeBO fee) throws CustomerException {

        UserContext userContext = TestUtils.makeUser();
        client.setUserContext(userContext);

        AccountFeesEntity accountFee = new AccountFeesEntity(null, fee, fee.getFeeAmount().getAmountDoubleValue());

        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();
        accountFees.add(accountFee);

        List<SavingsOfferingBO> selectedOfferings = new ArrayList<SavingsOfferingBO>();

        customerService.createClient(client, meeting, accountFees, selectedOfferings);
    }

    public static void saveFee(AmountFeeBO fee) {
        try {
//            StaticHibernateUtil.startTransaction();
            customerPersistence.createOrUpdate(fee);
            StaticHibernateUtil.flushSession();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void createProduct(PrdOfferingBO product) {
        try {
//            StaticHibernateUtil.startTransaction();
            customerPersistence.createOrUpdate(product);
            StaticHibernateUtil.flushSession();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveLoanAccount(LoanBO loanAccount) {
        try {
            loanAccount.save();
            StaticHibernateUtil.flushSession();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveSavingsAccount(SavingsBO savingsAccount) {
        try {
            savingsAccount.save();
            StaticHibernateUtil.flushSession();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void update(CustomerBO customer) {
        try {
            new CustomerPersistence().createOrUpdate(customer);
            StaticHibernateUtil.flushSession();
          } catch (Exception e) {
              throw new RuntimeException(e);
          }
    }

    /**
     * remove and use {@link IntegrationTestObjectMother#createHoliday(HolidayDetails, List)}
     */
    @Deprecated
    public static void saveHoliday (Holiday holiday) {
        try {
//            StaticHibernateUtil.startTransaction();
            genericDao.createOrUpdate(holiday);
            StaticHibernateUtil.flushSession();
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
    }

    public static void createPersonnel(PersonnelBO personnel) {
        try {
            StaticHibernateUtil.startTransaction();
            personnelDao.save(personnel);
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        }
    }

    public static void createHoliday(HolidayDetails holidayDetails, List<Short> officeIds) {
        holidayService.create(holidayDetails, officeIds);
    }

    public static OfficeBO findOfficeById(Short officeId) {
        return officeDao.findOfficeById(officeId);
    }

    public static HolidayBO findHolidayById(Integer id) {
        return holidayDao.findHolidayById(id);
    }

    public static void createOffice(OfficeBO office) {
        try {
//            StaticHibernateUtil.startTransaction();
            customerPersistence.createOrUpdate(office);
            StaticHibernateUtil.flushSession();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void createOffice(OfficeBuilder officeBuilder) {
        try {
//            StaticHibernateUtil.startTransaction();
            customerPersistence.createOrUpdate(officeBuilder.build());
            StaticHibernateUtil.flushSession();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static FundBO createFund(FundBO fund) throws Exception {
        try {
            fundDao.save(fund);
            StaticHibernateUtil.flushSession();
            return fund;
        } catch (Exception e) {
            throw new MifosRuntimeException(e.getMessage(), e);
        }
    }

    public static void createFundCode(FundCodeEntity fundCode) {
        try {
            customerPersistence.createOrUpdate(fundCode);
            StaticHibernateUtil.flushSession();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static FundBO findFundByName(final String fundName) {
        return fundDao.findByName(fundName);
    }

    public static SavingsBO findSavingsAccountById(Long savingsId) {
        return savingsDao.findById(savingsId);
    }

    public static CustomerBO findCustomerById(Integer customerId) {
        return customerDao.findCustomerById(customerId);
    }

    public static LoanBO findLoanBySystemId(String globalAccountNum) {
        return loanDao.findByGlobalAccountNum(globalAccountNum);
    }

    public static PersonnelBO findPersonnelById(Short personnelId) {
        return personnelDao.findPersonnelById(personnelId);
    }

    public static void applyAccountPayment(AccountBO loan, PaymentData paymentData) {

        try {
            StaticHibernateUtil.startTransaction();
            loan.applyPayment(paymentData);
            StaticHibernateUtil.commitTransaction();
        } catch (AccountException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new RuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }

    }

    public static CustomerCheckListBO createCustomerChecklist(Short customerLevel, Short customerStatus, Short checklistStatus, List<String> details) {

        try {
            StaticHibernateUtil.startTransaction();
            CustomerLevelEntity customerLevelEntity = new CustomerLevelEntity(CustomerLevel.getLevel(customerLevel));
            CustomerStatusEntity customerStatusEntity = new CustomerStatusEntity(CustomerStatus.fromInt(customerStatus));
            CustomerCheckListBO customerChecklist = new CustomerCheckListBO(customerLevelEntity, customerStatusEntity,
                    "productchecklist", checklistStatus, details, Short.valueOf("1"), PersonnelConstants.SYSTEM_USER);

            customerDao.save(customerChecklist);
            StaticHibernateUtil.commitTransaction();
            return customerChecklist;
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new RuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public static AccountCheckListBO createAccountChecklist(Short prdTypeId, AccountState accountState,
            Short checklistStatus, List<String> details) {
        try {
            StaticHibernateUtil.startTransaction();
            ProductTypeEntity productTypeEntity = (ProductTypeEntity) StaticHibernateUtil.getSessionTL().get(
                    ProductTypeEntity.class, prdTypeId);
            AccountStateEntity accountStateEntity = new AccountStateEntity(accountState);
            AccountCheckListBO accountChecklist = new AccountCheckListBO(productTypeEntity, accountStateEntity,
                    "productchecklist", checklistStatus, details, Short.valueOf("1"), PersonnelConstants.SYSTEM_USER);

            customerDao.save(accountChecklist);
            StaticHibernateUtil.commitTransaction();
            return accountChecklist;
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new RuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }

    }

    public static LoanOfferingBO findLoanProductBySystemId(String globalPrdOfferingNum) {

        return loanProductDao.findBySystemId(globalPrdOfferingNum);
    }

    public static LoanBO createClientLoan(CreateLoanAccount createLoanAccount) {
        
        List<QuestionGroupDetail> questionGroups = new ArrayList<QuestionGroupDetail>();
        LoanAccountCashFlow loanAccountCashFlow = null;
       
        LoanCreationResultDto result = loanAccountServiceFacade.createLoan(createLoanAccount, questionGroups, loanAccountCashFlow);
        return loanDao.findByGlobalAccountNum(result.getGlobalAccountNum());
    }
}