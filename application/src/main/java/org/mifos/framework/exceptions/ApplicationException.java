/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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
 * These are the type of exception which are based out of some BusinessLogic
 * where the user needs to be notified about it and then the user will take
 * certain action based on that.
 *
 * Although much code subclasses this, that is only desirable if the subclass
 * has some behavior of its own, there is code which wants to just catch
 * exceptions from the subclass, etc. Absent such a reason, it is better just to
 * throw an ApplicationException itself.
 */
public class ApplicationException extends Exception {

    /**
     * This is a string which points to the actual message in the resource
     * bundle. So the exception message to be shown to the user would be taken
     * from the resource bundle and hence could be localized.
     */
    protected final String key;

    /**
     * This is an array of object which might be needed to pass certain
     * parameters to the string in the resource bundle.
     */
    protected final Object[] values;

    public ApplicationException(Throwable cause) {
        this(null, cause, null);
    }

    public ApplicationException(String key) {
        this(key, (Object[]) null);
    }

    /**
     * @param internalMessage
     *            A message which is intended to be informative during
     *            development. It is not internationalized, and the key should
     *            still point to something appropriate for the end user (perhaps
     *            a generic "internal error" if that is appropriate).
     */
    public ApplicationException(String key, Object[] values, String internalMessage) {
        super(internalMessage);
        this.key = key;
        this.values = values;
    }

    public ApplicationException(String key, Object[] values) {
        // Putting the key in the message is to make debugging easier.
        this(key, values, key);
    }

    public ApplicationException(String key, Throwable cause) {
        this(key, cause, null);
    }

    public ApplicationException(String key, Throwable cause, Object[] values) {
        super(cause);
        this.key = key;
        this.values = values;
    }

    /**
     * Returns the key which maps to an entry in ExceptionResources file. The
     * message corresponding to this key is used for logging purposes as well as
     * for displaying message to the user
     */
    public String getKey() {
        if (null == key) {
            return "exception.framework.ApplicationException";
        } else {
            return this.key;
        }
    }

    public Object[] getValues() {
        return values;
    }

}
