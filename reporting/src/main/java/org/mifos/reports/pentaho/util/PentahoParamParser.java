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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.mifos.core.MifosRuntimeException;
import org.mifos.reports.pentaho.params.PentahoDateParameter;
import org.mifos.reports.pentaho.params.PentahoMultiSelectParameter;
import org.mifos.reports.pentaho.params.PentahoInputParameter;
import org.mifos.reports.pentaho.params.AbstractPentahoParameter;
import org.mifos.reports.pentaho.params.PentahoSingleSelectParameter;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterValues;
import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PentahoParamParser {

    private final static Logger logger = LoggerFactory.getLogger(PentahoParamParser.class);

    public List<AbstractPentahoParameter> parseReportParams(MasterReport report) {
        ParameterContext paramContext = null;
        try {
            paramContext = new DefaultParameterContext(report);
            ReportParameterDefinition paramDefinition = report.getParameterDefinition();

            List<AbstractPentahoParameter> result = new ArrayList<AbstractPentahoParameter>();
            for (ParameterDefinitionEntry paramDefEntry : paramDefinition.getParameterDefinitions()) {
                result.add(parseParam(paramDefEntry, paramContext));
            }

            return result;
        } catch (Exception ex) {
            throw new MifosRuntimeException(ex);
        } finally {
            if (paramContext != null) {
                try {
                    paramContext.close();
                } catch (ReportDataFactoryException ex) {
                    logger.error("Exception while closing parameter context", ex);
                }
            }
        }
    }

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
            Class<?> componentType = (clazz.isArray()) ? clazz.getComponentType() : clazz;
            result = ReflectionUtil.parseStringsToClass(multiSelectParam.getSelectedValues(), componentType);
        }

        return result;
    }

    private AbstractPentahoParameter parseParam(ParameterDefinitionEntry paramDefEntry, ParameterContext paramContext)
            throws ReportDataFactoryException {
        AbstractPentahoParameter result = null;
        if (paramDefEntry instanceof PlainParameter) {
            result = parsePlainParameter((PlainParameter) paramDefEntry);
        } else if (paramDefEntry instanceof ListParameter) {
            result = parseListParameter((ListParameter) paramDefEntry, paramContext);
        } else {
            return null;
        }

        result.setMandatory(paramDefEntry.isMandatory());
        result.setParamName(paramDefEntry.getName());
        try {
            result.setLabelName(paramDefEntry.getParameterAttribute(paramDefEntry.getParameterAttributeNamespaces()[0], "label", paramContext).replace(":", ""));
        } catch (NullPointerException ex) {
            result.setLabelName(paramDefEntry.getName());
        }
        return result;
    }

    private AbstractPentahoParameter parsePlainParameter(PlainParameter paramDefEntry) {
        AbstractPentahoParameter result = null;

        if (Date.class.isAssignableFrom(paramDefEntry.getValueType())) {
            result = parseDateParameter(paramDefEntry);
        } else {
            result = parseInputParameter(paramDefEntry);
        }
        return result;
    }

    private PentahoDateParameter parseDateParameter(PlainParameter paramDefEntry) {
        PentahoDateParameter result = new PentahoDateParameter();

        Date defaultValue = (Date) paramDefEntry.getDefaultValue();
        if (defaultValue != null) {
            result.setDate(new LocalDate(defaultValue));
        }
        return result;
    }

    private PentahoInputParameter parseInputParameter(PlainParameter paramDefEntry) {
        PentahoInputParameter result = new PentahoInputParameter();

        String defaultValue = String.valueOf(paramDefEntry.getDefaultValue());
        result.setValue(defaultValue);

        return result;
    }

    private AbstractPentahoParameter parseListParameter(ListParameter paramDefEntry, ParameterContext paramContext)
            throws ReportDataFactoryException {
        DefaultListParameter listParam = (DefaultListParameter) paramDefEntry;
        AbstractPentahoParameter result;
        if (listParam.isAllowMultiSelection()) {
            result = parseMultiListParameter(paramDefEntry, paramContext);
        } else {
            result = parseSingleListParameter(paramDefEntry, paramContext);
        }
        return result;
    }

    private PentahoSingleSelectParameter parseSingleListParameter(ListParameter paramDefEntry,
            ParameterContext paramContext) throws ReportDataFactoryException {
        PentahoSingleSelectParameter result = new PentahoSingleSelectParameter();

        Map<String, String> possibleValues = getPossibleValuesForParam(paramDefEntry, paramContext);
        result.setPossibleValues(possibleValues);

        Object defaultVal = paramDefEntry.getDefaultValue(paramContext);
        if (defaultVal != null && possibleValues.containsKey(String.valueOf(defaultVal))) {
            result.setSelectedValue(String.valueOf(defaultVal));
        }

        return result;
    }

    private PentahoMultiSelectParameter parseMultiListParameter(ListParameter paramDefEntry,
            ParameterContext paramContext) throws ReportDataFactoryException {
        PentahoMultiSelectParameter result = new PentahoMultiSelectParameter();

        Map<String, String> possibleValues = getPossibleValuesForParam(paramDefEntry, paramContext);
        result.setPossibleValuesOptions(possibleValues);

        return result;
    }

    private Map<String, String> getPossibleValuesForParam(ListParameter paramDefEntry, ParameterContext paramContext)
            throws ReportDataFactoryException {
        ParameterValues paramValues = paramDefEntry.getValues(paramContext);
        Map<String, String> result = new HashMap<String, String>();

        for (int i = 0; i < paramValues.getRowCount(); i++) {
            String key = String.valueOf(paramValues.getKeyValue(i));
            String value = String.valueOf(paramValues.getTextValue(i));
            result.put(key, value);
        }
        return result;
    }
}
