package org.mifos.application.productdefinition.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.struts.actionforms.BaseActionForm;


public class PrdCategoryActionForm extends BaseActionForm {

	private String productType;
	private String productCategoryName;
	private String productCategoryDesc;
	private String productCategoryStatus;
	private String globalPrdCategoryNum;

	public String getGlobalPrdCategoryNum() {
		return globalPrdCategoryNum;
	}
	public void setGlobalPrdCategoryNum(String globalPrdCategoryNum) {
		this.globalPrdCategoryNum = globalPrdCategoryNum;
	}
	public String getProductCategoryDesc() {
		return productCategoryDesc;
	}
	public void setProductCategoryDesc(String productCategoryDesc) {
		this.productCategoryDesc = productCategoryDesc;
	}
	public String getProductCategoryName() {
		return productCategoryName;
	}
	public void setProductCategoryName(String productCategoryName) {
		this.productCategoryName = productCategoryName;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getProductCategoryStatus() {
		return productCategoryStatus;
	}
	public void setProductCategoryStatus(String productCategoryStatus) {
		this.productCategoryStatus = productCategoryStatus;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String methodCalled = request.getParameter(Methods.method.toString());
		if (null != methodCalled) {
			if (Methods.createPreview.toString().equals(methodCalled))
				errors.add(super.validate(mapping, request));
			else if (Methods.managePreview.toString().equals(methodCalled))
				errors.add(super.validate(mapping, request));
		}
		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", methodCalled);
		}
		return errors;
	}

}
