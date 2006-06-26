/**
 
 * LoanActionForm.java    version: xxx
 
 
 
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

package org.mifos.application.accounts.loan.struts.actionforms;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.loan.exceptions.LoanExceptionConstants;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.loan.util.valueobjects.LoanPerfHistory;
import org.mifos.application.accounts.struts.actionforms.AccountActionForm;
import org.mifos.application.accounts.util.valueobjects.Account;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.fund.util.valueobjects.Fund;
import org.mifos.application.master.util.valueobjects.Currency;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.struts.plugin.helper.EntityMasterConstants;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;

/**
 * This class acts as action form for loan accounts.
 * 
 * @author ashishsm
 * 
 */
public class LoanActionForm extends AccountActionForm {

	/**
	 * 
	 */
	public LoanActionForm() {
		super();

	}

	/** The value of the simple loanAmount property. */
	private Money loanAmount;

	private String disbursementDate;

	private Integer gracePeriod;

	private Short intrestAtDisbursement;

	private Integer businessActivityId;

	private Short collateralTypeId;

	private LoanPerfHistory perfHistory;

	private List loanInstallments;

	private Short status;

	private Integer versionNo;

	/** The composite primary key value. */
	private Account account;

	/** The value of the currency association. */
	private Currency currency;

	/** The value of the fund association. */
	private Fund fund;

	/** The value of the interestTypes association. */
	private Short interestTypeId;

	/** The value of the loanPenaltySet one-to-many association. */
	private java.util.Set loanPenaltySet;

	/** The value of the simple gracePeriodDays property. */
	private java.lang.Short gracePeriodDuration;

	/** The value of the simple groupFlag property. */
	private java.lang.Short groupFlag;

	/** The value of the simple loanBalance property. */
	private java.lang.Double loanBalance;

	/** The value of the simple interestRateAmount property. */
	private java.lang.Double interestRateAmount;

	/** The value of the simple noOfInstallments property. */
	private java.lang.Short noOfInstallments;

	/** The value of the simple notes property. */
	private java.lang.String notes;

	private String collateralNote;

	/**
	 * This indicates the state in which the loan account is being saved.
	 */
	private String stateSelected;

	/**
	 * This indicates the prdOffering selected by the user in the UI.
	 */
	private Short selectedPrdOfferingId;

	private java.lang.Double minLoanAmount;

	private java.lang.Double maxLoanAmount;

	private java.lang.Double maxInterestRate;

	private java.lang.Double minInterestRate;

	private java.lang.Double maxNoInstallments;

	private java.lang.Double minNoInstallments;

