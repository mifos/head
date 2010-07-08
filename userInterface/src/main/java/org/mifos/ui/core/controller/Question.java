/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */

package org.mifos.ui.core.controller;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

public class Question implements Serializable{
    private String title;
    private static final long serialVersionUID = -2584259958410679795L;
    private String type;
    private String id;
    private boolean required;

    @org.hibernate.validator.constraints.NotEmpty
    @javax.validation.constraints.Size(min=1,max=50)
    public String getTitle() {
        return title;
    }



    public void setTitle(String title) {
        this.title = title;
        trimTitle();
    }

    public void trimTitle() {
        this.title = StringUtils.trim(this.title);
    }

    @org.hibernate.validator.constraints.NotEmpty
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
