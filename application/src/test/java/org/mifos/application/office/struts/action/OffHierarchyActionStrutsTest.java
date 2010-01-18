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

package org.mifos.application.office.struts.action;

import java.util.List;

import junit.framework.Assert;

import org.mifos.application.office.business.OfficeLevelEntity;
import org.mifos.application.office.persistence.OfficeHierarchyPersistence;
import org.mifos.application.office.struts.actionforms.OffHierarchyActionForm;
import org.mifos.application.office.util.helpers.OfficeConstants;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class OffHierarchyActionStrutsTest extends MifosMockStrutsTestCase {

    private static final int OFFICE_LEVELS = 5;

    private static final String CONFIGURED = "1";

    public OffHierarchyActionStrutsTest() throws Exception {
        super();
    }

    private String flowKey;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        UserContext userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        flowKey = createFlow(request, OffHierarchyAction.class);
    }

    @Override
    protected void tearDown() throws Exception {
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testLoad() throws PageExpiredException {
        setRequestPathInfo("/offhierarchyaction.do");
        addRequestParameter("method", "load");

        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.load_success.toString());

        List<OfficeLevelEntity> officeLevels = (List<OfficeLevelEntity>) SessionUtils.getAttribute(
                OfficeConstants.OFFICE_LEVELS, request);
       Assert.assertEquals(OFFICE_LEVELS, officeLevels.size());
        for (OfficeLevelEntity officeLevelEntity : officeLevels) {
           Assert.assertTrue(officeLevelEntity.isConfigured());
        }

        OffHierarchyActionForm offHierarchyActionForm = (OffHierarchyActionForm) request.getSession().getAttribute(
                "offhierarchyactionform");
       Assert.assertEquals(CONFIGURED, offHierarchyActionForm.getHeadOffice());
       Assert.assertEquals(CONFIGURED, offHierarchyActionForm.getRegionalOffice());
       Assert.assertEquals(CONFIGURED, offHierarchyActionForm.getSubRegionalOffice());
       Assert.assertEquals(CONFIGURED, offHierarchyActionForm.getAreaOffice());
       Assert.assertEquals(CONFIGURED, offHierarchyActionForm.getBranchOffice());
    }

    public void testUpdate() throws Exception {
        setRequestPathInfo("/offhierarchyaction.do");
        addRequestParameter("method", "load");
        actionPerform();

        flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
        setRequestPathInfo("/offhierarchyaction.do");
        addRequestParameter("method", "update");
        addRequestParameter("regionalOffice", CONFIGURED);
        addRequestParameter("areaOffice", CONFIGURED);
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.update_success.toString());

        UserContext userContext = (UserContext) request.getSession().getAttribute(Constants.USERCONTEXT);
        List<OfficeLevelEntity> officeLevels = new OfficeHierarchyPersistence().getOfficeLevels(userContext
                .getLocaleId());

       Assert.assertEquals(OFFICE_LEVELS, officeLevels.size());
        for (OfficeLevelEntity officeLevelEntity : officeLevels) {
            if (officeLevelEntity.getLevel().equals(OfficeLevel.SUBREGIONALOFFICE))
                Assert.assertFalse(officeLevelEntity.isConfigured());
            else
               Assert.assertTrue(officeLevelEntity.isConfigured());
        }

        resetData();
    }

    public void testCancel() throws PageExpiredException {
        setRequestPathInfo("/offhierarchyaction.do");
        addRequestParameter("method", "cancel");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.cancel_success.toString());
    }

    public void testCancelForPageExpiration() throws PageExpiredException {
        setRequestPathInfo("/offhierarchyaction.do");
        addRequestParameter("method", "cancel");

        actionPerform();
        verifyActionErrors(new String[] { "exception.framework.PageExpiredException" });
        verifyForwardPath("/pages/framework/jsp/pageexpirederror.jsp");
    }

    public void testUpdateForPageExpiration() throws PageExpiredException {
        setRequestPathInfo("/offhierarchyaction.do");
        addRequestParameter("method", "update");

        actionPerform();
        verifyActionErrors(new String[] { "exception.framework.PageExpiredException" });
        verifyForwardPath("/pages/framework/jsp/pageexpirederror.jsp");
    }

    private void resetData() throws Exception {
        StaticHibernateUtil.getSessionTL();
        StaticHibernateUtil.startTransaction();
        OfficeLevelEntity officeLevelEntity = (OfficeLevelEntity) StaticHibernateUtil.getSessionTL().get(
                OfficeLevelEntity.class, OfficeLevel.SUBREGIONALOFFICE.getValue());
        officeLevelEntity.update(true);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

    }

}
