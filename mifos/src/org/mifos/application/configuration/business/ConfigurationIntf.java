package org.mifos.application.configuration.business;

import java.util.Locale;

import org.mifos.application.configuration.exceptions.ConfigurationException;




public interface ConfigurationIntf {
	public  String getLabel(String key ,Locale locale)throws ConfigurationException ;
}
