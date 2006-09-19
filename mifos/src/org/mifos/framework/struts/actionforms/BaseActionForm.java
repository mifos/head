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
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationConstant;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationHelper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class BaseActionForm extends ValidatorActionForm {

	public void checkForMandatoryFields(ActionErrors errors,HttpServletRequest request){
		BusinessObject businessObject=(BusinessObject)SessionUtils.getAttribute(Constants.BUSINESS_KEY,request.getSession());
		List<FieldConfigurationEntity> mandatoryfieldList=businessObject.getMandatoryFieldList();
		for(FieldConfigurationEntity fieldConfigurationEntity : mandatoryfieldList){
			String propertyName=request.getParameter(fieldConfigurationEntity.getLabel());
			if(propertyName!=null && !propertyName.equals("") ){
				String propertyValue=request.getParameter(propertyName);
				Locale locale=((UserContext)request.getSession().getAttribute(LoginConstants.USERCONTEXT)).getPereferedLocale();
				if(propertyValue==null || propertyValue.equals(""))
					errors.add(fieldConfigurationEntity.getLabel(),
							new ActionMessage(FieldConfigurationConstant.EXCEPTION_MANDATORY,
									FieldConfigurationHelper.getLocalSpecificFieldNames(fieldConfigurationEntity.getLabel(),locale)));
			}
		}
	}
	
	protected void checkForMandatoryFields(Short entityId,ActionErrors errors,HttpServletRequest request){
		Map<Short,List<FieldConfigurationEntity>> entityMandatoryFieldMap=(Map<Short,List<FieldConfigurationEntity>>)request.getSession().getServletContext().getAttribute(Constants.FIELD_CONFIGURATION);
		List<FieldConfigurationEntity> mandatoryfieldList=entityMandatoryFieldMap.get(entityId);
		for(FieldConfigurationEntity fieldConfigurationEntity : mandatoryfieldList){
			String propertyName=request.getParameter(fieldConfigurationEntity.getLabel());
			if(propertyName!=null && !propertyName.equals("") ){
				String propertyValue=request.getParameter(propertyName);
				Locale locale=((UserContext)request.getSession().getAttribute(LoginConstants.USERCONTEXT)).getPereferedLocale();
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
		Date date = null;
		if (StringUtils.isNullAndEmptySafe(strDate))
			date = new Date(DateHelper.getLocaleDate(locale, strDate).getTime());
		return date;
	}
	
	protected UserContext getUserContext(HttpServletRequest request) {
		return (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
	}
	
	protected String getLabel(String key, HttpServletRequest request) {
		try {
			return MifosConfiguration.getInstance().getLabel(key,
					getUserContext(request).getPereferedLocale());
		} catch (ConfigurationException e) {
			return null;
		}
	}
}
