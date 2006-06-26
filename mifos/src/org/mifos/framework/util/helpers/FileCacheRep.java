/**
 * 
 */
package org.mifos.framework.util.helpers;

import java.util.HashMap;
import java.util.Map;

import org.mifos.framework.components.tabletag.Table;

/**
 * @author rohitr
 *
 */
public class FileCacheRep {
	private Map<String,Table> fileCache=null; 
	
	private static FileCacheRep cacheRep=new FileCacheRep();
	public static FileCacheRep getInstance() {
		return cacheRep;
	}
	/**
	 * 
	 */
	private FileCacheRep() {
		fileCache=new HashMap<String,Table>();
	}
	
	public void addTOCacherep(String key,Table object) {
		fileCache.put(key,object);
	}
	
	public Table getFromCacheRep(String key) {
		return fileCache.get(key);
	}
	
	public boolean isKeyPresent(String key) {
		return fileCache.containsKey(key);
	}
}
