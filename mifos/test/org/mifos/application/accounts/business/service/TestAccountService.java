/**
 * 
 */
package org.mifos.application.accounts.business.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.TestAccount;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.TransactionHistoryView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.service.SavingsBusinessService;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.TestObjectFactory;

/**
 * @author krishankg
 *
 */
public class TestAccountService extends TestAccount {
	
	public void testSuccessRemoveFees(){
		AccountBusinessService accountBusinessService=new AccountBusinessService();
		try {
			Set<AccountFeesEntity> accountFeesEntitySet=accountBO.getAccountFees();
			UserContext uc=TestObjectFactory.getUserContext();
			Iterator itr=accountFeesEntitySet.iterator();
			while(itr.hasNext()){
				AccountFeesEntity accountFeesEntity=(AccountFeesEntity)itr.next();
				accountBusinessService.removeFees(accountBO.getAccountId(),accountFeesEntity.getFees().getFeeId(),uc.getId());
				assertTrue(true);
			}
		} catch (Exception e) {
			assertTrue(false);
		}
	}
	
	public void testFailureRemoveFees(){
		AccountBusinessService accountBusinessService=new AccountBusinessService();
		try {
			UserContext uc=TestObjectFactory.getUserContext();
			Set<AccountFeesEntity> accountFeesEntitySet=accountBO.getAccountFees();
			Iterator itr=accountFeesEntitySet.iterator();
			while(itr.hasNext()){
				AccountFeesEntity accountFeesEntity=(AccountFeesEntity)itr.next();
				accountBusinessService.removeFees(Integer.valueOf("-1"),accountFeesEntity.getFees().getFeeId(),uc.getId());
				assertTrue(false);
			}
		} catch (Exception e) {
			assertTrue(true);
		}
	}
 public void testGetTrxnHistory()throws Exception{
	 AccountBusinessService accountBusinessService=new AccountBusinessService();
		Date currentDate = new Date(System.currentTimeMillis());
		LoanBO loan = (LoanBO) accountBO;

	 UserContext uc=TestObjectFactory.getUserContext();
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.addAll(loan.getAccountActionDates());
		PaymentData accountPaymentDataView = TestObjectFactory
				.getLoanAccountPaymentData(accntActionDates, TestObjectFactory
						.getMoneyForMFICurrency(0), null, Short.valueOf("1"),
						"receiptNum", Short.valueOf("1"), currentDate,
						currentDate);

		loan.applyPayment(accountPaymentDataView);
		TestObjectFactory.flushandCloseSession();
		loan = (LoanBO) TestObjectFactory.getObject(AccountBO.class, loan
				.getAccountId());
		loan.setUserContext(uc);

		List<TransactionHistoryView> trxnHistlist =  accountBusinessService.getTrxnHistory(loan,uc);
		assertNotNull("Account TrxnHistoryView list object should not be null",trxnHistlist);
		assertTrue("Account TrxnHistoryView list object Size should be greater than zero",trxnHistlist.size()>0);
		TestObjectFactory.flushandCloseSession();
		accountBO = (LoanBO) TestObjectFactory.getObject(AccountBO.class, loan
				.getAccountId());

 }
 
 	public void testGetAccountAction()throws Exception{
	 	AccountBusinessService service = (AccountBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Accounts);
		AccountActionEntity accountaction = service.getAccountAction(AccountConstants.ACTION_SAVINGS_DEPOSIT, Short.valueOf("1"));
		assertNotNull(accountaction);
		assertEquals(Short.valueOf("1"),accountaction.getLocaleId());
	}
}
