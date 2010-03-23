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

package org.mifos.accounts.fund.struts.action;

import java.util.List;

import junit.framework.Assert;

import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.fund.persistence.FundPersistence;
import org.mifos.accounts.fund.util.helpers.FundConstants;
import org.mifos.application.master.business.FundCodeEntity;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.UserContext;

public class FundActionStrutsTest extends MifosMockStrutsTestCase {
    public FundActionStrutsTest() throws Exception {
        super();
    }

    private String flowKey;
    private FundBO fund;
    private FundBO fundBO;
    private UserContext userContext = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        ActivityContext ac = TestObjectFactory.getActivityContext();
        request.getSession(false).setAttribute("ActivityContext", ac);
        flowKey = createFlow(request, FundAction.class);
    }

    private void reloadMembers() {
        if (fund != null) {
            fund = (FundBO) StaticHibernateUtil.getSessionTL().get(FundBO.class, fund.getFundId());
        }
        if (fundBO != null) {
            fundBO = (FundBO) StaticHibernateUtil.getSessionTL().get(FundBO.class, fundBO.getFundId());
        }

    }

    @Override
    protected void tearDown() throws Exception {
        reloadMembers();
        TestObjectFactory.removeObject(fund);
        TestObjectFactory.removeObject(fundBO);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testLoad() throws Exception {
        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", Methods.load.toString());
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.load_success.toString());
        List<FundCodeEntity> fundCodeList = (List<FundCodeEntity>) SessionUtils.getAttribute(
                FundConstants.ALL_FUNDLIST, request);
       Assert.assertEquals("The size of master data for funds should be 5", 5, fundCodeList.size());
    }

    public void testPreviewWithOutData() throws Exception {
        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", Methods.preview.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyActionErrors(new String[] { FundConstants.ERROR_MANDATORY, FundConstants.ERROR_SELECT });
        verifyInputForward();
    }

    public void testPreview() throws Exception {
        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", Methods.preview.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("fundName", "Fund-1");
        addRequestParameter("fundCode", "1");
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.preview_success.toString());
    }

    public void testPreviewForPageExpiration() throws Exception {
        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", Methods.preview.toString());
        addRequestParameter("fundName", "Fund-1");
        addRequestParameter("fundCode", "1");
        actionPerform();
        verifyForwardPath("/pages/framework/jsp/pageexpirederror.jsp");
    }

    public void testPrevious() throws Exception {
        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", Methods.previous.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.previous_success.toString());
    }

    public void testCancelCreate() throws Exception {
        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", Methods.cancelCreate.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.cancelCreate_success.toString());
        Assert.assertNull(((FlowManager) request.getSession().getAttribute(Constants.FLOWMANAGER)).getFlow(flowKey));
    }

    public void testCreate() throws Exception {
        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", "load");
        actionPerform();

        flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);

        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("fundName", "Fund-2");
        addRequestParameter("fundCode", "1");
        actionPerform();
        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", "create");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.create_success.toString());
        fund = new FundPersistence().getFund("Fund-2");

        Assert.assertNull(((FlowManager) request.getSession().getAttribute(Constants.FLOWMANAGER)).getFlow(flowKey));
    }

    public void testViewAllFunds() throws Exception {
        fund = createFund("Fund-1");
        StaticHibernateUtil.closeSession();
        fund = (FundBO) TestObjectFactory.getObject(FundBO.class, fund.getFundId());
        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", Methods.viewAllFunds.toString());
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.viewAllFunds_success.toString());
        List<FundBO> fundList = (List<FundBO>) SessionUtils.getAttribute(FundConstants.FUNDLIST, request);
       Assert.assertEquals("The size of master data for funds should be 6", 6, fundList.size());
    }

    public void testManage() throws Exception {
        fund = createFund("Fund-1");
        StaticHibernateUtil.closeSession();
        fund = (FundBO) TestObjectFactory.getObject(FundBO.class, fund.getFundId());
        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", Methods.manage.toString());
        addRequestParameter("fundCodeId", fund.getFundId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.manage_success.toString());
        StaticHibernateUtil.closeSession();
        fund = (FundBO) TestObjectFactory.getObject(FundBO.class, fund.getFundId());
        Assert.assertNotNull(fund);
       Assert.assertEquals("Fund-1", fund.getFundName());
       Assert.assertEquals("Fund-1", SessionUtils.getAttribute(FundConstants.OLDFUNDNAME, request));
    }

    public void testPreviewManageWithNullFundName() throws Exception {
        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", Methods.previewManage.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyActionErrors(new String[] { FundConstants.ERROR_MANDATORY });
        verifyInputForward();
    }

    public void testPreviewManage() throws Exception {
        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", Methods.previewManage.toString());
        addRequestParameter("fundName", "Fund");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.previewManage_success.toString());
    }

    public void testPreviousManage() throws Exception {
        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", Methods.previousManage.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.previousManage_success.toString());
    }

    public void testCancelManage() throws Exception {
        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", Methods.cancelManage.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.cancelManage_success.toString());
        Assert.assertNull(((FlowManager) request.getSession().getAttribute(Constants.FLOWMANAGER)).getFlow(flowKey));
    }

    public void testUpdateForNullFundName() throws Exception {
        fund = createFund("Fund-1");
        StaticHibernateUtil.closeSession();
        fund = (FundBO) TestObjectFactory.getObject(FundBO.class, fund.getFundId());
        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", Methods.manage.toString());
        addRequestParameter("fundCodeId", fund.getFundId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();

        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", Methods.previewManage.toString());
        addRequestParameter("fundName", "Fund-2");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();

        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", Methods.update.toString());
        addRequestParameter("fundName", "");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyActionErrors(new String[] { FundConstants.INVALID_FUND_NAME });
        verifyForward(ActionForwards.update_failure.toString());
    }

    public void testUpdateForDuplicateFundName() throws Exception {
        fund = createFund("Fund-1");
        fundBO = createFund("Fund-2");
        StaticHibernateUtil.closeSession();
        fund = (FundBO) TestObjectFactory.getObject(FundBO.class, fund.getFundId());
        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", Methods.manage.toString());
        addRequestParameter("fundCodeId", fund.getFundId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();

        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", Methods.previewManage.toString());
        addRequestParameter("fundName", "Fund-1");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();

        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", Methods.update.toString());
        addRequestParameter("fundName", fundBO.getFundName());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyActionErrors(new String[] { FundConstants.DUPLICATE_FUNDNAME_EXCEPTION });
        verifyForward(ActionForwards.update_failure.toString());
    }

    public void testUpdate() throws Exception {
        fund = createFund("Fund-1");
        StaticHibernateUtil.closeSession();
        fund = (FundBO) TestObjectFactory.getObject(FundBO.class, fund.getFundId());
        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", Methods.manage.toString());
        addRequestParameter("fundCodeId", fund.getFundId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();

        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", Methods.previewManage.toString());
        addRequestParameter("fundName", "Fund-2");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();

        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", Methods.update.toString());
        addRequestParameter("fundName", "Fund-2");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.update_success.toString());
        Assert.assertNull(((FlowManager) request.getSession().getAttribute(Constants.FLOWMANAGER)).getFlow(flowKey));
        StaticHibernateUtil.closeSession();
        fund = (FundBO) TestObjectFactory.getObject(FundBO.class, fund.getFundId());
        Assert.assertNotNull(fund);
       Assert.assertEquals("Fund-2", fund.getFundName());
    }

    public void testValidateForPreview() throws Exception {
        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", "validate");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        request.setAttribute(FundConstants.METHODCALLED, Methods.preview.toString());
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.preview_failure.toString());
    }

    public void testValidateForPreviewManage() throws Exception {
        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", "validate");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        request.setAttribute(FundConstants.METHODCALLED, Methods.previewManage.toString());
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.previewManage_failure.toString());
    }

    public void testVaildateForCreate() throws Exception {
        setRequestPathInfo("/fundAction.do");
        addRequestParameter("method", "validate");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        request.setAttribute(FundConstants.METHODCALLED, Methods.create.toString());
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.create_failure.toString());
    }

    private FundBO createFund(String fundName) throws Exception {
        FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class,
                (short) 1);
        return TestObjectFactory.createFund(fundCodeEntity, fundName);
    }
}
