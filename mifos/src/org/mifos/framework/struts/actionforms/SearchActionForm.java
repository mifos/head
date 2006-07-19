package org.mifos.framework.struts.actionforms;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts.validator.ValidatorActionForm;

public class SearchActionForm extends ValidatorActionForm {
	
	private Map<String,String> searchNodeMap = new HashMap<String,String>();
	
	public Map<String, String> getSearchNodeMap() {
		return searchNodeMap;
	}

	public void setSearchNodeMap(Map<String, String> searchNodeMap) {
		this.searchNodeMap = searchNodeMap;
	}
	
	public void setSearchNode(String key, String value){
		searchNodeMap.put(key, value);
	}

	public String getSearchNode(String key){
		return searchNodeMap.get(key);
	}

}
