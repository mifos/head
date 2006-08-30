/**
 * 
 */
package org.mifos.framework.struts.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.framework.components.tabletag.TableTagConstants;
import org.mifos.framework.struts.action.MifosBaseAction;
import org.mifos.framework.util.helpers.SessionUtils;

public abstract class MifosSearchAction extends MifosBaseAction {
	
	public MifosSearchAction() {
		super();
	}
	
	/**
	 * The method adds Search to the getKeyMethodMap of MifosBaseAction
	 * 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#appendToMap()
	 */
	public Map<String, String> appendToMap() {
		Map<String, String> searchMap = new HashMap<String, String>();
		searchMap.put(TableTagConstants.SEARCHPREV, "searchPrev");
		searchMap.put(TableTagConstants.SEARCHNEXT, "searchNext");
		return searchMap;
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
