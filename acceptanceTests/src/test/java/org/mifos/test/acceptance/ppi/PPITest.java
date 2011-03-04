package org.mifos.test.acceptance.ppi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mifos.framework.util.ConfigurationLocator;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.client.CreateClientEnterPersonalDataPage;
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.questionnaire.AttachQuestionGroupParameters;
import org.mifos.test.acceptance.framework.testhelpers.ClientTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.QuestionGroupTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("PMD.SignatureDeclareThrowsException")
@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(groups = {"ppi", "acceptance", "ui"})
public class PPITest extends UiTestCaseBase {

    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;

    private QuestionGroupTestHelper questionGroupTestHelper;
    private ClientTestHelper clientTestHelper;

    @Override
    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        super.setUp();
        questionGroupTestHelper= new QuestionGroupTestHelper(selenium);
        clientTestHelper = new ClientTestHelper(selenium);
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,11,7,10,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
    }

  //http://mifosforge.jira.com/browse/MIFOSTEST-147
    public void verifyPPIQuestionGroup() throws Exception {
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);
        String configPath = new ConfigurationLocator().getConfigurationDirectory();
        String fileName= "PPISurveyBANGLADESH2009.xml";
        String sourcePath = PPITest.class.getResource("/mpesa/"+fileName).getFile();
        String destPath = configPath +"/uploads/questionGroups/" + fileName;
        File sourceFile = new File(sourcePath);
        File destFile = new File(destPath);
        List <String> QGlist = new ArrayList<String>();
        QGlist.add("PPI Bangladesh 2009");
        CreateClientEnterPersonalDataPage.SubmitFormParameters formParameters = FormParametersHelper.getClientEnterPersonalDataPageFormParameters();
        QuestionResponseParameters responseParams = getQuestionResponseParametersForPPIIndia2008();
        AttachQuestionGroupParameters attachParams = new AttachQuestionGroupParameters();
        attachParams.setQuestionGroupName("PPI Bangladesh 2009");
        attachParams.addTextResponse("Date Survey Was Taken", "24/01/2011");
        attachParams.addCheckResponse("How many household members are 11-years-old or younger?", "Four or more");
        attachParams.addCheckResponse("Does any household member work for a daily wage?", "Yes");
        attachParams.addCheckResponse("What type of latrine does the household use?", "Open field");
        attachParams.addCheckResponse("How many rooms does the household occupy (excluding rooms used for business)?", "Four");
        attachParams.addCheckResponse("What is the main construction material of the walls?", "Brick/cement");
        attachParams.addCheckResponse("What is the main construction material of the roof?", "Cement");
        attachParams.addCheckResponse("What is the total cultivable agricultural land owned by the household?", "More than 1 acre");
        attachParams.addCheckResponse("Does the household own a television?", "Yes");
        attachParams.addCheckResponse("Does the household own a two-in-one cassette player?", "No");
        attachParams.addCheckResponse("Does the household own a wristwatch?", "Yes");
        String[] eventList = {"View Client","Create Client", "Close Client"};
        //When
        copyFile(sourceFile, destFile);
        questionGroupTestHelper.activatePPI("BANGLADESH2009");
        questionGroupTestHelper.changeAppliesTo("PPI Bangladesh 2009", eventList);
        questionGroupTestHelper.navigateToViewQuestionGroups(QGlist);
        String clientName = clientTestHelper.createClientWithQuestionGroups(formParameters, "MyGroup1232993846342", responseParams).getHeading();
        attachParams.setTarget(clientName);
        clientTestHelper.activateClient(clientName);
        questionGroupTestHelper.attachQuestionGroupToClient(attachParams);
        clientTestHelper.closeClientWithQG(clientName, responseParams);
     }

    private QuestionResponseParameters getQuestionResponseParametersForPPIIndia2008() {
        QuestionResponseParameters responseParams = new QuestionResponseParameters();
        responseParams.addTextAnswer("questionGroups[0].sectionDetails[0].questions[0].value", "24/01/2011");
        responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[1].value", "Four or more");
        responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[2].value", "Yes");
        responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[3].value", "Open field");
        responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[4].value", "Four");
        responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[5].value", "Brick/cement");
        responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[6].value", "Cement");
        responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[7].value", "More than 1 acre");
        responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[8].value", "Yes");
        responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[9].value", "No");
        responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[10].value", "Yes");
        return responseParams;
    }

    private static void copyFile(File sourceFile, File destFile) throws IOException {
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
     }


}
