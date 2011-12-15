package org.mifos.rest.approval.service;

import static org.junit.Assert.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.builders.MifosUserBuilder;
import org.mifos.platform.rest.controller.stub.StubRESTController;
import org.mifos.rest.approval.domain.ApprovalMethod;
import org.mifos.rest.approval.domain.ApprovalState;
import org.mifos.rest.approval.domain.MethodArgHolder;
import org.mifos.rest.approval.domain.RESTApprovalEntity;
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
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-context.xml")
@TransactionConfiguration(defaultRollback=true)
@Transactional
public class ApprovalServiceTest {

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private SessionFactory sessionFactory;

    @BeforeClass
    public static void init() {
        SecurityContext securityContext = new SecurityContextImpl();
        MifosUser principal = new MifosUserBuilder().nonLoanOfficer().withAdminRole().build();
        Authentication authentication = new TestingAuthenticationToken(principal, principal);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @After
    public void tearDown() {
        // force cleanup
        Session session = sessionFactory.getCurrentSession();
        for(Object e : session.createCriteria(RESTApprovalEntity.class).list()) {
            session.delete(e);
        }
    }

    @Test
    public void testCreate() throws Exception {
        createApprovalMethod();
        RESTApprovalEntity rae = approvalService.getDetails(1L);
        assertNotNull(rae);
        assertEquals(StubRESTController.class.getSimpleName().replace("RESTController", ""), rae.getType());
        assertNotNull(rae.getCreatedOn());
        assertEquals(getCurrentUserId(), rae.getCreatedBy());
        assertNull(rae.getApprovedOn());
        assertNull(rae.getApprovedBy());
        assertEquals(ApprovalState.WAITING, rae.getState());

    }

    @Test
    public void testApproveState() throws Exception {
        createApprovalMethod();
        RESTApprovalEntity rae = approvalService.getAllWaiting().get(0);
        assertNotNull(rae);
        assertEquals(ApprovalState.WAITING, rae.getState());

        approvalService.reject(rae.getId());
        rae = approvalService.getAllRejected().get(0);
        assertEquals(ApprovalState.REJECTED, rae.getState());
        assertNotNull(rae.getApprovedOn());
        assertEquals(getCurrentUserId(), rae.getApprovedBy());

        approvalService.approve(rae.getId());
        rae = approvalService.getAllApproved().get(0);
        assertEquals(ApprovalState.APPROVED, rae.getState());
    }

    @Test
    @Ignore
    public void testApproveFailure() throws Exception {
        createFailureApprovalMethod();
        RESTApprovalEntity rae = approvalService.getAllWaiting().get(0);
        assertNotNull(rae);
        assertEquals(ApprovalState.WAITING, rae.getState());

        Object result = approvalService.approve(rae.getId());

        assertEquals("Error : check parameters", result);
        assertEquals(ApprovalState.WAITING, rae.getState());
    }

    private void createApprovalMethod() throws Exception {
        Class[] c = new Class[1];
        c[0] = String.class;
        MethodArgHolder args = new MethodArgHolder(c, new Object[1]);
        ApprovalMethod am = new ApprovalMethod("updateCall", StubRESTController.class, args);
        try {
            approvalService.create(am);
            fail("should have thrown interrupt exception");
        } catch (RESTCallInterruptException e) {}
    }

    private void createFailureApprovalMethod() throws Exception {
        Class[] c = new Class[1];
        c[0] = String.class;
        MethodArgHolder args = new MethodArgHolder(c, new Object[1]);
        ApprovalMethod am = new ApprovalMethod("failCall", StubRESTController.class, args);
        try {
            approvalService.create(am);
            fail("should have thrown interrupt exception");
        } catch (RESTCallInterruptException e) {}
    }

    private Short getCurrentUserId() {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (short) user.getUserId();
    }
}
