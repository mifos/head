package org.mifos.platform.rest.approval.service;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.builders.MifosUserBuilder;
import org.mifos.platform.rest.approval.domain.ApprovalMethod;
import org.mifos.platform.rest.controller.stub.StubRESTController;
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
@ContextConfiguration("/dbIntegrationTest.xml")
@TransactionConfiguration(defaultRollback=true)
public class ApprovalServiceTest {

    @Autowired
    private ApprovalService approvalService;

    @BeforeClass
    public static void init() {
        SecurityContext securityContext = new SecurityContextImpl();
        MifosUser principal = new MifosUserBuilder().nonLoanOfficer().withAdminRole().build();
        Authentication authentication = new TestingAuthenticationToken(principal, principal);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testCreate() throws Exception {
        Method m = StubRESTController.class.getMethods()[0];
        ApprovalMethod am = new ApprovalMethod(m.getName(), StubRESTController.class, m.getParameterTypes(), new Object[1]);
        Long id = create(am);
        Assert.assertNotNull(approvalService.getDetails(id));
    }

    @Test
    public void testApprove() {
        fail("Not yet implemented");
    }

    @Test
    public void testReject() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetWaitingForApproval() {
        fail("Not yet implemented");
    }


    private Long create(ApprovalMethod am) throws Exception {
        try {
            approvalService.create(am);
            fail("should have thrown interrupt exception");
        } catch (RESTCallInterruptException e) {}
        return approvalService.getWaitingForApproval().get(0).getId();
    }
}
