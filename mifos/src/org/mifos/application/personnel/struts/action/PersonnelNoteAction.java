package org.mifos.application.personnel.struts.action;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelNotesEntity;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.personnel.struts.actionforms.PersonnelNoteActionForm;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.SearchAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class PersonnelNoteAction extends SearchAction {

	@Override
	protected BusinessService getService() throws ServiceException {
		return getPersonnelBizService();
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}
	
	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("personnelNoteAction");
		security.put("load", SecurityConstants.VIEW);
		security.put("preview", SecurityConstants.VIEW);
		security
				.put("previous", SecurityConstants.VIEW);
		security.put("create", SecurityConstants.VIEW);
		security.put("search", SecurityConstants.VIEW);
		return security;
	}
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		clearActionForm(form);
		PersonnelBO personnelBO = getPersonnelBizService().getPersonnel(getShortValue(((PersonnelNoteActionForm) form).getPersonnelId()));
		setFormAttributes(getUserContext(request).getPreferredLocale(), form,personnelBO);
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY,request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, personnelBO, request);
		return mapping.findForward(ActionForwards.load_success.toString());
	}
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		return mapping.findForward(ActionForwards.preview_success.toString());
	}
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		return mapping.findForward(ActionForwards.previous_success.toString());
	}
	
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		return mapping.findForward(ActionForwards.cancel_success.toString());
	}
	
	@CloseSession
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward create(ActionMapping mapping, ActionForm form,	HttpServletRequest request, HttpServletResponse response) throws Exception{
		PersonnelNoteActionForm actionForm = (PersonnelNoteActionForm) form;
		PersonnelBO personnel = getPersonnelBizService().getPersonnel(getShortValue(((PersonnelNoteActionForm) form).getPersonnelId()));
		PersonnelBO officer = getPersonnelBizService().getPersonnel(getUserContext(request).getId());
		if (personnel != null)
			checkPermissionForAddingNotesToPersonnel(getUserContext(request), personnel.getOffice()
							.getOfficeId(), personnel
							.getPersonnelId());
		else
			checkPermissionForAddingNotesToPersonnel(getUserContext(request), personnel.getOffice()
							.getOfficeId(), getUserContext(request).getId());
		PersonnelNotesEntity personnelNote = new PersonnelNotesEntity(actionForm.getComment(),officer,personnel);
		personnel.addNotes(getUserContext(request).getId(),personnelNote);
		personnel = null;
		officer = null;
		return mapping.findForward(ActionForwards.create_success.toString());
	}
	
	@TransactionDemarcate(joinToken = true)
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
		((PersonnelNoteActionForm)form).setComment("");
		((PersonnelNoteActionForm)form).setCommentDate("");
	}

	@Override
	protected QueryResult getSearchResult(ActionForm form)throws ApplicationException{
		return getPersonnelBizService().getAllPersonnelNotes(getShortValue(((PersonnelNoteActionForm)form).getPersonnelId()));
	}
	
	private void setFormAttributes(Locale locale , ActionForm form,PersonnelBO personnelBO ) throws ApplicationException{
		PersonnelNoteActionForm actionForm = (PersonnelNoteActionForm) form;
		actionForm.setPersonnelName(personnelBO.getDisplayName());
		actionForm.setOfficeName(personnelBO.getOffice().getOfficeName());
		actionForm.setGlobalPersonnelNum(personnelBO.getGlobalPersonnelNum());
		actionForm.setCommentDate(DateUtils.getCurrentDate(locale));
	}
	
	private PersonnelBusinessService getPersonnelBizService() {
		return (PersonnelBusinessService)ServiceFactory.getInstance().getBusinessService(
				BusinessServiceName.Personnel);
	}
	
	private void checkPermissionForAddingNotesToPersonnel(
			UserContext userContext, Short recordOfficeId,
			Short recordLoanOfficerId) throws ApplicationException {
		if (!isPermissionAllowed(userContext, recordOfficeId,
				recordLoanOfficerId))
			throw new CustomerException(
					SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
	}

	private boolean isPermissionAllowed(UserContext userContext,
			Short recordOfficeId, Short recordLoanOfficerId) {
		return ActivityMapper.getInstance().isAddingNotesPermittedForPersonnel(
				userContext, recordOfficeId, recordLoanOfficerId);
	}
}
