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

package org.mifos.test.acceptance.personnel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.ViewSystemUsersPage;
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.login.ChangePasswordPage;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.office.ChooseOfficePage;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupParameters;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionParameters;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionnairePage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.QuestionGroupTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.UserHelper;
import org.mifos.test.acceptance.framework.user.CreateUserConfirmationPage;
import org.mifos.test.acceptance.framework.user.CreateUserEnterDataPage;
import org.mifos.test.acceptance.framework.user.CreateUserParameters;
import org.mifos.test.acceptance.framework.user.CreateUserPreviewDataPage;
import org.mifos.test.acceptance.framework.user.EditUserDataPage;
import org.mifos.test.acceptance.framework.user.EditUserPreviewDataPage;
import org.mifos.test.acceptance.framework.user.UserViewDetailsPage;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"personnel", "acceptance", "ui", "no_db_unit"})
public class PersonnelTest extends UiTestCaseBase {

    private NavigationHelper navigationHelper;
    private UserHelper userHelper;
    private QuestionGroupTestHelper questionGroupTestHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        navigationHelper = new NavigationHelper(selenium);
        userHelper = new UserHelper(selenium);
        questionGroupTestHelper = new QuestionGroupTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createUserTest() throws Exception {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        CreateUserParameters formParameters = adminPage.getAdminUserParameters();
        //When
        userHelper.createUser(formParameters, "MyOfficeDHMFT");
        LoginPage loginPage = new AppLauncher(selenium).launchMifos();
        loginPage.verifyPage();
        //Then
        HomePage homePage = loginPage.loginSuccessfulAsWithChnagePasw(formParameters.getUserName(), formParameters.getPassword());
        homePage.verifyPage();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled = true, groups = {"acceptance"})
    public void editUserTest() throws Exception {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();

        UserViewDetailsPage userDetailsPage = userHelper.createUser(adminPage.getAdminUserParameters(), "MyOfficeDHMFT");

        EditUserDataPage editUserPage = userDetailsPage.navigateToEditUserDataPage();

        CreateUserParameters formParameters = new CreateUserParameters();
        formParameters.setFirstName("Update");
        formParameters.setLastName("User" + StringUtil.getRandomString(8));
        formParameters.setEmail("xxx.yyy@xxx.zzz");

        EditUserPreviewDataPage editPreviewDataPage = editUserPage.submitAndGotoEditUserPreviewDataPage(formParameters);
        UserViewDetailsPage userDetailsPage2 = editPreviewDataPage.submit();
        userDetailsPage2.verifyModifiedNameAndEmail(formParameters);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled = true, groups = {"acceptance"})
    public void findUserTest() throws Exception {
    	CreateUserParameters formParameters = new CreateUserParameters();
    	AdminPage adminPage = navigationHelper.navigateToAdminPage();
        formParameters = adminPage.getAdminUserParameters();
        formParameters.setSecondLastName("SecondLastName" + StringUtil.getRandomString(8));
        //When
        userHelper.createUser(formParameters, "MyOfficeDHMFT");
        //Then
        ViewSystemUsersPage findUserPage = navigationHelper.navigateToFindUserPage();
        String secondLastName = formParameters.getSecondLastName();
        findUserPage.searchAndNavigateToUserViewDetailsPage(secondLastName); 
    }
    
