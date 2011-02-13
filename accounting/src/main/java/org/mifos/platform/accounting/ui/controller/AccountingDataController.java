/*
 * Copyright Grameen Foundation USA
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

package org.mifos.platform.accounting.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifos.platform.accounting.AccountingDto;
import org.mifos.platform.accounting.service.ExportFileInfo;
import org.mifos.platform.accounting.service.IAccountingService;
import org.mifos.ui.core.controller.AdminBreadcrumbBuilder;
import org.mifos.ui.core.controller.BreadCrumbsLinks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AccountingDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountingDataController.class);

    private final IAccountingService accountingService;

    private static final String FROM_DATE = "fromDate";
    private static final String TO_DATE = "toDate";
    private static final String LIST_START_DAY = "listStartDay";

    @Autowired
    public AccountingDataController(IAccountingService accountingService) {
        this.accountingService = accountingService;
    }

    @RequestMapping("renderAccountingData.ftl")
    public final ModelAndView showAccountingDataFor(@RequestParam(value = FROM_DATE) String paramFromDate,
            @RequestParam(value = TO_DATE) String paramToDate) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        LocalDate fromDate = fmt.parseDateTime(paramFromDate).toLocalDate();
        LocalDate toDate = fmt.parseDateTime(paramToDate).toLocalDate();
        Boolean hasAlreadyRanQuery = Boolean.FALSE;
        String fileName = null;

        List<AccountingDto> accountingData = new ArrayList<AccountingDto>();
        try {
            fileName = accountingService.getExportOutputFileName(fromDate, toDate).replace(".xml", "");
            hasAlreadyRanQuery = accountingService.hasAlreadyRanQuery(fromDate, toDate);
            accountingData = accountingService.getExportDetails(fromDate, toDate);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        ModelAndView mav = new ModelAndView("renderAccountingData");
        List<BreadCrumbsLinks> breadcrumbs = new AdminBreadcrumbBuilder()
                .withLink("accounting.viewaccountingexports", "renderAccountingDataCacheInfo.ftl")
                .withLink(fileName, "").build();
        mav.addObject("breadcrumbs", breadcrumbs);
        mav.addObject("accountingData", accountingData);
        mav.addObject("hasAlreadyRanQuery", hasAlreadyRanQuery);
        mav.addObject("fileName", fileName);
        mav.addObject("fromDate", fromDate);
        mav.addObject("toDate", toDate);

        return mav;
    }

    @RequestMapping("confirmExportsDelete.ftl")
    public final ModelAndView confirmExportsDelete() {
        ModelAndView mav = new ModelAndView("confirmExportsDelete");
        List<BreadCrumbsLinks> breadcrumbs = new AdminBreadcrumbBuilder().withLink("accounting.viewaccountingexports",
        "confirmExportsDelete.ftl").build();
        mav.addObject("breadcrumbs", breadcrumbs);
        return mav;
    }

    @RequestMapping("deleteCacheDir.ftl")
    public final ModelAndView deleteCacheDir() {
        ModelAndView mav = new ModelAndView("deleteCacheDir");
        mav.addObject("result", accountingService.deleteCacheDir());
        return mav;
    }

    @RequestMapping("generateExportsList.ftl")
    public final ModelAndView generateList(@RequestParam(value = LIST_START_DAY) Integer listStartDay, @RequestParam(value = "type") String type) {
        ModelAndView mav = new ModelAndView("generateExportsList");
        List<ExportFileInfo> exports = accountingService.getLastTenExports(listStartDay);
        mav.addObject("exports", exports);
        mav.addObject("size", listStartDay);
        return mav;
    }

    @RequestMapping("renderAccountingDataCacheInfo.ftl")
    public final ModelAndView listAllExports() {
        List<BreadCrumbsLinks> breadcrumbs = new AdminBreadcrumbBuilder().withLink("accounting.viewaccountingexports",
                "renderAccountingDataCacheInfo.ftl").build();
        ModelAndView mav = new ModelAndView("renderAccountingDataCacheInfo");
        mav.addObject("breadcrumbs", breadcrumbs);
        mav.addObject("numberDaysFromStartOfFinancialTransactions",accountingService.getNumberDaysFromStartOfFinancialTransactions());
        return mav;
    }

}
