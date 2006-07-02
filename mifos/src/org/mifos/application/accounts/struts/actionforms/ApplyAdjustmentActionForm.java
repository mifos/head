/**

 * ApplyAdjustmentActionForm.java    version: 1.0

 

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
package org.mifos.application.accounts.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorActionForm;

/**
 * This class is the action form for Applying adjustments.
 * 
 * @author ashishsm
 *
 */
public class ApplyAdjustmentActionForm extends ValidatorActionForm {
	private String input;
	
	private String adjustmentNote;
	
	private String globalAccountNum;
	
	private boolean adjustcheckbox ;

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getAdjustmentNote() {
		return adjustmentNote;
	}

	public void setAdjustmentNote(String adjustmentNote) {
		this.adjustmentNote = adjustmentNote;
	}

	public String getGlobalAccountNum() {
		return globalAccountNum;
	}

	public void setGlobalAccountNum(String globalAccountNum) {
		this.globalAccountNum = globalAccountNum;
	}

	public boolean getAdjustcheckbox() {
		return adjustcheckbox;
	}

	public void setAdjustcheckbox(boolean adjustcheckbox) {
		this.adjustcheckbox = adjustcheckbox;
		
	}
	
	public void reset(ActionMapping actionMapping,HttpServletRequest request){
		this.adjustcheckbox = false;
		
	}
	
	public ActionErrors validate(ActionMapping actionMapping,HttpServletRequest request){
		
		ActionErrors actionErrors = new ActionErrors();
		if(null != request.getParameter("method") && request.getParameter("method").equals("previewAdjustment")){
			if(!adjustcheckbox){
				request.setAttribute("method", "loadAdjustment");
				 actionErrors.add("", new ActionMessage("errors.mandatorycheckbox"));
			}
			if(adjustmentNote == null || adjustmentNote.trim() == ""){
				request.setAttribute("method", "loadAdjustment");
				actionErrors.add("", new ActionMessage("errors.mandatorytextarea"));
			}else if(adjustmentNote.length()>300){
				request.setAttribute("method", "loadAdjustment");
				actionErrors.add("", new ActionMessage("errors.adjustmentNoteTooBig"));
			}
			if(!actionErrors.isEmpty()){
				return actionErrors;
			}
			 
			
		
		}
		return super.validate(actionMapping, request);
	}
}
