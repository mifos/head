package org.mifos.application.accounts.financial.util.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.BANKACCOUNTONE;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.BANKBALANCES;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.CLIENTSDEPOSITS;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.DIRECTEXPENDITURE;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.FEES;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.INCOMEMICROCREDIT;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.INTERESTINCOMELOANS;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.LOANSADVANCES;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.LOANTOCLIENTS;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.MANDATORYSAVINGS;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.PENALTY;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.ROUNDINGGL;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.SAVINGSMANDATORY;
import static org.mifos.application.accounts.financial.util.helpers.CategoryConstants.WRITEOFFS;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.CUSTOMERACCOUNTMISCFEESPOSTING;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.DISBURSAL;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.FEEPOSTING;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.INTERESTPOSTING;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.MANDATORYDEPOSIT;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.MANDATORYDEPOSIT_ADJUSTMENT;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.MANDATORYWITHDRAWAL;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.MANDATORYWITHDRAWAL_ADJUSTMENT;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.MISCFEEPOSTING;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.MISCPENALTYPOSTING;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.PENALTYPOSTING;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.PRINCIPALPOSTING;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.REVERSAL_ADJUSTMENT;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.ROUNDING;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.SAVINGS_INTERESTPOSTING;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.VOLUNTORYDEPOSIT;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.VOLUNTORYDEPOSIT_ADJUSTMENT;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.VOLUNTORYWITHDRAWAL;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.VOLUNTORYWITHDRAWAL_ADJUSTMENT;
import static org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants.WRITEOFF;

