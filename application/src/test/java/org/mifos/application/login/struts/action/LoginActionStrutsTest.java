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

package org.mifos.application.login.struts.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import junit.framework.Assert;

import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoginActionStrutsTest extends MifosMockStrutsTestCase {

    public LoginActionStrutsTest() throws Exception {
        super();
    }

    private OfficeBO office;

    private PersonnelBO personnel;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    private void reloadMembers() {
        if (personnel != null) {
            personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class,
                    personnel.getPersonnelId());
        }

    }

    @Override
    protected void tearDown() throws Exception {
        reloadMembers();
        TestObjectFactory.cleanUp(personnel);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testLoad() {
        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.load.toString());
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.load_success.toString());
        Assert.assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
    }

    public void testLoginForFirstTimeUser() throws Exception {
        loadLoginPage();
        Assert.assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
        personnel = createPersonnel();

        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.login.toString());
        addRequestParameter("userName", personnel.getUserName());
        addRequestParameter("password", "PASSWORD");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.loadChangePassword_success.toString());
        Assert.assertNull(SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession()));
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testLoginForUserNull() throws Exception {
        loadLoginPage();
        Assert.assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
        personnel = createPersonnel();

        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.login.toString());
        addRequestParameter("userName", "");
        addRequestParameter("password", "PASSWORD");
        actionPerform();
       Assert.assertEquals(1, getErrorSize());
        verifyInputForward();
        Assert.assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testLoginForPasswordNull() throws Exception {
        loadLoginPage();
        Assert.assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
        personnel = createPersonnel();

        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.login.toString());
        addRequestParameter("userName", personnel.getUserName());
        addRequestParameter("password", "");
        actionPerform();
       Assert.assertEquals(1, getErrorSize());
        verifyInputForward();
        Assert.assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testUpdatePassword() throws Exception {
        loadLoginPage();
        Assert.assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
        personnel = createPersonnel();

        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.login.toString());
        addRequestParameter("userName", personnel.getUserName());
        addRequestParameter("password", "PASSWORD");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.loadChangePassword_success.toString());
        Assert.assertNull(SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession()));
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.TEMPUSERCONTEXT, request);
        userContext.setId(PersonnelConstants.SYSTEM_USER);
        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.updatePassword.toString());
        addRequestParameter("userName", personnel.getUserName());
        addRequestParameter("oldPassword", "PASSWORD");
        addRequestParameter("newPassword", "NEW_PASSWORD");
        addRequestParameter("confirmPassword", "NEW_PASSWORD");
        addRequestParameter("input", "LoginChangePW");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.updatePassword_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession()));
        Assert.assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        StaticHibernateUtil.commitTransaction();
        personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class, personnel.getPersonnelId());
       Assert.assertTrue(personnel.isPasswordChanged());
        // add verifying change log
        List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(EntityType.PERSONNEL, personnel.getPersonnelId()
                .intValue());
       Assert.assertEquals(1, auditLogList.size());
        for (int auditLogListIndex = 0; auditLogListIndex < auditLogList.size(); auditLogListIndex++) {
            auditLogList.get(auditLogListIndex).getAuditLogRecords();
        }
       Assert.assertEquals(EntityType.PERSONNEL, auditLogList.get(0).getEntityTypeAsEnum());
       Assert.assertEquals(2, auditLogList.get(0).getAuditLogRecords().size());
        for (AuditLogRecord auditLogRecord : auditLogList.get(0).getAuditLogRecords()) {
           Assert.assertEquals(true, auditLogRecord.getFieldName().equalsIgnoreCase("lastLogin")
                    || auditLogRecord.getFieldName().equalsIgnoreCase("Password"));
        }
        TestObjectFactory.cleanUpChangeLog();
    }

    public void testUpdatePasswordWithUserNull() throws Exception {
        loadLoginPage();
        Assert.assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
        personnel = createPersonnel();

        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.login.toString());
        addRequestParameter("userName", personnel.getUserName());
        addRequestParameter("password", "PASSWORD");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.loadChangePassword_success.toString());
        Assert.assertNull(SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession()));
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.updatePassword.toString());
        addRequestParameter("userName", "");
        addRequestParameter("oldPassword", "PASSWORD");
        addRequestParameter("newPassword", "NEW_PASSWORD");
        addRequestParameter("confirmPassword", "NEW_PASSWORD");
        addRequestParameter("input", "LoginChangePW");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
       Assert.assertEquals(1, getErrorSize());
        verifyInputForward();
    }

    public void testUpdatePasswordWithOldPasswordNotMatching() throws Exception {
        loadLoginPage();
        Assert.assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
        personnel = createPersonnel();

        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.login.toString());
        addRequestParameter("userName", personnel.getUserName());
        addRequestParameter("password", "PASSWORD");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.loadChangePassword_success.toString());
        Assert.assertNull(SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession()));
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.updatePassword.toString());
        addRequestParameter("userName", personnel.getUserName());
        addRequestParameter("oldPassword", "WRONG_PASSWORD");
        addRequestParameter("newPassword", "NEW_PASSWORD");
        addRequestParameter("confirmPassword", "NEW_PASSWORD");
        addRequestParameter("input", "LoginChangePW");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyActionErrors(new String[] { LoginConstants.INVALIDOLDPASSWORD });
        verifyForward(ActionForwards.updatePassword_failure.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testUpdatePasswordWithSameOldAndNewPassword() throws Exception {
        loadLoginPage();
        Assert.assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
        personnel = createPersonnel();

        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.login.toString());
        addRequestParameter("userName", personnel.getUserName());
        addRequestParameter("password", "PASSWORD");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.loadChangePassword_success.toString());
        Assert.assertNull(SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession()));
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.updatePassword.toString());
        addRequestParameter("userName", personnel.getUserName());
        addRequestParameter("oldPassword", "oldPassword");
        addRequestParameter("newPassword", "oldPassword");
        addRequestParameter("confirmPassword", "oldPassword");
        addRequestParameter("input", "LoginChangePW");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyActionErrors(new String[] { LoginConstants.SAME_OLD_AND_NEW_PASSWORD });
        verifyInputForward();
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testLoginForRegularUser() throws Exception {
        loadLoginPage();
        Assert.assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
        personnel = createPersonnel();

        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.login.toString());
        addRequestParameter("userName", personnel.getUserName());
        addRequestParameter("password", "PASSWORD");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.loadChangePassword_success.toString());
        Assert.assertNull(SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession()));
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.TEMPUSERCONTEXT, request);
        userContext.setId(PersonnelConstants.SYSTEM_USER);
        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.updatePassword.toString());
        addRequestParameter("userName", personnel.getUserName());
        addRequestParameter("oldPassword", "PASSWORD");
        addRequestParameter("newPassword", "NEW_PASSWORD");
        addRequestParameter("confirmPassword", "NEW_PASSWORD");
        addRequestParameter("input", "LoginChangePW");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();

        StaticHibernateUtil.commitTransaction();
        personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class, personnel.getPersonnelId());

       Assert.assertTrue(personnel.isPasswordChanged());
        Assert.assertNotNull(SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession()));
        Assert.assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));

        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.login.toString());
        addRequestParameter("userName", personnel.getUserName());
        addRequestParameter("password", "NEW_PASSWORD");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.login_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession()));
        Assert.assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testLoginForInvalidUser() throws Exception {
        loadLoginPage();
        Assert.assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
        personnel = createPersonnel();
        Assert.assertFalse(personnel.isLocked());

        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.login.toString());
        addRequestParameter("userName", "WRONG_USERNAME");
        addRequestParameter("password", "PASSWORD");
        actionPerform();
        verifyActionErrors(new String[] { LoginConstants.KEYINVALIDUSER });
        verifyForward(ActionForwards.login_failure.toString());

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        personnel = TestObjectFactory.getPersonnel(personnel.getPersonnelId());
        Assert.assertFalse(personnel.isLocked());
       Assert.assertEquals(0, personnel.getNoOfTries().intValue());
    }

    public void testLoginForInvalidPassword() throws Exception {
        loadLoginPage();
        Assert.assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
        personnel = createPersonnel();

        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.login.toString());
        addRequestParameter("userName", personnel.getUserName());
        addRequestParameter("password", "WRONG_PASSWORD");
        actionPerform();
        verifyActionErrors(new String[] { LoginConstants.INVALIDOLDPASSWORD });
        verifyForward(ActionForwards.login_failure.toString());

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        personnel = TestObjectFactory.getPersonnel(personnel.getPersonnelId());
        Assert.assertFalse(personnel.isLocked());
       Assert.assertEquals(1, personnel.getNoOfTries().intValue());
    }

    public void testLoginForLockedUser() throws Exception {
        loadLoginPage();
        Assert.assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
        personnel = createPersonnel();
        lockUser();

        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.login.toString());
        addRequestParameter("userName", personnel.getUserName());
        addRequestParameter("password", "PASSWORD");
        actionPerform();
        verifyActionErrors(new String[] { LoginConstants.KEYUSERLOCKED });
        verifyForward(ActionForwards.login_failure.toString());

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        personnel = TestObjectFactory.getPersonnel(personnel.getPersonnelId());
       Assert.assertTrue(personnel.isLocked());
       Assert.assertEquals(5, personnel.getNoOfTries().intValue());
    }

    public void testLogout() {
        UserContext userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);

        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.logout.toString());
        actionPerform();

        Locale locale = userContext.getCurrentLocale();
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.LOGIN_UI_PROPERTY_FILE, locale);
        String errorMessage = resources.getString(LoginConstants.LOGOUTOUT);

        verifyActionErrors(new String[] { errorMessage });
        verifyForward(ActionForwards.logout_success.toString());
    }

    public void testCancel() throws Exception {
        loadLoginPage();
        Assert.assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
        personnel = createPersonnel();

        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.login.toString());
        addRequestParameter("userName", personnel.getUserName());
        addRequestParameter("password", "PASSWORD");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.loadChangePassword_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.cancel.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.cancel_success.toString());
        Assert.assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testLogin_batchJobNotRunning() throws Exception {
        loadLoginPage();
       Assert.assertEquals(false, MifosTask.isBatchJobRunning());
        Assert.assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
        personnel = createPersonnel();
        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.login.toString());
        addRequestParameter("userName", personnel.getUserName());
        addRequestParameter("password", "PASSWORD");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.loadChangePassword_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testLogin_batchJobRunning() throws Exception {
        loadLoginPage();
        MifosTask.batchJobStarted();
       Assert.assertEquals(true, MifosTask.isBatchJobRunning());
        Assert.assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
        personnel = createPersonnel();
        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.login.toString());
        addRequestParameter("userName", personnel.getUserName());
        addRequestParameter("password", "PASSWORD");
        actionPerform();
        verifyForward(ActionForwards.load_main_page.toString());
        MifosTask.batchJobFinished();
        Assert.assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    private PersonnelBO createPersonnel() throws Exception {
        office = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
        Name name = new Name("XYZ", null, null, null);
        List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
        customFieldView.add(new CustomFieldView(Short.valueOf("9"), "123456", CustomFieldType.NUMERIC));
        Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd");
        Date date = new Date();
        personnel = new PersonnelBO(PersonnelLevel.LOAN_OFFICER, office, Integer.valueOf("1"), Short.valueOf("1"),
                "PASSWORD", "USERNAME", "xyz@yahoo.com", null, customFieldView, name, "111111", date, Integer
                        .valueOf("1"), Integer.valueOf("1"), date, date, address, Short.valueOf("1"));
        personnel.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class, personnel.getPersonnelId());
        return personnel;
    }

    private void tryLoginWithWrongPassword() {
        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.login.toString());
        addRequestParameter("userName", personnel.getUserName());
        addRequestParameter("password", "WRONG_PASSWORD");
        actionPerform();
        verifyActionErrors(new String[] { LoginConstants.INVALIDOLDPASSWORD });
        verifyForward(ActionForwards.login_failure.toString());
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        personnel = TestObjectFactory.getPersonnel(personnel.getPersonnelId());
    }

    private void lockUser() {
        tryLoginWithWrongPassword();
        Assert.assertFalse(personnel.isLocked());
       Assert.assertEquals(1, personnel.getNoOfTries().intValue());
        tryLoginWithWrongPassword();
        Assert.assertFalse(personnel.isLocked());
       Assert.assertEquals(2, personnel.getNoOfTries().intValue());
        tryLoginWithWrongPassword();
        Assert.assertFalse(personnel.isLocked());
       Assert.assertEquals(3, personnel.getNoOfTries().intValue());
        tryLoginWithWrongPassword();
        Assert.assertFalse(personnel.isLocked());
       Assert.assertEquals(4, personnel.getNoOfTries().intValue());
        tryLoginWithWrongPassword();
       Assert.assertTrue(personnel.isLocked());
       Assert.assertEquals(5, personnel.getNoOfTries().intValue());
    }

    private void loadLoginPage() {
        setRequestPathInfo("/loginAction.do");
        addRequestParameter("method", Methods.load.toString());
        actionPerform();
    }
}
