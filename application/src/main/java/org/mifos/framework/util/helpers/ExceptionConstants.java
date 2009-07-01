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

public interface ExceptionConstants {
    public static String CFGFILENOTFOUND = "exceptions.hibernate.systemexception.cfgfilenotfound";
    public static String HIBERNATEPROPNOTFOUND = "exceptions.hibernate.systemexception.hibernatepropnotfound";
    public static String CONFIGPATHNOTFOUND = "exceptions.hibernate.systemexception.configpathnotfound";
    public static String SYSTEMEXCEPTION = "exception.framework.SystemException";
    public static String CONCURRENCYEXCEPTION = "exception.framework.ConcurrencyException";
    public static String DOUBLESUBMITEXCEPTION = "exception.framework.DoubleSubmitException";
    public static String SERVICEEXCEPTION = "exception.framework.ServiceException";
    public static String ILLEGALMONEYOPERATION = "The desired operation on money class is not possible because the two money objects are of different currencies.";
    public static String FRAMEWORKRUNTIMEEXCEPTION = "exception.framework.FrameworkRuntimeException";
    public static String CONSTANTSNOTLOADEDEXCEPTION = "exception.framework.ConstantsNotLoadedException";
    public static String STARTUP_EXCEPTION = "exception.framework.StartUpException";
    public static String ENUMSNOTLOADEDEXCEPTION = "exception.framework.EnumsNotLoadedException";
    public static String PAGEEXPIREDEXCEPTION = "exception.framework.PageExpiredException";
    public static String INVALIDDATEEXCEPTION = "exception.validation.InvalidDate";
    public static String PERMISSIONFAILUREEXCEPTION = "exception.framework.permission";
}
