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

package org.mifos.platform.accounting.ui.controller;

import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifos.platform.accounting.AccountingDto;
import org.mifos.platform.accounting.service.AccountingCacheFileInfo;
import org.mifos.platform.accounting.service.IAccountingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AccountingDataController {

    private IAccountingService accountingService;

    private final String FROM_DATE = "fromDate";
    private final String TO_DATE = "toDate";

    @Autowired
    public AccountingDataController(IAccountingService accountingService) {
        this.accountingService = accountingService;
    }

    @RequestMapping("renderAccountingData.ftl")
    public ModelAndView showAccountingDataFor(@RequestParam(value = FROM_DATE) String paramFromDate, @RequestParam(value = TO_DATE) String paramToDate) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        LocalDate fromDate = fmt.parseDateTime(paramFromDate).toLocalDate();
        LocalDate toDate = fmt.parseDateTime(paramToDate).toLocalDate();
        Boolean hasAlreadyRanQuery = Boolean.FALSE;

        List<AccountingDto> accountingData = null;
        try {
            hasAlreadyRanQuery = accountingService.hasAlreadyRanQuery(fromDate, toDate);
            accountingData = accountingService.getAccountingDataFor(fromDate, toDate);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ModelAndView mav = new ModelAndView("renderAccountingData");
        mav.addObject("accountingData", accountingData);
        mav.addObject("hasAlreadyRanQuery", hasAlreadyRanQuery);
        mav.addObject("fromDate", fromDate);
        mav.addObject("toDate", toDate);

        return mav;
    }

    @RequestMapping("mainAccountingScreen.ftl")
    public ModelAndView showMainScreen() {
        ModelAndView mav = new ModelAndView("mainAccountingScreen");
        return mav;
    }

    @RequestMapping("accountingDataForm.ftl")
    public ModelAndView showAccountingDataForm() {
        ModelAndView mav = new ModelAndView("accountingDataForm");
        return mav;
    }

    @RequestMapping("advanceOptions.ftl")
    public ModelAndView showAdvanceOptions() throws Exception {
        ModelAndView mav = new ModelAndView("advanceOptions");
        mav.addObject("fileName",accountingService.getTallyOutputFileName(new LocalDate(), new LocalDate()));
        return mav;
    }

    @RequestMapping("deleteCacheDir.ftl")
    public ModelAndView deleteCacheDir() {
        ModelAndView mav = new ModelAndView("deleteCacheDir");
        mav.addObject("result", accountingService.deleteCacheDir());
        return mav;
    }

    @RequestMapping("renderAccountingDataCacheInfo.ftl")
    public ModelAndView showAccountingCacheInfo() {
        List<AccountingCacheFileInfo> files = null;
        try {
            files = accountingService.getAccountingDataCacheInfo();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ModelAndView mav = new ModelAndView("renderAccountingDataCacheInfo");
        mav.addObject("files", files);
        return mav;
    }
}
