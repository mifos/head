package org.mifos.framework.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.tabletag.TableTagConstants;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.QueryResult;
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
		int current=Integer.valueOf((Integer)SessionUtils.getAttribute("current",request.getSession()));
		SessionUtils.setRemovableAttribute("current",Integer.valueOf(current-1),TableTagConstants.PATH,request.getSession());
		SessionUtils.setRemovableAttribute("meth","previous",TableTagConstants.PATH,request.getSession());
		return mapping.findForward((String)SessionUtils.getAttribute("forwardkey",request.getSession()));
	}
	
	public ActionForward searchNext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		int current=1;
		if(null !=SessionUtils.getAttribute("current",request.getSession())) {
		  current=Integer.valueOf((Integer)SessionUtils.getAttribute("current",request.getSession()));
		}
		SessionUtils.setRemovableAttribute("current",Integer.valueOf(current+1),TableTagConstants.PATH,request.getSession());
		SessionUtils.setRemovableAttribute("meth","next",TableTagConstants.PATH,request.getSession());
		return mapping.findForward((String)SessionUtils.getAttribute("forwardkey",request.getSession()));
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
		SessionUtils.setAttribute(Constants.SEARCH_RESULTS,getSearchResult(form),request);
		return mapping.findForward(ActionForwards.search_success.toString());
	}
	
	protected QueryResult getSearchResult(ActionForm form) throws Exception{
		return null;
	}
}
