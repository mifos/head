package org.mifos.framework.struts.actionforms;

import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;

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
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;

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
	
	
}
