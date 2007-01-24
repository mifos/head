package org.mifos.application.bulkentry.util.helpers;

import java.sql.Date;
import java.util.List;

import org.mifos.application.accounts.loan.util.helpers.LoanAccountsProductView;
import org.mifos.application.bulkentry.business.service.BulkEntryBusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class BulkEntryLoanThread implements Runnable {

	List<LoanAccountsProductView> accountViews;

	Short personnelId;

	String recieptId;

	Short paymentId;

	Date receiptDate;

	Date transactionDate;

	List<String> accountNums;

	StringBuffer isActivityDone;

//	public Exception exception;

	public BulkEntryLoanThread(List<LoanAccountsProductView> accountViews,
			Short personnelId, String recieptId, Short paymentId,
			Date receiptDate, Date transactionDate, List<String> accountNums,
			StringBuffer isActivityDone) {
		this.accountViews = accountViews;
		this.personnelId = personnelId;
		this.recieptId = recieptId;
		this.paymentId = paymentId;
		this.receiptDate = receiptDate;
		this.transactionDate = transactionDate;
		this.accountNums = accountNums;
		this.isActivityDone = isActivityDone;
	}

	public void run() {
		try {
			BulkEntryBusinessService bulkEntryBusinessService = new BulkEntryBusinessService();
			if (null != accountViews) {
				for (LoanAccountsProductView loanAccountsProductView : accountViews) {
					try {
						bulkEntryBusinessService.saveLoanAccount(
								loanAccountsProductView, personnelId,
								recieptId, paymentId, receiptDate,
								transactionDate);
						HibernateUtil.commitTransaction();
					} catch (ServiceException be) {
						accountNums.add((String) (be.getValues()[0]));
						HibernateUtil.rollbackTransaction();
					} finally {
						HibernateUtil.closeSession();
					}
				}
			}
		/* Commented out until we can figure out what is up
		   with TestBulkEntryAction#SUPPLY_ENTERED_AMOUNT_PARAMETERS
		} catch (Exception e) {
			this.exception = e;
			*/
		} finally {
			isActivityDone.append("Done");
		}
	}

}
