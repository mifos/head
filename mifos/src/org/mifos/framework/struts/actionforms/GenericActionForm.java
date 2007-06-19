package org.mifos.framework.struts.actionforms;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.surveys.helpers.SurveyType;

public class GenericActionForm extends ActionForm {
	
	private Map<String, String> values = new HashMap<String, String>();
	
	public void setValue(String key, String object) {
		values.put(key, object);
	}
	
	public String getValue(String key) {
		return values.get(key);
	}
	
	public boolean containsKey(String key) {
		return values.containsKey(key);
	}
}
