/**

 * CustomerNote.java    version: xxx



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

package org.mifos.application.customer.business;



import java.util.Date;

import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.PersistentObject;


/**
 * @author ashishsm
 */
public class CustomerNoteEntity extends PersistentObject{

   private Integer commentId;

   private Date commentDate;

   private String comment;
   
   private Short personnelId;

   private CustomerBO customer;
   
   private PersonnelBO personnel;

   private Integer customerId;

   public CustomerNoteEntity()
   {
   }
   public void setCustomerId(Integer customerId)
   {
	   this.customerId = customerId;
   }
   public Integer getCustomerId()
   {
	   return customerId;
   }
   public Integer getCommentId()
   {
       return commentId;
   }
   public void setCommentId(Integer commentId)
   {

       this.commentId = commentId;
   }
   public String getCommentDateStr()
   {
	   return (commentDate!=null)?this.commentDate.toString():"";	   
   }
   public Date getCommentDate()
   {
       return this.commentDate;
   }
   public void setCommentDate(Date commentDate)
   {
       this.commentDate = commentDate;
   }
   public String getComment()
   {
       return this.comment;
   }
   public void setComment(String comment)
   {
       this.comment = comment;
   }
   public CustomerBO getCustomer() {
	   return customer;
   }	
   public void setCustomer(CustomerBO customer)
   {
		if(customer != null)
	    	this.customerId = customer.getCustomerId();
	
		this.customer = customer;
   }
	public PersonnelBO getPersonnel()
	{
		return personnel;
	}
	public void setPersonnel(PersonnelBO personnel) {
		this.personnel = personnel;
	}
	public Short getPersonnelId() {
		return personnelId;
	}
	public void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}
	public String getPersonnelName()
	{
		return personnel.getDisplayName();
	}
}

