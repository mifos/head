/**

 * AccountNotes.java    version: xxx

 

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

package org.mifos.application.accounts.util.valueobjects;

import java.sql.Date;
import org.mifos.framework.util.valueobjects.ValueObject;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.personnel.util.valueobjects.Personnel;


/**
 * This class is used as value object for the AccountNotesAction.
 * It represents the notes entered for an account.
 * It has a one to many association with the account. 
 * @author ashishsm
 *
 */
public class AccountNotes extends ValueObject{

	/** The primary key value for account notes */
   private Integer commentId;
   
   /** The date on which comment has been added. */
   private Date commentDate;

   /** The value of the simple comment property. */
   private String comment;
   
   /** The personnel officer, who has added the comment */
   private Personnel officer;
   
   /** The personnel Id who has added the comment */
   private Short officerId;
   
   /** The account is for which note is to be added */
   private Integer accountId;
   
	/**
     * Simple constructor of AccountNotes instances.
     */
	public AccountNotes() {
		super();
	}

	/**
	 * This method returns the name by which a Account Note object will be stored in the context object. 
	 * Framework will set AccountNotes object to request with this name. 
	 * @return The name by which the AccountNotes  object can be referred to
	 */
   public String getResultName(){
		return AccountConstants.ACCOUNT_NOTES_VO;
	}
   
	/**
    * Return the simple primary key value that identifies this object.
    * @return Integer
    */
   public java.lang.Integer getCommentId()
   {
       return commentId;
   }

   /**
    * Sets the value of commentId attribute
    * @param commentId
    */
   public void setCommentId(Integer commentId)
   {
       this.commentId = commentId;
   }

   /**
    * Returns the value of commentDate as String
    * @return String
    */
   public String getCommentDateStr()
   {
	   return (commentDate!=null)?this.commentDate.toString():"";	   
   }
   
   /**
    * Return the value of the COMMENT_DATE column.
    * @return Date
    */
   public Date getCommentDate()
   {
       return this.commentDate;
   }

   /**
    * Set the value of the COMMENT_DATE column.
    * @param commentDate
    */
   public void setCommentDate(Date commentDate)
   {
       this.commentDate = commentDate;
   }

   /**
    * Return the value of the NOTE column.
    * @return String
    */
   public String getComment()
   {
       return this.comment;
   }

   /**
    * Set the value of the NOTE column.
    * @param comment
    */
    public void setComment(String comment)
    {
       this.comment = comment;
    }
    
   /**
    * Return the Personnel object who has added notes.
    * @return Personnel
    */
	public Personnel getOfficer() {
		return officer;
	}

   /**
    * Set the value of the officer who has added comment
    * @param comment
    */
	public void setOfficer(Personnel officer) {
		this.officer = officer;
	}

   /**
    * Return the value of the ACCOUNT_ID column.
    * @return Integer
    */
	public Integer getAccountId() {
		return accountId;
	}
	
   /**
    * Set the value of the accountId
    * @param accountId
    */
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	
	/**
	 * Returns the personnelName
	 * @return String
	 */
	public String getPersonnelName()
	{
		return officer.getDisplayName();
	}
	
	/**
	 * Returns the officerId, who has added comment
	 * @return String
	 */
	public Short getOfficerId() {
		return officerId;
	}
	
   /**
    * Set the value of the officerId
    * @param officerId
    */
	public void setOfficerId(Short officerId) {
		this.officerId = officerId;
	}   
	  
}
