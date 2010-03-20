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

import java.util.HashSet;
import java.util.Set;

public class ActivityRoles {

    /**
     * This would hold the id of the activity
     */
    private Short id;

    /**
     * This would hold the all the roles associated with this activity
     */
    private Set activityRoles = new HashSet();

    /**
     * This Function returns the activityRoles
     *
     * @return Returns the activityRoles.
     */
    public Set getActivityRoles() {
        return activityRoles;
    }

    /**
     * This function set the activityRoles
     *
     * @param activityRoles
     *            The activityRoles to set.
     */
    public void setActivityRoles(Set activityRoles) {
        this.activityRoles = activityRoles;
    }

    /**
     * This Function returns the id
     *
     * @return Returns the id.
     */
    public Short getId() {
        return id;
    }

    /**
     * This function set the id
     *
     * @param id
     *            The id to set.
     */
    public void setId(Short id) {
        this.id = id;
    }

}
