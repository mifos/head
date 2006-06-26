/**
 * 
 */
package org.mifos.application.personnel.business;

import org.mifos.framework.business.View;
import org.mifos.framework.security.util.UserContext;

/**
 * @author rohitr
 *
 */
public class PersonnelView extends View {

	/**
	 * @param userContext
	 */
	public PersonnelView(Short personnelId, String displayName) {
		this.personnelId =personnelId;
		this.displayName = displayName;
	}
	
	private Short personnelId;
	private String displayName;
	
	/**
	 * @return Returns the displayName.
	 */
	public String getDisplayName() {
		return displayName;
	}
	/**
	 * @param displayName The displayName to set.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	/**
	 * @return Returns the personnelId.
	 */
	public Short getPersonnelId() {
		return personnelId;
	}
	/**
	 * @param personnelId The personnelId to set.
	 */
	public void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}
	
	

}
