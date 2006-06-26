/**
 *
 */
package org.mifos.application.customer.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author sumeethaec
 *
 */
public class CollectionSheet extends ValueObject {

	private int collectionSheetType;
	private String collectionSheetName;

	/**
	 * Method which returns collectionSheetName
	 * @return Returns the collectionSheetName.
	 */
	public String getCollectionSheetName() {
		return collectionSheetName;
	}
	/**
	 * Method which sets collectionSheetName
	 * @param collectionSheetName The collectionSheetName to set.
	 */
	public void setCollectionSheetName(String collectionSheetName) {
		this.collectionSheetName = collectionSheetName;
	}
	/**
	 * Method which returns collectionSheetType
	 * @return Returns the collectionSheetType.
	 */
	public int getCollectionSheetType() {
		return collectionSheetType;
	}
	/**
	 * Method which sets collectionSheetType
	 * @param collectionSheetType The collectionSheetType to set.
	 */
	public void setCollectionSheetType(int collectionSheetType) {
		this.collectionSheetType = collectionSheetType;
	}

}
