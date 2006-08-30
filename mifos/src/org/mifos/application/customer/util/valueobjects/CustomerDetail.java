/**

 * CustomerDetail.java    version: xxx



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

package org.mifos.application.customer.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * A class that represents a row in the 'customer_detail' table.
 * This class may be customized as it is never re-generated
 * after being created.
 */
public class CustomerDetail extends ValueObject{
    /**
     * Simple constructor of CustomerDetail instances.
     */
    public CustomerDetail()
    {
    }

/** The composite primary key value. */
    private java.lang.Short customerDetailId;

    /** The value of the customer association. */
    private Integer customerId;

    private Customer customer;

    /** The value of the simple ethinicity property. */
    private java.lang.Integer ethinicity;

    /** The value of the simple citizenship property. */
    private java.lang.Integer citizenship;

    /** The value of the simple handicapped property. */
    private java.lang.Integer handicapped;

    /** The value of the simple businessActivities property. */
    private java.lang.Integer businessActivities;

    /** The value of the simple maritalStatus property. */
    private java.lang.Integer maritalStatus;

    /** The value of the simple educationLevel property. */
    private java.lang.Integer educationLevel;

    /** The value of the simple numChildren property. */
    private java.lang.Short numChildren;

    /** The value of the simple gender property. */
    private java.lang.Short gender;

    /** The value of the simple dateStarted property. */
    private java.util.Date dateStarted;

    /** The value of the simple handicappedDetails property. */
    private java.lang.String handicappedDetails;


    /**
     * Return the simple primary key value that identifies this object.
     * @return java.lang.Short
     */
    public java.lang.Short getCustomerDetailId()
    {
        return customerDetailId;
    }

    /**
     * Set the simple primary key value that identifies this object.
     * @param customerDetailId
     */
    public void setCustomerDetailId(java.lang.Short customerDetailId)
    {

        this.customerDetailId = customerDetailId;
    }



    /**
     * Return the value of the ETHINICITY column.
     * @return java.lang.Integer
     */
    public java.lang.Integer getEthinicity()
    {
        return this.ethinicity;
    }

    /**
     * Set the value of the ETHINICITY column.
     * @param ethinicity
     */
    public void setEthinicity(java.lang.Integer ethinicity)
    {
        this.ethinicity = ethinicity;
    }

    /**
     * Return the value of the CITIZENSHIP column.
     * @return java.lang.Integer
     */
    public java.lang.Integer getCitizenship()
    {
        return this.citizenship;
    }

    /**
     * Set the value of the CITIZENSHIP column.
     * @param citizenship
     */
    public void setCitizenship(java.lang.Integer citizenship)
    {
        this.citizenship = citizenship;
    }

    /**
     * Return the value of the HANDICAPPED column.
     * @return java.lang.Integer
     */
    public java.lang.Integer getHandicapped()
    {
        return this.handicapped;
    }

    /**
     * Set the value of the HANDICAPPED column.
     * @param handicapped
     */
    public void setHandicapped(java.lang.Integer handicapped)
    {
        this.handicapped = handicapped;
    }

    /**
     * Return the value of the BUSINESS_ACTIVITIES column.
     * @return java.lang.String
     */
    public java.lang.Integer getBusinessActivities()
    {
        return this.businessActivities;
    }

    /**
     * Set the value of the BUSINESS_ACTIVITIES column.
     * @param businessActivities
     */
    public void setBusinessActivities(java.lang.Integer businessActivities)
    {
        this.businessActivities = businessActivities;
    }

    /**
     * Return the value of the MARITAL_STATUS column.
     * @return java.lang.Integer
     */
    public java.lang.Integer getMaritalStatus()
    {
        return this.maritalStatus;
    }

    /**
     * Set the value of the MARITAL_STATUS column.
     * @param maritalStatus
     */
    public void setMaritalStatus(java.lang.Integer maritalStatus)
    {
        this.maritalStatus = maritalStatus;
    }

    /**
     * Return the value of the EDUCATION_LEVEL column.
     * @return java.lang.Integer
     */
    public java.lang.Integer getEducationLevel()
    {
        return this.educationLevel;
    }

    /**
     * Set the value of the EDUCATION_LEVEL column.
     * @param educationLevel
     */
    public void setEducationLevel(java.lang.Integer educationLevel)
    {
        this.educationLevel = educationLevel;
    }

    /**
     * Return the value of the NUM_CHILDREN column.
     * @return java.lang.Short
     */
    public java.lang.Short getNumChildren()
    {
        return this.numChildren;
    }

    /**
     * Set the value of the NUM_CHILDREN column.
     * @param numChildren
     */
    public void setNumChildren(java.lang.Short numChildren)
    {
        this.numChildren = numChildren;
    }

    /**
     * Return the value of the GENDER column.
     * @return java.lang.Short
     */
    public java.lang.Short getGender()
    {
        return this.gender;
    }

    /**
     * Set the value of the GENDER column.
     * @param gender
     */
    public void setGender(java.lang.Short gender)
    {
        this.gender = gender;
    }

    /**
     * Return the value of the DATE_STARTED column.
     * @return java.util.Date
     */
    public java.util.Date getDateStarted()
    {
        return this.dateStarted;
    }

    /**
     * Set the value of the DATE_STARTED column.
     * @param dateStarted
     */
    public void setDateStarted(java.util.Date dateStarted)
    {
        this.dateStarted = dateStarted;
    }

    /**
     * Return the value of the HANDICAPPED_DETAILS column.
     * @return java.lang.String
     */
    public java.lang.String getHandicappedDetails()
    {
        return this.handicappedDetails;
    }

    /**
     * Set the value of the HANDICAPPED_DETAILS column.
     * @param handicappedDetails
     */
    public void setHandicappedDetails(java.lang.String handicappedDetails)
    {
        this.handicappedDetails = handicappedDetails;
    }

	/**
	 * @return Returns the customerId}.
	 */
	public Integer getCustomerId() {
		return customerId;
	}

	/**
	 * @param customerId The customerId to set.
	 */
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
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


}
