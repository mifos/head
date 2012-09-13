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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.LocalDate;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.servicefacade.CenterServiceFacade;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.core.MifosRuntimeException;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.domain.PersonnelDto;
import org.mifos.dto.screen.ChangeAccountStatusDto;
import org.mifos.reports.pentaho.params.AbstractPentahoParameter;
import org.mifos.reports.pentaho.params.PentahoDateParameter;
import org.mifos.reports.pentaho.params.PentahoInputParameter;
import org.mifos.reports.pentaho.params.PentahoMultiSelectParameter;
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

    public List<AbstractPentahoParameter> parseReportParams(MasterReport report, HttpServletRequest request, Map<String, AbstractPentahoParameter> selectedValues, boolean update) {
        ParameterContext paramContext = null;
        try {
            paramContext = new DefaultParameterContext(report);
            ReportParameterDefinition paramDefinition = report.getParameterDefinition();

            List<AbstractPentahoParameter> result = new ArrayList<AbstractPentahoParameter>();
            for (ParameterDefinitionEntry paramDefEntry : paramDefinition.getParameterDefinitions()) {
                result.add(parseParam(paramDefEntry, paramContext, selectedValues, update));
            }

            return result;
        } catch (ReportDataFactoryException ex) {
        	throw new JNDIException("Problem with Pentaho Reports", request);
    	}catch (Exception ex) {
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

    private AbstractPentahoParameter parseParam(ParameterDefinitionEntry paramDefEntry, ParameterContext paramContext, Map<String, AbstractPentahoParameter> selectedValues, boolean update)
            throws ReportDataFactoryException {
        AbstractPentahoParameter result = null;
        if (paramDefEntry instanceof PlainParameter) {
            result = parsePlainParameter((PlainParameter) paramDefEntry);
        } else if (paramDefEntry instanceof ListParameter) {
            result = parseListParameter((ListParameter) paramDefEntry, paramContext, selectedValues, update);
        } else {
            return null;
        }

        result.setMandatory(paramDefEntry.isMandatory());
        result.setParamName(paramDefEntry.getName());
        if (null!=paramDefEntry.getParameterAttribute(paramDefEntry.getParameterAttributeNamespaces()[0], "label", paramContext)) {
            result.setLabelName(paramDefEntry.getParameterAttribute(paramDefEntry.getParameterAttributeNamespaces()[0], "label", paramContext).replace(":", ""));
        } else {
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

    private AbstractPentahoParameter parseListParameter(ListParameter paramDefEntry, ParameterContext paramContext,
    		Map<String, AbstractPentahoParameter> selectedValues, boolean update)
            throws ReportDataFactoryException {
        DefaultListParameter listParam = (DefaultListParameter) paramDefEntry;
        AbstractPentahoParameter result;
        if (listParam.isAllowMultiSelection()) {
            result = parseMultiListParameter(paramDefEntry, paramContext);
        } else {
            result = parseSingleListParameter(paramDefEntry, paramContext, selectedValues, update);
        }
        return result;
    }

    private PentahoSingleSelectParameter parseSingleListParameter(ListParameter paramDefEntry,
            ParameterContext paramContext,Map<String, AbstractPentahoParameter> selectedValues, boolean update) throws ReportDataFactoryException {
        PentahoSingleSelectParameter result = new PentahoSingleSelectParameter();

		Map<String, String> possibleValues = null;
		if (update == false) {
			possibleValues = getPossibleValuesForParam(paramDefEntry,
					paramContext);
		} else {
			possibleValues = updatePossibleValuesForParam(paramDefEntry,
					paramContext, selectedValues);
		}
        result.setPossibleValues(possibleValues);

        Object defaultVal = paramDefEntry.getDefaultValue(paramContext);
        if (defaultVal != null && possibleValues.containsKey(String.valueOf(defaultVal))) {
            result.setSelectedValue(String.valueOf(defaultVal));
        }
        else if (defaultVal==null){
        	result.setSelectedValue(String.valueOf(-1));
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
    

    private Map<String, String> updatePossibleValuesForParam(ListParameter paramDefEntry,
            ParameterContext paramContext, Map<String, AbstractPentahoParameter> selectedValues)
            throws ReportDataFactoryException {
        Map<String, String> result = new HashMap<String, String>();

        String id;
        if (parameterIsOffice(paramDefEntry.getName())) {
            ParameterValues paramValues = paramDefEntry.getValues(paramContext);
            for (int i = 0; i < paramValues.getRowCount(); i++) {
                String key = String.valueOf(paramValues.getKeyValue(i));
                String value = String.valueOf(paramValues.getTextValue(i));
                result.put(key, value);
            }
        } else if (parameterIsCenter(paramDefEntry.getName())) {
            String keyValue = searchKey("(.*officer$)|(.*officer_id$)|(selected_office$)|(.*BRANCH_NAME.*)", selectedValues,
                    paramDefEntry.getName());
            if (!keyValue.equals("")) {
                id = (String) selectedValues.get(keyValue).getParamValue();
                if (!id.equals("-1")) {
                    List<CustomerDetailDto> customerList = ApplicationContextProvider
                            .getBean(CenterServiceFacade.class).retrieveCustomersUnderUser(new Short(id));
                    for (CustomerDetailDto office : customerList) {
                        String key = office.getCustomerId().toString();
                        String value = office.getDisplayName();
                        result.put(key, value);
                    }
                } else {
                    ParameterValues paramValues = paramDefEntry.getValues(paramContext);
                    for (int i = 0; i < paramValues.getRowCount(); i++) {
                        String key = String.valueOf(paramValues.getKeyValue(i));
                        String value = String.valueOf(paramValues.getTextValue(i));
                        result.put(key, value);
                    }
                }
            } else {
                ParameterValues paramValues = paramDefEntry.getValues(paramContext);
                for (int i = 0; i < paramValues.getRowCount(); i++) {
                    String key = String.valueOf(paramValues.getKeyValue(i));
                    String value = String.valueOf(paramValues.getTextValue(i));
                    result.put(key, value);
                }
            }
            String key = "-1";
            String value = "All";
            result.put(key, value);

        }  else if (parameterIsGroup(paramDefEntry.getName())) {
            String keyValue = searchKey("(.*officer*)", selectedValues,
                    paramDefEntry.getName());
            if (!keyValue.equals("")) {
                id = (String) selectedValues.get(keyValue).getParamValue();
                if (!id.equals("-1")) {
                    List<CustomerDetailDto> customerList = ApplicationContextProvider
                            .getBean(CenterServiceFacade.class).retrieveGroupForPentahoReport(new Short(id));
                    for (CustomerDetailDto office : customerList) {
                        String key = office.getCustomerId().toString();
                        String value = office.getDisplayName();
                        result.put(key, value);
                    }
                } else {
                    ParameterValues paramValues = paramDefEntry.getValues(paramContext);
                    for (int i = 0; i < paramValues.getRowCount(); i++) {
                        String key = String.valueOf(paramValues.getKeyValue(i));
                        String value = String.valueOf(paramValues.getTextValue(i));
                        result.put(key, value);
                    }
                }
            } else {
                ParameterValues paramValues = paramDefEntry.getValues(paramContext);
                for (int i = 0; i < paramValues.getRowCount(); i++) {
                    String key = String.valueOf(paramValues.getKeyValue(i));
                    String value = String.valueOf(paramValues.getTextValue(i));
                    result.put(key, value);
                }
            }
            String key = "-1";
            String value = "All";
            result.put(key, value);

        } else if (parameterIsOfficer(paramDefEntry.getName())) {
            String keyValue = searchKey("(.*office$)|(.*office_id$)|(.*branch_id$)|(.*office$)|(.*selectedBranch.*)", selectedValues, paramDefEntry.getName());
            if(!keyValue.equals("")){
            id = (String) selectedValues.get(keyValue).getParamValue();
            if (!id.equals("-1")) {
                ChangeAccountStatusDto changeAccountStatusDto = ApplicationContextProvider
                        .getBean(LoanAccountServiceFacade.class)
                        .retrieveLoanOfficerDetailsForBranch(new Short(id));
                List<PersonnelDto> officers = changeAccountStatusDto
                        .getLoanOfficers();
                for (PersonnelDto officer : officers) {
                    String key = officer.getPersonnelId().toString();
                    String value = officer.getDisplayName();
                    result.put(key, value);
                }
            }
            String key = "-1";
            String value = "All";
            result.put(key, value);
            }
        } else {
            ParameterValues paramValues = paramDefEntry.getValues(paramContext);
            for (int i = 0; i < paramValues.getRowCount(); i++) {
                String key = String.valueOf(paramValues.getKeyValue(i));
                String value = String.valueOf(paramValues.getTextValue(i));
                result.put(key, value);
            }
        }
        return result;
    }
		
	private boolean parameterIsOffice(String name) {
	    Pattern p = Pattern.compile("(.*office_id$)|(.*branch_id$)|(.*office$)");
        Matcher m = p.matcher(name);
        boolean b = m.matches(); 
        return b;
	}
	
	private boolean parameterIsCenter(String name) {
        Pattern p = Pattern.compile("(.*center.*)|(.*CENTER_NAME.*)");
        Matcher m = p.matcher(name);
        boolean b = m.matches(); 
        return b;
    }
    
    private boolean parameterIsOfficer(String name) {
        Pattern p = Pattern.compile("(.*officer$)|(.*officer_id$)|(.*user.*)|(.*selectedLoanOfficer.*)");
        Matcher m = p.matcher(name);
        boolean b = m.matches(); 
        return b;
    }
    
    private boolean parameterIsGroup(String name) {
        Pattern p = Pattern.compile("(.*group.*)");
        Matcher m = p.matcher(name);
        boolean b = m.matches(); 
        return b;
    }
    
    private String searchKey(String regex,
            Map<String, AbstractPentahoParameter> selectedValues, String name) {
        String results = "";

        Pattern p = Pattern.compile(regex);

        Set<String> keys = selectedValues.keySet();
        Iterator<String> ite = keys.iterator();
        
        while (ite.hasNext()) {
            String candidate = ite.next();
            Matcher m = p.matcher(candidate);
            if (m.matches() && !name.equals(candidate)) {
                results = candidate;
            }
        }
        if (results.isEmpty()) {
            return "";
        } else {
            return results;
        }
    }
}
