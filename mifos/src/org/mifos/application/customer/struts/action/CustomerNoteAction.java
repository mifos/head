package org.mifos.application.customer.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.framework.struts.action.MifosSearchAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.application.customer.group.util.helpers.LinkParameters;
import org.mifos.application.customer.struts.actionforms.CustomerNoteActionForm;
import org.mifos.application.customer.util.helpers.CustomerConstants;

public class CustomerNoteAction extends MifosSearchAction {
	public String getPath() {
		  return CustomerConstants.CUSTOMER_NOTE_ACTION;
	  }
	
	 /**
	  *	Method which is called to decide the pages on which the errors on failure of validation will be displayed 
	  * this method forwards as per the respective input page 
	  * @param mapping indicates action mapping defined in struts-config.xml 
	  * @param form The form bean associated with this action
	  * @param request Contains the request parameters
	  * @param response
	  * @return The mapping to the next page
	  */
	public ActionForward customValidate(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) {
		String forward = null;	
		String methodCalled= (String)request.getAttribute("methodCalled");
		CustomerNoteActionForm customerNoteActionForm = (CustomerNoteActionForm)form;
			 //deciding forward page
			if(null !=methodCalled) {
				if(CustomerConstants.METHOD_PREVIEW.equals(methodCalled))
					forward = CustomerConstants.LOAD_SUCCESS;
			}
			return mapping.findForward(forward); 
	}
	
	public ActionForward customLoad(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		clearActionForm(form);
		return null;
	}
	public ActionForward customCreate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		return mapping.findForward(this.chooseForward(request));
	}
 
	public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		CustomerNoteActionForm notesForm = (CustomerNoteActionForm) form;
		notesForm.setSearchNode(Constants.SEARCH_NAME,CustomerConstants.NOTES_SEARCH);
		return mapping.findForward(CustomerConstants.GET_SUCCESS);
	}
	public ActionForward customSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		HttpSession session= request.getSession();
		LinkParameters params = (LinkParameters)session.getAttribute(CustomerConstants.LINK_VALUES);
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		context.addBusinessResults(CustomerConstants.LINK_VALUES,params);
		return null;
	}
	
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception
	{
		
		return mapping.findForward(chooseForward(request)); 
	}
	
	void clearActionForm(ActionForm form){
		CustomerNoteActionForm customerNoteActionForm = (CustomerNoteActionForm)form;
		customerNoteActionForm.setComment("");
	}
	/** 
	 * This method is called on cancel to choose cancel forward.
	 * Based on input page on which cancel has been clicked it forwards to appropriate page
	 * @param fromPage 
	 * @return page to which it has to forward
	 */
	private String chooseForward(HttpServletRequest request){
		String forward=null;
		HttpSession session= request.getSession();
		LinkParameters params = (LinkParameters)session.getAttribute(CustomerConstants.LINK_VALUES);
		switch(params.getLevelId()){
			case CustomerConstants.CLIENT_LEVEL_ID:
				forward=CustomerConstants.CLIENT_DETAILS_PAGE;
				break;
			case CustomerConstants.GROUP_LEVEL_ID:
				forward=CustomerConstants.GROUP_DETAILS_PAGE;
				break;
			case CustomerConstants.CENTER_LEVEL_ID:
				forward=CustomerConstants.CENTER_DETAILS_PAGE;
				break;
		}
		return forward;
	}

}
