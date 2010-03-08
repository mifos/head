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

package org.mifos.framework.exceptions;

/**
 * These are the exceptions which arise out of some malfunctioning in the
 * system, say the database is down or not able to find some services like the
 * TransactionManager which might be needed.
 */
public class SystemException extends RuntimeException {

    /** Generic message, along the lines of "something has failed". */
    public static final String DEFAULT_KEY = "exception.framework.SystemException";

    /**
     * This is a string which points to the actual message in the resource
     * bundle. The exception message to be shown to the user is taken from the
     * resource bundle and hence can be localized.
     */
    private final String key;

    private final Object[] values;

    public SystemException(final Throwable cause) {
        this(DEFAULT_KEY, cause.getMessage(), cause);
    }

    public SystemException(final String key, final Throwable cause) {
        this(key, cause.getMessage(), cause);
    }

    public SystemException(final String key) {
        this(key, (String) null);
    }

    public SystemException(final String key, final String internalMessage) {
        this(key, internalMessage, null);
    }

    /**
     * @param key
     *            A key for looking up the message in
     *            ExceptionResources.properties, or null to specify a generic
     *            message.
     * @param internalMessage
     *            A message which is just intended for developers; the user will
     *            not see this message but instead the message corresponding to
     *            key. Because the message is only for developers, it is not
     *            translated into different languages.
     */
    public SystemException(final String key, final String internalMessage, final Throwable cause) {
        this(key, internalMessage, cause, null);
    }

    public SystemException(final String key, final Throwable cause, final Object[] values) {
        this(key, null, cause, values);
    }

    @SuppressWarnings({"PMD.ArrayIsStoredDirectly", "PMD.NullAssignment"})
    // Rationale: It is not stored directly, it's clone():d.
    public SystemException(final String key, final String internalMessage, final Throwable cause, final Object[] values) {
        super(internalMessage, cause);
        this.key = key;

        // we clone to make sure that the Object[] isn't modified outside this method.
        if (values == null) {
            this.values = null;
        } else {
            this.values = values.clone();
        }
    }

    /**
     * Returns the key which maps to an entry in ExceptionResources file. The
     * message corresponding to this key is used for logging purposes as well as
     * for displaying message to the user
     */
    public String getKey() {
        return key;
    }

    /**
     * This is an array of object which might be needed to pass certain
     * parameters to the string in the resource bundle.
     */
    public Object[] getValues() {
        return values.clone();
    }
}
