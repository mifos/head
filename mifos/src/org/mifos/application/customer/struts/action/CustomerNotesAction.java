/**

* CustomerNotesAction.java version: 1.0



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

package org.mifos.application.customer.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerNoteEntity;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.struts.actionforms.CustomerNotesActionForm;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.SearchAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;

public class CustomerNotesAction extends SearchAction {

	CustomerBusinessService  customerBusinessService=null;
	private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);
	
	public CustomerNotesAction() throws ServiceException {
		customerBusinessService =(CustomerBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Customer);
	}
	
	protected BusinessService getService() {
		return customerBusinessService;
	}
	
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}
	
	public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		clearActionForm(form);
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		CustomerBO customerBO = customerBusinessService.getCustomer(Integer.valueOf(((CustomerNotesActionForm) form).getCustomerId()));
		customerBO.setUserContext(userContext);
		setFormAttributes(userContext, form,customerBO);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, customerBO, request.getSession());
		return mapping.findForward(ActionForwards.load_success.toString());
	}
	
	public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
			return mapping.findForward(ActionForwards.preview_success.toString());
	}
	
	public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		return mapping.findForward(ActionForwards.previous_success.toString());
	}
	
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		return mapping.findForward(getDetailCustomerPage(form));
	}
	
	public ActionForward create(ActionMapping mapping, ActionForm form,	HttpServletRequest request, HttpServletResponse response) throws CustomerException{
		ActionForward forward = null;
		CustomerNotesActionForm notesActionForm = (CustomerNotesActionForm) form;
		CustomerBO customerBO = customerBusinessService.getCustomer(Integer.valueOf(((CustomerNotesActionForm) form).getCustomerId()));
		UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
		PersonnelBO personnelBO = new PersonnelPersistence().getPersonnel(uc.getId());
		CustomerNoteEntity customerNote = new CustomerNoteEntity(notesActionForm.getComment(), new java.sql.Date(System.currentTimeMillis()),personnelBO,customerBO);
		customerBO.addCustomerNotes(customerNote);
		customerBO.setUserContext(uc);
		customerBO.update();
		forward = mapping.findForward(getDetailCustomerPage(notesActionForm));
		return forward;
	}
	
	private String getDetailCustomerPage(ActionForm form) {
		CustomerNotesActionForm notesActionForm = (CustomerNotesActionForm) form;
		String input = notesActionForm.getInput();
		String forward = null;
		if(input.equals("center"))
			forward = ActionForwards.center_detail_page.toString();
		if(input.equals("group"))
			forward = ActionForwards.group_detail_page.toString();
		if(input.equals("client"))
			forward = ActionForwards.client_detail_page.toString();
		return forward;
	}
	
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute(SavingsConstants.METHODCALLED);
		String forward = null;
		if (method != null) {
			if (method.equals(Methods.preview.toString()))
				forward = ActionForwards.preview_failure.toString();
			else if (method.equals(Methods.create.toString()))
				forward = ActionForwards.create_failure.toString();
		}
		return mapping.findForward(forward);
	}
	
	private void clearActionForm(ActionForm form){
		((CustomerNotesActionForm)form).setComment("");
		((CustomerNotesActionForm)form).setCommentDate("");
		((CustomerNotesActionForm)form).setSecurityParamInput("");
	}

	@Override
	protected QueryResult getSearchResult(ActionForm form)throws Exception {
		return customerBusinessService.getAllCustomerNotes(Integer.valueOf(((CustomerNotesActionForm)form).getCustomerId()));
	}
	
	private void setFormAttributes(UserContext userContext , ActionForm form,CustomerBO customerBO ) throws ApplicationException, SystemException {
		CustomerNotesActionForm notesActionForm = (CustomerNotesActionForm) form;
		notesActionForm.setLevelId(customerBO.getCustomerLevel().getId().toString());
		notesActionForm.setGlobalCustNum(customerBO.getGlobalCustNum());
		notesActionForm.setCustomerName(customerBO.getDisplayName());
		notesActionForm.setCommentDate(DateHelper.getCurrentDate(userContext.getPereferedLocale()));
		if(customerBO instanceof CenterBO) {
			//notesActionForm.setSecurityParamInput("Center");
			notesActionForm.setInput("center");
		}else if(customerBO instanceof GroupBO) {
		//	notesActionForm.setSecurityParamInput("Group");
			notesActionForm.setInput("group");
		}else if(customerBO instanceof ClientBO) {
		///	notesActionForm.setSecurityParamInput("Client");
			notesActionForm.setInput("client");
		}
	}

}
