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

package org.mifos.customers.office.struts.action;

import java.util.List;

import junit.framework.Assert;

import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficeView;
import org.mifos.customers.office.struts.actionforms.OffActionForm;
import org.mifos.customers.office.util.helpers.OfficeConstants;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.office.util.helpers.OperationMode;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.UserContext;

public class OfficeActionStrutsTest extends MifosMockStrutsTestCase {

    public OfficeActionStrutsTest() throws Exception {
        super();
    }

    private UserContext userContext;

    private String flowKey;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        flowKey = createFlow(request, OffAction.class);
    }

    @Override
    protected void tearDown() throws Exception {
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testGetAllOffices() throws Exception {
        setRequestPathInfo("/offAction.do");
        addRequestParameter("method", "getAllOffices");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.search_success.toString());
       Assert.assertEquals(1, ((List<OfficeBO>) SessionUtils.getAttribute(OfficeConstants.GET_HEADOFFICE, request)).size());
        Assert.assertNull(SessionUtils.getAttribute(OfficeConstants.GET_REGIONALOFFICE, request));
        Assert.assertNull(SessionUtils.getAttribute(OfficeConstants.GET_SUBREGIONALOFFICE, request));
       Assert.assertEquals(1, ((List) SessionUtils.getAttribute(OfficeConstants.GET_BRANCHOFFICE, request)).size());
       Assert.assertEquals(1, ((List) SessionUtils.getAttribute(OfficeConstants.GET_AREAOFFICE, request)).size());
       Assert.assertEquals(4, ((List) SessionUtils.getAttribute(OfficeConstants.OFFICELEVELLIST, request)).size());
    }

    public void testLoad() throws Exception {
        setRequestPathInfo("/offAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("method", Methods.load.toString());
        addRequestParameter("officeLevel", "5");
        actionPerform();
        verifyForward(ActionForwards.load_success.toString());
        List<OfficeView> parents = (List<OfficeView>) SessionUtils.getAttribute(OfficeConstants.PARENTS, request);
       Assert.assertEquals(2, parents.size());
        List<OfficeView> levels = (List<OfficeView>) SessionUtils
                .getAttribute(OfficeConstants.OFFICELEVELLIST, request);
       Assert.assertEquals(4, levels.size());
    }

    public void testLoadLevel() throws Exception {
        setRequestPathInfo("/offAction.do");
        addRequestParameter("method", Methods.load.toString());
        addRequestParameter("officeLevel", "5");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.load_success.toString());
        List<OfficeView> parents = (List<OfficeView>) SessionUtils.getAttribute(OfficeConstants.PARENTS, request);
       Assert.assertEquals(2, parents.size());
        List<OfficeView> levels = (List<OfficeView>) SessionUtils
                .getAttribute(OfficeConstants.OFFICELEVELLIST, request);
       Assert.assertEquals(4, levels.size());
        OffActionForm offActionForm = (OffActionForm) request.getSession().getAttribute("offActionForm");
        Assert.assertNotNull(offActionForm);
       Assert.assertEquals("5", offActionForm.getOfficeLevel());
    }

    public void testLoadParent() throws Exception {
        setRequestPathInfo("/offAction.do");
        addRequestParameter("method", Methods.loadParent.toString());
        addRequestParameter("officeLevel", "2");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.load_success.toString());
        List<OfficeView> parents = (List<OfficeView>) SessionUtils.getAttribute(OfficeConstants.PARENTS, request);
       Assert.assertEquals(1, parents.size());
    }

    public void testPreview() {
        setRequestPathInfo("/offAction.do");
        addRequestParameter("method", Methods.preview.toString());
        addRequestParameter("officeName", "abcd");
        addRequestParameter("shortName", "abcd");
        addRequestParameter("officeLevel", "5");
        addRequestParameter("parentOfficeId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.preview_success.toString());
    }

    public void testPreview_failure() {
        setRequestPathInfo("/offAction.do");
        addRequestParameter("method", Methods.preview.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
       Assert.assertEquals("Office Name", 1, getErrorSize(OfficeConstants.OFFICE_NAME));
       Assert.assertEquals("Office Short  Name", 1, getErrorSize(OfficeConstants.OFFICESHORTNAME));
       Assert.assertEquals("Office level", 1, getErrorSize(OfficeConstants.OFFICETYPE));
       Assert.assertEquals("Office parent", 1, getErrorSize(OfficeConstants.PARENTOFFICE));
        verifyInputForward();
    }

    public void testPrevious() {
        setRequestPathInfo("/offAction.do");
        addRequestParameter("method", Methods.previous.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.previous_success.toString());
    }

    public void testCreate() throws Exception {
        setRequestPathInfo("/offAction.do");
        addRequestParameter("method", Methods.create.toString());
        addRequestParameter("officeName", "abcd");
        addRequestParameter("shortName", "abcd");
        addRequestParameter("officeLevel", "5");
        addRequestParameter("parentOfficeId", "1");
        addRequestParameter("address.line1", "123");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.create_success.toString());
        OffActionForm offActionForm = (OffActionForm) request.getSession().getAttribute("offActionForm");
       Assert.assertEquals("abcd", offActionForm.getOfficeName());
       Assert.assertEquals("abcd", offActionForm.getShortName());
       Assert.assertEquals("123", offActionForm.getAddress().getLine1());
        OfficeBO officeBO = TestObjectFactory.getOffice(Short.valueOf(offActionForm.getOfficeId()));
        TestObjectFactory.cleanUp(officeBO);
    }

    public void testGet() throws Exception {
        setRequestPathInfo("/offAction.do");
        addRequestParameter("method", Methods.get.toString());
        addRequestParameter("officeId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        OfficeBO office = (OfficeBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        Assert.assertNotNull(office);
       Assert.assertEquals(1, office.getOfficeId().intValue());

    }

    public void testEdit() throws Exception {
        setRequestPathInfo("/offAction.do");
        addRequestParameter("method", Methods.edit.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        OfficeBO officeBO = createLoadOffice();
        actionPerform();
        verifyForward(ActionForwards.edit_success.toString());

        TestObjectFactory.cleanUp(officeBO);

    }

    public void testEditPreview() {
        setRequestPathInfo("/offAction.do");
        addRequestParameter("method", Methods.editpreview.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyInputForward();
    }

    public void testEditPrevious() {
        setRequestPathInfo("/offAction.do");
        addRequestParameter("method", Methods.editprevious.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.editprevious_success.toString());
    }

    public void testUpdate() throws Exception {
        setRequestPathInfo("/offAction.do");
        addRequestParameter("method", Methods.update.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);

        OfficeBO officeBO = createLoadOffice();
        addRequestParameter("officeName", "RAJOFFICE");
        addRequestParameter("shortName", "OFFI");
        addRequestParameter("officeLevel", officeBO.getOfficeLevel().getValue().toString());
        addRequestParameter("parentOfficeId", officeBO.getParentOffice().getOfficeId().toString());
        addRequestParameter("officeStatus", officeBO.getOfficeStatus().getValue().toString());
        actionPerform();
        verifyForward(ActionForwards.update_success.toString());
        TestObjectFactory.flushandCloseSession();
        officeBO = TestObjectFactory.getOffice(officeBO.getOfficeId());
       Assert.assertEquals("RAJOFFICE", officeBO.getOfficeName());
       Assert.assertEquals("OFFI", officeBO.getShortName());
        TestObjectFactory.cleanUp(officeBO);
    }

    public void testEditPreviewHO() throws Exception {

        addActionMethod(Methods.editpreview.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        OfficeBO headoffice = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
        addRequestParameter("officeName", "RAJOFFICE");
        addRequestParameter("shortName", headoffice.getShortName());
        addRequestParameter("officeLevel", headoffice.getOfficeLevel().getValue().toString());
        addRequestParameter("parentOfficeId", "");
        addRequestParameter("officeStatus", headoffice.getOfficeStatus().getValue().toString());
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.editpreview_success.toString());

    }

    private void addActionMethod(String method) {
        setRequestPathInfo("/offAction.do");
        addRequestParameter("method", method);
    }

    private OfficeBO createLoadOffice() throws Exception {
        OfficeBO parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
        OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE, parent, null, "abcd", "abcd", null,
                OperationMode.REMOTE_SERVER);
        officeBO.save();
        TestObjectFactory.flushandCloseSession();
        officeBO = TestObjectFactory.getOffice(officeBO.getOfficeId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, officeBO, request);
        return officeBO;
    }

    public void testFlowSuccess() throws Exception {
        setRequestPathInfo("/offAction.do");
        addRequestParameter("method", Methods.load.toString());
        addRequestParameter("officeLevel", "5");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.load_success.toString());
        flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
        FlowManager fm = (FlowManager) SessionUtils.getAttribute(Constants.FLOWMANAGER, request.getSession());
       Assert.assertEquals(true, fm.isFlowValid(flowKey));

        setRequestPathInfo("/offAction.do");
        addRequestParameter("method", Methods.preview.toString());
        addRequestParameter("officeName", "abcd");
        addRequestParameter("shortName", "abcd");
        addRequestParameter("officeLevel", "5");
        addRequestParameter("parentOfficeId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyInputForward();
        fm = (FlowManager) SessionUtils.getAttribute(Constants.FLOWMANAGER, request.getSession());
       Assert.assertEquals(true, fm.isFlowValid(flowKey));

        setRequestPathInfo("/offAction.do");
        addRequestParameter("method", Methods.create.toString());
        addRequestParameter("officeName", "abcd");
        addRequestParameter("shortName", "abcd");
        addRequestParameter("officeLevel", "5");
        addRequestParameter("parentOfficeId", "1");
        addRequestParameter("address.line1", "123");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.create_success.toString());
        OffActionForm offActionForm = (OffActionForm) request.getSession().getAttribute("offActionForm");
        fm = (FlowManager) SessionUtils.getAttribute(Constants.FLOWMANAGER, request.getSession());
       Assert.assertEquals(false, fm.isFlowValid(flowKey));

        OfficeBO officeBO = TestObjectFactory.getOffice(Short.valueOf(offActionForm.getOfficeId()));
        TestObjectFactory.cleanUp(officeBO);
    }

    public void testFlowFailure() throws Exception {
        setRequestPathInfo("/offAction.do");
        addRequestParameter("method", Methods.load.toString());
        addRequestParameter("officeLevel", "5");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.load_success.toString());
        flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
        FlowManager fm = (FlowManager) SessionUtils.getAttribute(Constants.FLOWMANAGER, request.getSession());
       Assert.assertEquals(true, fm.isFlowValid(flowKey));

        setRequestPathInfo("/offAction.do");
        addRequestParameter("method", Methods.preview.toString());
        addRequestParameter("officeName", "abcd");
        addRequestParameter("shortName", "abcd");
        addRequestParameter("officeLevel", "5");
        addRequestParameter("parentOfficeId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyInputForward();
        fm = (FlowManager) SessionUtils.getAttribute(Constants.FLOWMANAGER, request.getSession());
       Assert.assertEquals(true, fm.isFlowValid(flowKey));

        setRequestPathInfo("/offAction.do");
        addRequestParameter("method", Methods.create.toString());
        addRequestParameter("officeName", "abcd");
        addRequestParameter("shortName", "abcd");
        addRequestParameter("officeLevel", "5");
        addRequestParameter("parentOfficeId", "1");
        addRequestParameter("address.line1", "123");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);

        actionPerform();
        verifyForward(ActionForwards.create_success.toString());
        OffActionForm offActionForm = (OffActionForm) request.getSession().getAttribute("offActionForm");
        fm = (FlowManager) SessionUtils.getAttribute(Constants.FLOWMANAGER, request.getSession());
       Assert.assertEquals(false, fm.isFlowValid(flowKey));

        setRequestPathInfo("/offAction.do");
        addRequestParameter("method", Methods.create.toString());
        addRequestParameter("officeName", "abcd");
        addRequestParameter("shortName", "abcd");
        addRequestParameter("officeLevel", "5");
        addRequestParameter("parentOfficeId", "1");
        addRequestParameter("address.line1", "123");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyActionErrors(new String[] { "exception.framework.PageExpiredException" });
        verifyForwardPath("/pages/framework/jsp/pageexpirederror.jsp");
        OfficeBO officeBO = TestObjectFactory.getOffice(Short.valueOf(offActionForm.getOfficeId()));
        TestObjectFactory.cleanUp(officeBO);
    }
}
