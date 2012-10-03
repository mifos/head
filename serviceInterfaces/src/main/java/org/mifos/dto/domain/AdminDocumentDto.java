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

package org.mifos.dto.domain;

public class AdminDocumentDto {

    private final Integer id;
    private final String name;
    private final String identifier;
    private final boolean active;

    public AdminDocumentDto(Integer id, String name, String identifier, boolean active) {
        this.id = id;
        this.name = name;
        this.identifier = identifier;
        this.active = active;
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public boolean isActive() {
        return this.active;
    }

    @Override
    public boolean equals(Object obj) {
        boolean isEqual = false;
        if (obj instanceof AdminDocumentDto) {
            isEqual = this.id.equals(((AdminDocumentDto)obj).getId());
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return this.id.intValue();
    }

}