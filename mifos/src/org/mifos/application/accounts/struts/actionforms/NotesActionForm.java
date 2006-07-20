package org.mifos.application.accounts.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.struts.actionforms.SearchActionForm;
import org.mifos.framework.util.helpers.Constants;

public class NotesActionForm extends SearchActionForm {
	
	private String accountId;
	private String accountTypeId;
	private String prdOfferingName;
	private String comment;
	private String globalAccountNum;
	
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getPrdOfferingName() {
		return prdOfferingName;
	}
	public void setPrdOfferingName(String prdOfferingName) {
		this.prdOfferingName = prdOfferingName;
	}
	public String getAccountTypeId() {
		return accountTypeId;
	}
	public void setAccountTypeId(String accountTypeId) {
		this.accountTypeId = accountTypeId;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getGlobalAccountNum() {
		return globalAccountNum;
	}
	public void setGlobalAccountNum(String globalAccountNum) {
		this.globalAccountNum = globalAccountNum;
	}
	
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		String methodCalled = request.getParameter(Methods.method.toString());
		ActionErrors errors = null;
		if(null !=methodCalled) {
			if(Methods.cancel.toString().equals(methodCalled) || 
			  Methods.searchNext.toString().equals(methodCalled)||
			  Methods.search.toString().equals(methodCalled)||
			  Methods.searchPrev.toString().equals(methodCalled)||	 
			  Methods.load.toString().equals(methodCalled)){
				request.setAttribute(Constants.SKIPVALIDATION, Boolean.valueOf(true));
			} else if (Methods.preview.toString().equals(methodCalled)) {
				errors = handlePreviewValidations(request,errors);
			}
		}
		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", methodCalled);
		}
		return errors;	
	}
	
	private ActionErrors handlePreviewValidations(HttpServletRequest request,ActionErrors errors) {
		if (ValidateMethods.isNullOrBlank(getComment())) {
			if (null == errors) {
				errors = new ActionErrors();
			}
			errors.add(AccountConstants.ERROR_MANDATORY, new ActionMessage(
					AccountConstants.ERROR_MANDATORY, AccountConstants.NOTES));
		} else if (getComment().length() > AccountConstants.COMMENT_LENGTH) {
			// status length is more than 500, throw an exception
			if (null == errors) {
				errors = new ActionErrors();
			}
			errors.add(AccountConstants.MAX_LENGTH, new ActionMessage(
					AccountConstants.MAX_LENGTH, AccountConstants.NOTES,
					AccountConstants.COMMENT_LENGTH));
		}
		return errors;
	}

}
