package org.mifos.application.master.util.helpers;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: This class doesn't seem to do anything about synchronization.
 * That seems wrong...
 */
public class Cache {
	private static Map<Short,Object> cacheValue = new HashMap<Short,Object>();

	public static Map<Short, Object> getCacheValue() {
		return cacheValue;
	}
	
	public static void addToCache(Short id, Object value, Short accountTypeId) {
		cacheValue.put(id,value);
	}
}
