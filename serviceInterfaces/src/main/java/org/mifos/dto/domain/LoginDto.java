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

public class LoginDto {

    private final Short userId;
    private final Short officeId;
    private final boolean passwordChanged;
    private final boolean passwordExpired;
    
    public LoginDto(Short userId, Short officeId, boolean isPasswordChanged, boolean isPasswordExpired) {
        this.userId = userId;
        this.officeId = officeId;
        this.passwordChanged = isPasswordChanged;
        this.passwordExpired = isPasswordExpired;
    }

    public Short getUserId() {
        return this.userId;
    }

    public Short getOfficeId() {
        return this.officeId;
    }

    public boolean isPasswordChanged() {
        return this.passwordChanged;
    }
    
    public boolean isPasswordExpired() {
    	return this.passwordExpired;
    }
}