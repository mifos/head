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
 
package org.mifos.framework.util.helpers;

import static org.mifos.application.util.helpers.ValidationConstants.MIFOS_WARNING_MESSAGES;

import java.io.Serializable;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.hibernate.helper.QueryResult;

/**
 * This class has helper methods to set attributes in session and retrieve them.
 */
public class SessionUtils {

    /**
     * Sets the attribute in the session and the key is formed by appending path
     * to the key passed. These attributes would be removed from the session
     * when <code>doCleanUp(String path,HttpSession session)</code> is called
     */
    public static void setRemovableAttribute(String key, Object value, String path, HttpSession session) {
        session.setAttribute(path + "_" + key, value);
        MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
                "An attribute being set in the session with key being " + path + "_" + key);
    }

    public static void setContext(String key, Object value, HttpSession session) {
        session.setAttribute(key, value);
    }

    /**
     * Sets the attribute in the session against the key specified. These
     * attributes are removed from session only when session is invalidated
     */
    public static void setAttribute(String key, Serializable value, HttpSession session) {
        MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
                "An attribute being set in the session with key being " + key);
        session.setAttribute(key, value);
    }

    /**
     * Sets the attribute in the session against the key specified. These
     * attributes are removed from session only when session is invalidated
     */
    public static void setCollectionAttribute(String key, Collection<? extends Serializable> value, HttpSession session) {
        MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
                "An attribute being set in the session with key being " + key);
        session.setAttribute(key, value);
    }

    /**
     * Returns the object associated with the key specified. The object is
     * returned immaterial of the fact that they are stored using
     * <code>setRemovaleAttribute</code> or <code>setAttribute</code> methods.
     */
    public static Object getAttribute(String key, HttpSession session) {
        MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("The key to be compared is " + key);
        Enumeration keys = session.getAttributeNames();

        boolean sessionIsEmpty = null == keys;
        if (sessionIsEmpty || null == key) {
            return null;
        } else {
            // start searching in the session if the session has some attributes
            while (keys.hasMoreElements()) {

                String attributeKey = (String) keys.nextElement();
                MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
                        "The attribute name with which it is trying " + "to compare is " + attributeKey);
                // if the key passed is the same as in the enumeration
                // return the corresponding object from the session
                // this will happen if the user sets the attribute
                // using setAttribute method of SessionUtils
                // else check if attribute key has key as suffix
                // this will happen if the user sets the attribute
                // using setRemovableAttribute method of SessionUtils
                if (attributeKey.equals(key)) {
                    Object returnable = session.getAttribute(attributeKey);
                    MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
                            "An exact match has been found with key being " + key + " and attribute key being"
                                    + attributeKey);
                    return returnable;
                } else if (attributeKey.endsWith("_" + key)) {
                    Object returnable = session.getAttribute(attributeKey);
                    MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
                            "An approximate match has been found with key " + "being " + key
                                    + " and attribute key being" + attributeKey);
                    return returnable;
                } else {
                    MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
                            "No match was found for key " + key);
                }// end-if

            }// end-while
            return null;
        }// end-if

    }// end-getAttribute

    public static Object getContext(String key, HttpSession session) {
        MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("The key to be compared is   " + key);
        Enumeration keys = session.getAttributeNames();
        String attributeKey = null;
        Object returnable = null;
        // if key is null or session is empty return null
        if (null == keys || null == key) {
            return null;
        } else {
            // start searching in the session if the session has some attributes
            while (keys.hasMoreElements()) {

                attributeKey = (String) keys.nextElement();
                MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
                        "The attribute name with which it is trying to compare is  " + attributeKey);
                // if the key passed is the same as in the enumeration
                // return the corresponding object from the session
                // this will happen if the user sets the attribute
                // using setAttribute method of SessionUtils
                // else check if attribute key has key as suffix
                // this will happen if the user sets the attribute
                // using setRemovableAttribute method of SessionUtils
                if (attributeKey.equals(key)) {
                    returnable = session.getAttribute(attributeKey);
                    MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
                            "An exact match has been found with key being " + key + " and attribute key being"
                                    + attributeKey);
                    return returnable;
                } else {
                    MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
                            "No match was found for key " + key);
                }// end-if

            }// end-while
            return returnable;
        }// end-if

    }

    /**
     * Removes the attributes from the session where the key has path as its
     * prefix
     */
    public static void doCleanUp(String path, HttpSession session) {
        MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Clean up in session utils has been called");
        Enumeration keys = session.getAttributeNames();
        if (null != keys) {
            while (keys.hasMoreElements()) {
                String attributeKey = (String) keys.nextElement();
                if (attributeKey.startsWith(path)) {
                    MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
                            "The attribute being removed from session is" + attributeKey);
                    session.removeAttribute(attributeKey);
                }// end-if
            }// end-while
        }// end-if
    }// end-doCleanUp

    public static void doCleanUp(HttpSession session) {
        session.invalidate();
    }

    /**
     * This method is a placeholder which documents where objects implmenting
     * the QueryResult interface are saved into the httpsession. QueryResults
     * objects are not Serializable so they should not be saved into an
     * httpsession in their current form. They either need to be modified so
     * that they are Serializable or the mechanism to pass query results to the
     * presentation tier needs to be refactored.
     */
    public static void setQueryResultAttribute(String key, QueryResult value, HttpServletRequest request)
            throws PageExpiredException {
        MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
                "An attribute being set in the session with key being " + key);
        String currentFlowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
        HttpSession session = request.getSession();
        FlowManager flowManager = (FlowManager) session.getAttribute(Constants.FLOWMANAGER);
        flowManager.addQueryResultToFlow(currentFlowKey, key, value);
    }

    /**
     * Save a single Serializable object into the HttpSession via a FlowManager.
     */
    public static void setAttribute(String key, Serializable value, HttpServletRequest request)
            throws PageExpiredException {
        MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
                "An attribute being set in the session with key being " + key);
        String currentFlowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
        HttpSession session = request.getSession();
        FlowManager flowManager = (FlowManager) session.getAttribute(Constants.FLOWMANAGER);
        flowManager.addObjectToFlow(currentFlowKey, key, value);
    }

    /**
     * Save a Collection of Serializable objects into the HttpSession via a
     * FlowManager.
     */
    public static void setCollectionAttribute(String key, Collection<? extends Serializable> value,
            HttpServletRequest request) throws PageExpiredException {
        Flow flow = getFlow(key, request);
        flow.addCollectionToSession(key, value);
    }

    /**
     * Save a Map of Serializable objects into the HttpSession via a
     * FlowManager.
     */
    public static void setMapAttribute(String key, HashMap<? extends Serializable, ? extends Serializable> value,
            HttpServletRequest request) throws PageExpiredException {
        Flow flow = getFlow(key, request);
        flow.addMapToSession(key, value);
    }

    private static Flow getFlow(String key, HttpServletRequest request) throws PageExpiredException {
        MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
                "An attribute being set in the session with key being " + key);
        String currentFlowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
        HttpSession session = request.getSession();
        FlowManager flowManager = (FlowManager) session.getAttribute(Constants.FLOWMANAGER);
        Flow flow = flowManager.getFlowWithValidation(currentFlowKey);
        return flow;
    }

    public static Object getAttribute(String key, HttpServletRequest request) throws PageExpiredException {
        MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("The key to be compared is " + key);

        String currentFlowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
        HttpSession session = request.getSession();
        FlowManager flowManager = (FlowManager) session.getAttribute(Constants.FLOWMANAGER);
        return flowManager.getFromFlow(currentFlowKey, key);
    }

    public static void removeAttribute(String key, HttpServletRequest request) throws PageExpiredException {
        MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Clean up in session utils has been called");
        String currentFlowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
        HttpSession session = request.getSession();
        FlowManager flowManager = (FlowManager) session.getAttribute(Constants.FLOWMANAGER);

        flowManager.removeFromFlow(currentFlowKey, key);
        MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
                "The attribute being removed from session is" + key);
    }

    public static void removeAttribute(String key, HttpSession session) {
        MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Clean up in session utils has been called");
        session.removeAttribute(key);
        MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
                "The attribute being removed from session is" + key);
    }

    public static void addWarningMessage(HttpServletRequest request, String key) throws PageExpiredException {
        ActionMessages warningMessages = null;
        if (request.getAttribute(MIFOS_WARNING_MESSAGES) == null) {
            warningMessages = new ActionMessages();
            request.setAttribute(MIFOS_WARNING_MESSAGES, warningMessages);
        } else {
            warningMessages = (ActionMessages) getAttribute(MIFOS_WARNING_MESSAGES, request);
        }
        warningMessages.add(key, new ActionMessage(key));
    }

}
