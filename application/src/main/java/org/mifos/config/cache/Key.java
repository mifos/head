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

package org.mifos.config.cache;

/**
 * This class defines Key for OfficeCache. The key is defined as a composite key
 * of officeid and a string constant.
 */
public class Key {

    private Short officeId;
    private String key;

    public Key(Short officeId, String key) {
        this.key = key;
        this.officeId = officeId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Short getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Short officeId) {
        this.officeId = officeId;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 17;
        result = PRIME * result + ((key == null) ? 0 : key.toLowerCase().hashCode());
        result = PRIME * result + ((officeId == null) ? 0 : officeId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Key other = (Key) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equalsIgnoreCase(other.key)) {
            return false;
        }
        if (officeId == null) {
            if (other.officeId != null) {
                return false;
            }
        } else if (!officeId.equals(other.officeId)) {
            return false;
        }
        return true;
    }
}