    //http://mifosforge.jira.com/browse/MIFOSTEST-298
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled = true, groups = {"acceptance"})
    public void createUserWithNonAdminRoleTest() throws Exception {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        CreateUserParameters formParameters = adminPage.getNonAdminUserParameters();
        //When
        userHelper.createUser(formParameters, "MyOfficeDHMFT");
        LoginPage loginPage = new AppLauncher(selenium).launchMifos();
        loginPage.verifyPage();
        //Then
        HomePage homePage = loginPage.loginSuccessfulAsWithChnagePasw(formParameters.getUserName(), formParameters.getPassword());
        homePage.verifyPage();
        adminPage = navigationHelper.navigateToAdminPageAsLogedUser(formParameters.getUserName(), "newPasw");
        adminPage.navigateToCreateUserPage();
        String error = selenium.getText("admin.error.message");
        Assert.assertEquals(error.contains("You do not have permissions to perform this activity. Contact your system administrator to grant you the required permissions and try again."), true);
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-670
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createUserWithQuestionGroup()  throws Exception {
        //Given
        createQuestions();
        //When
        Map<String, List<String>> sectionQuestions = new HashMap<String, List<String>>();

        List<String> questions = new ArrayList<String>();

        questions.add("user question 1");

        sectionQuestions.put("Sec 1", questions);

        questions = new ArrayList<String>();
        questions.add("user question 2");
        questions.add("user question 3");

        sectionQuestions.put("Sec 2", questions);

        CreateQuestionGroupParameters createQuestionGroupParameters = new CreateQuestionGroupParameters();
        createQuestionGroupParameters.setAnswerEditable(true);
        createQuestionGroupParameters.setAppliesTo("Create Personnel");
        createQuestionGroupParameters.setTitle("Create Personnel QG1");
        createQuestionGroupParameters.setExistingQuestions(sectionQuestions);

        questionGroupTestHelper.createQuestionGroup(createQuestionGroupParameters);

        sectionQuestions = new HashMap<String, List<String>>();
        questions = new ArrayList<String>();
        questions.add("user question 4");
        sectionQuestions.put("Sec 3", questions);

        createQuestionGroupParameters = new CreateQuestionGroupParameters();
        createQuestionGroupParameters.setAnswerEditable(true);
        createQuestionGroupParameters.setAppliesTo("Create Personnel");
        createQuestionGroupParameters.setTitle("Create Personnel QG2");
        createQuestionGroupParameters.setExistingQuestions(sectionQuestions);

        questionGroupTestHelper.createQuestionGroup(createQuestionGroupParameters);

        AdminPage adminPage = navigationHelper.navigateToAdminPage();

        CreateUserParameters userParameters = adminPage.getAdminUserParameters();

        ChooseOfficePage createUserPage = adminPage.navigateToCreateUserPage();
        createUserPage.verifyPage();

        CreateUserEnterDataPage userEnterDataPage = createUserPage.selectOffice("MyOfficeDHMFT");

        QuestionResponsePage questionResponsePage = userEnterDataPage.submitAndNavigateToQuestionResponsePage(userParameters);
        questionResponsePage.verifyPage();

        QuestionResponseParameters responseParameters = new QuestionResponseParameters();
        responseParameters.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[0].value", "yes");
        responseParameters.addTextAnswer("questionGroups[0].sectionDetails[1].questions[1].value", "text1");

        questionResponsePage.populateAnswers(responseParameters);

        CreateUserPreviewDataPage createUserPreviewDataPage = questionResponsePage.continueAndNavigateToCreateUserPreviewPage();

        questionResponsePage = createUserPreviewDataPage.navigateToEditAdditionalInformation();

        questionResponsePage.populateTextAnswer("questionGroups[0].sectionDetails[1].questions[1].value", "text2");

        createUserPreviewDataPage = questionResponsePage.continueAndNavigateToCreateUserPreviewPage();

        CreateUserConfirmationPage userConfirmationPage = createUserPreviewDataPage.submit();

        QuestionnairePage questionnairePage = userConfirmationPage.navigateToUserViewDetailsPage().navigateToQuestionnairePage();
        //Then
        questionnairePage.verifyRadioGroup("details[0].sectionDetails[0].questions[0].value", "yes", true);
        questionnairePage.verifyRadioGroup("details[0].sectionDetails[1].questions[0].value", "good", false);
        questionnairePage.verifyField("details[0].sectionDetails[1].questions[1].value", "text2");
        //When
        questionnairePage.typeText("details[0].sectionDetails[1].questions[1].value", "text3");

        questionnairePage.submitAndNavigateToPersonnalDetailsPage();

        List<String> questionToAdd= new ArrayList<String>();
        questionToAdd.add("user question 5");
        questionToAdd.add("user question 6");

        List<String> questionToDesactivate = new ArrayList<String>();
        questionToDesactivate.add("user question 1");
        questionToDesactivate.add("user question 2");
        questionToDesactivate.add("user question 3");

        createQuestionGroupParameters = new CreateQuestionGroupParameters();
        for (String question : questionToAdd) {
            createQuestionGroupParameters.addExistingQuestion("Sec 1", question);
        }
        questionGroupTestHelper.addQuestionsToQuestionGroup("Create Personnel QG1", createQuestionGroupParameters.getExistingQuestions());

        for (String question : questionToDesactivate) {
            questionGroupTestHelper.markQuestionAsInactive(question);
        }
        questionGroupTestHelper.markQuestionGroupAsInactive("Create Personnel QG2");

        adminPage = navigationHelper.navigateToAdminPage();

        userParameters = adminPage.getAdminUserParameters();

        createUserPage = adminPage.navigateToCreateUserPage();
        createUserPage.verifyPage();

        userEnterDataPage = createUserPage.selectOffice("MyOfficeDHMFT");

        questionResponsePage = userEnterDataPage.submitAndNavigateToQuestionResponsePage(userParameters);
        questionResponsePage.verifyPage();
        //Then
        questionResponsePage.verifyQuestionsDoesnotappear(questionToDesactivate.toArray(new String[questionToDesactivate.size()]));
        questionResponsePage.verifyQuestionsExists(questionToAdd.toArray(new String[questionToAdd.size()]));
        questionResponsePage.verifySectionDoesnotappear("Sec 2");
        questionGroupTestHelper.markQuestionGroupAsInactive("Create Personnel QG1");
    }

    private void createQuestions() {
        List<CreateQuestionParameters> questions = new ArrayList<CreateQuestionParameters>();
        CreateQuestionParameters q1 = new CreateQuestionParameters();
        q1.setType(CreateQuestionParameters.TYPE_SINGLE_SELECT);
        q1.setText("user question 1");
        q1.setChoicesFromStrings(Arrays.asList(new String[] { "yes", "no" }));
        questions.add(q1);
        CreateQuestionParameters q2 = new CreateQuestionParameters();
        q2.setType(CreateQuestionParameters.TYPE_SINGLE_SELECT);
        q2.setText("user question 2");
        q2.setChoicesFromStrings(Arrays.asList(new String[] { "good", "bad", "average" }));
        questions.add(q2);
        CreateQuestionParameters q3 = new CreateQuestionParameters();
        q3.setType(CreateQuestionParameters.TYPE_FREE_TEXT);
        q3.setText("user question 3");
        questions.add(q3);
        CreateQuestionParameters q4 = new CreateQuestionParameters();
        q4.setType(CreateQuestionParameters.TYPE_DATE);
        q4.setText("user question 4");
        questions.add(q4);
        CreateQuestionParameters q5 = new CreateQuestionParameters();
        q5.setType(CreateQuestionParameters.TYPE_FREE_TEXT);
        q5.setText("user question 5");
        questions.add(q5);
        CreateQuestionParameters q6 = new CreateQuestionParameters();
        q6.setType(CreateQuestionParameters.TYPE_NUMBER);
        q6.setText("user question 6");
        q6.setNumericMax(10);
        q6.setNumericMin(0);
        questions.add(q6);
        questionGroupTestHelper.createQuestions(questions);
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-296
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void changePasswordTest() throws Exception {

        HomePage homePage = loginSuccessfully();
        AdminPage adminPage = homePage.navigateToAdminPage();

        CreateUserParameters userParameters = adminPage.getAdminUserParameters();
        ChooseOfficePage createUserPage = adminPage.navigateToCreateUserPage();
        createUserPage.verifyPage();

        CreateUserEnterDataPage userEnterDataPage = createUserPage.selectOffice("MyOfficeDHMFT");

        CreateUserPreviewDataPage userPreviewDataPage = userEnterDataPage.submitAndGotoCreateUserPreviewDataPage(userParameters);
        CreateUserConfirmationPage userConfirmationPage = userPreviewDataPage.submit();
        //Then
        userConfirmationPage.verifyPage();
        UserViewDetailsPage userDetailsPage = userConfirmationPage.navigateToUserViewDetailsPage();
        userDetailsPage.verifyPage();

        Assert.assertTrue(userDetailsPage.getFullName().contains(userParameters.getFirstName() + " " + userParameters.getLastName()));
        //When
        EditUserDataPage editUserPage = userDetailsPage.navigateToEditUserDataPage();

        CreateUserParameters passwordParameters = new CreateUserParameters();
        passwordParameters.setPassword("tester1");
        passwordParameters.setPasswordRepeat("tester");
        //Then
        editUserPage = editUserPage.submitWithInvalidData(passwordParameters);
        editUserPage.verifyPasswordChangeError();
        //When
        passwordParameters.setPasswordRepeat("tester1");
        //Then
        EditUserPreviewDataPage editPreviewDataPage = editUserPage.submitAndGotoEditUserPreviewDataPage(passwordParameters);
        UserViewDetailsPage submitUserpage = editPreviewDataPage.submit();
        submitUserpage.verifyPage();
        //When
        LoginPage loginPage = (new MifosPage(selenium)).logout();

        ChangePasswordPage changePasswordPage = loginPage.loginAndGoToChangePasswordPageAs(userParameters.getUserName(), passwordParameters.getPassword()); // tester1

        ChangePasswordPage.SubmitFormParameters changePasswordParameters = new ChangePasswordPage.SubmitFormParameters();
        changePasswordParameters.setOldPassword("tester"); // wrong old password
        changePasswordParameters.setNewPassword(""); // empty new password
        changePasswordParameters.setConfirmPassword("");

        //Then
        changePasswordPage = changePasswordPage.submitWithInvalidData(changePasswordParameters);
        //When
        changePasswordParameters.setNewPassword("tester2"); //wrong old password with good new
        changePasswordParameters.setConfirmPassword("tester2");
        //Then
        changePasswordPage = changePasswordPage.submitWithInvalidData(changePasswordParameters);
        //When
        changePasswordParameters.setOldPassword("tester1"); // good old password and good new
        changePasswordParameters.setNewPassword("tester2");
        changePasswordParameters.setConfirmPassword("tester2");
        //Then
        HomePage homePage2 = changePasswordPage.submitAndGotoHomePage(changePasswordParameters);
        Assert.assertTrue(homePage2.getWelcome().contains(userParameters.getFirstName()));

        loginPage = (new MifosPage(selenium)).logout();
        homePage = loginPage.loginSuccessfulAs(userParameters.getUserName(), "tester2");

        changePasswordPage = homePage.navigateToYourSettingsPage().navigateToChangePasswordPage();

        changePasswordPage = changePasswordPage.submitWithInvalidData(changePasswordParameters);
        //When
        changePasswordParameters.setNewPassword("tester2"); //wrong old password with good new
        changePasswordParameters.setConfirmPassword("tester2");
        //Then
        changePasswordPage = changePasswordPage.submitWithInvalidData(changePasswordParameters);
        //When
        changePasswordParameters.setOldPassword("tester2"); // good old password and good new
        changePasswordParameters.setNewPassword("tester3");
        changePasswordParameters.setConfirmPassword("tester3");
        changePasswordPage.submitAndGotoHomePage(changePasswordParameters);

        loginPage = (new MifosPage(selenium)).logout();
        homePage = loginPage.loginSuccessfulAs(userParameters.getUserName(), changePasswordParameters.getNewPassword());

        Assert.assertTrue(homePage.getWelcome().contains(userParameters.getFirstName()));
    }

    private HomePage loginSuccessfully() {
        (new MifosPage(selenium)).logout();
        LoginPage loginPage = new AppLauncher(selenium).launchMifos();
        loginPage.verifyPage();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        homePage.verifyPage();

        return homePage;
    }
    
    /**
     * This test checks if user permissions are refreshed properly
     * 
     * We create new user with Admin role
     * we log as this user and remove Admin rights
     * Then we check if we are allowed to define new fees
     * If error occurs then test passed 
     */
	@Test(enabled=true)
	public void userPermissionRefreshingTest() {
		//Given
		//center
		String officeName = "branch1";
		String accessDeniedElement = "admin.error.message";
		
		//new system user with Admin permissions
		String userName = "jtest1";
		String password = "jtest123";
		String userFirstName = "John";
		String userLastName = "Tester1";
		String adminRole = "Admin";
		
		CreateUserParameters userParameters = new CreateUserParameters();
		userParameters.setFirstName(userFirstName);
		userParameters.setLastName(userLastName);
		userParameters.setUserName(userName);
		userParameters.setPassword(password);
		userParameters.setPasswordRepeat(password);
		userParameters.setUserLevel(CreateUserParameters.NON_LOAN_OFFICER);
		userParameters.setDateOfBirthDD("10");
		userParameters.setDateOfBirthMM("10");
		userParameters.setDateOfBirthYYYY("1970");
		userParameters.setGender(CreateUserParameters.MALE);
		userParameters.setRole(adminRole);
		userHelper.createUser(userParameters, officeName);
		
		
		//When
		CreateUserParameters userParametersNewRole = new CreateUserParameters();
		userParametersNewRole.setRole("");
		
		navigationHelper.navigateToHomePageAsNewUser(userName, password)
			.navigateToAdminPage()
			.navigateToViewSystemUsersPage()
			.searchAndNavigateToUserViewDetailsPage(userLastName)
			.navigateToEditUserDataPage()
			.submitAndGotoEditUserPreviewDataPage(userParametersNewRole)
			.submit()
			.navigateToAdminPageUsingHeaderTab()
			.navigateToFeesCreate();
		
		
		//Then
		Assert.assertTrue(selenium.isElementPresent(accessDeniedElement));
	}
}
