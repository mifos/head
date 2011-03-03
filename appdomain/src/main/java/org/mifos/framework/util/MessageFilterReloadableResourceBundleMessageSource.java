/**
 * 
 */
package org.mifos.framework.util;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 *
 */
public class MessageFilterReloadableResourceBundleMessageSource extends
		ReloadableResourceBundleMessageSource {

	protected MessageFormat resolveCode(String code, Locale locale) {
		MessageFormat messageFormat = super.resolveCode(code, locale);
		return messageFormat;
	}
	
	protected String resolveCodeWithoutArguments(String code, Locale locale) {
		return replaceSubstitutions(super.resolveCodeWithoutArguments(code, locale));
	}
	
    static public String replaceSubstitutions(String message) {
    	if (message == null) return message;
    	
    	String newMessage = message;
    	LinkedHashMap<String,String> map = new LinkedHashMap<String,String>();
    	map.put("Center", "Kendra");
    	map.put("center", "kendra");
    	map.put("Client", "Borrower");
    	map.put("a Loan", "an Obligation");
    	map.put("Loan", "Obligation");
    	map.put("Database", "DB");
    	map.put("Application Pending Approval", "Pending Approval (almost done)");
    	
        for (Map.Entry<String, String> entry : map.entrySet()) { 
        	newMessage = newMessage.replace(entry.getKey(), entry.getValue());
        }
    	return newMessage;
    }	
}
