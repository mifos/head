/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.framework.security.util;

/**
 * RoleChangeEvent
 */
public class RoleChangeEvent implements SecurityEvent {
    /**
     * This would hold kind of operation we are performing
     */
    private String eventType;
    /**
     * This would hold the actual object with latest values
     */
    private Object object;

    /**
     * This Function returns the object
     * 
     * @return Returns the object.
     */
    public Object getObject() {
        return object;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mifos.framework.security.Util.SecurityEvent#getEventType()
     */
    public String getEventType() {
        // TODO Auto-generated method stub
        return eventType;
    }

    /**
     * @param eventType
     * @param object
     */
    public RoleChangeEvent(String eventType, Object object) {
        // TODO Auto-generated constructor stub
        this.eventType = eventType;
        this.object = object;
    }
}
