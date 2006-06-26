/**

 * CustomerAddressDetail.java    version: xxx



 * Copyright © 2005-2006 Grameen Foundation USA

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

package org.mifos.application.customer.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author ashishsm
 *
 */
/**
 * A class that represents a row in the 'customer_address_detail' table.
 * This class may be customized as it is never re-generated
 * after being created.
 */
public class CustomerAddressDetail extends ValueObject{
    /**
     * Simple constructor of CustomerAddressDetail instances.
     */
    public CustomerAddressDetail()
    {
    }

/** The composite primary key value. */
    private java.lang.Integer customerAddressId;

    /** The value of the customer association. */
    private Customer customer;

    private String phoneNumber;

    /** The value of the simple addressName property. */
    private java.lang.String addressName;

    /** The value of the simple line1 property. */
    private java.lang.String line1;

    /** The value of the simple line2 property. */
    private java.lang.String line2;

    /** The value of the simple line3 property. */
    private java.lang.String line3;

    /** The value of the simple city property. */
    private java.lang.String city;

    /** The value of the simple state property. */
    private java.lang.String state;

    /** The value of the simple country property. */
    private java.lang.String country;

    /** The value of the simple zip property. */
    private java.lang.String zip;

    /** The value of the simple addressStatus property. */
    private java.lang.Short addressStatus;



    /**
     * Return the simple primary key value that identifies this object.
     * @return java.lang.Integer
     */
    public java.lang.Integer getCustomerAddressId()
    {
        return customerAddressId;
    }

    /**
     * Set the simple primary key value that identifies this object.
     * @param customerAddressId
     */
    public void setCustomerAddressId(java.lang.Integer customerAddressId)
    {

        this.customerAddressId = customerAddressId;
    }



    /**
     * Return the value of the ADDRESS_NAME column.
     * @return java.lang.String
     */
    public java.lang.String getAddressName()
    {
        return this.addressName;
    }

    /**
     * Set the value of the ADDRESS_NAME column.
     * @param addressName
     */
    public void setAddressName(java.lang.String addressName)
    {
        this.addressName = addressName;
    }

    /**
     * Return the value of the LINE_1 column.
     * @return java.lang.String
     */
    public java.lang.String getLine1()
    {
        return this.line1;
    }

    /**
     * Set the value of the LINE_1 column.
     * @param line1
     */
    public void setLine1(java.lang.String line1)
    {
        this.line1 = line1;
    }

    /**
     * Return the value of the LINE_2 column.
     * @return java.lang.String
     */
    public java.lang.String getLine2()
    {
        return this.line2;
    }

    /**
     * Set the value of the LINE_2 column.
     * @param line2
     */
    public void setLine2(java.lang.String line2)
    {
        this.line2 = line2;
    }

    /**
     * Return the value of the LINE_3 column.
     * @return java.lang.String
     */
    public java.lang.String getLine3()
    {
        return this.line3;
    }

    /**
     * Set the value of the LINE_3 column.
     * @param line3
     */
    public void setLine3(java.lang.String line3)
    {
        this.line3 = line3;
    }

    /**
     * Return the value of the CITY column.
     * @return java.lang.String
     */
    public java.lang.String getCity()
    {
        return this.city;
    }

    /**
     * Set the value of the CITY column.
     * @param city
     */
    public void setCity(java.lang.String city)
    {
        this.city = city;
    }

    /**
     * Return the value of the STATE column.
     * @return java.lang.String
     */
    public java.lang.String getState()
    {
        return this.state;
    }

    /**
     * Set the value of the STATE column.
     * @param state
     */
    public void setState(java.lang.String state)
    {
        this.state = state;
    }

    /**
     * Return the value of the COUNTRY column.
     * @return java.lang.String
     */
    public java.lang.String getCountry()
    {
        return this.country;
    }

    /**
     * Set the value of the COUNTRY column.
     * @param country
     */
    public void setCountry(java.lang.String country)
    {
        this.country = country;
    }

    /**
     * Return the value of the ZIP column.
     * @return java.lang.String
     */
    public java.lang.String getZip()
    {
        return this.zip;
    }

    /**
     * Set the value of the ZIP column.
     * @param zip
     */
    public void setZip(java.lang.String zip)
    {
        this.zip = zip;
    }

    /**
     * Return the value of the ADDRESS_STATUS column.
     * @return java.lang.Short
     */
    public java.lang.Short getAddressStatus()
    {
        return this.addressStatus;
    }

    /**
     * Set the value of the ADDRESS_STATUS column.
     * @param addressStatus
     */
    public void setAddressStatus(java.lang.Short addressStatus)
    {
        this.addressStatus = addressStatus;
    }

	/**
	 * @return Returns the customer}.
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * @param customer The customer to set.
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * @return Returns the phoneNumber}.
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber The phoneNumber to set.
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}





}
