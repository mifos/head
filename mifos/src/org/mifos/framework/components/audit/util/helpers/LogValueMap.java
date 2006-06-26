/**
 * 
 */
package org.mifos.framework.components.audit.util.helpers;

import java.util.HashMap;
import java.util.Map;

/**
 * @author krishankg
 *
 */
public class LogValueMap {
	
	private Map valueMap;

	public LogValueMap (){
		valueMap=new HashMap();
	}

	public Map getValueMap() {
		return valueMap;
	}

	private void setValueMap(Map valueMap) {
		this.valueMap = valueMap;
	}
	
	
	public void put(Object key,Object value){
		if(key instanceof String && value instanceof String)
			valueMap.put(key.toString().toUpperCase(),value.toString().toUpperCase());
		else
			valueMap.put(key,value);
	}
	
	
	

}
