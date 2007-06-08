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
import org.mifos.framework.util.helpers.StringUtils;

public class GroupCustActionForm extends CustomerActionForm {

	private CustomerBO parentCustomer;
	private String centerSystemId;
	private String parentOfficeId;
	private String trainedDateYY;
	private String trainedDateMM;
	private String trainedDateDD = "";
	
	
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
	public String getTrainedDate() {
		if (!StringUtils.isNullAndEmptySafe(trainedDateDD)
				|| !StringUtils.isNullAndEmptySafe(trainedDateMM)
				|| !StringUtils.isNullAndEmptySafe(trainedDateYY)) {
			return null;
		}
		return this.trainedDateDD + "/" + this.trainedDateMM + "/" + this.trainedDateYY;
	}
	
	public void setTrainedDateYY(String trainedDateYY) {
		this.trainedDateYY = trainedDateYY;
	}

	public String getTrainedDateYY() {
		return trainedDateYY;
	}

	public void setTrainedDateMM(String trainedDateMM) {
		this.trainedDateMM = trainedDateMM;
	}

	public String getTrainedDateMM() {
		return trainedDateMM;
	}

	public void setTrainedDateDD(String trainedDateDD) {
		this.trainedDateDD = trainedDateDD;
	}

	public String getTrainedDateDD() {
		return trainedDateDD;
	}

	@Override
	protected ActionErrors validateFields(HttpServletRequest request, String method)throws ApplicationException {		
		ActionErrors errors = new ActionErrors();
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
		
		return errors;
	}
	
	@Override
	protected MeetingBO getCustomerMeeting(HttpServletRequest request)throws ApplicationException{		
		if(parentCustomer!=null)
			return parentCustomer.getCustomerMeeting().getMeeting();
		 return (MeetingBO) SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING,request);		
	}
}
