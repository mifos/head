package org.mifos.framework.components.fieldConfiguration.util.helpers;

import java.util.Locale;
import java.util.PropertyResourceBundle;

import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.exceptions.ConfigurationException;

public class FieldConfigurationHelper {
	
	public static String getLocalSpecificFieldNames(String fieldName,Locale locale){
		try{
			String configuredLabel=getConfiguredFieldName(fieldName,locale);
			if(configuredLabel!=null)
				return configuredLabel;
			PropertyResourceBundle propertyString = (PropertyResourceBundle)PropertyResourceBundle.getBundle(FieldConfigurationConstant.FIELD_CONF_PROPERTYFILE,locale);
			return propertyString.getString(fieldName);
		}catch(Exception e){
			e.printStackTrace();
		}
		return fieldName;
	}
	
	public static String getConfiguredFieldName(String fieldName,Locale locale){
		try{
			String labelName=fieldName.substring(fieldName.indexOf(".")+1);
			labelName= MifosConfiguration.getInstance().getLabel(labelName,locale);
			if(labelName!=null)
				return labelName;
			}catch(ConfigurationException ce){
				ce.printStackTrace();
			}
		return null;
	}

}
