package org.mifos.application.reports.struts.actionforms;

import org.apache.struts.validator.ValidatorActionForm;

public class ReportsCategoryActionForm extends ValidatorActionForm {

	private String categoryName;
	
	public ReportsCategoryActionForm() {
		super();
	}
	
	public void clear() {
		categoryName = null;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
}
