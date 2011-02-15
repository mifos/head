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

package org.mifos.ui.core.controller;

import org.springframework.validation.DefaultBindingErrorProcessor;
import org.springframework.validation.BindingResult;
import org.springframework.beans.PropertyAccessException;
import org.springframework.util.StringUtils;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;


public class LazyBindingErrorProcessor extends DefaultBindingErrorProcessor {
    private Object target;
    private String objectName;
    final private Map<String, Object> rejectedValues = new HashMap<String, Object>();

    @Override
    public void processPropertyAccessException(PropertyAccessException e, BindingResult bindingResult) {
        target = bindingResult.getTarget();
        objectName = bindingResult.getObjectName();
        Object rejectedValue = e.getValue();
        if (rejectedValue != null && rejectedValue.getClass().isArray()) {
            rejectedValue = StringUtils.arrayToCommaDelimitedString(ObjectUtils.toObjectArray(rejectedValue));
        }
        rejectedValues.put(e.getPropertyName(), rejectedValue);
    }

    public Object getTarget() {
        return target;
    }

    public String getObjectName() {
        return objectName;
    }

    public Object getRejectedValue(String field) {
        return rejectedValues.get(field);
    }
}