

package org.mifos.framework.struts.action;

import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.fund.persistence.FundDao;
import org.mifos.accounts.fund.persistence.FundDaoHibernate;
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
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.persistance.LoanDaoHibernate;
import org.mifos.accounts.loan.persistance.LoanPersistence;
import org.mifos.accounts.loan.schedule.calculation.ScheduleCalculator;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.productdefinition.persistence.LoanProductDaoHibernate;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.application.admin.servicefacade.HolidayServiceFacade;
import org.mifos.application.holiday.business.service.HolidayService;
import org.mifos.application.holiday.business.service.HolidayServiceImpl;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.holiday.persistence.HolidayDaoHibernate;
import org.mifos.application.holiday.persistence.HolidayServiceFacadeWebTier;
import org.mifos.application.servicefacade.LoanServiceFacade;
import org.mifos.application.servicefacade.LoanServiceFacadeWebTier;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.office.persistence.OfficeDaoHibernate;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerDaoHibernate;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.personnel.persistence.PersonnelDaoHibernate;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;

public class ApplicationOnlyDependencyInjectedServiceLocator {

    private static HolidayServiceFacade holidayServiceFacade;

    // services
    private static HolidayService holidayService;
    private static AccountBusinessService accountBusinessService;
    private static ConfigurationBusinessService configurationBusinessService;

    // daos
    private static LoanPersistence loanPersistence = new LoanPersistence();
    private static GenericDao genericDao = new GenericDaoHibernate();
    private static FundDao fundDao = new FundDaoHibernate(genericDao);
    private static LoanDao loanDao = new LoanDaoHibernate(genericDao);
    private static OfficeDao officeDao = new OfficeDaoHibernate(genericDao);
    private static PersonnelDao personnelDao = new PersonnelDaoHibernate(genericDao);
    private static HolidayDao holidayDao = new HolidayDaoHibernate(genericDao);
    private static CustomerDao customerDao = new CustomerDaoHibernate(genericDao);
    private static LoanProductDao loanProductDao = new LoanProductDaoHibernate(genericDao);

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