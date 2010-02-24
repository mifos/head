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

package org.mifos.security.util;

/**
 * This class encapsulate the officechange event
 */
public class OfficeChangeEvent implements SecurityEvent {

    /**
     * This would hold kind of operation we are performing
     */
    private final String eventType;
    /**
     * This would hold the actual object with latest values
     */
    private final Object object;

    /**
     * @param eventType
     * @param object
     */
    public OfficeChangeEvent(final String eventType, final Object object) {
        this.eventType = eventType;
        this.object = object;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mifos.security.util.SecurityEvent#getEventType()
     */
    public String getEventType() {
        return this.eventType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mifos.security.util.SecurityEvent#getObject()
     */
    public Object getObject() {
        return this.object;
    }

}
