

package org.mifos.framework.struts.action;

import org.mifos.accounts.api.AccountService;
import org.mifos.accounts.api.StandardAccountService;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.fees.business.service.FeeService;
import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.fees.persistence.FeeDaoHibernate;
import org.mifos.accounts.financial.business.service.GeneralLedgerDao;
import org.mifos.accounts.financial.business.service.GeneralLedgerDaoHibernate;
import org.mifos.accounts.fund.persistence.FundDao;
import org.mifos.accounts.fund.persistence.FundDaoHibernate;
import org.mifos.accounts.fund.servicefacade.FundServiceFacade;
import org.mifos.accounts.loan.business.ScheduleCalculatorAdaptor;
import org.mifos.accounts.loan.business.ScheduleMapper;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.business.service.validators.InstallmentFormatValidator;
import org.mifos.accounts.loan.business.service.validators.InstallmentFormatValidatorImpl;
import org.mifos.accounts.loan.business.service.validators.InstallmentRulesValidator;
import org.mifos.accounts.loan.business.service.validators.InstallmentRulesValidatorImpl;
import org.mifos.accounts.loan.business.service.validators.InstallmentsValidator;
import org.mifos.accounts.loan.business.service.validators.InstallmentsValidatorImpl;
import org.mifos.accounts.loan.business.service.validators.ListOfInstallmentsValidator;
import org.mifos.accounts.loan.business.service.validators.ListOfInstallmentsValidatorImpl;
import org.mifos.accounts.loan.persistance.ClientAttendanceDao;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.persistance.LoanDaoHibernate;
import org.mifos.accounts.loan.persistance.LoanPersistence;
import org.mifos.accounts.loan.persistance.StandardClientAttendanceDao;
import org.mifos.accounts.loan.schedule.calculation.ScheduleCalculator;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.productdefinition.persistence.LoanProductDaoHibernate;
import org.mifos.accounts.productdefinition.persistence.SavingsProductDao;
import org.mifos.accounts.productdefinition.persistence.SavingsProductDaoHibernate;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.accounts.savings.persistence.SavingsDaoHibernate;
import org.mifos.accounts.savings.persistence.SavingsPersistence;
import org.mifos.accounts.servicefacade.AccountServiceFacade;
import org.mifos.application.admin.servicefacade.CheckListServiceFacade;
import org.mifos.application.admin.servicefacade.FeeServiceFacade;
import org.mifos.application.admin.servicefacade.HolidayServiceFacade;
import org.mifos.application.admin.servicefacade.OfficeServiceFacade;
import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.application.admin.servicefacade.RolesPermissionServiceFacade;
import org.mifos.application.collectionsheet.persistence.CollectionSheetDao;
import org.mifos.application.collectionsheet.persistence.CollectionSheetDaoHibernate;
import org.mifos.application.holiday.business.service.HolidayService;
import org.mifos.application.holiday.business.service.HolidayServiceImpl;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.holiday.persistence.HolidayDaoHibernate;
import org.mifos.application.holiday.persistence.HolidayServiceFacadeWebTier;
import org.mifos.application.importexport.business.service.ImportedFilesService;
import org.mifos.application.importexport.persistence.ImportedFilesDao;
import org.mifos.application.importexport.persistence.ImportedFilesDaoHibernate;
import org.mifos.application.importexport.servicefacade.ImportTransactionsServiceFacade;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.servicefacade.CenterServiceFacade;
import org.mifos.application.servicefacade.ClientServiceFacade;
import org.mifos.application.servicefacade.CollectionSheetService;
import org.mifos.application.servicefacade.CollectionSheetServiceFacade;
import org.mifos.application.servicefacade.CustomerServiceFacade;
import org.mifos.application.servicefacade.GroupServiceFacade;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.application.servicefacade.LoanServiceFacade;
import org.mifos.application.servicefacade.LoanServiceFacadeWebTier;
import org.mifos.application.servicefacade.MeetingServiceFacade;
import org.mifos.application.servicefacade.NewLoginServiceFacade;
import org.mifos.application.servicefacade.SavingsServiceFacade;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.office.persistence.OfficeDaoHibernate;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerDaoHibernate;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.service.PersonnelService;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.personnel.persistence.PersonnelDaoHibernate;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;
import org.mifos.security.AuthenticationAuthorizationServiceFacade;

