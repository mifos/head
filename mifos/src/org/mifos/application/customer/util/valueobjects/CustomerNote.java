/**

 * CustomerNote.java    version: xxx



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

import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.valueobjects.Customer;


/**
 * A class that represents a row in the 'customer_note' table.
 * This class may be customized as it is never re-generated
 * after being created.
 * @author ashishsm
 */
public class CustomerNote extends ValueObject{
   /**
    * Simple constructor of CustomerNote instances.
    */
   public CustomerNote()
   {
   }
   
   /**
	 * This method returns the name by which a Customer Note object will be stored in the context object. 
	 * Framework will set CustomerNote object to request with this name. 
	 * @return The name by which the CustomerNote  object can be referred to
	 */
   public String getResultName(){
		return CustomerConstants.CUSTOMER_NOTE_VO;
	}
   /** The composite primary key value. */
   private java.lang.Integer commentId;

   /** The value of the simple commentDate property. */
   private java.sql.Date commentDate;

   /** The value of the simple comment property. */
   private java.lang.String comment;
   
   /** The value of the simple personnelId property. It indicates person who has entered the note*/
   private Short personnelId;

   /** The customer composition in note */
   private Customer customer;
   
   /** The personnel composition in note */
   private Personnel personnel;

   /** The value of customerId */
   private Integer customerId;

   /**
    * Sets the value of customerId.
    * @param customerId
    */
   public void setCustomerId(Integer customerId)
   {
	   this.customerId = customerId;
   }

   /**
    * Return the simple value of customerId attribute
    * @return java.lang.Integer
    */
   public Integer getCustomerId()
   {
	   return customerId;
   }


   /**
    * Return the simple primary key value that identifies this object.
    * @return java.lang.Integer
    */
   public java.lang.Integer getCommentId()
   {
       return commentId;
   }

   /**
    * Sets the value of commentId attribute
    * @param commentId
    */
   public void setCommentId(java.lang.Integer commentId)
   {

       this.commentId = commentId;
   }

   /**
    * Returns the value of commentDate as String
    * @return java.lang.String
    */
   public String getCommentDateStr()
   {
	   return (commentDate!=null)?this.commentDate.toString():"";	   
   }
   
   /**
    * Return the value of the COMMENT_DATE column.
    * @return java.util.Date
    */
   public java.sql.Date getCommentDate()
   {
       return this.commentDate;
   }

   /**
    * Set the value of the COMMENT_DATE column.
    * @param commentDate
    */
   public void setCommentDate(java.sql.Date commentDate)
   {
       this.commentDate = commentDate;
   }

   /**
    * Return the value of the COMMENT column.
    * @return java.lang.String
    */
   public java.lang.String getComment()
   {
       return this.comment;
   }

   /**
    * Set the value of the COMMENT column.
    * @param comment
    */
   public void setComment(String comment)
   {
       this.comment = comment;
   }

   /**
    * Returns the customer who has entered the note.
    * @return Customer 
    */
   public Customer getCustomer() {
	   return customer;
   }	

   /** Sets the customer
    * @param customer
    */	
   public void setCustomer(Customer customer)
   {
		if(customer != null)
	    	this.customerId = customer.getCustomerId();
	
		this.customer = customer;
   }

	/**
	 * Returns the personnel
	 * @return Personnel.
	 */
	public Personnel getPersonnel()
	{
		return personnel;
	}

	/**
	 * Sets the personnelId
	 * @param personnelId 
	 */
	public void setPersonnel(Personnel personnel) {
		this.personnel = personnel;
	}
	
	/**
	 * Returns the personnelId
	 * @return Short
	 */
	public Short getPersonnelId() {
		return personnelId;
	}

	/**
	 * Sets the personnelId
	 * @param personnelId 
	 */
	public void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}

	/**
	 * Returns the personnelName
	 * @return String
	 */
	public String getPersonnelName()
	{
		return personnel.getDisplayName();
	}

}

