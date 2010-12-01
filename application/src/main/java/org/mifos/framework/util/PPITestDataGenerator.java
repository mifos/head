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

package org.mifos.framework.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.config.exceptions.ConfigurationException;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerDaoHibernate;
import org.mifos.framework.ApplicationInitializer;
import org.mifos.framework.components.batchjobs.exceptions.TaskSystemException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.platform.questionnaire.builders.QuestionGroupInstanceDtoBuilder;
import org.mifos.platform.questionnaire.builders.QuestionGroupResponseDtoBuilder;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.service.test.TestingService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


@SuppressWarnings( { "PMD.SystemPrintln", "PMD.SingularField" })
public class PPITestDataGenerator {
    // Command line flag configuration.
    private static final String HELP_OPTION_NAME = "h";
    private static final String TEST_DATA_FILE_OPTION_NAME = "f";
    private static final String TEST_DATA_DIRECTORY_OPTION_NAME = "a";

    // Options
    private final Options options = new Options();
    private Option helpOption;
    private Option allFilesInDirecoryOption;
    private Option testDataFileOption;

    // Variables for data from command line
    String testDataDirectoryName;
    String dataSetName;

    QuestionnaireServiceFacade questionnaireServiceFacade;
    CustomerDao customerDao;

    public static void main(String[] args) throws URISyntaxException, TaskSystemException, PersistenceException, ConfigurationException, FinancialException, FileNotFoundException, IOException {
        PPITestDataGenerator util = new PPITestDataGenerator();

        util.loadTestDataFiles(args);
    }

    public PPITestDataGenerator() {
        defineOptions();
    }

    private QuestionGroupDetail findQuestionGroup(String name) {
        List<QuestionGroupDetail> list = questionnaireServiceFacade.getAllQuestionGroups();
        for (QuestionGroupDetail qg: list) {
            if (qg.getTitle().compareTo(name) == 0) {
                return qg;
            }
        }
        throw new RuntimeException("Could not find a question group named: " + name);
    }


    public void loadTestDataFiles(String[] args) throws IOException, URISyntaxException, TaskSystemException, PersistenceException, ConfigurationException, FinancialException {
        parseOptions(args);
        ApplicationContext applicationContext = initializeSpring();
        customerDao = applicationContext.getBean(CustomerDaoHibernate.class);
        questionnaireServiceFacade = applicationContext.getBean(QuestionnaireServiceFacade.class);

        if (testDataDirectoryName != null) {
            File directory = new File(testDataDirectoryName);

            File[] listOfFiles = directory.listFiles();

            if (listOfFiles != null ) {
                int totalFilesToLoad = countFilesToUpgrade(listOfFiles);
                int filesLoaded = 0;
                for (File listOfFile : listOfFiles) {
                    String currentFilename = listOfFile.getName();
                    if (listOfFile.isFile() &&
                            currentFilename.endsWith("Testing.properties")) {
                        String dataFileName = testDataDirectoryName + File.separator + currentFilename;
                        loadData(dataFileName, applicationContext);
                        filesLoaded++;
                        System.out.println("Finished " + filesLoaded + "/" + totalFilesToLoad);
                    }
                }
            } else {
                fail("No files found in directory: " + testDataDirectoryName);
            }
        } else {
            loadData(dataSetName, applicationContext);
        }
    }

    private int countFilesToUpgrade(File[] listOfFiles) {
        int fileCount = 0;
        for (File listOfFile : listOfFiles) {
            String currentFilename = listOfFile.getName();
            if (listOfFile.isFile() &&
                    currentFilename.endsWith("Testing.properties")) {
                ++fileCount;
            }
        }
        return fileCount;
    }

    private void loadData(String filename, ApplicationContext applicationContext) throws TaskSystemException, PersistenceException, ConfigurationException, FinancialException, FileNotFoundException, IOException {

        System.setProperty(TestingService.TEST_MODE_SYSTEM_PROPERTY,"acceptance");
        ApplicationInitializer applicationInitializer = new ApplicationInitializer();
        applicationInitializer.dbUpgrade(applicationContext);
        applicationInitializer.setAttributesOnContext(null);

        CustomerBO customer = customerDao.findClientBySystemId("0006-000000063");
        System.out.println("Found: " + customer.getDisplayName());

        Properties properties = new Properties();

        properties.load(new FileReader(filename));

        System.out.println("Loading data for: " + properties.getProperty("questionGroup.name"));
        questionnaireServiceFacade.uploadPPIQuestionGroup(properties.getProperty("questionGroup.xml.filename"));

        QuestionGroupDetail qg = findQuestionGroup(properties.getProperty("questionGroup.name"));

        int surveyCount = Integer.parseInt(properties.getProperty("survey.count"));
        for (int surveyNum = 1; surveyNum <= surveyCount; ++surveyNum) {
            createSurveyInstance(customer, properties, qg, surveyNum);
        }

        System.out.println("done!");

    }

