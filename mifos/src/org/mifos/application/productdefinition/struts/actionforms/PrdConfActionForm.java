package org.mifos.application.productdefinition.struts.actionforms;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.security.util.UserContext;
import org.mifos.application.login.util.helpers.LoginConstants;

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
		UserContext userContext = (UserContext)request.getSession().getAttribute(LoginConstants.USERCONTEXT);
		Locale locale = userContext.getPreferredLocale();
		ResourceBundle resources = ResourceBundle.getBundle
				(FilePaths.PRODUCT_DEFINITION_UI_RESOURCE_PROPERTYFILE, locale);
		String latenessDays = resources.getString("product.latenessDays");
		String dormancyDays = resources.getString("product.dormancyDays");
		if (method.equals(Methods.update.toString())) {
			if (StringUtils.isNullOrEmpty(getLatenessDays()))
				addError(errors, "latenessDays",
						ProductDefinitionConstants.ERROR_MANDATORY,
						latenessDays);
			else if (getIntegerValue(getLatenessDays()) > 32767)
				addError(errors, "latenessDays",
						ProductDefinitionConstants.ERROR_MAX_DAYS,
						latenessDays,
						ProductDefinitionConstants.MAX_DAYS);
			if (StringUtils.isNullOrEmpty(getDormancyDays()))
				addError(errors, "dormancyDays",
						ProductDefinitionConstants.ERROR_MANDATORY,
						dormancyDays);
			else if (getIntegerValue(getDormancyDays()) > 32767)
				addError(errors, "dormancyDays",
						ProductDefinitionConstants.ERROR_MAX_DAYS,
						dormancyDays,
						ProductDefinitionConstants.MAX_DAYS);
		}
		if (!method.equals(Methods.validate.toString()))
			request.setAttribute("methodCalled", method);
		return errors;
	}
}
