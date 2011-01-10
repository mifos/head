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

package org.mifos.framework.util.helpers;

@SuppressWarnings("PMD")
public interface ExceptionConstants {
    String CFGFILENOTFOUND = "exceptions.hibernate.systemexception.cfgfilenotfound";
    String HIBERNATEPROPNOTFOUND = "exceptions.hibernate.systemexception.hibernatepropnotfound";
    String CONFIGPATHNOTFOUND = "exceptions.hibernate.systemexception.configpathnotfound";
    String SYSTEMEXCEPTION = "exception.framework.SystemException";
    String CONCURRENCYEXCEPTION = "exception.framework.ConcurrencyException";
    String DOUBLESUBMITEXCEPTION = "exception.framework.DoubleSubmitException";
    String SERVICEEXCEPTION = "exception.framework.ServiceException";
    String ILLEGALMONEYOPERATION = "The desired operation on money class is not possible because the two money objects are of different currencies.";
    String FRAMEWORKRUNTIMEEXCEPTION = "exception.framework.FrameworkRuntimeException";
    String CONSTANTSNOTLOADEDEXCEPTION = "exception.framework.ConstantsNotLoadedException";
    String STARTUP_EXCEPTION = "exception.framework.StartUpException";
    String ENUMSNOTLOADEDEXCEPTION = "exception.framework.EnumsNotLoadedException";
    String PAGEEXPIREDEXCEPTION = "exception.framework.PageExpiredException";
    String PERMISSIONFAILUREEXCEPTION = "exception.framework.permission";
    String CURRENCY_MUST_NOT_BE_NULL ="Currency must not be null";
    String AMMOUNT_MUST_NOT_BE_NULL = "Amount must not be null";
}
