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
package org.mifos.reports.pentaho.params;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PentahoMultiSelectParameter extends AbstractPentahoParameter {

    private List<String> selectedValues = new ArrayList<String>();
    private List<String> possibleValues = new ArrayList<String>();
    private Map<String, String> possibleValuesOptions = new HashMap<String, String>();
    private Map<String, String> selectedValuesOptions = new HashMap<String, String>();

    public List<String> getSelectedValues() {
        return selectedValues;
    }

    public void setSelectedValues(List<String> selectedValues) {
        this.selectedValues = selectedValues;
    }

    public List<String> getPossibleValues() {
        return possibleValues;
    }

    public void setPossibleValues(List<String> possibleValues) {
        this.possibleValues = possibleValues;
    }

    public Map<String, String> getPossibleValuesOptions() {
        return possibleValuesOptions;
    }

    public void setPossibleValuesOptions(Map<String, String> possibleValuesOptions) {
        this.possibleValuesOptions = possibleValuesOptions;
    }

    public Map<String, String> getSelectedValuesOptions() {
        return selectedValuesOptions;
    }

    public void setSelectedValuesOptions(Map<String, String> selectedValuesOptions) {
        this.selectedValuesOptions = selectedValuesOptions;
    }

    @Override
    public Object getParamValue() {
        return getSelectedValues();
    }
}
