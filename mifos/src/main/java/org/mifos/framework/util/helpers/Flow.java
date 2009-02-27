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

package org.mifos.framework.util.helpers;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.mifos.framework.hibernate.helper.QueryResult;

public class Flow implements Serializable {

    private Map<String, Object> sessionData = new HashMap<String, Object>();

    public Flow() {
    }

    /**
     * This is placeholder method documenting the use of non-Serializable
     * QueryResults. It should be removed after QueryResults are made
     * Serializable or refactored.
     */
    public void addQueryResultToSession(String key, QueryResult value) {
        sessionData.put(key, value);
    }

    public void addObjectToSession(String key, Serializable value) {
        sessionData.put(key, value);
    }

    public void addCollectionToSession(String key, Collection<? extends Serializable> value) {
        sessionData.put(key, value);
    }

    public boolean isKeyPresent(String key) {
        return sessionData.containsKey(key);
    }

    public Object getObjectFromSession(String key) {
        return sessionData.get(key);
    }

    public void removeFromSession(String key) {
        sessionData.remove(key);
    }

}
