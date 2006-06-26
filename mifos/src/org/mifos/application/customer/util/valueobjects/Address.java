/**
 *
 */
package org.mifos.application.customer.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author sumeethaec
 */
public class Address extends ValueObject {

	/**Denotes the id of the customer to which the address belongs*/
	private int customerId;
	/**Denotes line 1 of the address*/
	private String address1;
	/**Denotes line 2 of the address*/
	private String address2;
	/**Denotes line 3 of the address*/
	private String address3;
	/**Denotes the city of the center*/
	private String city;
	/**Denotes the state of the center*/
	private String state;
	/**Denotes the country of the center*/
	private String country;
	/**Denotes the zip code*/
	private String zipCode;
	/**Denotes the telephone number of the center*/
	private String telephoneNumber;

	/**
	 * Method which returns address1
	 * @return Returns the address1.
	 */
	public String getAddress1() {
		return address1;
	}
	/**
	 * Method which sets address1
	 * @param address1 The address1 to set.
	 */
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	/**
	 * Method which returns address2
	 * @return Returns the address2.
	 */
	public String getAddress2() {
		return address2;
	}
	/**
	 * Method which sets address2
	 * @param address2 The address2 to set.
	 */
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	/**
	 * Method which returns address3
	 * @return Returns the address3.
	 */
	public String getAddress3() {
		return address3;
	}
	/**
	 * Method which sets address3
	 * @param address3 The address3 to set.
	 */
	public void setAddress3(String address3) {
		this.address3 = address3;
	}
	/**
	 * Method which returns city
	 * @return Returns the city.
	 */
	public String getCity() {
		return city;
	}
	/**
	 * Method which sets city
	 * @param city The city to set.
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * Method which returns country
	 * @return Returns the country.
	 */
	public String getCountry() {
		return country;
	}
	/**
	 * Method which sets country
	 * @param country The country to set.
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	/**
	 * Method which returns customerId
	 * @return Returns the customerId.
	 */
	public int getCustomerId() {
		return customerId;
	}
	/**
	 * Method which sets customerId
	 * @param customerId The customerId to set.
	 */
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	/**
	 * Method which returns state
	 * @return Returns the state.
	 */
	public String getState() {
		return state;
	}
	/**
	 * Method which sets state
	 * @param state The state to set.
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * Method which returns telephoneNumber
	 * @return Returns the telephoneNumber.
	 */
	public String getTelephoneNumber() {
		return telephoneNumber;
	}
	/**
	 * Method which sets telephoneNumber
	 * @param telephoneNumber The telephoneNumber to set.
	 */
	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	/**
	 * Method which returns zipCode
	 * @return Returns the zipCode.
	 */
	public String getZipCode() {
		return zipCode;
	}
	/**
	 * Method which sets zipCode
	 * @param zipCode The zipCode to set.
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

}
