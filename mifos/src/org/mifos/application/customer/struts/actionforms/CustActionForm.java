package org.mifos.application.customer.struts.actionforms;

import org.mifos.framework.struts.actionforms.BaseActionForm;

public class CustActionForm extends BaseActionForm {
	private String customerId;

	private String globalCustNum;

	private String input;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
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

}
