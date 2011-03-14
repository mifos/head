/**
 * 
 */
package org.mifos.framework.util;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.application.admin.servicefacade.CustomizedTextServiceFacade;
import org.mifos.application.servicefacade.AdminServiceFacadeWebTier;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 *
 */
public class MessageFilterReloadableResourceBundleMessageSource extends
		ReloadableResourceBundleMessageSource {

	CustomizedTextServiceFacade customizedTextServiceFacade;	

	public CustomizedTextServiceFacade getMessageCustomizerServiceFacade() {
		return customizedTextServiceFacade;
	}

	public void setMessageCustomizerServiceFacade(
			CustomizedTextServiceFacade customizedTextServiceFacade) {
		this.customizedTextServiceFacade = customizedTextServiceFacade;
	}

	protected MessageFormat resolveCode(String code, Locale locale) {
		MessageFormat messageFormat = super.resolveCode(code, locale);
		return messageFormat;
	}
	
	protected String resolveCodeWithoutArguments(String code, Locale locale) {
		if (code.endsWith("NO_CUSTOMIZING")) {
			return super.resolveCodeWithoutArguments(code, locale);			
		}
		return customizedTextServiceFacade.replaceSubstitutions(super.resolveCodeWithoutArguments(code, locale));
	}
	
}
