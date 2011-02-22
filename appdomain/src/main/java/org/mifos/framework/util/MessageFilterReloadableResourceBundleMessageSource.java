/**
 * 
 */
package org.mifos.framework.util;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.application.servicefacade.AdminServiceFacadeWebTier;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 *
 */
public class MessageFilterReloadableResourceBundleMessageSource extends
		ReloadableResourceBundleMessageSource {

	AdminServiceFacade adminServiceFacade;	

	public AdminServiceFacade getAdminServiceFacade() {
		return adminServiceFacade;
	}

	public void setAdminServiceFacade(AdminServiceFacade adminServiceFacade) {
		this.adminServiceFacade = adminServiceFacade;
	}

	protected MessageFormat resolveCode(String code, Locale locale) {
		MessageFormat messageFormat = super.resolveCode(code, locale);
		return messageFormat;
	}
	
	protected String resolveCodeWithoutArguments(String code, Locale locale) {
		return adminServiceFacade.replaceSubstitutions(super.resolveCodeWithoutArguments(code, locale));
	}
	
}
