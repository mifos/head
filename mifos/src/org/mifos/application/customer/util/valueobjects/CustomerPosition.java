
/**

 * CustomerPosition.java    version: xxx



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

	  import org.mifos.application.program.util.valueobjects.Program;

	  import org.mifos.application.master.util.valueobjects.Position;

/**
* A class that represents a row in the customer position table.
*/
       public class CustomerPosition extends ValueObject
       {

		public CustomerPosition()
		{

		}


      	  private java.lang.Integer customerId;



/** The value of the positionId property. */
           private java.lang.Integer positionId;

           private java.lang.Integer versionNo;

/** Reference to the customer holding the position. */
           private Customer customerPosition;

/** Reference to the parent customer  */
            private Customer parentCustomer;

           private String customerName;

           private  java.lang.Integer customerPositionId;
  /**
    * Set the parent customer
    * @param customer
    */
		  public void setParentCustomer(Customer customer)
		  {



			  this.parentCustomer = customer;
		  }
   /**
    * Return the parent customer
    * @return Customer
    */
		  public Customer getParentCustomer()
		  {
		  			  return parentCustomer;
		  }
  /**
    * Sets the primary key , called internally by hibernate
    * @param customerPositionId
    */
           public void setCustomerPositionId(java.lang.Integer customerPositionId)
           {

			   this.customerPositionId = customerPositionId;
		   }
 /**
    * Return the primary key associated to the object
    * @return java.lang.Integer
    */
           public java.lang.Integer getCustomerPositionId()
           {

			   return customerPositionId;
		   }
 /**
    * Set the position for the customer
    * @param positionId
    */

           public void setPositionId(java.lang.Integer positionId)
           {
			   this.positionId = positionId;
		   }
 /**
    * Return the position
    * @return java.lang.Integer
    */
           public java.lang.Integer getPositionId()
           {
			   return positionId;
		   }
 /**
    * Set the position for the customer
    * @param customerPosition
    */
		   public void setCustomerPosition(Customer customerPosition)
		   {
			   this.customerPosition = customerPosition;
			   if(customerPosition != null)
			      this.customerName = customerPosition.getDisplayName();

		   }
 /**
    * Return the Customer associated
    * @return Customer
    */
           public Customer getCustomerPosition()
           {
			   return customerPosition;
		   }
 /**
    * Return the Customer name
    * @return String
    */
           public String getCustomerName()
           {
        	   return customerName;
           }
 /**
    * Return the Customer associated
    * @return java.lang.Integer
    */
		public java.lang.Integer getCustomerId()
		{
			return customerId;
		}
 /**
    * Set the customer
    * @param customerId
    */
		public void setCustomerId(java.lang.Integer customerId)
		{

			this.customerId = customerId;
		}

/**
    * Return the version number
    * @return java.lang.Integer
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


	}
