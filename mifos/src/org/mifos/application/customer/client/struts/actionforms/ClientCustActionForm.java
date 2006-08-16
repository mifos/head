/**

* ClientCustActionForm.java version: 1.0



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

package org.mifos.application.customer.client.struts.actionforms;


import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.upload.FormFile;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientDetailView;
import org.mifos.application.customer.client.business.ClientNameDetailView;
import org.mifos.application.customer.struts.actionforms.CustomerActionForm;

public class ClientCustActionForm extends CustomerActionForm {

	private CustomerBO parentGroup;
	private String isClientUnderGroup;
	private ClientDetailView clientDetailView;
	private ClientNameDetailView clientName;
	private ClientNameDetailView spouseName;
	private String parentGroupId;
	private String governmentId;
	private String dateOfBirth;
	/**Denotes the picture of the client*/
	private InputStream customerPicture;

	/**Denotes the picture of the client*/
	private FormFile picture;
	public String getIsClientUnderGroup() {
		return isClientUnderGroup;
	}


	public void setIsClientUnderGroup(String isClientUnderGroup) {
		this.isClientUnderGroup = isClientUnderGroup;
	}

	public CustomerBO getParentGroup() {
		return parentGroup;
	}

	public void setParentGroup(CustomerBO parentGroup) {
		this.parentGroup = parentGroup;
	}

	
	public ClientDetailView getClientDetailView() {
		return clientDetailView;
	}


	public void setClientDetailView(ClientDetailView clientDetailView) {
		this.clientDetailView = clientDetailView;
	}


	public ClientNameDetailView getClientName() {
		return clientName;
	}


	public void setClientName(ClientNameDetailView clientName) {
		this.clientName = clientName;
	}


	public ClientNameDetailView getSpouseName() {
		return spouseName;
	}


	public void setSpouseName(ClientNameDetailView spouseName) {
		this.spouseName = spouseName;
	}


	public String getParentGroupId() {
		return parentGroupId;
	}


	public void setParentGroupId(String parentGroupId) {
		this.parentGroupId = parentGroupId;
	}


	public String getGovernmentId() {
		return governmentId;
	}


	public void setGovernmentId(String governmentId) {
		this.governmentId = governmentId;
	}


	public String getDateOfBirth() {
		return dateOfBirth;
	}


	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public FormFile getPicture() {
		return picture;
	}

	public void setPicture(FormFile picture) {
		this.picture = picture;
		try{
			customerPicture = picture.getInputStream();
		}
		catch(IOException ioe){

		}
	}
	@Override
	protected ActionErrors validateFields(HttpServletRequest request,
			String method) {
		ActionErrors errors = new ActionErrors();
		return errors;
	}

}
