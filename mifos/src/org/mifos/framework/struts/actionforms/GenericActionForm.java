package org.mifos.framework.struts.actionforms;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts.action.ActionForm;
import org.mifos.framework.util.helpers.StringUtils;

public class GenericActionForm extends ActionForm {
	
	private Map<String, String> values = new HashMap<String, String>();
	
	public Map<String, String> getMap() {
		return values;
	}
	
	public void setValue(String key, String object) {
		values.put(key, object);
	}
	
	public void setValue(String key, int value) {
		values.put(key, Integer.toString(value));
	}
	
	public String getValue(String key) {
		return values.get(key);
	}
	
	public boolean containsKey(String key) {
		return values.containsKey(key);
	}
	
	public String getDateValue(String keyPrefix) {
		String day = getValue(keyPrefix + "_DD");
		String month = getValue(keyPrefix + "_MM");
		String year = getValue(keyPrefix + "_YY");
		
		if (StringUtils.isNullOrEmpty(day) ||
				StringUtils.isNullOrEmpty(month) || StringUtils.isNullOrEmpty(year))
			return null;
		
		return day + "/" + month + "/" + year;
	}
	
	public Map<String, String> getAll(String prefix) {
		Map<String, String> result = new HashMap<String, String>();
		for (String key : values.keySet()) {
			if (key.startsWith(prefix))
				result.put(key.substring(prefix.length()), values.get(key));
		}
		return result;
	}
	
	public void clear() {
		values.clear();
	}
}
