package org.mifos.application.reports.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.application.reports.util.helpers.ReportsConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.StringUtils;

public class ReportsCategoryActionForm extends ValidatorActionForm {

	private String categoryName;
	private short categoryId;

	public ReportsCategoryActionForm() {
		super();
	}

	public void clear() {
		categoryId = 0;
		categoryName = null;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	public short getCategoryId() {
		return categoryId;
	}
	
	public void setCategoryId(short categoryId) {
		this.categoryId = categoryId;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		request.setAttribute(Constants.CURRENTFLOWKEY, request
				.getParameter(Constants.CURRENTFLOWKEY));
		request.getSession().setAttribute(Constants.CURRENTFLOWKEY,
				request.getParameter(Constants.CURRENTFLOWKEY));
		String method = request.getParameter("method");

		validateMethod(errors, Methods.preview.toString(), method);
		validateMethod(errors, Methods.editPreview.toString(), method);


		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", method);
		}

		return errors;
	}

	private void validateMethod(ActionErrors errors, String verifyMethod,
			String methodFromRequest) {
		if (methodFromRequest.equals(verifyMethod)) {
			if (StringUtils.isNullOrEmpty(categoryName)) {
				errors.add(ReportsConstants.ERROR_CATEGORYNAME,
						new ActionMessage(ReportsConstants.ERROR_CATEGORYNAME));
			}
		}
	}

}
