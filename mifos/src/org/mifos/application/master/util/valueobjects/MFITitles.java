/**
 * 
 */
package org.mifos.application.master.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author sumeethaec
 *
 */
public class MFITitles extends ValueObject {
	private Integer titleId;
	private String titleName;
	private LookUpEntity lookUpEnity;
	/**
	 * Method which returns the lookUpValue	
	 * @return Returns the lookUpValue.
	 */
	public LookUpEntity getLookUpEntity() {
		return lookUpEnity;
	}
	/**
	 * Method which sets the lookUpValue
	 * @param lookUpValue The lookUpValue to set.
	 */
	public void setLookUpEntity(LookUpEntity lookUpEnity) {
		this.lookUpEnity = lookUpEnity;
	}
	/**
	 * Method which returns the titleId	
	 * @return Returns the titleId.
	 */
	public Integer getTitleId() {
		return titleId;
	}
	/**
	 * Method which sets the titleId
	 * @param titleId The titleId to set.
	 */
	public void setTitleId(Integer titleId) {
		this.titleId = titleId;
	}
	/**
	 * Method which returns the titleName	
	 * @return Returns the titleName.
	 */
	public String getTitleName() {
		return titleName;
	}
	/**
	 * Method which sets the titleName
	 * @param titleName The titleName to set.
	 */
	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}
}
