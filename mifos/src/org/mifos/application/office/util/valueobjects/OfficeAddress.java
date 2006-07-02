/**

 * OfficeAddress.java    version: 1.0



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */
package org.mifos.application.office.util.valueobjects;

//import org.mifos.application.office.util.helpers.ObjectAnalyzer;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class represent the address for the office
 * @author rajenders
 *
 */
public class OfficeAddress extends ValueObject {
	private static final long serialVersionUID= 888888888888l;
	
	/**
	 * This would hold the address1
	 */
	private String address1;
	/**
	 * This would hold the address2
	 */
	private String address2;
	/**
	 * This would hold the address3
	 */
	private String address3;
	/**
	 * This would hold the city for address
	 */
	private String city;
	/**
	 * This would hold the state for address
	 */
	
	private String state;
	/**
	 * This would hold the countryfor address
	 */

	private String country;
	/**
	 * This would hold the postalcode for address
	 */

	private String postalCode;
	/**
	 * This would hold the telephoneNo for address
	 */

	private String telephoneNo;
	/**
	 * This would hold the Office for address
	 */
	
	private Office office;
	/**
	 * This would hold the officeAdressId
	 */
	private Short officeAdressId;
	/**
	 * This Function returns the address1
	 * @return Returns the address1.
	 */
	public String getAddress1() {
		return address1;
	}
	/**
	 * This function sets the address1
	 * @param address1 The address1 to set.
	 */
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	/**
	 * This Function returns the address2
	 * @return Returns the address2.
	 */
	public String getAddress2() {
		return address2;
	}
	/**
	 * This function sets the address2
	 * @param address2 The address2 to set.
	 */
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	/**
	 * This Function returns the city
	 * @return Returns the city.
	 */
	public String getCity() {
		return city;
	}
	/**
	 * This function sets the city
	 * @param city The city to set.
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * This Function returns the country
	 * @return Returns the country.
	 */
	public String getCountry() {
		return country;
	}
	/**
	 * This function sets the country
	 * @param country The country to set.
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	/**
	 * This Function returns the officeId
	 * @return Returns the officeId.
	 */
	public Office getOffice() {
		return office;
	}
	/**
	 * This function sets the officeId
	 * @param officeId The officeId to set.
	 */
	public void setOffice(Office office) {
		this.office = office;
	}
	/**
	 * This Function returns the postalCode
	 * @return Returns the postalCode.
	 */
	public String getPostalCode() {
		return postalCode;
	}
	/**
	 * This function sets the postalCode
	 * @param postalCode The postalCode to set.
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	/**
	 * This Function returns the state
	 * @return Returns the state.
	 */
	public String getState() {
		return state;
	}
	/**
	 * This function sets the state
	 * @param state The state to set.
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * This Function returns the telephoneNo
	 * @return Returns the telephoneNo.
	 */
	public String getTelephoneNo() {
		return telephoneNo;
	}
	/**
	 * This function sets the telephoneNo
	 * @param telephoneNo The telephoneNo to set.
	 */
	public void setTelephoneNo(String telephoneNo) {
		this.telephoneNo = telephoneNo;
	}
	/**
	 * This function returns the address3
	 * @return Returns the address3.
	 */
	public String getAddress3() {
		return address3;
	}
	/**
	 * This function sets the address3
	 * @param address3 The address3 to set.
	 */
	public void setAddress3(String address3) {
		this.address3 = address3;
	}
	/**
	 * This function returns the officeAdressId
	 * @return Returns the officeAdressId.
	 */
	public Short getOfficeAdressId() {
		return officeAdressId;
	}
	/**
	 * This function sets the officeAdressId
	 * @param officeAdressId The officeAdressId to set.
	 */
	public void setOfficeAdressId(Short officeAdressId) {
		this.officeAdressId = officeAdressId;
	}


}