	/**
	 * @return Returns the account}.
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * @param account
	 *            The account to set.
	 */
	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * @return Returns the currency}.
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * @param currency
	 *            The currency to set.
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	/**
	 * @return Returns the fund}.
	 */
	public Fund getFund() {
		return fund;
	}

	/**
	 * @param fund
	 *            The fund to set.
	 */
	public void setFund(Fund fund) {
		this.fund = fund;
	}

	/**
	 * @return Returns the gracePeriod}.
	 */
	public Integer getGracePeriod() {
		return gracePeriod;
	}

	/**
	 * @param gracePeriod
	 *            The gracePeriod to set.
	 */
	public void setGracePeriod(Integer gracePeriod) {
		this.gracePeriod = gracePeriod;
	}

	/**
	 * @return Returns the groupFlag}.
	 */
	public java.lang.Short getGroupFlag() {
		return groupFlag;
	}

	/**
	 * @param groupFlag
	 *            The groupFlag to set.
	 */
	public void setGroupFlag(java.lang.Short groupFlag) {
		this.groupFlag = groupFlag;
	}

	/**
	 * @return Returns the interestRateAmount}.
	 */
	public java.lang.Double getInterestRateAmount() {
		return interestRateAmount;
	}

	/**
	 * @param interestRateAmount
	 *            The interestRateAmount to set.
	 */
	public void setInterestRateAmount(java.lang.Double interestRateAmount) {
		this.interestRateAmount = interestRateAmount;
	}

	/**
	 * @return Returns the loanBalance}.
	 */
	public java.lang.Double getLoanBalance() {
		return loanBalance;
	}

	/**
	 * @param loanBalance
	 *            The loanBalance to set.
	 */
	public void setLoanBalance(java.lang.Double loanBalance) {
		this.loanBalance = loanBalance;
	}

	/**
	 * @return Returns the loanInstallments}.
	 */
	public List getLoanInstallments() {
		return loanInstallments;
	}

	/**
	 * @param loanInstallments
	 *            The loanInstallments to set.
	 */
	public void setLoanInstallments(List loanInstallments) {
		this.loanInstallments = loanInstallments;
	}

	/**
	 * @return Returns the loanPenaltySet}.
	 */
	public java.util.Set getLoanPenaltySet() {
		return loanPenaltySet;
	}

	/**
	 * @param loanPenaltySet
	 *            The loanPenaltySet to set.
	 */
	public void setLoanPenaltySet(java.util.Set loanPenaltySet) {
		this.loanPenaltySet = loanPenaltySet;
	}

	/**
	 * @return Returns the noOfInstallments}.
	 */
	public java.lang.Short getNoOfInstallments() {
		return noOfInstallments;
	}

	/**
	 * @param noOfInstallments
	 *            The noOfInstallments to set.
	 */
	public void setNoOfInstallments(java.lang.Short noOfInstallments) {
		this.noOfInstallments = noOfInstallments;
	}

	/**
	 * @return Returns the notes}.
	 */
	public java.lang.String getNotes() {
		return notes;
	}

	/**
	 * @param notes
	 *            The notes to set.
	 */
	public void setNotes(java.lang.String notes) {
		this.notes = notes;
	}

	/**
	 * @return Returns the perfHistory}.
	 */
	public LoanPerfHistory getPerfHistory() {
		return perfHistory;
	}

	/**
	 * @param perfHistory
	 *            The perfHistory to set.
	 */
	public void setPerfHistory(LoanPerfHistory perfHistory) {
		this.perfHistory = perfHistory;
	}

	/**
	 * @return Returns the status}.
	 */
	public Short getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            The status to set.
	 */
	public void setStatus(Short status) {
		this.status = status;
	}

	/**
	 * @return Returns the versionNo}.
	 */
	public Integer getVersionNo() {
		return versionNo;
	}

	/**
	 * @param versionNo
	 *            The versionNo to set.
	 */
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

	/**
	 * @return Returns the collateralNote}.
	 */
	public String getCollateralNote() {
		return collateralNote;
	}

	/**
	 * @param collateralNote
	 *            The collateralNote to set.
	 */
	public void setCollateralNote(String collateralNote) {
		this.collateralNote = collateralNote;
	}

	/**
	 * @return Returns the loanAmount}.
	 */
	public Money getLoanAmount() {
		return loanAmount;
	}

	public double getLoanAmountDoubleValue() {
		return loanAmount.getAmountDoubleValue();
	}

	/**
	 * @param loanAmount
	 *            The loanAmount to set.
	 */
	public void setLoanAmount(Money loanAmount) {
		this.loanAmount = loanAmount;
	}

	/**
	 * @return Returns the collateralTypeId}.
	 */
	public Short getCollateralTypeId() {
		return collateralTypeId;
	}

	/**
	 * @param collateralTypeId
	 *            The collateralTypeId to set.
	 */
	public void setCollateralTypeId(Short collateralTypeId) {
		this.collateralTypeId = collateralTypeId;
	}

	/**
	 * @return Returns the interestTypeId}.
	 */
	public Short getInterestTypeId() {
		return interestTypeId;
	}

	/**
	 * @param interestTypeId
	 *            The interestTypeId to set.
	 */
	public void setInterestTypeId(Short interestTypeId) {
		this.interestTypeId = interestTypeId;
	}

	/**
	 * @return Returns the gracePeriodDuration}.
	 */
	public java.lang.Short getGracePeriodDuration() {
		return gracePeriodDuration;
	}

	/**
	 * @param gracePeriodDuration
	 *            The gracePeriodDuration to set.
	 */
	public void setGracePeriodDuration(java.lang.Short gracePeriodDuration) {
		this.gracePeriodDuration = gracePeriodDuration;
	}

	/**
	 * @return Returns the businessActivityId}.
	 */
	public Integer getBusinessActivityId() {
		return businessActivityId;
	}

	/**
	 * @param businessActivityId
	 *            The businessActivityId to set.
	 */
	public void setBusinessActivityId(Integer businessActivityId) {
		this.businessActivityId = businessActivityId;
	}

	/**
	 * @return Returns the stateSelected}.
	 */
	public String getStateSelected() {
		return stateSelected;
	}

	/**
	 * @param stateSelected
	 *            The stateSelected to set.
	 */
	public void setStateSelected(String stateSelected) {
		this.stateSelected = stateSelected;
	}

	/**
	 * @return Returns the disbursementDate.
	 */
	public String getDisbursementDate() {
		return disbursementDate;
	}

	/**
	 * @param disbursementDate
	 *            The disbursementDate to set.
	 */
	public void setDisbursementDate(String disbursementDate) {
		this.disbursementDate = disbursementDate;
	}

	/**
	 * @return Returns the intrestAtDisbursement}.
	 */
	public Short getIntrestAtDisbursement() {
		return intrestAtDisbursement;
	}

	/**
	 * @param intrestAtDisbursement
	 *            The intrestAtDisbursement to set.
	 */
	public void setIntrestAtDisbursement(Short intrestAtDisbursement) {
		this.intrestAtDisbursement = intrestAtDisbursement;
	}

	/**
	 * @return Returns the selectedPrdOfferingId}.
	 */
	public Short getSelectedPrdOfferingId() {
		return selectedPrdOfferingId;
	}

	/**
	 * @param selectedPrdOfferingId
	 *            The selectedPrdOfferingId to set.
	 */
	public void setSelectedPrdOfferingId(Short selectedPrdOfferingId) {
		this.selectedPrdOfferingId = selectedPrdOfferingId;
	}

	/**
	 * This is the custom method to do any custom validations. This method is
	 * also used to specify the methods where validation from the validation.xml
	 * is to be skipped. It also checks on next if there is any accountFee with
	 * zero or null amnt which is not allowed and hence returns an acction
	 * errors with a corresponding error message.
	 * 
	 * @param mapping
	 * @param request
	 * @return ActionErrors
	 * @throws ApplicationException
	 */
	public ActionErrors customValidate(ActionMapping mapping,
			HttpServletRequest request) throws ApplicationException {
		String methodCalled = request.getParameter(MethodNameConstants.METHOD);
		String input = request.getParameter("input");
		ActionErrors actionErrors = new ActionErrors();
		if (null != methodCalled) {
			if (MethodNameConstants.GETPRDOFFERINGS.equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION, Boolean
						.valueOf(true));
			} else if ((MethodNameConstants.LOAD).equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION, Boolean
						.valueOf(true));
			} else if ((MethodNameConstants.GET).equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION, Boolean
						.valueOf(true));
			} else if ((MethodNameConstants.UPDATE).equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION, Boolean
						.valueOf(true));
			} else if ((MethodNameConstants.MANAGE).equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION, Boolean
						.valueOf(true));
			} else if ((MethodNameConstants.PREVIOUS).equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION, Boolean
						.valueOf(true));
			} else if ((MethodNameConstants.CANCEL).equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION, Boolean
						.valueOf(true));
			} else if ((MethodNameConstants.SEARCH).equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION, Boolean
						.valueOf(true));
			} else if ((LoanConstants.GET_INSTALLMENT_DETAILS)
					.equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION, Boolean
						.valueOf(true));
			} else if ((LoanConstants.WAIVE).equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION, Boolean
						.valueOf(true));
			} else if ((MethodNameConstants.NEXT).equals(methodCalled)
					|| ((MethodNameConstants.PREVIEW).equals(methodCalled) && (input)
							.equals("editDetails"))) {
				checkForMandatoryFields(EntityMasterConstants.Loan,
						actionErrors, request);
				if (null != loanAmount && null != minLoanAmount
						&& null != maxLoanAmount) {
					if (loanAmount.getAmountDoubleValue() > maxLoanAmount
							|| loanAmount.getAmountDoubleValue() < minLoanAmount) {
						actionErrors
								.add(
										LoanConstants.LOANAMOUNT,
										new ActionMessage(
												LoanExceptionConstants.INVALIDLOANFIELD,
												LoanConstants.LOANAMOUNT));
					}
					else if (loanAmount.getAmountDoubleValue() <= 0) {
						actionErrors.add(LoanExceptionConstants.INVALIDFIELD,
								new ActionMessage(
										LoanExceptionConstants.INVALIDFIELD,
										LoanConstants.LOANAMOUNT));
					}
				}
				if (null != interestRateAmount && null != minInterestRate
						&& null != maxInterestRate) {
					if (interestRateAmount > maxInterestRate
							|| interestRateAmount < minInterestRate) {
						actionErrors
								.add(
										LoanConstants.INTERESTRATE,
										new ActionMessage(
												LoanExceptionConstants.INVALIDLOANFIELD,
												LoanConstants.INTERESTRATE));
					}
				}
				if (null != noOfInstallments && null != minNoInstallments
						&& null != maxNoInstallments) {
					if (noOfInstallments > maxNoInstallments
							|| noOfInstallments < minNoInstallments) {
						actionErrors
								.add(
										LoanConstants.NO_OF_INST,
										new ActionMessage(
												LoanExceptionConstants.INVALIDLOANFIELD,
												LoanConstants.NO_OF_INST));
					}
				}
				if (intrestAtDisbursement != null
						&& intrestAtDisbursement
								.equals(LoanConstants.INTEREST_DEDUCTED_AT_DISBURSMENT)
						&& noOfInstallments != null
						&& noOfInstallments.shortValue() <= 1) {
					actionErrors
							.add(
									LoanConstants.NO_OF_INST,
									new ActionMessage(
											LoanExceptionConstants.NOOFINSTALLMENTSSHOULDBEGREATERTHANONE,
											LoanConstants.NO_OF_INST));
				}
				if (isAnyAccountFeesWithoutAmnt()) {
					actionErrors.add("feeAmnt", new ActionMessage(
							LoanExceptionConstants.INVALIDFEEAMNT));
				}
				return actionErrors;
			}
		}
		return null;
	}

	/**
	 * This method returns true if there is any accountFee with null or zero
	 * amnt. it checks if the fees id is not null , then amount should not be
	 * null.
	 * 
	 * @return
	 */
	private boolean isAnyAccountFeesWithoutAmnt() {
		Set<Integer> accountFeesMapKeys = accountFeesMap.keySet();
		if (null != accountFeesMapKeys && !accountFeesMapKeys.isEmpty()) {
			for (Integer accountFeeMapKey : accountFeesMapKeys) {
				AccountFees accountFees = accountFeesMap.get(accountFeeMapKey);
				if (null != accountFees.getFees()
						&& null != accountFees.getFees().getFeeId()
						&& (null == accountFees.getFeeAmount() || accountFees
								.getFeeAmount().equals(Double.valueOf("0")))) {

					return true;
				}
			}
		}

		return false;
	}

	/**
	 * This method cleans up certain thing before the values from the fields are
	 * displayed on the UI. When we are creating a new loan account, all fields
	 * on the create page should be cleared and should have default values
	 * according to the UI.
	 * 
	 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		if (request.getParameter(MethodNameConstants.METHOD) != null
				&& request.getParameter(MethodNameConstants.METHOD).equals(
						MethodNameConstants.NEXT)) {
			accountFeesMap.clear();
			intrestAtDisbursement = Short.valueOf("0");
			gracePeriodDuration = Short.valueOf("0");
		} else if (request.getParameter(MethodNameConstants.METHOD) != null
				&& request.getParameter(MethodNameConstants.METHOD).equals(
						MethodNameConstants.GETPRDOFFERINGS)) {
			disbursementDate = null;
			selectedPrdOfferingId = Short.valueOf("0");
		} else if (request.getParameter(MethodNameConstants.METHOD) != null
				&& request.getParameter(MethodNameConstants.METHOD).equals(
						MethodNameConstants.PREVIEW)
				&& request.getParameter("input").equals("editDetails")) {
			intrestAtDisbursement = Short.valueOf("0");
			gracePeriodDuration = Short.valueOf("0");
		} else if (request.getParameter(MethodNameConstants.METHOD) != null
				&& request.getParameter(MethodNameConstants.METHOD).equals(
						MethodNameConstants.LOAD)) {
			businessActivityId = Integer.valueOf("0");
			collateralTypeId = Short.valueOf("0");
			collateralNote = "";
		}

	}

	public java.lang.Double getMaxLoanAmount() {
		return maxLoanAmount;
	}

	public void setMaxLoanAmount(java.lang.Double maxLoanAmount) {
		this.maxLoanAmount = maxLoanAmount;
	}

	public java.lang.Double getMinLoanAmount() {
		return minLoanAmount;
	}

	public void setMinLoanAmount(java.lang.Double minLoanAmount) {
		this.minLoanAmount = minLoanAmount;
	}

	public java.lang.Double getMaxInterestRate() {
		return maxInterestRate;
	}

	public void setMaxInterestRate(java.lang.Double maxInterestRate) {
		this.maxInterestRate = maxInterestRate;
	}

	public java.lang.Double getMaxNoInstallments() {
		return maxNoInstallments;
	}

	public void setMaxNoInstallments(java.lang.Double maxNoInstallments) {
		this.maxNoInstallments = maxNoInstallments;
	}

	public java.lang.Double getMinInterestRate() {
		return minInterestRate;
	}

	public void setMinInterestRate(java.lang.Double minInterestRate) {
		this.minInterestRate = minInterestRate;
	}

	public java.lang.Double getMinNoInstallments() {
		return minNoInstallments;
	}

	public void setMinNoInstallments(java.lang.Double minNoInstallments) {
		this.minNoInstallments = minNoInstallments;
	}

}
