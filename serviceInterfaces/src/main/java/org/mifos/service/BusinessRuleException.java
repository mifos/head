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

package org.mifos.service;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="EI_EXPOSE_REP", justification="should do copy of array")
@SuppressWarnings("PMD")
public class BusinessRuleException extends RuntimeException {

    private final String messageKey;
    private final Object[] messageValues;

    public BusinessRuleException(String messageKey) {
        super();
        this.messageKey = messageKey;
        this.messageValues = null;
    }

    public BusinessRuleException(String messageKey, Object[] messageValues) {
        super();
        this.messageKey = messageKey;
        this.messageValues = messageValues;
    }

    public BusinessRuleException(String messageKey, Throwable e) {
        super(e);
        this.messageKey = messageKey;
        this.messageValues = null;
    }

    public String getMessageKey() {
        return this.messageKey;
    }

    public Object[] getMessageValues() {
        return this.messageValues;
    }
}