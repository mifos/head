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

import org.mifos.framework.util.helpers.Constants;

public class EventFactory {
    /**
     * This would hold the EventFactory instance
     */
    private static EventFactory ef = new EventFactory();

    /**
     * This function returns the EventFactory object
     *
     * @return ef event factory object
     */
    public static EventFactory getEventFactory() {
        return ef;
    }

    /**
     * This function creates the event based on the passed parameter
     *
     * @param name
     *            name of the event
     * @return SecurityEvent interface
     */
    public SecurityEvent createEvent(String name, Object obj, String type) {
        if (name.equalsIgnoreCase(Constants.ROLECHANGEEVENT)) {
            return new RoleChangeEvent(type, obj);
        } else if (name.equalsIgnoreCase(Constants.ACTIVITYCHANGEEVENT)) {
            return new ActivityChangeEvent(type, obj);
        } else if (name.equalsIgnoreCase(SecurityConstants.OFFICECHANGEEVENT)) {
            return new OfficeChangeEvent(type, obj);
        } else {
            return null;
        }
    }

}
