package org.mifos.application.master.business;

import org.mifos.framework.business.View;

/**
 * It is unclear why this class extends View.
 * Note that the class {@link BusinessActivityEntity} serves
 * the same purpose but doesn't include the additional id.
 * A CustomValueListElement represents a {@link CustomValueList} element for a
 * given locale.  This corresponds to a {@link LookUpValueEntity} with the
 * text pulled in from a {@link LookUpValueLocaleEntity} for a given locale.
 * 
 * A better name for this class might be ValueListElementForLocale.
 */
public class CustomValueListElement extends View {

	/**
	 * The id of an associated object linked to this list element.
	 * This only appears to be used with {@link CustomerAttendence} for use
	 * related to bulk entry.  
	 */
	private Integer id;

	/**
	 * The id of the {@link LookUpValueEntity} corresponding to this value list element
	 */
	private Integer lookUpId;

	/**
	 * The text value of this list element for a given locale (from the {@link LookUpValueLocaleEntity}) 
	 */
	private String lookUpValue;

	public CustomValueListElement() {
	}

	public CustomValueListElement(java.lang.Integer lookUpId, String lookUpValue) {

		this.lookUpId = lookUpId;
		this.lookUpValue = lookUpValue;
	}

	/**
	 * Possibly used in MasterPersistence.getCustomValueListElements
	 */
	public CustomValueListElement(java.lang.Short id, java.lang.Integer lookUpId,
			String lookUpValue) {

		this.lookUpId = lookUpId;
		this.lookUpValue = lookUpValue;
		this.id = id.intValue();
	}

	/**
	 * Possibly used in MasterPersistence.getCustomValueListElements
	 */
	public CustomValueListElement(java.lang.Integer id, java.lang.Integer lookUpId,
			String lookUpValue) {

		this.lookUpId = lookUpId;
		this.lookUpValue = lookUpValue;
		this.id = id;
	}

	/**
	 * This method is used in some places, but it is unclear if the id value 
	 * is ever set.
	 */
	public java.lang.Integer getId() {
		return id;
	}

	public java.lang.Integer getLookUpId() {
		return lookUpId;
	}

	public java.lang.String getLookUpValue() {
		return lookUpValue;
	}

	public void setLookupValue(String newValue) {
		lookUpValue = newValue;
	}
}
