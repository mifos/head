/**

 * BulkEntryAction.java    version: 1.0

 

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

package org.mifos.application.bulkentry.struts.action;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.business.CustomerAccountView;
import org.mifos.application.accounts.business.LoanAccountsProductView;
import org.mifos.application.accounts.business.SavingsAccountView;
import org.mifos.application.bulkentry.business.BulkEntryBO;
import org.mifos.application.bulkentry.business.BulkEntryView;
import org.mifos.application.bulkentry.business.service.BulkEntryBusinessService;
import org.mifos.application.bulkentry.exceptions.BulkEntryAccountUpdateException;
import org.mifos.application.bulkentry.struts.actionforms.BulkEntryActionForm;
import org.mifos.application.bulkentry.util.helpers.BulkEntryConstants;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.master.business.PaymentTypeView;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.master.util.valueobjects.LookUpMaster;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.persistence.service.OfficePersistenceService;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;

public class BulkEntryAction extends BaseAction {
	private BulkEntryBusinessService bulkEntryBusinessService;

	private MasterDataService masterService;

	public BulkEntryAction() throws ServiceException {
		bulkEntryBusinessService = (BulkEntryBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.BulkEntryService);
		masterService = (MasterDataService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.MasterDataService);
	}

	@Override
	protected BusinessService getService() {
		return bulkEntryBusinessService;
	}

	protected boolean startSession() {
		return false;
	}

	/**
	 * This method is called before the load page for center is called It sets
	 * this information in session and context.This should be removed after
	 * center was successfully created.
	 */
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			HttpSession session = request.getSession();
			session.setAttribute(BulkEntryConstants.BULKENTRYACTIONFORM, null);
			session.setAttribute(Constants.BUSINESS_KEY, null);
			session.setAttribute(BulkEntryConstants.BULKENTRY, null);
			UserContext userContext = (UserContext) session
					.getAttribute(Constants.USER_CONTEXT_KEY);
			List<OfficeView> activeBranches = masterService
					.getActiveBranches(userContext.getBranchId());
			session.setAttribute(OfficeConstants.OFFICESBRANCHOFFICESLIST,
					activeBranches);
			boolean isCenterHeirarchyExists = Configuration.getInstance()
					.getCustomerConfig(
							new OfficePersistenceService().getHeadOffice()
									.getOfficeId()).isCenterHierarchyExists();
			session.setAttribute(BulkEntryConstants.ISCENTERHEIRARCHYEXISTS,
					isCenterHeirarchyExists ? Constants.YES : Constants.NO);
			if (activeBranches.size() == 1) {
				List<PersonnelView> loanOfficers = loadLoanOfficersForBranch(
						userContext, activeBranches.get(0).getOfficeId());
				session.setAttribute(CustomerConstants.LOAN_OFFICER_LIST,
						loanOfficers);
				if (loanOfficers.size() == 1) {
					List<CustomerView> parentCustomerList = loadCustomers(
							loanOfficers.get(0).getPersonnelId(),
							activeBranches.get(0).getOfficeId());
					session.setAttribute(BulkEntryConstants.CUSTOMERSLIST,
							parentCustomerList);
					request.setAttribute(BulkEntryConstants.REFRESH,
							Constants.NO);
				} else {
					session.setAttribute(BulkEntryConstants.CUSTOMERSLIST,
							new ArrayList<CustomerView>());
					request.setAttribute(BulkEntryConstants.REFRESH,
							Constants.YES);
				}
			} else {
				session.setAttribute(CustomerConstants.LOAN_OFFICER_LIST,
						new ArrayList<PersonnelView>());
				session.setAttribute(BulkEntryConstants.CUSTOMERSLIST,
						new ArrayList<CustomerView>());
				request.setAttribute(BulkEntryConstants.REFRESH, Constants.YES);
			}
			session
					.setAttribute(
							BulkEntryConstants.PAYMENT_TYPES_LIST,
							masterService
									.getMasterData(
											MasterConstants.PAYMENT_TYPE,
											userContext.getLocaleId(),
											"org.mifos.application.productdefinition.util.valueobjects.PaymentType",
											"paymentTypeId").getLookUpMaster());
			session.setAttribute(BulkEntryConstants.ISBACKDATEDTRXNALLOWED,
					Constants.NO);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapping.findForward(BulkEntryConstants.LOADSUCCESS);
	}

	/**
	 * This method retrieves the last meeting date for the chosen customer. This
	 * meeting date is put as the default date for the tranasaction date in the
	 * search criteria
	 * 
	 */
	public ActionForward getLastMeetingDateForCustomer(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		BulkEntryActionForm actionForm = (BulkEntryActionForm) form;
		try {

			Date meetingDate = bulkEntryBusinessService
					.getLastMeetingDateForCustomer(Integer.valueOf(actionForm
							.getCustomerId()));
			if (meetingDate != null) {

				actionForm.setTransactionDate(DateHelper.getUserLocaleDate(
						getUserLocale(request), meetingDate.toString()));
			} else {
				actionForm.setTransactionDate(DateHelper
						.getCurrentDate(getUserLocale(request)));
			}

			session.setAttribute("LastMeetingDate", meetingDate);
			boolean isBackDatedTrxnAllowed = false;
			if (actionForm.getOfficeId() != null)
				isBackDatedTrxnAllowed = Configuration.getInstance()
						.getAccountConfig(
								Short.valueOf(actionForm.getOfficeId()))
						.isBackDatedTxnAllowed();
			session.setAttribute(BulkEntryConstants.ISBACKDATEDTRXNALLOWED,
					isBackDatedTrxnAllowed ? Constants.YES : Constants.NO);
		} catch (SystemException se) {

		} catch (ApplicationException ae) {

		}

		return mapping.findForward(BulkEntryConstants.LOADSUCCESS);
	}

	public ActionForward loadLoanOfficers(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		UserContext userContext = (UserContext) session
				.getAttribute(Constants.USER_CONTEXT_KEY);
		BulkEntryActionForm bulkEntryActionForm = (BulkEntryActionForm) form;
		Short officeId = Short.valueOf(bulkEntryActionForm.getOfficeId());
		List<PersonnelView> loanOfficers = loadLoanOfficersForBranch(
				userContext, officeId);
		session.setAttribute(CustomerConstants.LOAN_OFFICER_LIST, loanOfficers);
		return mapping.findForward(BulkEntryConstants.LOADSUCCESS);
	}

	public ActionForward loadCustomerList(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		BulkEntryActionForm bulkEntryActionForm = (BulkEntryActionForm) form;
		Short personnelId = Short.valueOf(bulkEntryActionForm
				.getLoanOfficerId());
		Short officeId = Short.valueOf(bulkEntryActionForm.getOfficeId());
		List<CustomerView> parentCustomerList = loadCustomers(personnelId,
				officeId);
		session.setAttribute(BulkEntryConstants.CUSTOMERSLIST,
				parentCustomerList);
		boolean isCenterHeirarchyExists = Configuration.getInstance()
				.getCustomerConfig(officeId).isCenterHierarchyExists();
		session.setAttribute(BulkEntryConstants.ISCENTERHEIRARCHYEXISTS,
				isCenterHeirarchyExists ? Constants.YES : Constants.NO);
		return mapping.findForward(BulkEntryConstants.LOADSUCCESS);
	}

	private List<PersonnelView> loadLoanOfficersForBranch(
			UserContext userContext, Short branchId) {
		return masterService.getListOfActiveLoanOfficers(
				PersonnelConstants.LOAN_OFFICER, branchId, userContext.getId(),
				userContext.getLevelId());
	}

	/**
	 * This method loads either the centers or groups under a particular loan
	 * officer as this list of parent customer If the center hierarchy exists,
	 * then the list of centers under the loan officer is retrieved as the list
	 * of parent customers, else it is the list of groups.
	 * 
	 * @throws SystemException
	 */
	private List<CustomerView> loadCustomers(Short personnelId, Short officeId)
			throws SystemException {
		Short customerLevel;
		if (Configuration.getInstance().getCustomerConfig(officeId)
				.isCenterHierarchyExists()) {
			customerLevel = new Short(CustomerConstants.CENTER_LEVEL_ID);
		} else {
			customerLevel = new Short(CustomerConstants.GROUP_LEVEL_ID);
		}
		List<CustomerView> activeParentUnderLoanOfficer = masterService
				.getListOfActiveParentsUnderLoanOfficer(personnelId,
						customerLevel, officeId);
		return activeParentUnderLoanOfficer;
	}

	/**
	 * This method is called once the search criterias have been entered by the
	 * user to generate the bulk entry details for a particular customer It
	 * retrieves the loan officer office, and parent customer that was selected
	 * and sets them into the bulk entry business object. The list of attendance
	 * types and product list associated with the center, and its children are
	 * also retrieved
	 */
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			HttpSession session = request.getSession();
			UserContext userContext = (UserContext) session
					.getAttribute(Constants.USER_CONTEXT_KEY);
			Date meetingDate = (Date) session.getAttribute("LastMeetingDate");
			BulkEntryBO bulkEntry = (BulkEntryBO) session
					.getAttribute(Constants.BUSINESS_KEY);
			PersonnelView loanOfficer = getSelectedLO(session, form);
			bulkEntry.setLoanOfficer(loanOfficer);
			bulkEntry.setOffice(getSelectedBranchOffice(session, form));
			bulkEntry.setPaymentType(getSelectedPaymentType(session, form));
			CustomerView parentCustomer = getSelectedCustomer(session, form);
			bulkEntry.buildBulkEntryView(parentCustomer);
			bulkEntry.setLoanProducts(masterService
					.getLoanProductsAsOfMeetingDate(meetingDate, parentCustomer
							.getCustomerSearchId(), loanOfficer
							.getPersonnelId()));
			bulkEntry.setSavingsProducts(masterService
					.getSavingsProductsAsOfMeetingDate(meetingDate,
							parentCustomer.getCustomerSearchId(), loanOfficer
									.getPersonnelId()));
			session.setAttribute(BulkEntryConstants.BULKENTRY, bulkEntry);
			session
					.setAttribute(
							BulkEntryConstants.CUSTOMERATTENDANCETYPES,
							masterService
									.getMasterData(
											MasterConstants.ATTENDENCETYPES,
											userContext.getLocaleId(),
											"org.mifos.application.master.business.CustomerAttendance",
											"attendanceId").getLookUpMaster());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapping.findForward(BulkEntryConstants.GETSUCCESS);
	}

	/**
	 * This method retrieves the loan officer which was selected from the list
	 * of loan officers
	 * 
	 */
	private PersonnelView getSelectedLO(HttpSession session, ActionForm form) {
		BulkEntryActionForm bulkEntryForm = (BulkEntryActionForm) form;
		Short personnelId = Short.valueOf(bulkEntryForm.getLoanOfficerId());
		List<PersonnelView> loanOfficerList = (List<PersonnelView>) session
				.getAttribute(CustomerConstants.LOAN_OFFICER_LIST);
		for (PersonnelView loanOfficer : loanOfficerList) {
			if (personnelId.shortValue() == loanOfficer.getPersonnelId()
					.shortValue()) {
				return loanOfficer;
			}
		}
		return null;
	}

	/**
	 * This method retrieves the branch office which was selected from the list
	 * of branch offices
	 * 
	 */
	private OfficeView getSelectedBranchOffice(HttpSession session,
			ActionForm form) {
		BulkEntryActionForm bulkEntryForm = (BulkEntryActionForm) form;
		Short officeId = Short.valueOf(bulkEntryForm.getOfficeId());
		List<OfficeView> branchList = (List<OfficeView>) session
				.getAttribute(OfficeConstants.OFFICESBRANCHOFFICESLIST);
		for (OfficeView branch : branchList) {
			if (officeId.shortValue() == branch.getOfficeId().shortValue()) {
				return branch;
			}
		}
		return null;
	}

	/**
	 * This method retrieves the parent customer which was selected from the
	 * list of customers which belong to a particualr branch and have a
	 * particular loan officer
	 */
	private CustomerView getSelectedCustomer(HttpSession session,
			ActionForm form) {
		int i = 0;
		BulkEntryActionForm bulkEntryForm = (BulkEntryActionForm) form;
		Integer customerId = Integer.valueOf(bulkEntryForm.getCustomerId());
		List<CustomerView> parentCustomerList = (List<CustomerView>) session
				.getAttribute(BulkEntryConstants.CUSTOMERSLIST);
		for (i = 0; i < parentCustomerList.size(); i++) {
			if (customerId.intValue() == parentCustomerList.get(i)
					.getCustomerId().intValue()) {
				break;
			}
		}
		return parentCustomerList.get(i);
	}

	/**
	 * This method retrieves the payment type which was selected from the list
	 * of payement types
	 */
	private PaymentTypeView getSelectedPaymentType(HttpSession session,
			ActionForm form) {
		int i = 0;
		BulkEntryActionForm bulkEntryForm = (BulkEntryActionForm) form;
		Short paymentTypeId = Short.valueOf(bulkEntryForm.getPaymentId());
		List<LookUpMaster> paymentTypeList = (List<LookUpMaster>) session
				.getAttribute(BulkEntryConstants.PAYMENT_TYPES_LIST);
		for (i = 0; i < paymentTypeList.size(); i++) {
			if (paymentTypeId.shortValue() == paymentTypeList.get(i).getId()
					.shortValue()) {
				break;
			}
		}
		PaymentTypeView paymentType = new PaymentTypeView();
		paymentType.setPaymentTypeId(paymentTypeList.get(i).getId()
				.shortValue());
		paymentType
				.setPaymentTypeValue(paymentTypeList.get(i).getLookUpValue());
		return paymentType;
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(BulkEntryConstants.PREVIEWSUCCESS);
	}

	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(BulkEntryConstants.PREVIUOSSUCCESS);
	}

	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = null;
		String methodCalled = request.getParameter(BulkEntryConstants.METHOD);
		String input = request.getParameter("input");
		if (null != methodCalled) {
			if ("load".equals(input)) {
				forward = BulkEntryConstants.LOADSUCCESS;
			} else if ("get".equals(input)) {
				forward = BulkEntryConstants.GETSUCCESS;
			}
		}
		if (null != forward) {
			return mapping.findForward(forward);
		}
		return null;
	}

	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		List<String> loanAccountNums = new ArrayList<String>();
		List<String> savingsDepositAccountNums = new ArrayList<String>();
		List<String> savingsWithdrawalsAccountNums = new ArrayList<String>();
		List<String> customerAccountNums = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		ActionErrors actionErrors = new ActionErrors();
		HttpSession session = request.getSession();
		UserContext userContext = (UserContext) session
				.getAttribute(LoginConstants.USERCONTEXT);
		BulkEntryBO bulkEntry = (BulkEntryBO) session
				.getAttribute(BulkEntryConstants.BULKENTRY);
		Date meetingDate = (Date) session.getAttribute("LastMeetingDate");
		Short personnelId = userContext.getId();
		saveData(bulkEntry, personnelId, meetingDate, loanAccountNums,
				savingsDepositAccountNums, savingsWithdrawalsAccountNums,
				customerAccountNums);
		if (loanAccountNums.size() > 0 || savingsDepositAccountNums.size() > 0
				|| savingsWithdrawalsAccountNums.size() > 0
				|| customerAccountNums.size() > 0) {
			getErrorString(builder, loanAccountNums, "Loan");
			getErrorString(builder, savingsDepositAccountNums,
					"Savings Deposit");
			getErrorString(builder, savingsWithdrawalsAccountNums,
					"Savings Withdrawal");
			getErrorString(builder, customerAccountNums, "A/C collections");
			builder.append("<br><br>");
			actionErrors.add(BulkEntryConstants.ERRORSUPDATE,
					new ActionMessage(BulkEntryConstants.ERRORSUPDATE, builder
							.toString()));
			request.setAttribute(Globals.ERROR_KEY, actionErrors);
		}
		return mapping.findForward(BulkEntryConstants.CREATESUCCESS);
	}

	private void getErrorString(StringBuilder builder,
			List<String> accountNums, String message) {
		if (accountNums.size() != 0) {
			ListIterator<String> iter = accountNums.listIterator();
			builder.append("<br>");
			builder.append(message + "-	");
			while (iter.hasNext()) {
				builder.append(iter.next());
				if (iter.hasNext())
					builder.append(", ");
			}
		}
	}

	private void saveData(BulkEntryBO bulkEntry, Short personnelId,
			Date meetingDate, List<String> loanAccountNums,
			List<String> savingsDepositAccountNums,
			List<String> savingsWithdrawalsAccountNums,
			List<String> customerAccountNums) {
		BulkEntryView bulkEntryParentView = bulkEntry.getBulkEntryParent();
		String receiptId = bulkEntry.getReceiptId();
		Short paymentId = bulkEntry.getPaymentType().getPaymentTypeId();
		Date receiptDate = bulkEntry.getReceiptDate();
		Date transactionDate = bulkEntry.getTransactionDate();
		saveCollections(bulkEntryParentView, personnelId, receiptId, paymentId,
				receiptDate, meetingDate, transactionDate, loanAccountNums,
				savingsDepositAccountNums, customerAccountNums);
		saveWithdrawals(bulkEntryParentView, personnelId, receiptId, paymentId,
				receiptDate, transactionDate, meetingDate,
				savingsWithdrawalsAccountNums);
	}

	private void saveCollections(BulkEntryView parent, Short personnelId,
			String receiptId, Short paymentId, Date receiptDate,
			Date meetingDate, Date transactionDate,
			List<String> loanAccountNums,
			List<String> savingsDepositAccountNums,
			List<String> customerAccountNums) {
		List<BulkEntryView> children = parent.getBulkEntryChildren();
		Short levelId = parent.getCustomerDetail().getCustomerLevelId();
		if (null != children) {
			for (BulkEntryView bulkEntryView : children) {
				saveCollections(bulkEntryView, personnelId, receiptId,
						paymentId, receiptDate, meetingDate, transactionDate,
						loanAccountNums, savingsDepositAccountNums,
						customerAccountNums);
			}
		}
		if (!levelId.equals(CustomerConstants.CENTER_LEVEL_ID)) {
			saveLoanAccount(parent.getLoanAccountDetails(), personnelId,
					receiptId, paymentId, receiptDate, transactionDate,
					loanAccountNums);
		}
		if (levelId.equals(CustomerConstants.CLIENT_LEVEL_ID)) {
			saveAttendance(parent, meetingDate);
		}
		saveSavingsCollection(parent.getSavingsAccountDetails(), personnelId,
				receiptId, paymentId, receiptDate, transactionDate,
				savingsDepositAccountNums, levelId, parent.getCustomerDetail()
						.getCustomerId());
		saveCustomerAccountCollections(parent.getCustomerAccountDetails(),
				personnelId, receiptId, paymentId, receiptDate,
				transactionDate, customerAccountNums);
	}

	private void saveCustomerAccountCollections(
			CustomerAccountView customerAccountView, Short personnelId,
			String recieptId, Short paymentId, Date receiptDate,
			Date transactionDate, List<String> accountNums) {
		if (null != customerAccountView) {
			String amount = customerAccountView
					.getCustomerAccountAmountEntered();
			if (null != amount && !Double.valueOf(amount).equals(0.0)) {
				try {
					bulkEntryBusinessService.saveCustomerAccountCollections(
							customerAccountView, personnelId, recieptId,
							paymentId, receiptDate, transactionDate);
					HibernateUtil.commitTransaction();
				} catch (BulkEntryAccountUpdateException be) {
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

	private void saveWithdrawals(BulkEntryView parent, Short personnelId,
			String receiptId, Short paymentId, Date receiptDate,
			Date transactionDate, Date meetingDate,
			List<String> savingsWithdrawalsAccountNums) {
		List<BulkEntryView> children = parent.getBulkEntryChildren();
		if (null != children) {
			for (BulkEntryView bulkEntryView : children) {
				saveWithdrawals(bulkEntryView, personnelId, receiptId,
						paymentId, receiptDate, transactionDate, meetingDate,
						savingsWithdrawalsAccountNums);
			}
		}
		saveSavingsWithdrawals(parent.getSavingsAccountDetails(), personnelId,
				receiptId, paymentId, receiptDate, transactionDate,
				savingsWithdrawalsAccountNums, parent.getCustomerDetail()
						.getCustomerId());
	}

	private void saveSavingsWithdrawals(List<SavingsAccountView> accountViews,
			Short personnelId, String recieptId, Short paymentId,
			Date receiptDate, Date transactionDate, List<String> accountNums,
			Integer customerId) {
		if (null != accountViews) {
			for (SavingsAccountView accountView : accountViews) {
				String amount = accountView.getWithDrawalAmountEntered();
				if (null != amount && !"".equals(amount.trim())
						&& !Double.valueOf(amount).equals(0.0)) {
					try {

						bulkEntryBusinessService.saveSavingsWithdrawalAccount(
								accountView, personnelId, recieptId, paymentId,
								receiptDate, transactionDate, customerId);
						HibernateUtil.commitTransaction();
					} catch (BulkEntryAccountUpdateException be) {
						accountNums.add((String) (be.getValues()[0]));
						HibernateUtil.rollbackTransaction();
					} catch (Exception e) {
						accountNums.add(accountView.getAccountId().toString());
						HibernateUtil.rollbackTransaction();

					} finally {
						HibernateUtil.closeSession();
					}
				}
			}
		}
	}

	private void saveLoanAccount(List<LoanAccountsProductView> accountViews,
			Short personnelId, String recieptId, Short paymentId,
			Date receiptDate, Date transactionDate, List<String> accountNums) {
		if (null != accountViews) {
			for (LoanAccountsProductView loanAccountsProductView : accountViews) {
				try {
					bulkEntryBusinessService.saveLoanAccount(
							loanAccountsProductView, personnelId, recieptId,
							paymentId, receiptDate, transactionDate);
					HibernateUtil.commitTransaction();
				} catch (BulkEntryAccountUpdateException be) {
					accountNums.add((String) (be.getValues()[0]));
					HibernateUtil.rollbackTransaction();
				} catch (Exception e) {
					accountNums
							.add("Accounts for "
									+ loanAccountsProductView
											.getPrdOfferingShortName());
					HibernateUtil.rollbackTransaction();
				} finally {
					HibernateUtil.closeSession();
				}
			}
		}
	}

	private void saveSavingsCollection(List<SavingsAccountView> accountViews,
			Short personnelId, String recieptId, Short paymentId,
			Date receiptDate, Date transactionDate, List<String> accountNums,
			Short levelId, Integer customerId) {
		if (null != accountViews) {
			for (SavingsAccountView accountView : accountViews) {
				String amount = accountView.getDepositAmountEntered();
				if (null != amount && !Double.valueOf(amount).equals(0.0)) {
					try {
						boolean isCenterGroupIndvAccount = false;
						if (levelId.equals(CustomerConstants.CENTER_LEVEL_ID)
								|| (levelId
										.equals(CustomerConstants.GROUP_LEVEL_ID) && accountView
										.getSavingsOffering()
										.getRecommendedAmntUnit()
										.getRecommendedAmntUnitId()
										.equals(
												ProductDefinitionConstants.PERINDIVIDUAL))) {
							isCenterGroupIndvAccount = true;
						}
						bulkEntryBusinessService.saveSavingsDepositAccount(
								accountView, personnelId, recieptId, paymentId,
								receiptDate, transactionDate,
								isCenterGroupIndvAccount, customerId);
						HibernateUtil.commitTransaction();
					} catch (BulkEntryAccountUpdateException be) {
						accountNums.add((String) (be.getValues()[0]));
						HibernateUtil.rollbackTransaction();
					} catch (Exception e) {
						accountNums.add(accountView.getAccountId().toString());
						HibernateUtil.rollbackTransaction();

					} finally {
						HibernateUtil.closeSession();
					}
				}
			}
		}
	}

	private void saveAttendance(BulkEntryView bulkEntryView, Date meetingDate) {
		try {
			Short attendance = Short.valueOf(bulkEntryView.getAttendence());
			bulkEntryBusinessService.saveAttendance(bulkEntryView
					.getCustomerDetail().getCustomerId(), meetingDate,
					attendance);
			HibernateUtil.commitTransaction();

		} catch (BulkEntryAccountUpdateException e) {
			HibernateUtil.rollbackTransaction();
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}
	}

	protected Locale getUserLocale(HttpServletRequest request) {
		Locale locale = null;
		HttpSession session = request.getSession();
		if (session != null) {
			UserContext userContext = (UserContext) session
					.getAttribute(LoginConstants.USERCONTEXT);
			if (null != userContext) {
				locale = userContext.getPereferedLocale();
				if (null == locale) {
					locale = userContext.getMfiLocale();
				}
			}
		}
		return locale;
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		if (method.equals(BulkEntryConstants.CREATEMETHOD)) {
			return true;
		}
		return false;
	}

}
