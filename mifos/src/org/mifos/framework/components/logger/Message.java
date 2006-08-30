/**
 * Message.java version:1.0  
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
package org.mifos.framework.components.logger;

/**	
 *  This class represents the log message to be logged in case of a direct statement
 *  and not using a resource bundle.
 */
public class Message {

  /**user id of the person logging the message*/
  private String loggedUser;
  
  /**office id of the person logging the message*/
  private String userOffice;

  /**Message that is being logged*/
  private String message;
  
  /**
	 * Constructor: 
	 * Initialises the values of the message 
	 * @param str  The statement to be logged
	 */
  public Message(String str) {
	  this.message=str;
  }//end-constructor 
  /**
	 * Constructor: 
	 * Initialises the values of the message , user id and office id
	 * @param str  The statement to be logged
	 * @param loggerUser UserId of the user logging the statement
	 * @param userOffice OfficeId of the user logging the statement
	 */
  public Message(String str, String loggedUser, String userOffice) {
	  this.message=str;
	  this.loggedUser=loggedUser;
	  this.userOffice=userOffice;
  }//end-constructor
  /** 
   * This method is overridden to append the user id and office id to the actual message passed by the user.
   * @return The string with the message, userid and office id 
   */
  public String toString() {
	  String str=this.message + ApplicationConfig.getUserIdMarker() + this.loggedUser + " " + ApplicationConfig.getOfficeMarker()  + this.userOffice;
	  return(str);
  }//end-method toString
  /** 
   * Function to obtain the office id of the user logging the statement
   * @return The office id 
   */
  public String getUserOffice() {
	return userOffice;
  }//end-method getUserOffice
  
  /** 
   * Function to set the office id of the user logging the statement
   * @param userOffice The office id to be set
   */
  public void setUserOffice(String userOffice) {
	this.userOffice = userOffice;
  }//end-method setUserOffice

  /** 
   * Function to obtain the user id of the user logging the statement
   * @return The  User Id
   */

  public String getLoggedUser() {
	return loggedUser;
  }//end-method getLoggedUser
  
  /** 
   * Function to set the user id of the user logging the statement
   * @param loggedUser The user id to be set 
   */
  public void setLoggedUser(String loggedUser) {
	this.loggedUser = loggedUser;
  }//end-method setLoggedUser
 }//~
