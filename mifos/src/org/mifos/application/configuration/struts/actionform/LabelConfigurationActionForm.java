/**

 * LabelConfigurationActionForm.java    version: xxx



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

package org.mifos.application.configuration.struts.actionform;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.struts.actionforms.BaseActionForm;

public class LabelConfigurationActionForm extends BaseActionForm {
	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.CONFIGURATION_LOGGER);

	private String headOffice;

	private String regionalOffice;

	private String subRegionalOffice;

	private String areaOffice;

	private String branchOffice;

	private String client;

	private String group;

	private String center;

	private String cash;

	private String check;

	private String vouchers;

	private String loans;

	private String savings;

	private String state;

	private String postalCode;

	private String ethnicity;

	private String citizenship;

	private String handicapped;

	private String govtId;

	private String address1;

	private String address2;

	private String address3;

	private String partialApplication;

	private String pendingApproval;

	private String approved;

	private String cancel;

	private String closed;

	private String onhold;

	private String active;

	private String inActive;

	private String activeInGoodStanding;

	private String activeInBadStanding;

	private String closedObligationMet;

	private String closedRescheduled;

	private String closedWrittenOff;

	private String none;

	private String graceOnAllRepayments;

	private String principalOnlyGrace;

	private String interest;

	private String externalId;

	private String bulkEntry;

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getActiveInBadStanding() {
		return activeInBadStanding;
	}

	public void setActiveInBadStanding(String activeInBadStanding) {
		this.activeInBadStanding = activeInBadStanding;
	}

	public String getActiveInGoodStanding() {
		return activeInGoodStanding;
	}

	public void setActiveInGoodStanding(String activeInGoodStanding) {
		this.activeInGoodStanding = activeInGoodStanding;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getApproved() {
		return approved;
	}

	public void setApproved(String approved) {
		this.approved = approved;
	}

	public String getAreaOffice() {
		return areaOffice;
	}

	public void setAreaOffice(String areaOffice) {
		this.areaOffice = areaOffice;
	}

	public String getBranchOffice() {
		return branchOffice;
	}

	public void setBranchOffice(String branchOffice) {
		this.branchOffice = branchOffice;
	}

	public String getBulkEntry() {
		return bulkEntry;
	}

	public void setBulkEntry(String bulkEntry) {
		this.bulkEntry = bulkEntry;
	}

	public String getCancel() {
		return cancel;
	}

	public void setCancel(String cancel) {
		this.cancel = cancel;
	}

	public String getCash() {
		return cash;
	}

	public void setCash(String cash) {
		this.cash = cash;
	}

	public String getCenter() {
		return center;
	}

	public void setCenter(String center) {
		this.center = center;
	}

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

	public String getCitizenship() {
		return citizenship;
	}

	public void setCitizenship(String citizenship) {
		this.citizenship = citizenship;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getClosed() {
		return closed;
	}

	public void setClosed(String closed) {
		this.closed = closed;
	}

	public String getClosedObligationMet() {
		return closedObligationMet;
	}

	public void setClosedObligationMet(String closedObligationMet) {
		this.closedObligationMet = closedObligationMet;
	}

	public String getClosedRescheduled() {
		return closedRescheduled;
	}

	public void setClosedRescheduled(String closedRescheduled) {
		this.closedRescheduled = closedRescheduled;
	}

	public String getClosedWrittenOff() {
		return closedWrittenOff;
	}

	public void setClosedWrittenOff(String closedWrittenOff) {
		this.closedWrittenOff = closedWrittenOff;
	}

	public String getEthnicity() {
		return ethnicity;
	}

	public void setEthnicity(String ethnicity) {
		this.ethnicity = ethnicity;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getGovtId() {
		return govtId;
	}

	public void setGovtId(String govtId) {
		this.govtId = govtId;
	}

	public String getGraceOnAllRepayments() {
		return graceOnAllRepayments;
	}

	public void setGraceOnAllRepayments(String graceOnAllRepayments) {
		this.graceOnAllRepayments = graceOnAllRepayments;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getHandicapped() {
		return handicapped;
	}

	public void setHandicapped(String handicapped) {
		this.handicapped = handicapped;
	}

	public String getHeadOffice() {
		return headOffice;
	}

	public void setHeadOffice(String headOffice) {
		this.headOffice = headOffice;
	}

	public String getInActive() {
		return inActive;
	}

	public void setInActive(String inActive) {
		this.inActive = inActive;
	}

	public String getInterest() {
		return interest;
	}

	public void setInterest(String interest) {
		this.interest = interest;
	}

	public String getLoans() {
		return loans;
	}

	public void setLoans(String loans) {
		this.loans = loans;
	}

	public String getNone() {
		return none;
	}

	public void setNone(String none) {
		this.none = none;
	}

	public String getOnhold() {
		return onhold;
	}

	public void setOnhold(String onhold) {
		this.onhold = onhold;
	}

	public String getPartialApplication() {
		return partialApplication;
	}

	public void setPartialApplication(String partialApplication) {
		this.partialApplication = partialApplication;
	}

	public String getPendingApproval() {
		return pendingApproval;
	}

	public void setPendingApproval(String pendingApproval) {
		this.pendingApproval = pendingApproval;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getPrincipalOnlyGrace() {
		return principalOnlyGrace;
	}

	public void setPrincipalOnlyGrace(String principalOnlyGrace) {
		this.principalOnlyGrace = principalOnlyGrace;
	}

	public String getRegionalOffice() {
		return regionalOffice;
	}

	public void setRegionalOffice(String regionalOffice) {
		this.regionalOffice = regionalOffice;
	}

	public String getSavings() {
		return savings;
	}

	public void setSavings(String savings) {
		this.savings = savings;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSubRegionalOffice() {
		return subRegionalOffice;
	}

	public void setSubRegionalOffice(String subRegionalOffice) {
		this.subRegionalOffice = subRegionalOffice;
	}

	public String getVouchers() {
		return vouchers;
	}

	public void setVouchers(String vouchers) {
		this.vouchers = vouchers;
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		logger.debug("Inside validate method");
		String method = request.getParameter(Methods.method.toString());
		ActionErrors errors = new ActionErrors();
		if (method.equals(Methods.update.toString())) {
			errors = super.validate(mapping, request);
		}
		if (!errors.isEmpty()) {
			request.setAttribute("methodCalled", method);
		}
		logger.debug("outside validate method");
		return errors;

	}

	public void clear() {
		this.headOffice = null;
		this.regionalOffice = null;
		this.subRegionalOffice = null;
		this.areaOffice = null;
		this.branchOffice = null;
		this.client = null;
		this.group = null;
		this.center = null;
		this.cash = null;
		this.check = null;
		this.vouchers = null;
		this.loans = null;
		this.savings = null;
		this.state = null;
		this.postalCode = null;
		this.ethnicity = null;
		this.citizenship = null;
		this.handicapped = null;
		this.govtId = null;
		this.address1 = null;
		this.address2 = null;
		this.address3 = null;
		this.partialApplication = null;
		this.pendingApproval = null;
		this.approved = null;
		this.cancel = null;
		this.closed = null;
		this.onhold = null;
		this.active = null;
		this.inActive = null;
		this.activeInGoodStanding = null;
		this.activeInBadStanding = null;
		this.closedObligationMet = null;
		this.closedRescheduled = null;
		this.closedWrittenOff = null;
		this.none = null;
		this.graceOnAllRepayments = null;
		this.principalOnlyGrace = null;
		this.interest = null;
		this.externalId = null;
		this.bulkEntry = null;
	}

}
