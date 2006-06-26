package org.mifos.application.customer.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.application.customer.util.helpers.CustomerConstants;

public class CustomerApplyAdjustmentActionForm extends ValidatorActionForm {
	
	private String input;
	
	private String adjustmentNote;
	
	private String globalCustNum;
	
	private boolean adjustcheckbox ;

	public boolean isAdjustcheckbox() {
		return adjustcheckbox;
	}

	public void setAdjustcheckbox(boolean adjustcheckbox) {
		this.adjustcheckbox = adjustcheckbox;
	}

	public String getAdjustmentNote() {
		return adjustmentNote;
	}

	public void setAdjustmentNote(String adjustmentNote) {
		this.adjustmentNote = adjustmentNote;
	}

	public String getGlobalCustNum() {
		return globalCustNum;
	}

	public void setGlobalCustNum(String globalCustNum) {
		this.globalCustNum = globalCustNum;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}
	
	public void reset(ActionMapping actionMapping,HttpServletRequest request){
		this.adjustcheckbox = false;
	}
	
	public ActionErrors validate(ActionMapping actionMapping,HttpServletRequest request){
		ActionErrors actionErrors = new ActionErrors();
		if(null != request.getParameter(CustomerConstants.METHOD) && request.getParameter(CustomerConstants.METHOD).equals(CustomerConstants.METHOD_PREVIEW_ADJUSTMENT)){
			if(!adjustcheckbox){
				request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_LOAD_ADJUSTMENT);
				 actionErrors.add("", new ActionMessage(CustomerConstants.ERROR_MANDATORY_CHECKBOX));
			}
			if(adjustmentNote == null || adjustmentNote.trim() == ""){
				request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_LOAD_ADJUSTMENT);
				actionErrors.add("", new ActionMessage(CustomerConstants.ERROR_MANDATORY_TEXT_AREA));
			}else if(adjustmentNote.length()>300){
				request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_LOAD_ADJUSTMENT);
				actionErrors.add("", new ActionMessage(CustomerConstants.ERROR_ADJUSTMENT_NOTE_TOO_BIG));
			}
			if(!actionErrors.isEmpty()){
				return actionErrors;
			}
			 
			
		
		}
		return super.validate(actionMapping, request);
	}
	

}
