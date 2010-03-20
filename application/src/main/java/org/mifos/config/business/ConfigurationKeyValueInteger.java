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

package org.mifos.config.business;

/**
 * ConfigurationKeyValueInteger object hold String->int key value pairs that can
 * be persisted to a database using Hibernate. Helper methods for manipulating
 * these values can be found in
 * {@link org.mifos.config.persistence.ConfigurationPersistence}
 */
public class ConfigurationKeyValueInteger {
    @SuppressWarnings("unused")
    // see .hbm.xml file
    private Integer configurationId;
    private String key;
    private Integer value;

    public ConfigurationKeyValueInteger() {
    }

    public ConfigurationKeyValueInteger(String key, int value) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("A null key is not allowed for ConfigurationKeyValueInteger");
        }
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    @SuppressWarnings("unused")
    // see .hbm.xml file
    private void setKey(String key) {
        this.key = key;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
