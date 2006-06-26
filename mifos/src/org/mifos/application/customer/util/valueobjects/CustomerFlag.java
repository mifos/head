/**

 * CustomerFlag.java    version: xxx



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
    import java.util.Set;
    import org.mifos.application.customer.util.valueobjects.Customer;
/**
 * A class that represents a row in the 'customer flag' table.

 */
    public class CustomerFlag extends ValueObject
    {

        private java.lang.Integer customerFlagId;
        private java.lang.Integer customerId;
        private java.lang.Short flagId;
        private java.lang.Short flagStatus;

        private java.lang.Short createdBy;
        private java.sql.Date createdDate;
         private Integer versionNo;
         private Customer customer;




        public CustomerFlag()
        {
		}

        public void setCustomerFlagId(java.lang.Integer customerFlagId)
        {
             this.customerFlagId = customerFlagId;
        }

	/**
	 * Method which returns the flag
	 * @return java.lang.Integer
	 */

	public java.lang.Integer getCustomerFlagId()
        {
             return customerFlagId;
        }

    /**
     * Set the value of the customer
     * @param customerId
     */
      public void setCustomerId(Integer customerId)
      {

		  this.customerId = customerId;
	  }

/**
	 * Method which returns the customer
	 * @return java.lang.Integer
	 */
      public Integer getCustomerId()
      {

		  return customerId;
	  }

    /**
     * Set the value of the flag
     * @param flagId
     */
     public void setFlagId(java.lang.Short flagId)
        {
             this.flagId = flagId;
        }
	/**
	 * Method which returns the flag
	 * @return java.lang.Short
	 */

	public java.lang.Short getFlagId()
        {
             return flagId;
        }
    /**
     * Set the flag status
     * @param flagStatus
     */

        public void setFlagStatus(java.lang.Short flagStatus)
        {
             this.flagStatus = flagStatus;
        }

/**
	 * Method which returns the flag status
	 * @return java.lang.Short
	 */
	public java.lang.Short getFlagStatus()
        {
             return flagStatus;
        }
        public void setCreatedBy(java.lang.Short createdBy)
	{
	             this.createdBy = createdBy;
	}
/**
	 * Method which returns the person who created this record
	 * @return java.lang.Short
	 */
	public java.lang.Short getCreatedBy()
	{
	             return createdBy;
        }

     /**
     * Set the created date
     * @param createdDate
     */
     public void setCreatedDate(java.sql.Date createdDate)
	{
	             this.createdDate = createdDate;
	}

/**
	 * Method which returns the created date
	 * @return java.sql.Date
	 */
	public java.sql.Date getCreatedDate()
	{
	             return createdDate;
        }

   /**
	 * Method which returns the version number
	 * @return Integer
	 */
	 public Integer getVersionNo()
        {
		return versionNo;
	}

   /**
     * Set the version number
     * @param versionNo
     */
	public void setVersionNo(Integer versionNo)
	{
		this.versionNo = versionNo;
	}
	 /**
		 * Method which returns the customer
		 * @return Customer
	 */

	public Customer getCustomer()
	{
		return customer;
	}

	/**
	 * @param customer The customer to set.
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

		}


