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

package org.mifos.framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
import org.mifos.db.upgrade.DatabaseUpgradeSupport;
import org.mifos.framework.ApplicationInitializer;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.SqlExecutor;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.service.test.TestingService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Log4jConfigurer;

public class DataSetUpgradeUtil {
    // Command line flag configuration.
    private static final String DATA_SET_OPTION_NAME = "f";
    private static final String DATA_DIRECTORY_OPTION_NAME = "a";
    private static final String USER_OPTION_NAME = "u";
    private static final String PASSWORD_OPTION_NAME = "p";
    private static final String PORT_OPTION_NAME = "P";
    private static final String HELP_OPTION_NAME = "h";
    private static final String DATABASE_OPTION_NAME = "d";
    private static final String SCHEMA_FILE_OPTION_NAME = "s";

    // Options
    private final Options options = new Options();
    private Option outputFile;
    private Option userOption;
    private Option passwordOption;
    private Option helpOption;
    private Option databaseOption;
    private Option allFilesInDirecoryOption;
    private Option schemaFileOption;
    private Option portOption;

    // Variables for data from command line
    String dataSetName;
    String databaseName = "mifos_gazelle_acceptance";
    String user;
    String password = "";
    String dataSetDirectoryName;
    String schemaFileName = "base_schema.sql";
    String port = "3306";

    DbUnitUtilities dbUnitUtilities;

    public static void main(String[] args) throws Exception {
        Log4jConfigurer.initLogging(new ConfigurationLocator().getFilePath(FilePaths.LOG_CONFIGURATION_FILE));
        DataSetUpgradeUtil util = new DataSetUpgradeUtil();
        util.doUpgrades(args);
    }

    public DataSetUpgradeUtil() {
        defineOptions();
    }

    public void doUpgrades(String[] args) throws Exception {
        parseOptions(args);
        System.out.println("Using database: " + databaseName);
        System.out.println("Using schema file: " + schemaFileName);
        ApplicationContext applicationContext = initializeSpring();
        if (dataSetDirectoryName != null) {
            File directory = new File(dataSetDirectoryName);

            File[] listOfFiles = directory.listFiles();

            if (listOfFiles != null) {
                int totalFilesToUpgrade = countFilesToUpgrade(listOfFiles);
                int filesUpgraded = 0;
                for (File listOfFile : listOfFiles) {
                    String currentFilename = listOfFile.getName();
                    if (listOfFile.isFile() && currentFilename.endsWith("dbunit.xml")) {
                        String dataFileName = dataSetDirectoryName + File.separator + currentFilename;
                        upgrade(dataFileName, applicationContext);
                        dump(dataFileName);
                        filesUpgraded++;
                        System.out.println("Finished " + filesUpgraded + "/" + totalFilesToUpgrade);
                    }
                }
            } else {
                fail("No files found in data set directory: " + dataSetDirectoryName);
            }
        } else {
            upgrade(dataSetName, applicationContext);
            dump(dataSetName);
        }
    }

    private int countFilesToUpgrade(File[] listOfFiles) {
        int fileCount = 0;
        for (File listOfFile : listOfFiles) {
            String currentFilename = listOfFile.getName();
            if (listOfFile.isFile() && currentFilename.endsWith("dbunit.xml")) {
                ++fileCount;
            }
        }
        return fileCount;
    }

