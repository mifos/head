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

package org.mifos.framework.formulaic;

import java.util.Map;
import java.util.Set;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

public class SchemaValidationError extends ValidationError {

    private static final String FORM_MSG = "errors.formulaic.invalidform";
    private Map<String, ValidationError> fieldErrors;

    public SchemaValidationError(Map<String, Object> data, Map<String, ValidationError> fieldErrors) {
        super(data, new ActionMessage(SchemaValidationError.FORM_MSG));
        this.fieldErrors = fieldErrors;
    }

    public ActionMessages makeActionMessages() {
        return Schema.makeActionMessages(this);
    }

    public void addErrors(SchemaValidationError errors) {
        fieldErrors.putAll(errors.fieldErrors);
    }

    public int size() {
        return fieldErrors.size();
    }

    public Set<String> keySet() {
        return fieldErrors.keySet();
    }

    public ValidationError getFieldError(String key) {
        return fieldErrors.get(key);
    }

    public String getFieldMsg(String key) {
        return fieldErrors.get(key).getMsg();
    }

    public boolean containsField(String key) {
        return fieldErrors.containsKey(key);
    }

}
