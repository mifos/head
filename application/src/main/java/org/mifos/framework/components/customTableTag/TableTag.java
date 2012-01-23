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

package org.mifos.framework.components.customTableTag;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.TableTagParseException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.security.util.UserContext;

public class TableTag extends BodyTagSupport {

    Table table = null;

    StringBuilder tableInfo = null;

    Locale locale = null;

    Locale mfiLocale = null;

    Locale prefferedLocale = null;

    private String source = null;

    private String scope = null;

    private String xmlFileName = null;

    private String moduleName = null;

    private String passLocale = null;
    
    private String randomNUm = null;
    
    private String currentFlowKey = null;

    // FIXME: now unused and should be able to be deleted
    private String rootName;

    public String getRootName() {
        return rootName;
    }

    public void setRootName(String rootName) {
        this.rootName = rootName;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public String getPassLocale() {
        return passLocale;
    }

    public void setPassLocale(String passLocale) {
        this.passLocale = passLocale;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getScope() {
        return scope;
    }

    public void setXmlFileName(String xmlFileName) {
        this.xmlFileName = xmlFileName;
    }

    public String getXmlFileName() {
        return xmlFileName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleName() {
        return moduleName;
    }
    
    public String getRandomNUm() {
        return randomNUm;
    }

    public void setRandomNUm(String randomNUm) {
        this.randomNUm = randomNUm;
    }

    public String getCurrentFlowKey() {
        return currentFlowKey;
    }

    public void setCurrentFlowKey(String currentFlowKey) {
        this.currentFlowKey = currentFlowKey;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int doStartTag() throws JspException {
        try {

            table = TableTagParser.getInstance().parser(moduleName + "/" + xmlFileName);

            Column[] column = table.getRow().getColumn();
            for (int i = 0; i < column.length; ++i) {
                if (column[i].getLinkDetails() != null) {
                    if (randomNUm == null || currentFlowKey == null) {
                        column[i] = null;
                        
                        if(i < column.length - 1) {
                            int j = i;
                            
                            for(; j < column.length - 1; ++j) {
                                column[j] = column[j + 1];
                            }
                        }
                        
                        column = Arrays.copyOf(column, column.length - 1);
                        table.getRow().setColumn(column);
                    } else {
                        LinkDetails linkDetails = column[i].getLinkDetails();
                        
                        for (ActionParam param : linkDetails.getActionParam()) {
                            if (param.getValueType().equalsIgnoreCase(TableTagConstants.INBUILD)) {
                                if (param.getName().equalsIgnoreCase("randomNUm")) {
                                    param.setValue(randomNUm);
                                } else if (param.getName().equalsIgnoreCase("currentFlowKey")) {
                                    param.setValue(currentFlowKey);
                                }
                            }
                        }
                    }
                }
            }

            tableInfo = new StringBuilder();
            if (source == null || scope == null) {
                throw new JspException();
            }
            List obj = null;
            if (scope.equalsIgnoreCase("session")) {
                obj = (List) pageContext.getSession().getAttribute(source);
            } else if (scope.equalsIgnoreCase("request")) {
                obj = (List) pageContext.getRequest().getAttribute(source);
            }
            if (obj == null) {
                try {
                    obj = (List) SessionUtils.getAttribute(source, (HttpServletRequest) pageContext.getRequest());
                } catch (PageExpiredException e) {
                }
            }

            if (obj == null || obj.isEmpty()) {
                return super.doStartTag();
            }

            if (passLocale != null && passLocale.equalsIgnoreCase("true")) {
                UserContext userContext = (UserContext) pageContext.getSession().getAttribute(
                        Constants.USER_CONTEXT_KEY);

                populateLocale(userContext);

            }

            table.getTable(source, tableInfo, obj, locale, mfiLocale, pageContext,
                    getResourcebundleName());

        } catch (TableTagParseException ex) {
            throw new JspException(ex);
        }

        try {
            pageContext.getOut().print(tableInfo.toString());
        } catch (IOException e) {
            throw new JspException(e);
        }
        return super.doStartTag();
    }

    private void populateLocale(UserContext userContext) {
        if (userContext.getPreferredLocale() != null) {
            locale = userContext.getPreferredLocale();
        }
        if (locale == null && userContext.getMfiLocale() != null) {
            // locale = userContext.getMfiLocale();
            locale = userContext.getCurrentLocale();
        }
        // Setting prefferedLocale and mfiLocale
        if (userContext.getPreferredLocale() != null) {
            prefferedLocale = userContext.getPreferredLocale();
        }
        if (userContext.getMfiLocale() != null) {
            mfiLocale = userContext.getMfiLocale();
        }
    }

    private String getResourcebundleName() {
        return "LookupValueMessages";
    }

}