    private void resetDatabase(String databaseName, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            statement.execute("DROP DATABASE IF EXISTS " + databaseName);
            statement.execute("CREATE DATABASE " + databaseName);
            statement.execute("USE " + databaseName);
        } finally {
            statement.close();
        }
    }

    private void upgrade(String fileName, ApplicationContext applicationContext) throws Exception {
        System.out.println("Upgrading: " + fileName);
        dbUnitUtilities = new DbUnitUtilities();
        IDataSet dataSet = null;
        try {
            dataSet = dbUnitUtilities.getDataSetFromFile(fileName);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Class.forName("com.mysql.jdbc.Driver");
        Connection jdbcConnection = null;

        try {
            // TODO use same db cxn info that Mifos uses! (local.properties)
            jdbcConnection = DriverManager.getConnection(getUrl(databaseName, port, params), user, password);
            jdbcConnection.setAutoCommit(false);
            resetDatabase(databaseName, jdbcConnection);
            SqlExecutor.execute(new FileInputStream(schemaFileName), jdbcConnection);
            jdbcConnection.commit();
            jdbcConnection.setAutoCommit(true);
            IDatabaseConnection databaseConnection = new DatabaseConnection(jdbcConnection);
            databaseConnection.getConfig()
                    .setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, Boolean.FALSE);
            DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, dataSet);
        } finally {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }

        System.setProperty(TestingService.TEST_MODE_SYSTEM_PROPERTY, "acceptance");
        ApplicationInitializer applicationInitializer = new ApplicationInitializer();
        applicationInitializer.dbUpgrade(applicationContext);
        applicationContext.getBean(DatabaseUpgradeSupport.class).contraction();
        applicationInitializer.setAttributesOnContext(null);
        StaticHibernateUtil.closeSession();
        System.out.println(" upgrade done!");
    }

    public static ApplicationContext initializeSpring() {
        return new ClassPathXmlApplicationContext("classpath:/org/mifos/config/resources/applicationContext.xml",
                "classpath:META-INF/spring/DbUpgradeContext.xml", "classpath:META-INF/spring/CashFlowContext.xml",
                "classpath:META-INF/spring/QuestionnaireContext.xml");
    }

    private static final String params = "?sessionVariables=FOREIGN_KEY_CHECKS=0";

    private String getUrl(String databaseName, String port, String params) {
        return String.format("jdbc:mysql://localhost:%s/%s%s", port, databaseName, params);
    }

    // Rationale: jdbcConnection is closed.
    private void dump(String fileName) throws ClassNotFoundException, SQLException, DatabaseUnitException, IOException {
        System.out.println("Dumping :" + fileName);
        Connection jdbcConnection = DriverManager.getConnection(getUrl(databaseName, port, params), user, password);

        String fileEnding = fileName.substring(fileName.length() - 3, fileName.length());

        if ("xml".equals(fileEnding)) {
            // dump xml
            dbUnitUtilities.dumpDatabase(fileName, jdbcConnection);
        } else if ("sql".equals(fileEnding)) {
            // dump sql
            fail("ERROR: SQL dumping not implemented yet. Please dump " + databaseName
                    + " manually with mysqldump. The upgrades have been applied.");
        } else {
            fail("ERROR: Bad data set name. The file ending has to be .xml or .sql");
        }

        jdbcConnection.close();
        System.out.println(" dump done!");
    }

    @SuppressWarnings("static-access")
    private void defineOptions() {
        outputFile = OptionBuilder
                .withArgName("data set file name")
                .withLongOpt("dataset")
                .hasArg()
                .withDescription(
                        "Upgrade the test data set with this name-- include the full file path (e.g. /home/me/dataSets/data_dbunit.xml")
                .create(DATA_SET_OPTION_NAME);

        allFilesInDirecoryOption = OptionBuilder
                .withArgName("data set directory")
                .withLongOpt("dataDirectory")
                .hasArg()
                .withDescription(
                        "Upgrade all test data sets in this directory-- include the full path (e.g. /home/me/dataSets )")
                .create(DATA_DIRECTORY_OPTION_NAME);

        userOption = OptionBuilder.withArgName("user name").withLongOpt("user").hasArg()
                .withDescription("database user name").create(USER_OPTION_NAME);

        passwordOption = OptionBuilder.withArgName("password").withLongOpt("password").hasArg()
                .withDescription("database password").create(PASSWORD_OPTION_NAME);

        databaseOption = OptionBuilder
                .withArgName("database")
                .withLongOpt("database")
                .hasArg()
                .withDescription(
                        "database name (default=" + databaseName
                                + ") this must match whatever acceptance.database points to in local.properties")
                .create(DATABASE_OPTION_NAME);

        schemaFileOption = OptionBuilder.withArgName("schema file").withLongOpt("schema").hasArg()
                .withDescription("schema file to upgrade from (default=" + schemaFileName + ")")
                .create(SCHEMA_FILE_OPTION_NAME);

        portOption = OptionBuilder.withArgName("port").withLongOpt("port").hasArg()
                .withDescription("mysql server port (default=" + port + ")").create(PORT_OPTION_NAME);

        helpOption = OptionBuilder.withLongOpt("help").withDescription("display help").create(HELP_OPTION_NAME);

        options.addOption(outputFile);
        options.addOption(userOption);
        options.addOption(passwordOption);
        options.addOption(helpOption);
        options.addOption(databaseOption);
        options.addOption(allFilesInDirecoryOption);
        options.addOption(schemaFileOption);
        options.addOption(portOption);
    }

    public void parseOptions(String[] args) throws URISyntaxException {
        // create the command line parser
        CommandLineParser parser = new PosixParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            if (line.hasOption(HELP_OPTION_NAME)) {
                showHelp(options);
                System.exit(0);
            }

            if (line.hasOption(DATA_SET_OPTION_NAME)) {
                if (line.hasOption(DATA_DIRECTORY_OPTION_NAME)) {
                    fail("Specify either a data set (-f) or data directory (-a) but not both.");
                }
                dataSetName = line.getOptionValue(DATA_SET_OPTION_NAME);
            } else if (line.hasOption(DATA_DIRECTORY_OPTION_NAME)) {
                dataSetDirectoryName = line.getOptionValue(DATA_DIRECTORY_OPTION_NAME);
            } else {
                fail("Specify either a data set (-f) or data directory (-a)");
            }

            if (line.hasOption(USER_OPTION_NAME)) {
                user = line.getOptionValue(USER_OPTION_NAME);
            } else {
                missingOption(userOption);
            }
            if (line.hasOption(PASSWORD_OPTION_NAME)) {
                password = line.getOptionValue(PASSWORD_OPTION_NAME);
            }
            if (line.hasOption(DATABASE_OPTION_NAME)) {
                databaseName = line.getOptionValue(DATABASE_OPTION_NAME);
            }
            if (line.hasOption(SCHEMA_FILE_OPTION_NAME)) {
                schemaFileName = line.getOptionValue(SCHEMA_FILE_OPTION_NAME);
                File file = new File(schemaFileName);
                if (!file.exists()) {
                    fail("Unable to find schema file: " + schemaFileName);
                }
            } else {
                missingOption(schemaFileOption);
            }

            if (line.hasOption(PORT_OPTION_NAME)) {
                port = line.getOptionValue(PORT_OPTION_NAME);
            }
        } catch (ParseException exp) {
            fail("Parsing failed.  Reason: " + exp.getMessage());
        }
    }

    private static void showHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("DbUnitDataExport", options);
    }

    private static void missingOption(Option option) {
        fail("Missing required option: " + option.getArgName());
    }

    private static void fail(String string) {
        System.err.println(string);
        System.exit(0);
    }
}
