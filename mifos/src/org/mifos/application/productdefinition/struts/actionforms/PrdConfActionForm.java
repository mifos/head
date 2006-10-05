package org.mifos.application.productdefinition.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.StringUtils;

public class PrdConfActionForm extends BaseActionForm {

	private String productTypeId;

	private String latenessDays;

	private String dormancyDays;

	public String getDormancyDays() {
		return dormancyDays;
	}

	public void setDormancyDays(String dormancyDays) {
		this.dormancyDays = dormancyDays;
	}

	public String getLatenessDays() {
		return latenessDays;
	}

	public void setLatenessDays(String latenessDays) {
		this.latenessDays = latenessDays;
	}

	public String getProductTypeId() {
		return productTypeId;
	}

	public void setProductTypeId(String productTypeId) {
		this.productTypeId = productTypeId;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String method = request.getParameter("method");
		if (method.equals(Methods.update.toString())) {
			if (StringUtils.isNullOrEmpty(getLatenessDays()))
				addError(errors, "latenessDays",
						ProductDefinitionConstants.ERRORMANDATORY,
						ProductDefinitionConstants.LATENESSDAYS);
			if (StringUtils.isNullOrEmpty(getDormancyDays()))
				addError(errors, "dormancyDays",
						ProductDefinitionConstants.ERRORMANDATORY,
						ProductDefinitionConstants.DORMANCYDAYS);
		}
		if (!method.equals(Methods.validate.toString()))
			request.setAttribute("methodCalled", method);
		return errors;
	}
}
