/**

 * OfficeChangeEvent.java    version: 1.0

 

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
package org.mifos.framework.security.util;

/**
 * This class encapsulate the officechange event
 * @author rajenders
 *
 */
public class OfficeChangeEvent implements SecurityEvent {

	/**
	 * This would hold kind of operation we are performing
	 */
    private	String eventType;
	/**
	 * This would hold the actual object with latest values 
	 */
    private	Object object; 

 
	/**
	 * @param eventType
	 * @param object
	 */
	public OfficeChangeEvent(String eventType, Object object) {
		// TODO Auto-generated constructor stub
		this.eventType = eventType;
		this.object = object;
	}

	/**
	 * This function sets the eventType
	 * @param eventType the eventType to set.
	 */
	
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	/**
	 * This function sets the object
	 * @param object the object to set.
	 */
	
	public void setObject(Object object) {
		this.object = object;
	}

	/**
	 * Default Constructor
	 */
	public OfficeChangeEvent() {
		super();

	}

	/* (non-Javadoc)
	 * @see org.mifos.framework.security.util.SecurityEvent#getEventType()
	 */
	public String getEventType() {
		return this.eventType;
	}

	/* (non-Javadoc)
	 * @see org.mifos.framework.security.util.SecurityEvent#getObject()
	 */
	public Object getObject() {
		return this.object;
	}

}
