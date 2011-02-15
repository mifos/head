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

package org.mifos.dto.screen;

import org.joda.time.LocalDate;

public class CustomerNoteFormDto {

    private final String globalNum;
    private final String displayName;
    private final Integer customerLevel;
    private final LocalDate commentDate;
    private final String commentUser;
    private final String comment;

    public CustomerNoteFormDto(String globalNum, String displayName, Integer customerLevel, LocalDate commentDate, String commentUser, String comment) {
        this.globalNum = globalNum;
        this.displayName = displayName;
        this.customerLevel = customerLevel;
        this.commentDate = commentDate;
        this.commentUser = commentUser;
        this.comment = comment;
    }

    public String getGlobalNum() {
        return this.globalNum;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Integer getCustomerLevel() {
        return this.customerLevel;
    }

    public LocalDate getCommentDate() {
        return this.commentDate;
    }

    public String getCommentUser() {
        return this.commentUser;
    }

    public String getComment() {
        return this.comment;
    }
}