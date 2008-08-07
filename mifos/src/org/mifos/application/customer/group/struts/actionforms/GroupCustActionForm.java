/**

* GroupCustActionForm.java version: 1.0



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

package org.mifos.application.customer.group.struts.actionforms;


import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.struts.actionforms.CustomerActionForm;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class GroupCustActionForm extends CustomerActionForm {

	private CustomerBO parentCustomer;
	private String centerSystemId;
	private String parentOfficeId;
	
	
	public String getCenterSystemId() {
		return centerSystemId;
	}

	public void setCenterSystemId(String centerSystemId) {
		this.centerSystemId = centerSystemId;
	}

	public CustomerBO getParentCustomer() {
		return parentCustomer;
	}

	public void setParentCustomer(CustomerBO parentCustomer) {
		this.parentCustomer = parentCustomer;
	}

	public String getParentOfficeId() {
		return parentOfficeId;
	}

	public void setParentOfficeId(String parentOfficeId) {
		this.parentOfficeId = parentOfficeId;
	}
	
	/*@Override
	public void setTrainedDate(String s) {
		throw new IllegalStateException();
	}*/	

	@Override
	protected ActionErrors validateFields(HttpServletRequest request, String method)throws ApplicationException {		
		ActionErrors errors = new ActionErrors();
		try {
		if(method.equals(Methods.previewManage.toString())){
			validateName(errors);
			validateTrained(request, errors);
			validateConfigurableMandatoryFields(request,errors,EntityType.GROUP);
			validateCustomFields(request,errors);
		}else if(method.equals(Methods.preview.toString())){
			validateName(errors);
			validateFormedByPersonnel(errors);
			validateTrained(request, errors);
			validateConfigurableMandatoryFields(request,errors,EntityType.GROUP);
			validateCustomFields(request,errors);			
			validateFees(request,errors);
		}
        } catch (ApplicationException ae) {
			// Discard other errors (is that right?)
        	ae.printStackTrace();
			errors = new ActionErrors();
			errors.add(ae.getKey(), new ActionMessage(ae.getKey(), ae
					.getValues()));
		}
		if (!errors.isEmpty()) {
			request.setAttribute(GroupConstants.METHODCALLED, method);
		}
		return errors;
	}
	
	@Override
	protected MeetingBO getCustomerMeeting(HttpServletRequest request)throws ApplicationException{		
		if(parentCustomer!=null)
			return parentCustomer.getCustomerMeeting().getMeeting();
		 return (MeetingBO) SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING,request);		
	}
}
