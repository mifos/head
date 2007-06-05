package org.mifos.framework.struts.actionforms;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationConstant;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationHelper;
import org.mifos.framework.components.tabletag.TableTagConstants;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class BaseActionForm extends ValidatorActionForm {

	protected void checkForMandatoryFields(Short entityId,ActionErrors errors,HttpServletRequest request){
		Map<Short,List<FieldConfigurationEntity>> entityMandatoryFieldMap=(Map<Short,List<FieldConfigurationEntity>>)request.getSession().getServletContext().getAttribute(Constants.FIELD_CONFIGURATION);
		List<FieldConfigurationEntity> mandatoryfieldList=entityMandatoryFieldMap.get(entityId);
		for(FieldConfigurationEntity fieldConfigurationEntity : mandatoryfieldList){
			String propertyName=request.getParameter(fieldConfigurationEntity.getLabel());
			if(propertyName!=null && !propertyName.equals("") ){
				String propertyValue=request.getParameter(propertyName);
				Locale locale=((UserContext)request.getSession().getAttribute(LoginConstants.USERCONTEXT)).getPreferredLocale();
				if(propertyValue==null || propertyValue.equals(""))
					errors.add(fieldConfigurationEntity.getLabel(),
							new ActionMessage(FieldConfigurationConstant.EXCEPTION_MANDATORY,
									FieldConfigurationHelper.getLocalSpecificFieldNames(fieldConfigurationEntity.getLabel(),locale)));
			}
		}
	}

	protected Short getShortValue(String str) {
		return StringUtils.isNullAndEmptySafe(str) ? Short.valueOf(str) : null;
	}

	protected Integer getIntegerValue(String str) {
		return StringUtils.isNullAndEmptySafe(str) ? Integer.valueOf(str) : null;
	}

	protected Double getDoubleValue(String str) {
		return StringUtils.isNullAndEmptySafe(str) ? Double.valueOf(str) : null;
	}

	protected boolean getBooleanValue(String value) {
		Short shortValue = getShortValue(value);
		return shortValue !=null &&  shortValue > 0;
	}

	protected Money getMoney(String str) {
		return (StringUtils.isNullAndEmptySafe(str) && !str.trim().equals(".")) ? new Money(
				str)
				: new Money();
	}

	protected String getStringValue(Double value) {
		return value != null ? String.valueOf(value) : null;
	}

	protected String getStringValue(Short value) {
		return value != null ? String.valueOf(value) : null;
	}

	protected String getStringValue(boolean value) {
		return value ? "1" : "0";
	}

	protected void addError(ActionErrors errors, String property, String key,
			String... arg) {
		errors.add(property, new ActionMessage(key, arg));
	}

	protected Date getDateFromString(String strDate, Locale locale) {
		if (StringUtils.isNullAndEmptySafe(strDate)) {
			return new Date(DateUtils.getLocaleDate(locale, strDate).getTime());
		}
		return null;
	}

	protected UserContext getUserContext(HttpServletRequest request) {
		return (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
	}

	protected String getLabel(String key, HttpServletRequest request) {
		try {
			return MifosConfiguration.getInstance().getLabel(key,
					getUserContext(request).getPreferredLocale());
		} catch (ConfigurationException e) {
			return null;
		}
	}
	protected void cleanUpSearch(HttpServletRequest request) throws PageExpiredException
	{
		SessionUtils.setRemovableAttribute("TableCache",null,TableTagConstants.PATH,request.getSession());
		SessionUtils.setRemovableAttribute("current",null,TableTagConstants.PATH,request.getSession());
		SessionUtils.setRemovableAttribute("meth",null,TableTagConstants.PATH,request.getSession());
		SessionUtils.setRemovableAttribute("forwardkey",null,TableTagConstants.PATH,request.getSession());
		SessionUtils.setRemovableAttribute("action",null,TableTagConstants.PATH,request.getSession());
		SessionUtils.removeAttribute(Constants.SEARCH_RESULTS,request);
	}

	public CustomFieldView getCustomField(
			List<CustomFieldView> customFields, int i) {
		while (i >= customFields.size()) {
			customFields.add(new CustomFieldView());
		}
		return customFields.get(i);
	}

}
