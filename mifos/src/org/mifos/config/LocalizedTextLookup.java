
package org.mifos.config;

/**
 * A class which implements this interface can 
 * be used in conjunction with {@link MessageLookup}
 * to lookup a localized text value associated with
 * the key returned by the getPropertiesKey method.
 * 
 * A typical use of this interface would be by an
 * enumerated type to associated a localized text
 * value with each of its entries.  
 * 
 * Examples include {@link WeekDay}, {@link CustomFieldType}
 */
public interface LocalizedTextLookup {
	/**
	 * Return the key to use for looking up the
	 * localized text value associated with an object.
	 */
	public String getPropertiesKey();
}