public class ApplicationOnlyDependencyInjectedServiceLocator {

    private static CollectionSheetServiceFacade collectionSheetServiceFacade;
    private static LoanAccountServiceFacade loanAccountServiceFacade;
    private static SavingsServiceFacade savingsServiceFacade;
    private static CustomerServiceFacade customerServiceFacade;
    private static CenterServiceFacade centerServiceFacade;
    private static GroupServiceFacade groupServiceFacade;
    private static ClientServiceFacade clientServiceFacade;
    private static NewLoginServiceFacade loginServiceFacade;
    private static MeetingServiceFacade meetingServiceFacade;

    private static HolidayServiceFacade holidayServiceFacade;
    private static OfficeServiceFacade officeServiceFacade;
    private static FeeServiceFacade feeServiceFacade;
    private static FundServiceFacade fundServiceFacade;
    private static PersonnelServiceFacade personnelServiceFacade;
    private static RolesPermissionServiceFacade rolesPermissionServiceFacade;
    private static CheckListServiceFacade checkListServiceFacade;
    private static AccountServiceFacade accountServiceFacade;
    private static AuthenticationAuthorizationServiceFacade authenticationAuthorizationServiceFacade;
    private static ImportTransactionsServiceFacade importTransactionsServiceFacade;

    // services
    private static CollectionSheetService collectionSheetService;
    private static CustomerService customerService;
    private static HolidayService holidayService;
    private static FeeService feeService;
    private static PersonnelService personnelService;
    private static AccountBusinessService accountBusinessService;
    private static ConfigurationBusinessService configurationBusinessService;
    private static StandardAccountService accountService;
    private static ImportedFilesService importedFilesService;

    // daos
    private static OfficePersistence officePersistence = new OfficePersistence();
    private static MasterPersistence masterPersistence = new MasterPersistence();
    private static PersonnelPersistence personnelPersistence = new PersonnelPersistence();
    private static CustomerPersistence customerPersistence = new CustomerPersistence();
    private static SavingsPersistence savingsPersistence = new SavingsPersistence();
    private static LoanPersistence loanPersistence = new LoanPersistence();
    private static AccountPersistence accountPersistence = new AccountPersistence();
    private static ClientAttendanceDao clientAttendanceDao = new StandardClientAttendanceDao(masterPersistence);
    private static GenericDao genericDao = new GenericDaoHibernate();
    private static ImportedFilesDao importedFilesDao = new ImportedFilesDaoHibernate(genericDao);
    private static FundDao fundDao = new FundDaoHibernate(genericDao);
    private static LoanDao loanDao = new LoanDaoHibernate(genericDao);
    private static FeeDao feeDao = new FeeDaoHibernate(genericDao);
    private static OfficeDao officeDao = new OfficeDaoHibernate(genericDao);
    private static PersonnelDao personnelDao = new PersonnelDaoHibernate(genericDao);
    private static HolidayDao holidayDao = new HolidayDaoHibernate(genericDao);
    private static CustomerDao customerDao = new CustomerDaoHibernate(genericDao);
    private static LoanProductDao loanProductDao = new LoanProductDaoHibernate(genericDao);
    private static SavingsDao savingsDao = new SavingsDaoHibernate(genericDao);
    private static SavingsProductDao savingsProductDao = new SavingsProductDaoHibernate(genericDao);
    private static CollectionSheetDao collectionSheetDao = new CollectionSheetDaoHibernate(savingsDao);
    private static GeneralLedgerDao generalLedgerDao = new GeneralLedgerDaoHibernate(genericDao);

    private static HibernateTransactionHelper hibernateTransactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();
    private static ScheduleCalculatorAdaptor scheduleCalculatorAdaptor;
    private static ScheduleCalculator scheduleCalculator;

    // validators
    private static InstallmentFormatValidator installmentFormatValidator;
    private static ListOfInstallmentsValidator listOfInstallmentsValidator;
    private static InstallmentRulesValidator installmentRulesValidator;
    private static InstallmentsValidator installmentsValidator;

    // business services
    private static LoanBusinessService loanBusinessService;
    private static LoanPrdBusinessService loanPrdBusinessService;

