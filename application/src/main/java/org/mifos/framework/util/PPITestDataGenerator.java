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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.config.exceptions.ConfigurationException;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerDaoHibernate;
import org.mifos.framework.ApplicationInitializer;
import org.mifos.framework.components.batchjobs.exceptions.TaskSystemException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.SqlExecutor;
import org.mifos.framework.persistence.SqlResource;
import org.mifos.platform.questionnaire.builders.QuestionGroupInstanceDtoBuilder;
import org.mifos.platform.questionnaire.builders.QuestionGroupResponseDtoBuilder;
import org.mifos.platform.questionnaire.domain.QuestionnaireService;
import org.mifos.platform.questionnaire.domain.QuestionnaireServiceImpl;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacadeImpl;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupInstanceDto;
import org.mifos.service.test.TestingService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


@SuppressWarnings( { "PMD.SystemPrintln", "PMD.SingularField" })
public class PPITestDataGenerator {
    // Command line flag configuration.
    private static final String HELP_OPTION_NAME = "h";


    // Options
    private final Options options = new Options();
    private Option helpOption;


    // Variables for data from command line

    public static void main(String[] args) throws URISyntaxException, TaskSystemException, PersistenceException, ConfigurationException, FinancialException {
        PPITestDataGenerator util = new PPITestDataGenerator();
        util.parseOptions(args);

        ApplicationContext applicationContext = PPITestDataGenerator.initializeSpring();
        util.upgrade(applicationContext);
    }

    public PPITestDataGenerator() {
        defineOptions();
    }

    private void upgrade(ApplicationContext applicationContext) throws TaskSystemException, PersistenceException, ConfigurationException, FinancialException {

        System.setProperty(TestingService.TEST_MODE_SYSTEM_PROPERTY,"acceptance");
        ApplicationInitializer applicationInitializer = new ApplicationInitializer();
        applicationInitializer.dbUpgrade(applicationContext);
        applicationInitializer.setAttributesOnContext(null);

        CustomerDao customerDao = applicationContext.getBean(CustomerDaoHibernate.class);
        CustomerBO customer = customerDao.findClientBySystemId("0006-000000063");
        System.out.println("Found: " + customer.getDisplayName());

        QuestionnaireServiceFacade questionnaireServiceFacade = applicationContext.getBean(QuestionnaireServiceFacade.class);
        questionnaireServiceFacade.uploadPPIQuestionGroup("BANGLADESH2009");
        List<QuestionGroupDetail> list = questionnaireServiceFacade.getAllQuestionGroups();
        assert(list.size() == 1);

        QuestionGroupDetail qg = list.get(0);
        Integer creatorId = 1;

        QuestionGroupInstanceDtoBuilder instanceBuilder = new QuestionGroupInstanceDtoBuilder();
        instanceBuilder.withQuestionGroup(qg.getId()).withCompleted(true).withCreator(creatorId).
            withEventSource(1).withEntity(customer.getCustomerId()).withVersion(1);
        List<SectionQuestionDetail> questions = qg.getSectionDetail(0).getQuestions();
        for (SectionQuestionDetail question: questions) {
            QuestionGroupResponseDtoBuilder responseBuilder = new QuestionGroupResponseDtoBuilder();
            if (question.getQuestionType() == QuestionType.DATE) {
                responseBuilder.withSectionQuestion(question.getQuestionId()).withResponse("11/11/2010");
            } else {
                responseBuilder.withSectionQuestion(question.getQuestionId()).withResponse(
                        question.getAnswerChoices().get(0).getValue());
            }
            instanceBuilder.addResponses(responseBuilder.build());
        }

        questionnaireServiceFacade.saveQuestionGroupInstance(instanceBuilder.build());

        System.out.println("done!");

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


        options.addOption(helpOption);

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
