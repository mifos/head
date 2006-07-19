package org.mifos.framework.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.tabletag.TableTagConstants;
import org.mifos.framework.exceptions.ServiceException;
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
}
