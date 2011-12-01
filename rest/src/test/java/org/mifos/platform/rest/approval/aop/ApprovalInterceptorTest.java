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
package org.mifos.platform.rest.approval.aop;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.rest.controller.stub.StubRESTController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("testRESTContext.xml")
public class ApprovalInterceptorTest {

    @Autowired
    StubRESTController stubRestController;

    @Autowired
    StubMethodInvocationHandler invocationHandler;

    @Test
    public void testIntercept() throws Exception {

        Assert.assertEquals(0, invocationHandler.getNoOfExecution());
        Assert.assertEquals(StubMethodInvocationHandler.NOT_EXECUTED, invocationHandler.getExecuted());

        String result = stubRestController.createCall("Hello");
        stubRestController.readCall("Hello");
        stubRestController.updateCall("Hello");


        Assert.assertEquals(1, invocationHandler.getNoOfExecution());
        Assert.assertEquals(StubMethodInvocationHandler.EXECUTED, invocationHandler.getExecuted());
        Assert.assertEquals(StubMethodInvocationHandler.HI, result);


    }


}
