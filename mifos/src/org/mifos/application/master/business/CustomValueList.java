package org.mifos.application.master.business;

import java.util.List;

/**
 * A CustomValueList is a customizable list of values.  
 * A typical use would be to store a list of values to choose from for
 * assigning to a given object field.  For example, a CustomValueList 
 * for "payment type" might consist of the elements "cash" and "check".  
 */
public interface CustomValueList {
	/**
	 * Return a label associated with this list.  "Payment Type" might
	 * be the label for or "payment type" list example.
	 */
	public String getLabel();
	public List<CustomValueListElement> getElements();
	public void addElement(CustomValueListElement newElement);
}
