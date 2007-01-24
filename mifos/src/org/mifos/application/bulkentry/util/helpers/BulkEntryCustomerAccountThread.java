package org.mifos.application.bulkentry.util.helpers;

import java.sql.Date;
import java.util.List;

import org.mifos.application.bulkentry.business.service.BulkEntryBusinessService;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class BulkEntryCustomerAccountThread implements Runnable {

	List<CustomerAccountView> customerAccounts;

	Short personnelId;

	String recieptId;

	Short paymentId;

	Date receiptDate;

	Date transactionDate;

	List<String> accountNums;

	StringBuffer isActivityDone;

	public BulkEntryCustomerAccountThread(
			List<CustomerAccountView> customerAccounts, Short personnelId,
			String recieptId, Short paymentId, Date receiptDate,
			Date transactionDate, List<String> accountNums,
			StringBuffer isActivityDone) {
		this.customerAccounts = customerAccounts;
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
			BulkEntryBusinessService bulkEntryBusinessService = 
				new BulkEntryBusinessService();
			for (CustomerAccountView customerAccountView : customerAccounts) {
				if (null != customerAccountView) {
					String amount = customerAccountView
							.getCustomerAccountAmountEntered();
					if (null != amount && !Double.valueOf(amount).equals(0.0)) {
						try {
							bulkEntryBusinessService
									.saveCustomerAccountCollections(
											customerAccountView, personnelId,
											recieptId, paymentId, receiptDate,
											transactionDate);
							HibernateUtil.commitTransaction();
						} catch (ServiceException be) {
							accountNums.add((String) (be.getValues()[0]));
							HibernateUtil.rollbackTransaction();
						} catch (Exception e) {
							accountNums.add(customerAccountView.getAccountId()
									.toString());
							HibernateUtil.rollbackTransaction();
						} finally {
							HibernateUtil.closeSession();
						}
					}
				}
			}
		} finally {
			isActivityDone.append("Done");
		}
	}

}
