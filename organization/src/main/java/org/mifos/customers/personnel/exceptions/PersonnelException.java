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

package org.mifos.customers.personnel.exceptions;

import org.mifos.framework.exceptions.ApplicationException;

/**
 * This is general exception for the personnel
 */
public class PersonnelException extends ApplicationException {

    public PersonnelException(String key) {
        super(key);
    }

    /**
     * Constructor for PersonnelException
     *
     * @param key
     * @param values
     */
    public PersonnelException(String key, Object[] values) {
        super(key, values);
    }

    /**
     * Constructor for PersonnelException
     *
     * @param cause
     */
    public PersonnelException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor for PersonnelException
     *
     * @param cause
     */
    public PersonnelException(String key, Throwable cause) {
        super(key, cause);
    }
    
    public PersonnelException(String key, Throwable cause, Object[] values) {
        super(key, cause, values);
    }

}
