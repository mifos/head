package org.mifos.framework.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.struts.actionforms.CustSearchActionForm;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.tabletag.TableTagConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;

public class SearchAction extends BaseAction {
	
	public SearchAction() {
		
	}

	@Override
	protected BusinessService getService() throws ServiceException {
		return null;
	}
	
	public ActionForward searchPrev(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//Integer current = (Integer)SessionUtils.getAttribute("current",request);
		
		//if( current ==null) throw new PageExpiredException(ExceptionConstants.PAGEEXPIREDEXCEPTION);
		//SessionUtils.setRemovableAttribute("current",current-1,TableTagConstants.PATH,request.getSession());
		checkForValidData(request);
		SessionUtils.setRemovableAttribute("meth","previous",TableTagConstants.PATH,request.getSession());
		String forwardkey = (String)SessionUtils.getAttribute("forwardkey",request.getSession());
		if( forwardkey ==null)throw new PageExpiredException();
		return mapping.findForward(forwardkey);
	}
	
	public ActionForward searchNext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		//Integer current=1;
		//if(null !=SessionUtils.getAttribute("current",request.getSession())) {
		//  current=(Integer)SessionUtils.getAttribute("current",request.getSession());
		//}
		//SessionUtils.setRemovableAttribute("current",current+1,TableTagConstants.PATH,request.getSession());
		checkForValidData(request);
		SessionUtils.setRemovableAttribute("meth","next",TableTagConstants.PATH,request.getSession());
		String forwardkey = (String)SessionUtils.getAttribute("forwardkey",request.getSession());
		if (forwardkey == null) {
			throw new PageExpiredException();
		}
		return mapping.findForward(forwardkey);
	}
	
	protected void cleanUpSearch(HttpServletRequest request) throws PageExpiredException
	{
		SessionUtils.setRemovableAttribute("TableCache",null,TableTagConstants.PATH,request.getSession());
		SessionUtils.setRemovableAttribute("current",null,TableTagConstants.PATH,request.getSession());
		SessionUtils.setRemovableAttribute("meth",null,TableTagConstants.PATH,request.getSession());
		SessionUtils.setRemovableAttribute("forwardkey",null,TableTagConstants.PATH,request.getSession());
		SessionUtils.setRemovableAttribute("action",null,TableTagConstants.PATH,request.getSession());
		SessionUtils.removeAttribute(Constants.SEARCH_RESULTS,request);
	}
	
	protected void addSeachValues(String searchString , String officeId,String officeName,HttpServletRequest request) throws PageExpiredException{
		
		SessionUtils.setAttribute(Constants.SEARCH_STRING,searchString,request);
		SessionUtils.setAttribute(Constants.BRANCH_ID,officeId,request);
		SessionUtils.setAttribute(Constants.OFFICE_NAME,officeName,request);
		
	}
	public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		cleanUpSearch(request);
		SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS,getSearchResult(form),request);
		return mapping.findForward(ActionForwards.search_success.toString());
	}
	
//	 added for moratorium [start]
	public ActionForward searchForMoratorium(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		cleanUpSearch(request);
		SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS,getSearchResult(form),request);
		CustSearchActionForm actionForm = (CustSearchActionForm) form;
		//if(actionForm.getInput() != null && actionForm.getInput().equals("moratorium"))
			return mapping.findForward(ActionForwards.moratorium_search_success.toString());
		//else
			//return mapping.findForward(ActionForwards.search_success.toString());
	}
	// added for moratorium [end]
	
	protected QueryResult getSearchResult(ActionForm form) throws Exception{
		return null;
	}
	
	
	private void checkForValidData(HttpServletRequest request) throws PageExpiredException{
		
		 SessionUtils.getAttribute(
				Constants.SEARCH_STRING, request);
		SessionUtils.getAttribute(
				Constants.OFFICE_NAME,  request);
		SessionUtils.getAttribute(
				Constants.BRANCH_ID,  request);

		
	}
	protected void checkPermissionForAddingNotes(AccountTypes accountTypes,CustomerLevel customerLevel,
			UserContext userContext, Short recordOfficeId,
			Short recordLoanOfficerId) throws ApplicationException {
		if (!isPermissionAllowed(accountTypes, customerLevel, userContext,
				recordOfficeId, recordLoanOfficerId))
			throw new CustomerException(
					SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
	}

	private boolean isPermissionAllowed(AccountTypes accountTypes,CustomerLevel customerLevel,
			UserContext userContext, Short recordOfficeId,
			Short recordLoanOfficerId) {
		return ActivityMapper.getInstance().isAddingNotesPermittedForAccounts(
				accountTypes, customerLevel, userContext, recordOfficeId,
				recordLoanOfficerId);
	}
}
