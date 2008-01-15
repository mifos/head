/**

 * MultipleLoanAccountsCreationActionForm.java    version: xxx



 * Copyright (c) 2005-2006 Grameen Foundation USA

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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.loan.util.helpers.LoanExceptionConstants;
import org.mifos.application.accounts.loan.util.helpers.MultipleLoanCreationViewHelper;
import org.mifos.application.bulkentry.util.helpers.BulkEntryConstants;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class MultipleLoanAccountsCreationActionForm extends BaseActionForm {
	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	private String branchOfficeId;

	private String loanOfficerId;

	private String centerId;

	private String centerSearchId;

	private String prdOfferingId;

	private List<String> clients;

	private List<MultipleLoanCreationViewHelper> clientDetails;

	private String stateSelected;

	public MultipleLoanAccountsCreationActionForm() {
		clients = new ArrayList<String>();
		clientDetails = new ArrayList<MultipleLoanCreationViewHelper>();
	}

	public List<MultipleLoanCreationViewHelper> getClientDetails() {
		return clientDetails;
	}

	public void setClientDetails(
			List<MultipleLoanCreationViewHelper> clientDetails) {
		this.clientDetails = clientDetails;
	}

	public MultipleLoanCreationViewHelper getClientDetails(int i) {
		while (i >= clientDetails.size())
			clientDetails.add(new MultipleLoanCreationViewHelper());
		return clientDetails.get(i);
	}

	public void setClientDetails(int i,
			MultipleLoanCreationViewHelper clientDetail) {
		while (this.clientDetails.size() <= i)
			this.clientDetails.add(new MultipleLoanCreationViewHelper());
		this.clientDetails.set(i, clientDetail);
	}

	public List<MultipleLoanCreationViewHelper> getApplicableClientDetails() {
		List<MultipleLoanCreationViewHelper> applicableClientDetails = new ArrayList<MultipleLoanCreationViewHelper>();
		List<String> applicableClients = getApplicableClients();
		if (applicableClients != null && applicableClients.size() > 0) {
			for (String clientId : applicableClients) {
				for (MultipleLoanCreationViewHelper clientDetail : clientDetails) {
					if (clientDetail.getClientId().equals(clientId)) {
						applicableClientDetails.add(clientDetail);
					}
				}
			}
		}
		return applicableClientDetails;
	}

	public List<String> getClients() {
		return clients;
	}

	public void setClients(List<String> accountRecords) {
		this.clients = accountRecords;
	}

	public String getClients(int i) {
		while (i >= clients.size())
			clients.add("");
		return clients.get(i).toString();
	}

	public void setClients(int i, String string) {
		while (this.clients.size() <= i)
			this.clients.add(new String());
		this.clients.set(i, string);
	}

	private List<String> getApplicableClients() {
		List<String> applicableClients = new ArrayList<String>();
		for (String clientId : getClients())
			if (StringUtils.isNullAndEmptySafe(clientId))
				applicableClients.add(clientId);
		return applicableClients;
	}

	public String getBranchOfficeId() {
		return branchOfficeId;
	}

	public void setBranchOfficeId(String branchOfficeId) {
		this.branchOfficeId = branchOfficeId;
	}

	public String getLoanOfficerId() {
		return loanOfficerId;
	}

	public void setLoanOfficerId(String loanOfficerId) {
		this.loanOfficerId = loanOfficerId;
	}

	public String getCenterId() {
		return centerId;
	}

	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	public String getPrdOfferingId() {
		return prdOfferingId;
	}

	public void setPrdOfferingId(String prdOfferingId) {
		this.prdOfferingId = prdOfferingId;
	}

	public String getCenterSearchId() {
		return centerSearchId;
	}

	public void setCenterSearchId(String centerSearchId) {
		this.centerSearchId = centerSearchId;
	}

	public String getStateSelected() {
		return stateSelected;
	}

	public void setStateSelected(String stateSelected) {
		this.stateSelected = stateSelected;
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		clients.clear();
		clients = new ArrayList<String>();
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		logger.debug("Inside validate method");
		String method = request.getParameter(Methods.method.toString());
		ActionErrors errors = new ActionErrors();
		try {
			if (method.equals(Methods.get.toString())) {
				request.setAttribute(Constants.CURRENTFLOWKEY, request
						.getParameter(Constants.CURRENTFLOWKEY));
				checkValidationForLoad(errors, getUserContext(request), 
						(Short) SessionUtils.getAttribute(
								BulkEntryConstants.ISCENTERHEIRARCHYEXISTS,
								request));
			} else if (method.equals(Methods.create.toString())) {
				request.setAttribute(Constants.CURRENTFLOWKEY, request
						.getParameter(Constants.CURRENTFLOWKEY));
				checkValidationForCreate(errors, request);
			} else if (method.equals(Methods.getLoanOfficers.toString())) {
				checkValidationForBranchOffice(errors, getUserContext(request));
			} else if (method.equals(Methods.getCenters.toString())) {
				checkValidationForBranchOffice(errors, getUserContext(request));
				checkValidationForLoanOfficer(errors);
			} else if (method.equals(Methods.getPrdOfferings.toString())) {
				request.setAttribute(Constants.CURRENTFLOWKEY, request
						.getParameter(Constants.CURRENTFLOWKEY));
				checkValidationForBranchOffice(errors, getUserContext(request));
				checkValidationForLoanOfficer(errors);
				checkValidationForCenter(errors, getUserContext(request),
						(Short) SessionUtils
						.getAttribute(
								BulkEntryConstants.ISCENTERHEIRARCHYEXISTS,
								request));
			}
		} catch (PageExpiredException e) {
			errors.add(ExceptionConstants.PAGEEXPIREDEXCEPTION,
					new ActionMessage(ExceptionConstants.PAGEEXPIREDEXCEPTION));
		}
		if (!errors.isEmpty()) {
			request.setAttribute("methodCalled", method);
		}
		logger.debug("outside validate method");
		return errors;
	}

	private void checkValidationForCreate(ActionErrors errors,
			HttpServletRequest request) throws PageExpiredException {
		logger.debug("inside checkValidationForCreate method");
		LoanOfferingBO loanOffering = (LoanOfferingBO) SessionUtils
				.getAttribute(LoanConstants.LOANOFFERING, request);
		List<MultipleLoanCreationViewHelper> applicableClientDetails = getApplicableClientDetails();
		if (applicableClientDetails != null
				&& applicableClientDetails.size() > 0) {
			for (MultipleLoanCreationViewHelper clientDetail : applicableClientDetails) {
				String loanAmount = clientDetail.getLoanAmount();
				if (StringUtils.isNullOrEmpty(loanAmount)
						|| getDoubleValue(loanAmount).doubleValue() > loanOffering
								.getMaxLoanAmount().getAmountDoubleValue()
						|| getDoubleValue(loanAmount).doubleValue() < loanOffering
								.getMinLoanAmount().getAmountDoubleValue()) {
					addError(errors, LoanConstants.LOANAMOUNT,
							LoanExceptionConstants.INVALIDMINMAX,
							LoanConstants.LOAN_AMOUNT_FOR
									+ clientDetail.getClientName(),
							loanOffering.getMinLoanAmount().toString(),
							loanOffering.getMaxLoanAmount().toString());
				}
			}
		} else {
			addError(errors, LoanConstants.APPL_RECORDS,
					LoanExceptionConstants.SELECT_ATLEAST_ONE_RECORD, getLabel(
							ConfigurationConstants.CLIENT, getUserContext(
									request)));
		}
		logger.debug("outside checkValidationForCreate method");
	}

	private void checkValidationForLoad(ActionErrors errors, UserContext userContext,
			short isCenterHeirarchyExists) {
		logger.debug("Inside checkValidationForLoad method");
		checkValidationForBranchOffice(errors, userContext);
		checkValidationForLoanOfficer(errors);
		checkValidationForCenter(errors, userContext, isCenterHeirarchyExists);
		checkValidationForPrdOfferingId(errors, userContext);
		logger.debug("outside checkValidationForLoad method");
	}

	private void checkValidationForBranchOffice(ActionErrors errors,
			UserContext userContext) {
		if (StringUtils.isNullOrEmpty(branchOfficeId)) {
			addError(errors, ConfigurationConstants.BRANCHOFFICE,
					LoanConstants.MANDATORY_SELECT, getLabel(
							ConfigurationConstants.BRANCHOFFICE, userContext));
		}
	}

	private void checkValidationForLoanOfficer(ActionErrors errors) {
		if (StringUtils.isNullOrEmpty(loanOfficerId)) {
			addError(errors, LoanConstants.LOANOFFICERS,
					LoanConstants.MANDATORY_SELECT, LoanConstants.LOANOFFICERS);
		}
	}

	private void checkValidationForCenter(ActionErrors errors,
			UserContext userContext, short isCenterHeirarchyExists) {
		String customerLabel = isCenterHeirarchyExists == Constants.YES ? ConfigurationConstants.CENTER
				: ConfigurationConstants.GROUP;
		if (StringUtils.isNullOrEmpty(centerId)) {
			addError(errors, ConfigurationConstants.CENTER,
					LoanConstants.MANDATORY_SELECT, getLabel(customerLabel,
							userContext));
		}
	}

	private void checkValidationForPrdOfferingId(ActionErrors errors,
			UserContext userContext) {
		if (StringUtils.isNullOrEmpty(prdOfferingId)) {
			addError(errors, LoanConstants.PRDOFFERINGID,
					LoanConstants.LOANOFFERINGNOTSELECTEDERROR, getLabel(
							ConfigurationConstants.LOAN, userContext),
					LoanConstants.INSTANCENAME);
		}
	}

}
