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

package org.mifos.framework.components.tabletag;

public class Param {

    public Param() {
        super();
        // TODO Auto-generated constructor stub
    }

    /** Used to set the value of parameterName */
    private String parameterName;

    /** Used to set the value of parameterValue */
    private String parameterValue;

    /** Used to set the value of parameterValueType */
    private String parameterValueType;

    /**
     * @return Returns the parameterName.
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * @param parameterName
     *            The parameterName to set.
     */
    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    /**
     * @return Returns the parameterValue.
     */
    public String getParameterValue() {
        return parameterValue;
    }

    /**
     * @param parameterValue
     *            The parameterValue to set.
     */
    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    /**
     * @return Returns the parameterValueType.
     */
    public String getParameterValueType() {
        return parameterValueType;
    }

    /**
     * @param parameterValueType
     *            The parameterValueType to set.
     */
    public void setParameterValueType(String parameterValueType) {
        this.parameterValueType = parameterValueType;
    }

}
