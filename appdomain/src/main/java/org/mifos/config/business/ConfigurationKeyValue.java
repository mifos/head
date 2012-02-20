/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

public class ConfigurationKeyValue {

    public static enum Type {INTEGER(0), TEXT(1);
        private Short typeId;

        Type(int typeId) {
            this.typeId = (short) typeId;
        }

        public Short getTypeId() {
            return typeId;
        }

        public static Type fromShort(Short value) {
            for (Type candidate : Type.values()) {
                if (candidate.typeId.equals(value)) {
                    return candidate;
                }
            }
            throw new IllegalArgumentException("Unknown Configuration Type: " + value);
        }
    }

    @SuppressWarnings("unused")
    private Integer configurationId;
    private Short type;
    private String key;
    private String value;

    public ConfigurationKeyValue() {
    }

    public ConfigurationKeyValue(String key, int value) throws IllegalArgumentException {
        this(key, Type.INTEGER.typeId, Integer.toString(value));
    }

    public ConfigurationKeyValue(String key, Short type, String value) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("A null key is not allowed for ConfigurationKeyValue");
        }
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    @SuppressWarnings("unused")
    private void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
