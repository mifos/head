package org.mifos.application.customer.struts.actionforms;

import org.mifos.framework.struts.actionforms.BaseActionForm;

public class CustomerAccountActionForm extends BaseActionForm{
	
	private String globalCustNum;

	public String getGlobalCustNum() {
		return globalCustNum;
	}

	public void setGlobalCustNum(String globalCustNum) {
		this.globalCustNum = globalCustNum;
	}
	
}
