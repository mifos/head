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
package org.mifos.platform.rest.ui.controller;

import javax.servlet.http.HttpServletResponse;

import org.mifos.service.test.TestMode;
import org.mifos.service.test.TestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * This is a dummy rest controller which is used to make sure if the rest
 * services are available
 *
 * /mifos/status.json
 *
 */
@Controller
public class JSONAjaxController {

    @Autowired
    private TestingService testingService;

    @RequestMapping("jsonAjax.ftl")
    public ModelAndView deleteCacheDir(HttpServletResponse response) {
        ModelAndView mav;
        if (TestMode.MAIN == testingService.getTestMode()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            mav = new ModelAndView("pageNotFound");
        } else {
            mav = new ModelAndView("jsonAjax");
        }
        return mav;
    }
}