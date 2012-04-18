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
package org.mifos.reports.pentaho.util;

import java.util.Date;

import org.joda.time.LocalDate;
import org.mifos.reports.pentaho.params.PentahoDateParameter;
import org.mifos.reports.pentaho.params.PentahoMultiSelectParameter;
import org.mifos.reports.pentaho.params.PentahoInputParameter;
import org.mifos.reports.pentaho.params.AbstractPentahoParameter;
import org.mifos.reports.pentaho.params.PentahoSingleSelectParameter;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;

public class PentahoParamParser {

    public Object parseParamValue(AbstractPentahoParameter param, ParameterDefinitionEntry paramDefEntry)
            throws ReflectionException {
        Object result = null;
        Class<?> clazz = paramDefEntry.getValueType();

        if (param instanceof PentahoDateParameter) {
            PentahoDateParameter dateParam = (PentahoDateParameter) param;
            LocalDate date = dateParam.getDate();
            Date javaDate = (date == null) ? null : date.toDateMidnight().toDate();
            result = ReflectionUtil.parseDateToClass(javaDate, clazz);
        } else if (param instanceof PentahoInputParameter) {
            PentahoInputParameter inputParam = (PentahoInputParameter) param;
            result = ReflectionUtil.parseStringToClass(inputParam.getValue(), clazz);
        } else if (param instanceof PentahoSingleSelectParameter) {
            PentahoSingleSelectParameter singleSelectParam = (PentahoSingleSelectParameter) param;
            result = ReflectionUtil.parseStringToClass(singleSelectParam.getSelectedValue(), clazz);
        } else if (param instanceof PentahoMultiSelectParameter) {
            PentahoMultiSelectParameter multiSelectParam = (PentahoMultiSelectParameter) param;
            String[] selectedValues = multiSelectParam.getSelectedValues();
            Class<?> componentType = (clazz.isArray()) ? clazz.getComponentType() : clazz;
            result = ReflectionUtil.parseStringsToClass(selectedValues, componentType);
        }

        return result;
    }

    public AbstractPentahoParameter parseParam(ParameterDefinitionEntry paramDefEntry) {
        AbstractPentahoParameter result = null;
        if (paramDefEntry instanceof PlainParameter) {
            result = parsePlainParameter(paramDefEntry);
        } else if (paramDefEntry instanceof DefaultListParameter) {
            result = parseListParameter(paramDefEntry);
        } else {
            return null;
        }

        result.setMandatory(paramDefEntry.isMandatory());
        result.setParamName(paramDefEntry.getName());
        return result;
    }

    private AbstractPentahoParameter parsePlainParameter(ParameterDefinitionEntry paramDefEntry) {
        AbstractPentahoParameter result = null;

        if (Date.class.isAssignableFrom(paramDefEntry.getValueType())) {
            result = parseDateParameter(paramDefEntry);
        } else {
            result = parseInputParameter(paramDefEntry);
        }

        return result;
    }

    private PentahoDateParameter parseDateParameter(ParameterDefinitionEntry paramDefEntry) {
        PentahoDateParameter result = new PentahoDateParameter();

        Date defaultValue = null;
        if (paramDefEntry instanceof PlainParameter) {
            PlainParameter plainParam = (PlainParameter) paramDefEntry;
            defaultValue = (Date) plainParam.getDefaultValue();
        } else if (paramDefEntry instanceof DefaultListParameter) {
            DefaultListParameter listParam = (DefaultListParameter) paramDefEntry;
            defaultValue = (Date) listParam.getDefaultValue();
        }

        if (defaultValue != null) {
            result.setDate(new LocalDate(defaultValue));
        }
        return result;
    }

    private PentahoInputParameter parseInputParameter(ParameterDefinitionEntry paramDefEntry) {
        PentahoInputParameter result = new PentahoInputParameter();
        String defaultValue = null;
        if (paramDefEntry instanceof PlainParameter) {
            PlainParameter plainParam = (PlainParameter) paramDefEntry;
            defaultValue = String.valueOf(plainParam.getDefaultValue());
        } else if (paramDefEntry instanceof DefaultListParameter) {
            DefaultListParameter listParam = (DefaultListParameter) paramDefEntry;
            defaultValue = String.valueOf(listParam.getDefaultValue());
        }
        result.setValue(defaultValue);

        return result;
    }

    private AbstractPentahoParameter parseListParameter(ParameterDefinitionEntry paramDefEntry) {
        DefaultListParameter listParam = (DefaultListParameter) paramDefEntry;
        AbstractPentahoParameter result;
        if (listParam.isAllowMultiSelection()) {
            result = parseMultiListParameter(paramDefEntry);
        } else {
            result = parseSingleListParameter(paramDefEntry);
        }
        return result;
    }

    private AbstractPentahoParameter parseSingleListParameter(ParameterDefinitionEntry paramDefEntry) {
        AbstractPentahoParameter result;
        if (paramDefEntry.getValueType().equals(Date.class)) {
            result = parseDateParameter(paramDefEntry);
        } else {
            result = parseInputParameter(paramDefEntry);
        }
        return result;
    }

    private AbstractPentahoParameter parseMultiListParameter(ParameterDefinitionEntry paramDefEntry) {
        AbstractPentahoParameter result;

        result = new PentahoMultiSelectParameter();

        return result;
    }
}
