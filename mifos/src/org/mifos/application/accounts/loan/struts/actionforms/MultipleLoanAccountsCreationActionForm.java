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
import static org.mifos.framework.util.CollectionUtils.select;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.loan.util.helpers.LoanExceptionConstants;
import org.mifos.application.accounts.loan.util.helpers.MultipleLoanCreationViewHelper;
import org.mifos.application.bulkentry.util.helpers.BulkEntryConstants;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.Predicate;
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

	private List<MultipleLoanCreationViewHelper> clientDetails;

	private String stateSelected;

	public MultipleLoanAccountsCreationActionForm() {
		clientDetails = new ArrayList<MultipleLoanCreationViewHelper>();
	}

	public List<MultipleLoanCreationViewHelper> getClientDetails() {
		return clientDetails;
	}

	public void setClientDetails(
			List<MultipleLoanCreationViewHelper> clientDetails) {
		this.clientDetails = clientDetails;
	}

	public List<MultipleLoanCreationViewHelper> getApplicableClientDetails() {
		try {
			return (List<MultipleLoanCreationViewHelper>) select(clientDetails,
							new Predicate<MultipleLoanCreationViewHelper>() {
								public boolean evaluate(
										MultipleLoanCreationViewHelper clientDetail)
										throws Exception {
									return clientDetail.isApplicable();
								}
							});
		}
		catch (Exception e) {
			return new ArrayList<MultipleLoanCreationViewHelper>();
		}
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
		catch (ServiceException e) {
			errors.add(ExceptionConstants.SERVICEEXCEPTION,
					new ActionMessage(ExceptionConstants.SERVICEEXCEPTION));
		}
		if (!errors.isEmpty()) {
			request.setAttribute("methodCalled", method);
		}
		logger.debug("outside validate method");
		return errors;
	}

	private void checkValidationForCreate(ActionErrors errors,
			HttpServletRequest request) throws PageExpiredException,
			ServiceException {
		logger.debug("inside checkValidationForCreate method");
		List<MultipleLoanCreationViewHelper> applicableClientDetails = getApplicableClientDetails();
		if (CollectionUtils.isEmpty(applicableClientDetails)) {
			addError(errors, LoanConstants.APPL_RECORDS,
					LoanExceptionConstants.SELECT_ATLEAST_ONE_RECORD, getLabel(
							ConfigurationConstants.CLIENT,
							getUserContext(request)));
			return;
		}
		Locale locale = getUserContext(request).getPreferredLocale();
		ResourceBundle resources = ResourceBundle.getBundle(
				FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE, locale);
		for (MultipleLoanCreationViewHelper clientDetail : applicableClientDetails) {
			if (clientDetail.isLoanAmountInRange())
				continue;
			addError(errors, LoanConstants.LOANAMOUNT,
					LoanExceptionConstants.INVALIDMINMAX, resources
							.getString("loan.loanAmountFor")
							+ clientDetail.getClientName(), clientDetail
							.getMinLoanAmount().toString(), clientDetail
							.getMaxLoanAmount().toString());
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
					LoanConstants.MANDATORY_SELECT, getMessageText(
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

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		CollectionUtils.forAllDo(clientDetails, new Closure() {
			public void execute(Object arg0) {
				((MultipleLoanCreationViewHelper)arg0).resetSelected();
			}
		});
	}
}
