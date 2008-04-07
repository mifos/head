/**

* EditCustomerStatusActionForm.java version: 1.0



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

package org.mifos.application.customer.struts.actionforms;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.FilePaths;

public class EditCustomerStatusActionForm extends BaseActionForm {
	
	public EditCustomerStatusActionForm() {
		selectedItems = new String[50];
	}

	private String customerId;

	private String globalAccountNum;

	private String customerName;
	
	private String levelId;

	private String currentStatusId;

	private String newStatusId;

	private String flagId;

	private String notes;

	private String[] selectedItems;
	
	private String input;
	
	private String commentDate;
	
	public String getCommentDate() {
		return commentDate;
	}

	public void setCommentDate(String commentDate) {
		this.commentDate = commentDate;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	

	public String getCustomerId() {
		return customerId;
	}
	
	public Integer getCustomerIdValue() {
		return getIntegerValue(customerId);
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	
	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCurrentStatusId() {
		return currentStatusId;
	}

	public void setCurrentStatusId(String currentStatusId) {
		this.currentStatusId = currentStatusId;
	}

	public String getFlagId() {
		return flagId;
	}
	
	public Short getFlagIdValue() {
		return getShortValue(flagId);
	}

	public void setFlagId(String flagId) {
		this.flagId = flagId;
	}

	public String getGlobalAccountNum() {
		return globalAccountNum;
	}

	public void setGlobalAccountNum(String globalAccountNum) {
		this.globalAccountNum = globalAccountNum;
	}
	
	public String getLevelId() {
		return levelId;
	}
	
	public Short getLevelIdValue() {
		return getShortValue(levelId);
	}

	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}


	public String getNewStatusId() {
		return newStatusId;
	}
	
	public Short getNewStatusIdValue() {
		return getShortValue(newStatusId);
	}

	public void setNewStatusId(String newStatusId) {
		this.newStatusId = newStatusId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String[] getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(String[] selectedItems) {
		this.selectedItems = selectedItems;
	}
	
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		String methodCalled = request.getParameter(Methods.method.toString());
		if (null != methodCalled) {
			if ((Methods.previewStatus.toString()).equals(methodCalled)) {
				this.flagId = null;
				this.selectedItems = null; 
			}
		}
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String methodCalled = request.getParameter(Methods.method.toString());
		if (null != methodCalled) {
			if ((Methods.previewStatus.toString()).equals(methodCalled)) {
				handleStatusPreviewValidations(request, errors);
			} else if ((Methods.updateStatus.toString()).equals(methodCalled)) {
				handleUpdateStatus(request, errors);
			}
		}
		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", methodCalled);
		}
		return errors;
	}

	private ActionErrors handleUpdateStatus(HttpServletRequest request,
			ActionErrors errors) {
		Object obj=request.getParameter("chklistSize");
		if(request.getParameter("selectedItems")==null){
			selectedItems=null;
		}if(obj!=null){
			
			int totalItems = new Integer(obj.toString()).intValue();
			if((totalItems>0 && selectedItems==null) ||(selectedItems!=null && totalItems!=selectedItems.length)){
				errors.add(CustomerConstants.INCOMPLETE_CHECKLIST_EXCEPTION,new ActionMessage(CustomerConstants.INCOMPLETE_CHECKLIST_EXCEPTION));
			}
		
		}
		return errors;
		
	}

	private ActionErrors handleStatusPreviewValidations(
			HttpServletRequest request, ActionErrors errors) {
		UserContext userContext = (UserContext)request.getSession().getAttribute(LoginConstants.USERCONTEXT);
		Locale locale = userContext.getPreferredLocale();
		ResourceBundle resources = ResourceBundle.getBundle
				(FilePaths.CUSTOMER_UI_RESOURCE_PROPERTYFILE, locale);
		if (!StringUtils.isNullSafe(newStatusId))
			errors.add(CustomerConstants.MANDATORY_SELECT, new ActionMessage(CustomerConstants.MANDATORY_SELECT, resources.getString("Customer.status")));
		else if (isNewStatusHasFlag() && !StringUtils.isNullAndEmptySafe(flagId))
			errors.add(CustomerConstants.MANDATORY_SELECT, new ActionMessage(CustomerConstants.MANDATORY_SELECT, resources.getString("Customer.flag")));
		if (StringUtils.isNullOrEmpty(notes))
			errors.add(CustomerConstants.MANDATORY_TEXTBOX, new ActionMessage(CustomerConstants.MANDATORY_TEXTBOX, resources.getString("Customer.notes")));
		else if (notes.length() > CustomerConstants.COMMENT_LENGTH)
			errors.add(CustomerConstants.MAXIMUM_LENGTH, new ActionMessage(CustomerConstants.MAXIMUM_LENGTH, resources.getString("Customer.notes") ,CustomerConstants.COMMENT_LENGTH));
		return errors;
	}

	private boolean isCheckListNotComplete(String chklistSize) {
		return (isPartialSelected(chklistSize) || isNoneSelected(chklistSize));
	}
	
	private boolean isPartialSelected(String chklistSize) {
		return (selectedItems != null) && (Integer.valueOf(chklistSize).intValue() != selectedItems.length);
	}
	
	private boolean isNoneSelected(String chklistSize) {
		return (Integer.valueOf(chklistSize).intValue() > 0) && (selectedItems == null);
	}
	
	private boolean isNewStatusHasFlag() {
		return (Short.valueOf(newStatusId).equals(CustomerStatus.CLIENT_CANCELLED.getValue())) || (Short.valueOf(newStatusId).equals(CustomerStatus.CLIENT_CLOSED.getValue()))
				|| (Short.valueOf(newStatusId).equals(CustomerStatus.GROUP_CLOSED.getValue()))|| (Short.valueOf(newStatusId).equals(CustomerStatus.GROUP_CANCELLED.getValue()));
	}

}
