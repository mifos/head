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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This is a dummy rest controller which is used to make sure if the rest services are available
 *
 * /mifos/status.json
 *
 */
@Controller
public class StatusRESTController {

    @RequestMapping("status")
    public final @ResponseBody
    StatusJSON status() {
        StatusJSON json = new StatusJSON();
        json.setStatus("Success");
        return json;
    }

    @RequestMapping("accessDenied")
    public final @ResponseBody
    StatusJSON accessDenied() {
        StatusJSON json = new StatusJSON();
        json.setStatus("session expired");
        return json;
    }

    @RequestMapping("restLogin")
    public final String restLoginForm() {
        return "restLogin";
    }

}

class StatusJSON {

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}