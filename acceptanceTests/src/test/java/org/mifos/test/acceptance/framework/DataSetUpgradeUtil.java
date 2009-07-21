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

package org.mifos.test.acceptance.framework;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;

/*

 888888ba                             .8888b             .8888b 
 88    `8b                            88   "             88   " 
 a88aaaa8P' 88d888b. .d8888b. .d8888b. 88aaa     .d8888b. 88aaa  
 88        88'  `88 88'  `88 88'  `88 88        88'  `88 88     
 88        88       88.  .88 88.  .88 88        88.  .88 88     
 dP        dP       `88888P' `88888P' dP        `88888P' dP     


 a88888b.                                                dP   
 d8'   `88                                                88   
 88        .d8888b. 88d888b. .d8888b. .d8888b. 88d888b. d8888P 
 88        88'  `88 88'  `88 88'  `"" 88ooood8 88'  `88   88   
 Y8.   .88 88.  .88 88    88 88.  ... 88.  ... 88.  .88   88   
 Y88888P' `88888P' dP    dP `88888P' `88888P' 88Y888P'   dP   
 88              
 dP              

 */

@SuppressWarnings( { "PMD.SystemPrintln", "PMD.SingularField" })
public class DataSetUpgradeUtil {
    // Command line flag configuration.
    private static final String DATA_SET_OPTION_NAME = "f";
    private static final String USER_OPTION_NAME = "u";
    private static final String PASSWORD_OPTION_NAME = "p";
    private static final String HELP_OPTION_NAME = "h";
    private static final String DATABASE_OPTION_NAME = "d";

    // Options
    private final Options options = new Options();
    private Option outputFile;
    private Option userOption;
    private Option passwordOption;
    private Option helpOption;
    private Option databaseOption;
    
    // Variables for data from command line
    String dataSetName;
    String databaseName = "mifostest";
    String user;
    String password;

    DbUnitUtilities dbUnitUtilities;
    private static final String ACCEPTANCE_TEST_DATA_SET_DIRECTORY = "acceptanceTests/src/test/resources/";
    
    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException,
            SQLException, DatabaseUnitException {
        DataSetUpgradeUtil util = new DataSetUpgradeUtil();
        util.defineOptions();
        util.parseOptions(args);
        System.out.print("Upgrading...");
        util.upgrade();
        System.out.println(" done!");
        System.out.print("Dumping...");
        util.dump();
        System.out.println(" done!");
    }
    
    private void upgrade() throws URISyntaxException, DataSetException, IOException {
        dbUnitUtilities = new DbUnitUtilities();
        IDataSet dataSet = dbUnitUtilities.getDataSetFromFile(dataSetName);
        
        DataSetUpgrade upgrade = new DataSetUpgrade(dataSet, databaseName, user, password);
        
        try {
            upgrade.upgrade();
        } catch (FileNotFoundException fnfe) {
            fail("ERROR: Data set file not found.");
        } catch (Exception e) {
            fail("ERROR: Something went wrong.");
        }
    }
    
    @SuppressWarnings("PMD.CloseResource")
    // Rationale: jdbcConnection is closed.
    private void dump() throws FileNotFoundException, ClassNotFoundException, SQLException, DatabaseUnitException, IOException {
        Connection jdbcConnection = DriverManager.getConnection("jdbc:mysql://localhost/" + databaseName
                + "?sessionVariables=FOREIGN_KEY_CHECKS=0", user, password);
        
        dbUnitUtilities.dumpDatabase(/*ACCEPTANCE_TEST_DATA_SET_DIRECTORY +*/ dataSetName, jdbcConnection);
        
        // TODO zip
        
        jdbcConnection.close();
    }
    
    
    
    
    
    private void defineOptions() {
        outputFile = OptionBuilder.withArgName( "data set name" )
        .withLongOpt("dataset")
        .hasArg()
        .withDescription( "Upgrade the test data set with this name. Do not include any path, just \"acceptance_small_003_dbunit.xml.zip\"." )
        .create( DATA_SET_OPTION_NAME );

        userOption = OptionBuilder.withArgName( "user name" )
        .withLongOpt("user")
        .hasArg()
        .withDescription( "database user name" )
        .create( USER_OPTION_NAME );

        passwordOption = OptionBuilder.withArgName( "password" )
        .withLongOpt("password")
        .hasArg()
        .withDescription( "database password" )
        .create( PASSWORD_OPTION_NAME );

        databaseOption = OptionBuilder.withArgName( "database" )
        .withLongOpt("database")
        .hasArg()
        .withDescription( "database name (default=" + databaseName + ")" )
        .create( DATABASE_OPTION_NAME );

        helpOption = OptionBuilder
        .withLongOpt("help")
        .withDescription( "display help" )
        .create( HELP_OPTION_NAME );


        options.addOption(outputFile);
        options.addOption(userOption);
        options.addOption(passwordOption);
        options.addOption(helpOption);         
        options.addOption(databaseOption);        
    }
    
    public void parseOptions(String[] args) {
        // create the command line parser
        CommandLineParser parser = new PosixParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );
            if( line.hasOption( HELP_OPTION_NAME ) ) {
                showHelp(options);
                System.exit(0);
            }
            if( line.hasOption( DATA_SET_OPTION_NAME ) ) {
                dataSetName = line.getOptionValue(DATA_SET_OPTION_NAME);
            } else {
                missingOption(outputFile);
            }
            if( line.hasOption( USER_OPTION_NAME ) ) {
                user = line.getOptionValue(USER_OPTION_NAME);
            } else {
                missingOption(userOption);
            }
            if( line.hasOption( PASSWORD_OPTION_NAME ) ) {
                password = line.getOptionValue(PASSWORD_OPTION_NAME);
            } else {
                missingOption(passwordOption);
            }
            if( line.hasOption( DATABASE_OPTION_NAME ) ) {
                databaseName = line.getOptionValue(DATABASE_OPTION_NAME);
            } 
        }
        catch( ParseException exp ) {
            fail( "Parsing failed.  Reason: " + exp.getMessage() );
        }
    }
    
    private static void showHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "DbUnitDataExport", options );   
    }

    private static void missingOption(Option option) {
        fail("Missing required option: " + option.getArgName());
    }

    private static void fail(String string) {
        System.err.println( string );
        System.exit(0);
    }
}
