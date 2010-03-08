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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.jsp.PageContext;

import org.mifos.framework.exceptions.TableTagException;

/**
 * This class renders the value of parameters required for display name link.
 */
public class Parameters {

    /** Used to set the value of parameterName */
    private Param[] param;

    public Parameters() {
    }

    /**
     * @return Returns the param.
     */
    public Param[] getParam() {
        return param;
    }

    /**
     * @param param
     *            The param to set.
     */
    public void setParam(Param[] param) {
        this.param = param;
    }

    /**
     * Function to get the parameters.
     *
     * @param obj
     * @return string string array of parameters.
     * @throws TableTagException
     */
    public String[] getParameters(PageContext pageContext, Param[] param, Object obj, Locale locale)
            throws TableTagException {

        // Used to store the value of label if it is a string
        String[] stringArray = new String[param.length];

        // Used to store the value of label if it is a collection
        Collection paramCollection = null;

        // Used to store the value of parameter
        String[] paramString = null;

        // Check the type of object we are getting for each label.
        // whether that is a collection or a string.

        for (int i = 0; i < param.length; i++) {
            Object object = TableTagUtils.getInstance().helper(pageContext, param[i].getParameterValue(),
                    param[i].getParameterValueType(), obj, locale);

            // if object is a collection also then store the value of collection
            // in collectionObject and string in a string array.

            if (object instanceof Collection) {
                // Used to store the value of collection if object is a
                // collection
                paramCollection = (Collection) object;

                // Used to store the value of string if object is a string
                stringArray[i] = null;
            }

            // if object is a string only then store the value of string in
            // a string array.
            // also check whether the string is empty or not.
            // if the string is empty then store null.

            else {
                String stringObject = null;
                if (object != null) {
                    stringObject = object.toString();
                }
                if (stringObject != null && !(stringObject.trim().equals(""))
                        && !(stringObject.trim().equalsIgnoreCase("null"))) {
                    stringArray[i] = stringObject;
                } else {
                    stringArray[i] = null;
                }
            }
        }
        if (paramCollection != null) {
            paramString = new String[paramCollection.size()];
            Iterator it = paramCollection.iterator();
            for (int k = 0; it.hasNext(); k++) {
                StringBuilder stringbuilder = new StringBuilder();
                String collValue = (String) it.next();
                if (collValue != null && !(collValue.trim().equals("")) && !(collValue.trim().equalsIgnoreCase("null"))) {
                    for (int i = 0; i < param.length; i++) {

                        try {
                            stringbuilder.append(param[i].getParameterName()).append("=").append(
                                    URLEncoder.encode((stringArray[i] == null ? collValue : stringArray[i]), "UTF-8"))
                                    .append((i == (param.length - 1)) ? "" : "&");
                        } catch (UnsupportedEncodingException uee) {
                            throw new TableTagException(uee);

                        }
                    }
                    paramString[k] = stringbuilder.toString();
                } else {
                    paramString[k] = null;
                }
            }
        } else {
            paramString = new String[1];
            StringBuilder str = new StringBuilder();
            if (stringArray != null) {

                for (int i = 0; i < stringArray.length; i++) {
                    try {
                        str.append(param[i].getParameterName()).append("=").append(
                                URLEncoder.encode((stringArray[i] != null ? stringArray[i] : ""), "UTF-8")).append(
                                (i == (param.length - 1)) ? "" : "&");
                    } catch (UnsupportedEncodingException uee) {
                        throw new TableTagException(uee);
                    }
                }
                paramString[0] = str.toString();
            } else
                paramString[0] = null;
        }
        return paramString;
    }
}