    // rules
    private static FiscalCalendarRules fiscalCalendarRules;
    private static ScheduleMapper scheduleMapper;

    private static LoanServiceFacade loanServiceFacade;

    public static LoanServiceFacade locateLoanServiceFacade() {
        if (loanServiceFacade == null) {
            loanServiceFacade = new LoanServiceFacadeWebTier(loanProductDao, customerDao, personnelDao, fundDao,
                    loanDao, locateInstallmentsValidator(), locateScheduleCalculatorAdaptor(),
                    locateLoanBusinessService(), locateHolidayServiceFacade(), locateLoanPrdBusinessService());
        }
        return loanServiceFacade;
    }

    public static LoanPrdBusinessService locateLoanPrdBusinessService() {
        if (loanPrdBusinessService == null) {
            loanPrdBusinessService = new LoanPrdBusinessService();
        }
        return loanPrdBusinessService;
    }

    public static HolidayServiceFacade locateHolidayServiceFacade() {
        if (holidayServiceFacade == null) {
            holidayServiceFacade = new HolidayServiceFacadeWebTier(locateHolidayService(), holidayDao);
        }
        return holidayServiceFacade;
    }

    public static HolidayService locateHolidayService() {

        if (holidayService == null) {
            holidayService = new HolidayServiceImpl(officeDao, holidayDao, hibernateTransactionHelper, locateFiscalCalendarRules());
        }
        return holidayService;
    }

    public static FiscalCalendarRules locateFiscalCalendarRules() {
        if (fiscalCalendarRules == null) {
            fiscalCalendarRules = new FiscalCalendarRules();
        }
        return fiscalCalendarRules;
    }

    public static LoanBusinessService locateLoanBusinessService() {
        if (loanBusinessService == null) {
            loanBusinessService = new LoanBusinessService(loanPersistence, locateConfigurationBusinessService(), locateAccountBusinessService(),
                    locateHolidayService(), locateScheduleCalculatorAdaptor());
        }
        return loanBusinessService;
    }

    public static AccountBusinessService locateAccountBusinessService() {
        if (accountBusinessService == null) {
            accountBusinessService = new AccountBusinessService();
        }
        return accountBusinessService;
    }

    private static ConfigurationBusinessService locateConfigurationBusinessService() {
        if (configurationBusinessService == null) {
            configurationBusinessService = new ConfigurationBusinessService();
        }
        return configurationBusinessService;
    }

    public static InstallmentsValidator locateInstallmentsValidator() {
        if (installmentsValidator == null) {
            installmentsValidator = new InstallmentsValidatorImpl(locateInstallmentFormatValidator(), locateListOfInstallmentsValidator(), locateInstallmentRulesValidator());
        }
        return installmentsValidator;
    }

    public static ScheduleCalculatorAdaptor locateScheduleCalculatorAdaptor() {
        if (scheduleCalculatorAdaptor == null) {
            scheduleCalculatorAdaptor = new ScheduleCalculatorAdaptor(locateScheduleCalculator(), locateScheduleMapper());
        }
        return scheduleCalculatorAdaptor;
    }

    public static ScheduleCalculator locateScheduleCalculator() {
        if (scheduleCalculator == null) {
            scheduleCalculator = new ScheduleCalculator();
        }
        return scheduleCalculator;
    }

    public static ScheduleMapper locateScheduleMapper() {
        if (scheduleMapper == null) {
            scheduleMapper = new ScheduleMapper();
        }
        return scheduleMapper;
    }

    public static InstallmentFormatValidator locateInstallmentFormatValidator() {
        if (installmentFormatValidator == null) {
            installmentFormatValidator = new InstallmentFormatValidatorImpl();
        }
        return installmentFormatValidator;
    }

    public static ListOfInstallmentsValidator locateListOfInstallmentsValidator() {
        if (listOfInstallmentsValidator == null) {
            listOfInstallmentsValidator = new ListOfInstallmentsValidatorImpl();
        }
        return listOfInstallmentsValidator;
    }

    public static InstallmentRulesValidator locateInstallmentRulesValidator() {
        if (installmentRulesValidator == null) {
            installmentRulesValidator = new InstallmentRulesValidatorImpl();
        }
        return installmentRulesValidator;
    }
}