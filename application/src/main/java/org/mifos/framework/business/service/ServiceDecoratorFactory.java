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

package org.mifos.framework.business.service;

import java.lang.reflect.Proxy;

import org.mifos.application.reports.business.service.IBranchCashConfirmationReportService;
import org.mifos.application.reports.business.service.IBranchReportService;
import org.mifos.application.reports.business.service.ICollectionSheetReportService;
import org.mifos.framework.components.logger.ServiceLogger;

public class ServiceDecoratorFactory {
    private static Object newInstance(Object obj, ServiceLogger logger) {
        return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(),
                new ServiceProxy(obj, logger));
    }

    public static ICollectionSheetReportService decorate(ICollectionSheetReportService service, ServiceLogger logger) {
        return (ICollectionSheetReportService) newInstance(service, logger);
    }

    public static IBranchCashConfirmationReportService decorate(IBranchCashConfirmationReportService service,
            ServiceLogger serviceLogger) {
        return (IBranchCashConfirmationReportService) newInstance(service, serviceLogger);
    }

    public static IBranchReportService decorate(IBranchReportService service, ServiceLogger serviceLogger) {
        return (IBranchReportService) newInstance(service, serviceLogger);
    }

    // this generic implementation is not working with spring container, not
    // sure why, but would love to have
    // this in place instead of the above specific method
    // public static<T> T getLoggerWrappedService(
    // T service) {
    // return (T) newInstance(service);
    // }
}
