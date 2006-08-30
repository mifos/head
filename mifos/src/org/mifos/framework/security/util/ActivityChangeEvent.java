/**

 * ActivityChangeEvent.java    version: xxx

 

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


package org.mifos.framework.security.util;

/**
 * ActivityChangeEvent
 */
public class ActivityChangeEvent implements SecurityEvent {
	
	/**
	 * This would hold the type of operation we are performing e.g insert 
	 */
    private 	String eventType;
    /**
     * Object which we are modifying e.g Role object
     */
    private 	Object object;

    /**
	 * This Function returns the object
	 * @return Returns the object.
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * This function set the object
	 * @param object The object to set.
	 */
	public void setObject(Object object) {
		this.object = object;
	}

	/* (non-Javadoc)
     * @see org.mifos.framework.security.Util.SecurityEvent#getEventType()
     */
    public String getEventType() {
        // TODO Auto-generated method stub
        return eventType;
    }

	/**
	 * This function set the eventType
	 * @param eventType The eventType to set.
	 */
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	/**
	 * @param eventType
	 * @param object
	 */
	public ActivityChangeEvent(String eventType, Object object) {
		// TODO Auto-generated constructor stub
		this.eventType = eventType;
		this.object = object;
	}
}
