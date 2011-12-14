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
package org.mifos.platform.rest.controller.stub;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StubRESTController {

    @RequestMapping(value = "read", method = RequestMethod.GET)
    public @ResponseBody String readCall(@PathVariable String arg) throws Exception {
        return dummyCall(arg);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public @ResponseBody String updateCall(@PathVariable String arg) throws Exception {
        return dummyCall(arg);
    }

    @RequestMapping(value = "fail", method = RequestMethod.POST)
    public @ResponseBody String failCall(@PathVariable String arg) throws Exception {
        throw new IllegalArgumentException();
    }

    @RequestMapping(value = "create", method = RequestMethod.PUT)
    public @ResponseBody String createCall(@PathVariable String arg) throws Exception {
        return dummyCall(arg);
    }

    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    public @ResponseBody String deleteCall(@PathVariable String arg) throws Exception {
        return dummyCall(arg);
    }

    private String dummyCall(String arg) {
        return arg;
    }
}
