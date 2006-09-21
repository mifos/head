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
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.struts.actionforms.CustomerActionForm;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.util.helpers.SessionUtils;

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
	
	@Override
	protected ActionErrors validateFields(HttpServletRequest request, String method)throws ApplicationException {		
		ActionErrors errors = new ActionErrors();
		if(method.equals(Methods.previewManage.toString())){
			validateCommonFields(request, errors);
		}else if(method.equals(Methods.preview.toString())){
			validateCommonFields(request, errors);
			validateFormedByPersonnel(errors);
			validateFees(request,errors);
		}
		return errors;
	}
	
	private void validateCommonFields(HttpServletRequest request, ActionErrors errors)throws ApplicationException{
		validateName(errors);
		validateTrained(request, errors);
		validateConfigurableMandatoryFields(request,errors,EntityType.GROUP);
		validateCustomFields(request,errors);
	}
	
	@Override
	protected MeetingBO getCustomerMeeting(HttpServletRequest request)throws ApplicationException{		
		if(parentCustomer!=null)
			return parentCustomer.getCustomerMeeting().getMeeting();
		 return (MeetingBO) SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING,request);		
	}
}
