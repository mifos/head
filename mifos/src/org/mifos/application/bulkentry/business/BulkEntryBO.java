/**

 * BulkEntryBO.java    version: 1.0

 

 * Copyright © 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied.  

 *

 */
package org.mifos.application.bulkentry.business;

import java.sql.Date;
import java.util.List;

import org.mifos.application.bulkentry.util.helpers.BulkEntryDataView;
import org.mifos.application.bulkentry.util.helpers.BulkEntryNodeBuilder;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.persistence.service.CustomerPersistenceService;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.master.business.PaymentTypeView;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;

/**
 * @author rohitr
 * 
 */
public class BulkEntryBO extends BusinessObject {

	private CustomerPersistenceService customerDbService;

	private PersonnelView loanOfficer;

	private OfficeView office;

	private PaymentTypeView paymentType;

	private BulkEntryView bulkEntryParent;

	private String receiptId;

	private Date receiptDate;

	private Date transactionDate;

	private List<PrdOfferingBO> loanProducts;

	private List<PrdOfferingBO> savingsProducts;

	private int totalCustomers;

	public BulkEntryBO() {
		super();
		customerDbService = new CustomerPersistenceService();
	}

	public BulkEntryBO(UserContext userContext) {
		super(userContext);
		customerDbService = new CustomerPersistenceService();
	}

	public BulkEntryView getBulkEntryParent() {
		return bulkEntryParent;
	}

	public void setBulkEntryParent(BulkEntryView bulkEntryParent) {
		this.bulkEntryParent = bulkEntryParent;
	}

	public PersonnelView getLoanOfficer() {
		return loanOfficer;
	}

	public void setLoanOfficer(PersonnelView loanOfficer) {
		this.loanOfficer = loanOfficer;
	}

	public OfficeView getOffice() {
		return office;
	}

	public void setOffice(OfficeView office) {
		this.office = office;
	}

