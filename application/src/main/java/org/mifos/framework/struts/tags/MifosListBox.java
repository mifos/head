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

package org.mifos.framework.struts.tags;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts.taglib.TagUtils;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;

/**
 * This class renders the listbox with anchors in it on screen
 */

public class MifosListBox extends BodyTagSupport {

    /** Name of the bean from which you want to pupulate the list */
    private String name;

    /** name of the Bean string array from which you want to populate the list */
    private String property;

    static final long serialVersionUID = 1;

    /** used to set the height of listbox */
    private String height;

    /** used to set the width of listbox */
    private String width;

    /**
     * used to get userContext object
     */
    // todo --string right now, may be object later
    private String userContext;

    /**
     * Function return the property represent the input list
     *
     * @return property. Get from the user
     */

    public String getProperty() {
        return property;
    }

    /**
     * Function set the property represent the input list
     *
     * @param property
     *            . The property to set
     */

    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * Function get the name of the bean
     *
     * @return name. The name of the bean
     */

    public String getName() {
        return name;
    }

    /**
     * Function set the name of the bean
     *
     * @param name
     *            . The name to set
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Default constructor
     */

    public MifosListBox() {
        super();
    }

    /**
     * function get the height of the listbox
     *
     * @return height. Height of the listbox
     */

    public String getHeight() {
        return height;
    }

    /**
     * function set the height of the listbox
     *
     * @param height
     *            . Height of the listbox
     */

    public void setHeight(String height) {
        this.height = height;
    }

    /**
     * function get the userContext
     *
     * @return userContext.The userContext get from the user
     */

    public String getUserContext() {
        return userContext;
    }

    /**
     * function set the userContext
     *
     * @param usercontext
     *            . It sets the userContext
     */

    public void setUserContext(String usercontext) {
        this.userContext = usercontext;
    }

    /**
     * function get the width of the listbox
     *
     * @return width. Width of the listBox
     */
    public String getWidth() {
        return width;
    }

    /**
     * function set the widht of the listbox
     *
     * @param width
     *            . Set the width of the listbox by user
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * variable to hold the bean class
     */
    // private Class beanClass = null;
    /**
     * variable to hold the getlist method
     */

    private Method getList = null;

    /**
     * Function to render the tag in jsp
     *
     * @throws JspException
     */
    @Override
    public int doEndTag() throws JspException {

        // String mifosLocale=null;
        MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Inside doEndTag of MifosListBox Tag");
        StringBuffer results = new StringBuffer();
        Object obj = pageContext.findAttribute(getName());
        if (null == obj) {

            MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("object of the bean is null");

        }
        MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("object is " + obj);
        String[] inputList = null;

        /*
         * to check whether we are getting any userContext or not
         */

        try {
            if (null == getUserContext()) {
                MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("userContext is null");

                getList = obj.getClass().getDeclaredMethod("get" + getProperty(), (Class[]) null);

                MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("method called is " + getList);

                inputList = (String[]) getList.invoke(obj, (Object[]) null);

                MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("List got is " + inputList);
            } else {
                MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("userContext is not null");

                getList = obj.getClass().getDeclaredMethod("get" + getProperty(), new Class[] { Object.class });

                MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("method called is " + getList);

                inputList = (String[]) getList.invoke(obj, new Object[] { new Object() });

                MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("List got is " + inputList);
            }

        } catch (Exception e) {
            throw new JspException(e.getMessage());
        }

        /*
         * todo -- rightnow we are taking hardcoded value we can use UserContext
         * object to get mifos_locale
         */

        // String
        // mifos_locale=((UserContext)pageContext.getSession().getAttribute("userContext")).getLocale();
        // String
        // mifos_locale=(String)pageContext.getServletContext().getInitParameter(mifosLocale);
        String mifos_locale = "en";
        TagUtils.getInstance().write(
                pageContext,
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"pages/framework/css/special_" + mifos_locale
                        + ".css\"/>");

        results.append(" <ul class=\"special_en\" style=\"" + "height: " + getHeight() + ";width: " + getWidth()
                + "\" >");

        for (int i = 0; i < inputList.length; i++) {
            results.append(BuildHref(inputList[i], obj));
            results.append("\" title= \" " + inputList[i] + "\" >");
            results.append(inputList[i] + "</a></li>");
        }

        TagUtils.getInstance().write(pageContext, results.toString());
        return super.doEndTag();
    }

    /**
     * a helper method in the bean which directs the href as to where the code
     * should go
     *
     * @param text
     *            inputList item
     *
     * @param obj
     *            bean object
     *
     * @return link To get link in the listbox
     */

    private String BuildHref(String text, Object obj)

    {
        MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("BuildHref method is invoked");
        StringBuffer buff = new StringBuffer();
        try {
            buff.append("<li><a href=\"");
            buff.append((obj.getClass().getDeclaredMethod("BuildLink", new Class[] { String.class })).invoke(obj,
                    new Object[] { text }));
        }

        catch (NoSuchMethodException nsme) {
            MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).error("No Such Method Found");

        } catch (InvocationTargetException ite) {
            MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Invocation target exception");
        } catch (IllegalAccessException iae) {
            MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Illegal access");
        }

        return buff.toString();
    }

}