    private void createSurveyInstance(CustomerBO customer, Properties properties, QuestionGroupDetail qg, int surveyNum) {
        Integer creatorId = 1;
        QuestionGroupInstanceDtoBuilder instanceBuilder = new QuestionGroupInstanceDtoBuilder();
        instanceBuilder.withQuestionGroup(qg.getId()).withCompleted(true).withCreator(creatorId).
            withEventSource(1).withEntity(customer.getCustomerId()).withVersion(1);
        List<SectionQuestionDetail> questions = qg.getSectionDetail(0).getQuestions();
        for (SectionQuestionDetail question: questions) {
            String responseKey = "survey." + surveyNum + ".question." + question.getSequenceNumber() + ".response.text";
            String questionResponse = properties.getProperty(responseKey);
            QuestionGroupResponseDtoBuilder responseBuilder = new QuestionGroupResponseDtoBuilder();
            responseBuilder.withSectionQuestion(question.getQuestionId()).withResponse(questionResponse);
            instanceBuilder.addResponses(responseBuilder.build());
        }

        questionnaireServiceFacade.saveQuestionGroupInstance(instanceBuilder.build());
    }

    public static ApplicationContext initializeSpring() {
        return new ClassPathXmlApplicationContext(
                "classpath:/org/mifos/config/resources/messageSourceBean.xml",
                "classpath:/org/mifos/config/resources/services.xml",
                "classpath:/org/mifos/config/resources/hibernate-daos.xml",
                "classpath:/org/mifos/config/resources/persistenceContext.xml",
                "classpath:/org/mifos/config/resources/dataSourceContext.xml",
                "classpath:/META-INF/spring/QuestionnaireContext.xml");
    }


    private void defineOptions() {

        helpOption = OptionBuilder
        .withLongOpt("help")
        .withDescription( "display help" )
        .create( HELP_OPTION_NAME );

        allFilesInDirecoryOption = OptionBuilder.withArgName( "test data directory" )
        .withLongOpt("dataDirectory")
        .hasArg()
        .withDescription( "Use all test data in this directory-- include the full path (e.g. /home/me/testData )" )
        .create( TEST_DATA_DIRECTORY_OPTION_NAME );

        testDataFileOption = OptionBuilder.withArgName( "test data file name" )
        .withLongOpt("dataset")
        .hasArg()
        .withDescription( "Use the test data file with this name-- include the full file path (e.g. /home/me/testData/Bangladesh2009Testing.properties" )
        .create( TEST_DATA_FILE_OPTION_NAME );

        options.addOption(helpOption);
        options.addOption(allFilesInDirecoryOption);
        options.addOption(testDataFileOption);
    }

    public void parseOptions(String[] args) throws URISyntaxException {
        // create the command line parser
        CommandLineParser parser = new PosixParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );
            if( line.hasOption( HELP_OPTION_NAME ) ) {
                showHelp(options);
                System.exit(0);
            }
            if( line.hasOption( TEST_DATA_FILE_OPTION_NAME ) ) {
                if (line.hasOption( TEST_DATA_DIRECTORY_OPTION_NAME)) {
                    fail("Specify either a data set (-f) or data directory (-a) but not both.");
                }
                dataSetName = line.getOptionValue(TEST_DATA_FILE_OPTION_NAME);
            } else if( line.hasOption( TEST_DATA_DIRECTORY_OPTION_NAME ) ) {
                testDataDirectoryName = line.getOptionValue(TEST_DATA_DIRECTORY_OPTION_NAME);
            } else {
                fail("Specify either a data set (-f) or data directory (-a)");
            }

        } catch( ParseException exp ) {
            fail( "Parsing failed.  Reason: " + exp.getMessage() );
        }
    }

    private static void showHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "PPITestDataGenerator", options );
    }

    private static void missingOption(Option option) {
        fail("Missing required option: " + option.getArgName());
    }

    private static void fail(String string) {
        System.err.println( string );
        System.exit(0);
    }
}