	public PaymentTypeView getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentTypeView paymentType) {
		this.paymentType = paymentType;
	}

	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public List<PrdOfferingBO> getLoanProducts() {
		return loanProducts;
	}

	public void setLoanProducts(List<PrdOfferingBO> loanProducts) {
		this.loanProducts = loanProducts;
	}

	public List<PrdOfferingBO> getSavingsProducts() {
		return savingsProducts;
	}

	public void setSavingsProducts(List<PrdOfferingBO> savingsProducts) {
		this.savingsProducts = savingsProducts;
	}

	public int getTotalCustomers() {
		return totalCustomers;
	}

	public void setTotalCustomers(int totalCustomers) {
		this.totalCustomers = totalCustomers;
	}

	/**
	 * This method populates all the data required for the BulkEntry. This
	 * method retrieves all the customers and there respective accounts. This
	 * forms a BulkEntry object which can be used to display data in BulkEntry
	 * page.
	 * 
	 */
	public void buildBulkEntryView(CustomerView parentCustomer)
			throws SystemException, ApplicationException {
		List<CustomerView> allChildNodes = retrieveChildrenForParents(
				parentCustomer.getCustomerId(), parentCustomer
						.getCustomerSearchId(), office.getOfficeId());
		totalCustomers = allChildNodes.size();
		bulkEntryParent = BulkEntryNodeBuilder.buildBulkEntryNode(
				allChildNodes, parentCustomer, transactionDate);
		BulkEntryNodeBuilder.buildBulkEntrySavingsAccounts(bulkEntryParent,
				transactionDate);
	}

	private List<CustomerView> retrieveChildrenForParents(Integer customerId,
			String searchId, Short officeId) throws SystemException,
			ApplicationException {
		return customerDbService.getChildrenForParent(customerId, searchId,
				officeId);
	}

	public void setBulkEntryDataView(BulkEntryDataView bulkEntryDataView) {
		if (null == bulkEntryParent || null == bulkEntryDataView) {
			return;
		}
		if (bulkEntryParent.getCustomerDetail().getCustomerLevelId().equals(
				CustomerConstants.CENTER_LEVEL_ID)) {
			populateCenter(bulkEntryDataView);
		} else {
			populateGroup(bulkEntryDataView);
		}
	}

	private void populateCenter(BulkEntryDataView bulkEntryDataView) {
		List<BulkEntryView> bulkEntryChildrenViews = bulkEntryParent
				.getBulkEntryChildren();
		int rowIndex = 0;
		for (BulkEntryView bulkEntryChildernView : bulkEntryChildrenViews) {
			List<BulkEntryView> bulkEntrySubChildrenViews = bulkEntryChildernView
					.getBulkEntryChildren();
			for (BulkEntryView bulkEntrySubChildView : bulkEntrySubChildrenViews) {
				setLoanAmountEntered(bulkEntrySubChildView, rowIndex,
						bulkEntryDataView.getLoanAmountEntered(),
						bulkEntryDataView.getDisbursementAmountEntered());
				setSavingsAmountEntered(bulkEntrySubChildView, rowIndex,
						bulkEntryDataView.getDepositAmountEntered(),
						bulkEntryDataView.getWithDrawalAmountEntered());
				setCustomerAccountAmountEntered(bulkEntrySubChildView,
						rowIndex, bulkEntryDataView
								.getCustomerAccountAmountEntered());
				setClientAttendance(bulkEntrySubChildView, bulkEntryDataView
						.getAttendance()[rowIndex]);
				rowIndex++;
			}
			setLoanAmountEntered(bulkEntryChildernView, rowIndex,
					bulkEntryDataView.getLoanAmountEntered(), bulkEntryDataView
							.getDisbursementAmountEntered());
			setSavingsAmountEntered(bulkEntryChildernView, rowIndex,
					bulkEntryDataView.getDepositAmountEntered(),
					bulkEntryDataView.getWithDrawalAmountEntered());
			setCustomerAccountAmountEntered(bulkEntryChildernView, rowIndex,
					bulkEntryDataView.getCustomerAccountAmountEntered());
			rowIndex++;
		}
		setSavingsAmountEntered(bulkEntryParent, rowIndex, bulkEntryDataView
				.getDepositAmountEntered(), bulkEntryDataView
				.getWithDrawalAmountEntered());
		setCustomerAccountAmountEntered(bulkEntryParent, rowIndex,
				bulkEntryDataView.getCustomerAccountAmountEntered());
	}

	private void populateGroup(BulkEntryDataView bulkEntryDataView) {
		int rowIndex = 0;
		List<BulkEntryView> bulkEntrySubChildrens = bulkEntryParent
				.getBulkEntryChildren();
		for (BulkEntryView bulkEntrySubChildView : bulkEntrySubChildrens) {
			setLoanAmountEntered(bulkEntrySubChildView, rowIndex,
					bulkEntryDataView.getLoanAmountEntered(), bulkEntryDataView
							.getDisbursementAmountEntered());
			setSavingsAmountEntered(bulkEntrySubChildView, rowIndex,
					bulkEntryDataView.getDepositAmountEntered(),
					bulkEntryDataView.getWithDrawalAmountEntered());
			setCustomerAccountAmountEntered(bulkEntrySubChildView, rowIndex,
					bulkEntryDataView.getCustomerAccountAmountEntered());
			setClientAttendance(bulkEntrySubChildView, bulkEntryDataView
					.getAttendance()[rowIndex]);
			rowIndex++;
		}

		setLoanAmountEntered(bulkEntryParent, rowIndex, bulkEntryDataView
				.getLoanAmountEntered(), bulkEntryDataView
				.getDisbursementAmountEntered());
		setSavingsAmountEntered(bulkEntryParent, rowIndex, bulkEntryDataView
				.getDepositAmountEntered(), bulkEntryDataView
				.getWithDrawalAmountEntered());
		setCustomerAccountAmountEntered(bulkEntryParent, rowIndex,
				bulkEntryDataView.getCustomerAccountAmountEntered());
	}

	private void setLoanAmountEntered(BulkEntryView bulkEntryView,
			int rowIndex, String[][] loanAmountsEntered,
			String[][] disBursementAmountEntered) {
		int columnIndex = 0;
		for (PrdOfferingBO prdOffering : loanProducts) {
			String enteredAmountValue = loanAmountsEntered[rowIndex][columnIndex];
			String disbursementAmountEntered = disBursementAmountEntered[rowIndex][columnIndex];
			bulkEntryView.setLoanAmountsEntered(prdOffering.getPrdOfferingId(),
					enteredAmountValue, disbursementAmountEntered);
			columnIndex++;
		}
	}

	private void setSavingsAmountEntered(BulkEntryView bulkEntryView,
			int rowIndex, String[][] depositAmountsEntered,
			String[][] withDrawalsAmountEntered) {
		int columnIndex = 0;
		for (PrdOfferingBO prdOffering : savingsProducts) {
			String depositAmountEnteredValue = depositAmountsEntered[rowIndex][columnIndex];
			String withDrawalAmountEnteredValue = withDrawalsAmountEntered[rowIndex][columnIndex];
			if (depositAmountEnteredValue != null
					|| withDrawalAmountEnteredValue != null) {
				bulkEntryView.setSavinsgAmountsEntered(prdOffering
						.getPrdOfferingId(), depositAmountEnteredValue,
						withDrawalAmountEnteredValue);
			}

			columnIndex++;
		}
	}

	private void setCustomerAccountAmountEntered(BulkEntryView bulkEntryView,
			int rowIndex, String[] customerAccountAmountEntered) {
		String customerAccountAmountEnteredValue = customerAccountAmountEntered[rowIndex];
		if (customerAccountAmountEnteredValue != null)
			bulkEntryView
					.setCustomerAccountAmountEntered(customerAccountAmountEnteredValue);
	}

	private void setClientAttendance(BulkEntryView bulkEntryView,
			String attendance) {
		if (null != attendance) {
			bulkEntryView.setAttendence(attendance);
		}
	}

	@Override
	public Short getEntityID() {
		return null;
	}
}
