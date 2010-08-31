/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

public class AuditLogDto {

    private final String date;
    private final String field;
    private final String oldValue;
    private final String newValue;
    private final String user;

    public AuditLogDto(String date, String field, String oldValue, String newValue, String user) {
        this.date = date;
        this.field = field;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.user = user;
    }

    public String getDate() {
        return this.date;
    }

    public String getField() {
        return this.field;
    }

    public String getOldValue() {
        return this.oldValue;
    }

    public String getNewValue() {
        return this.newValue;
    }

    public String getUser() {
        return this.user;
    }
}