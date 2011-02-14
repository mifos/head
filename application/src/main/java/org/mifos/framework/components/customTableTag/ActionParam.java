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

package org.mifos.framework.components.customTableTag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.mifos.framework.exceptions.TableTagParseException;

public class ActionParam {

    private String name = null;
    private String value = null;
    private String valueType = null;

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getValueType() {
        return valueType;
    }

    public void generateParameter(StringBuilder tableInfo, Object obj) throws TableTagParseException {
        tableInfo.append(getName() + "=");
        Method[] methods = obj.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase("get".concat(getValue()))) {
                try {
                    tableInfo.append(method.invoke(obj, new Object[] {}));
                } catch (IllegalAccessException e) {
                    throw new TableTagParseException(e);
                } catch (InvocationTargetException ex) {
                    throw new TableTagParseException(ex);
                }
            }
        }
    }

}