import java.util.HashMap;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.FilePaths;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestFinancialRules {
	private static Map financialActionToCategoryDebit = new HashMap<FinancialActionConstants, Short>();
	private static Map financialActionToCategoryCredit = new HashMap<FinancialActionConstants, Short>();

	private static ApplicationContext context;
	
	@BeforeClass
	public static void init() throws Exception {
		{
			financialActionToCategoryDebit.put(PRINCIPALPOSTING, 		BANKACCOUNTONE);
			financialActionToCategoryDebit.put(INTERESTPOSTING, 		BANKACCOUNTONE);
			financialActionToCategoryDebit.put(FEEPOSTING, 				BANKACCOUNTONE);
			financialActionToCategoryDebit.put(PENALTYPOSTING, 			BANKBALANCES);
			financialActionToCategoryDebit.put(ROUNDING, 				ROUNDINGGL);
			financialActionToCategoryDebit.put(MANDATORYDEPOSIT, 		BANKACCOUNTONE);
			financialActionToCategoryDebit.put(VOLUNTORYDEPOSIT, 		BANKACCOUNTONE);
			financialActionToCategoryDebit.put(MANDATORYWITHDRAWAL,		MANDATORYSAVINGS);
			financialActionToCategoryDebit.put(VOLUNTORYWITHDRAWAL, 	CLIENTSDEPOSITS);
			financialActionToCategoryDebit.put(SAVINGS_INTERESTPOSTING,	DIRECTEXPENDITURE);
			financialActionToCategoryDebit.put(DISBURSAL,	 			LOANTOCLIENTS);
			financialActionToCategoryDebit.put(MISCFEEPOSTING, 			BANKACCOUNTONE);
			financialActionToCategoryDebit.put(MISCPENALTYPOSTING, 		BANKACCOUNTONE);
			financialActionToCategoryDebit.put(CUSTOMERACCOUNTMISCFEESPOSTING, 	BANKACCOUNTONE);
			financialActionToCategoryDebit.put(MANDATORYDEPOSIT_ADJUSTMENT, 	MANDATORYSAVINGS);
			financialActionToCategoryDebit.put(VOLUNTORYDEPOSIT_ADJUSTMENT, 	CLIENTSDEPOSITS);
			financialActionToCategoryDebit.put(MANDATORYWITHDRAWAL_ADJUSTMENT, 	BANKACCOUNTONE);
			financialActionToCategoryDebit.put(VOLUNTORYWITHDRAWAL_ADJUSTMENT, 	BANKACCOUNTONE);
			financialActionToCategoryDebit.put(WRITEOFF,				 		WRITEOFFS);

			financialActionToCategoryCredit.put(PRINCIPALPOSTING, 			LOANSADVANCES);
			financialActionToCategoryCredit.put(INTERESTPOSTING, 			INTERESTINCOMELOANS);
			financialActionToCategoryCredit.put(FEEPOSTING, 				INCOMEMICROCREDIT);
			financialActionToCategoryCredit.put(PENALTYPOSTING, 			PENALTY);
			financialActionToCategoryCredit.put(ROUNDING, 					ROUNDINGGL);
			financialActionToCategoryCredit.put(MANDATORYDEPOSIT, 			MANDATORYSAVINGS);
			financialActionToCategoryCredit.put(VOLUNTORYDEPOSIT, 			CLIENTSDEPOSITS);
			financialActionToCategoryCredit.put(MANDATORYWITHDRAWAL, 		BANKACCOUNTONE);
			financialActionToCategoryCredit.put(VOLUNTORYWITHDRAWAL, 	 	BANKACCOUNTONE);
			financialActionToCategoryCredit.put(SAVINGS_INTERESTPOSTING, 	SAVINGSMANDATORY);
			financialActionToCategoryCredit.put(DISBURSAL, 					BANKACCOUNTONE);
			financialActionToCategoryCredit.put(MISCFEEPOSTING, 			FEES);
			financialActionToCategoryCredit.put(MISCPENALTYPOSTING, 		PENALTY);
			financialActionToCategoryCredit.put(CUSTOMERACCOUNTMISCFEESPOSTING, FEES);
			financialActionToCategoryCredit.put(MANDATORYDEPOSIT_ADJUSTMENT, 	BANKACCOUNTONE);
			financialActionToCategoryCredit.put(VOLUNTORYDEPOSIT_ADJUSTMENT, 	BANKACCOUNTONE);
			financialActionToCategoryCredit.put(MANDATORYWITHDRAWAL_ADJUSTMENT,	MANDATORYSAVINGS);
			financialActionToCategoryCredit.put(VOLUNTORYWITHDRAWAL_ADJUSTMENT,	CLIENTSDEPOSITS);
			financialActionToCategoryCredit.put(WRITEOFF, 						LOANTOCLIENTS);
		}

		DatabaseSetup.initializeHibernate();		
		MifosLogManager.configure(FilePaths.LOGFILE);
		
		context = new ClassPathXmlApplicationContext(
			"org/mifos/config/applicationContext.xml");
		
	}
	
	@Test
	public void testGetCategoryAssociatedToActionBankbalanceSucess()
			throws FinancialException {
		assertEquals(FinancialRules.getCategoryAssociatedToAction(
				FinancialActionConstants.PRINCIPALPOSTING, FinancialConstants.DEBIT),
				CategoryConstants.BANKACCOUNTONE);
	}

	@Test	
	public void testGetCategoryAssociatedToActionException() {
		try {
			FinancialRules.getCategoryAssociatedToAction((short) -1, FinancialConstants.DEBIT);
			fail();
		} catch (Exception fine) {

			assertTrue(true);

		}
	}

	@Test	
	public void testGetCategoryAssociatedToActionLoanclientSucess()
			throws FinancialException {
		assertEquals(FinancialRules.getCategoryAssociatedToAction(
				FinancialActionConstants.PRINCIPALPOSTING, FinancialConstants.CREDIT),
				CategoryConstants.LOANSADVANCES);
	}
	
	@Test	
	public void testAllCategories() throws Exception {									
		
		Short[] transactionTypes = {FinancialConstants.DEBIT, FinancialConstants.CREDIT};
		
		for (FinancialActionConstants financialAction : FinancialActionConstants.values()) {
			for (Short transactionType : transactionTypes) {
				// TODO: REVERSAL_ADJUSTMENT is not handled by the current implementation
				if (financialAction != REVERSAL_ADJUSTMENT) {
					if (transactionType == FinancialConstants.DEBIT) {
						assertEquals(financialActionToCategoryDebit.get(financialAction), 
								FinancialRules.getCategoryAssociatedToAction(financialAction, transactionType));
					} else if (transactionType == FinancialConstants.CREDIT) {
						assertEquals(financialActionToCategoryCredit.get(financialAction), 
								FinancialRules.getCategoryAssociatedToAction(financialAction, transactionType));					
					} else {
						fail("Unrecognized transaction type");
					}
				}
			}
		}
	}

	@Test	
	public void testAllCategoriesDynamic() throws Exception {									
		
		Short[] transactionTypes = {FinancialConstants.DEBIT, FinancialConstants.CREDIT};
		
		DynamicFinancialRules financialRulesDynamic = (DynamicFinancialRules)context.getBean("dynamicFinancialRules");
//		DynamicFinancialRules financialRulesDynamic = new DynamicFinancialRules();
//		financialRulesDynamic.initByGlCode();
		
		for (FinancialActionConstants financialAction : FinancialActionConstants.values()) {
			for (Short transactionType : transactionTypes) {
				// TODO: REVERSAL_ADJUSTMENT is not handled by the current implementation
				if (financialAction != REVERSAL_ADJUSTMENT) {
					if (transactionType == FinancialConstants.DEBIT) {
						assertEquals(financialActionToCategoryDebit.get(financialAction), 
								financialRulesDynamic.getCategoryAssociatedToAction(financialAction, transactionType));
					} else if (transactionType == FinancialConstants.CREDIT) {
						assertEquals(financialActionToCategoryCredit.get(financialAction), 
								financialRulesDynamic.getCategoryAssociatedToAction(financialAction, transactionType));					
					} else {
						fail("Unrecognized transaction type");
					}
				}
			}
		}
	}
	
	public static junit.framework.Test testSuite() {
		return new JUnit4TestAdapter(TestFinancialRules.class);
	}
	
}
