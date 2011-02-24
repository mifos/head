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

package org.mifos.test.acceptance.framework.admin;

public class DefineLookupOptionParameters {

    public final static int TYPE_SALUTATION = 0;
    public final static int TYPE_USER_TITLE = 1;
    public final static int TYPE_MARITAL_STATUS = 2;
    public final static int TYPE_ETHNICITY = 3;
    public final static int TYPE_EDUCATION_LEVEL = 4;
    public final static int TYPE_CITIZENSHIP = 5;
    public final static int TYPE_BUSINESS_ACTIVITY = 6;
    public final static int TYPE_PURPOSES_OF_LOAN = 7;
    public final static int TYPE_HANDICAPPED = 8;
    public final static int TYPE_COLLATERAL_TYPE = 9;
    public final static int TYPE_OFFICE_TITLE = 10;
    public final static int TYPE_PAYMENT_TYPE = 11;

    private int type;
    private String name;

    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
