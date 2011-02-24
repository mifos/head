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

import java.util.HashMap;
import java.util.Map;

public class DefineLabelsParameters {

    public static final String CITIZENSHIP = "citizenship";
    public static final String GOVERNMENT_ID = "govtId";
    public static final String STATE = "state";
    public static final String POSTAL_CODE = "postalCode";

    private Map<String, String> labelMap = new HashMap<String, String>();

    public void setLabelMap(Map<String, String> labelMap) {
        this.labelMap = labelMap;
    }

    public Map<String, String> getLabelMap() {
        return labelMap;
    }

    public void setLabel(String label, String text) {
        this.labelMap.put(label, text);
    }

    public String[] getKeys() {
        return this.labelMap.keySet().toArray(new String[0]);
    }

    public String getLabelText(String label) {
        return this.labelMap.get(label);
    }
}
