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

package org.mifos.framework.struts.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;

import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;
import org.mifos.framework.exceptions.ConstantsNotLoadedException;
import org.mifos.framework.exceptions.EnumsNotLoadedException;
import org.mifos.framework.util.helpers.ExceptionConstants;

public class EnumPlugin implements PlugIn {

    private String enumFileNames;

    public void setEnumFileNames(String constantFileNames) {
        this.enumFileNames = constantFileNames;
    }

    public void init(ActionServlet actionServlet, ModuleConfig config) throws ServletException {
        try {
            ServletContext servletContext = actionServlet.getServletContext();
            List<String> enumFileNameList = getEnumFileNames();
            List<Class> enumClassList = buildClasses(enumFileNameList);
            EnumMapBuilder constantBuilder = EnumMapBuilder.getInstance();
            for (Class cl : enumClassList) {
                servletContext.setAttribute(getName(cl), constantBuilder.buildMap(cl));
            }
        } catch (Exception e) {
            UnavailableException ue = new UnavailableException(e.getMessage(), 0);
            ue.initCause(e);
            throw ue;
        }
    }

    private String getName(Class cl) {
        String className = cl.getName();
        String constantKeyName = className.substring(className.lastIndexOf(".") + 1);
        return constantKeyName;
    }

    public void destroy() {
    }

    private List<String> getEnumFileNames() {
        StringTokenizer tokenizer = new StringTokenizer(enumFileNames, ",");
        List<String> fileNameList = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
            fileNameList.add(tokenizer.nextToken());
        }
        return fileNameList;
    }

    List<Class> buildClasses(List<String> enumFileNameList) throws EnumsNotLoadedException {
        List<Class> enumClassNameList = new ArrayList<Class>();
        String constantFileName = null;
        try {
            for (String fileName : enumFileNameList) {
                constantFileName = fileName.trim();
                enumClassNameList.add(Class.forName(constantFileName));
            }
        } catch (ClassNotFoundException cnfe) {
            Object[] values = new Object[] { constantFileName };
            throw new EnumsNotLoadedException(ExceptionConstants.ENUMSNOTLOADEDEXCEPTION, cnfe, values);
        }
        return enumClassNameList;
    }

    private static class EnumMapBuilder {
        private static EnumMapBuilder instance = new EnumMapBuilder();

        private EnumMapBuilder() {
        }

        public static EnumMapBuilder getInstance() {
            return instance;
        }

        public Map buildMap(Class constantClass) throws ConstantsNotLoadedException {
            Map<String, Object> constantsMap = new HashMap<String, Object>();
            Object[] fields = constantClass.getEnumConstants();

            for (int i = 0; i < fields.length; i++) {
                String fieldName = fields[i].toString();
                Object fieldValue = fields[i];
                constantsMap.put(fieldName, fieldValue);
            }
            return constantsMap;
        }
    }

}
