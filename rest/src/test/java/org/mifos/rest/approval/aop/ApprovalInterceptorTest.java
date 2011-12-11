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
package org.mifos.rest.approval.aop;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.builders.MifosUserBuilder;
import org.mifos.framework.hibernate.helper.Transactional;
import org.mifos.platform.rest.controller.stub.StubRESTController;
import org.mifos.rest.approval.service.ApprovalService;
import org.mifos.rest.approval.service.RESTCallInterruptException;
import org.mifos.security.MifosUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-context.xml")
@TransactionConfiguration
public class ApprovalInterceptorTest {

    @Autowired
    StubRESTController stubRestController;

    @Autowired
    ApprovalService approvalService;

    @BeforeClass
    public static void init() {
        SecurityContext securityContext = new SecurityContextImpl();
        MifosUser principal = new MifosUserBuilder().nonLoanOfficer().withAdminRole().build();
        Authentication authentication = new TestingAuthenticationToken(principal, principal);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @Ignore
    @Transactional
    public void testRESTCallExecution() throws Exception {
        Assert.assertEquals(0, approvalService.getAllWaiting().size());
        try {
            stubRestController.createCall("HELLO");
            Assert.fail("should throw interrupt exception");
        } catch (RESTCallInterruptException e) {}
        Assert.assertEquals(1, approvalService.getAllWaiting().size());
    }
}
