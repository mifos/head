/**

 * PersonnelNotes.java    version: xxx

 

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

package org.mifos.application.personnel.util.valueobjects;

import java.sql.Date;
import org.mifos.framework.util.valueobjects.ValueObject;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;


/**
 * This class is used as value object for the PersonnelNotesAction.
 * It represents the notes entered for a personnel.
 * It has a one to many association with the personnel. 
 */
public class PersonnelNotes extends ValueObject{

	/** The primary key value for personnel notes */
   private Integer commentId;
   
   /** The date on which comment has been added. */
   private Date commentDate;

   /** The value of the simple comment property. */
   private java.lang.String comment;
   
   /** The personnel officer, who has added the comment */
   private Personnel officer;
   
   /** The personnel Id who has added the comment */
   private Short officerId;
   
   /** The personnel is for which note is to be added */
   private Short personnelId;
   
	/**
     * Simple constructor of PersonnelNotes instances.
     */
	public PersonnelNotes() {
		super();
	}

	/**
	 * This method returns the name by which a Personnel Note object will be stored in the context object. 
	 * Framework will set PersonnelNotes object to request with this name. 
	 * @return The name by which the PersonnelNotes  object can be referred to
	 */
   public String getResultName(){
		return PersonnelConstants.PERSONNEL_NOTES_VO;
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
   public void setCommentId(java.lang.Integer commentId)
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
    * Return the value of the PERSONNEL_ID column.
    * @return Short
    */
	public Short getPersonnelId() {
		return personnelId;
	}
	
   /**
    * Set the value of the personnelId
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
