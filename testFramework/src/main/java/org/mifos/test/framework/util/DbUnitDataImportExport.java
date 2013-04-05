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

package org.mifos.test.framework.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;

@SuppressWarnings({"PMD.SystemPrintln",  // as a command line utility System.out output seems ok
                   "PMD.SingularField",  // Option fields could be local, but for consistency keep them at the class level
                   "PMD.TooManyFields", // What to do?
                   "PMD.CyclomaticComplexity"})
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"DM_EXIT"}, justification="Command line tool exit")
public class DbUnitDataImportExport {

    private static final String FILE_OPTION_NAME = "f";
    private static final String USER_OPTION_NAME = "u";
    private static final String PASSWORD_OPTION_NAME = "p";
    private static final String HELP_OPTION_NAME = "h";
    private static final String IMPORT_OPTION_NAME = "i";
    private static final String EXPORT_OPTION_NAME = "x";
    private static final String DATABASE_OPTION_NAME = "d";
    private static final String SQL_OPTION_NAME = "s";

    private String fileName = "dbunit.xml";
    private String password = "";
    private String user = "";
    private boolean doExport = true;
    private boolean exportAsSql = false;
    private String databaseName = "mifos";

    private final Options options = new Options();
    private Option outputFile;
    private Option userOption;
    private Option passwordOption;
    private Option helpOption;
    private Option importOption;
    private Option exportOption;
    private Option databaseOption;
    private Option sqlOption;

    public static void main(String[] args) throws ClassNotFoundException, SQLException, DatabaseUnitException, FileNotFoundException, IOException {
        DbUnitDataImportExport exporter = new DbUnitDataImportExport();
        exporter.parseOptions(args);
        exporter.importExport();
    }

    @SuppressWarnings("static-access")
    private void defineOptions() {
        outputFile = OptionBuilder.withArgName( "file name" )
        .withLongOpt("fileName")
        .hasArg()
        .withDescription( "use given file for dbunit import/export (default= " + fileName + ")" )
        .create( FILE_OPTION_NAME );

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
        .withDescription( "database name (default= " + databaseName + ")" )
        .create( DATABASE_OPTION_NAME );

        helpOption = OptionBuilder
        .withLongOpt("help")
        .withDescription( "display help" )
        .create( HELP_OPTION_NAME );

        importOption = OptionBuilder
        .withLongOpt("import")
        .withDescription( "import from a file" )
        .create( IMPORT_OPTION_NAME );

        exportOption = OptionBuilder
        .withLongOpt("export")
        .withDescription( "export to a file" )
        .create( EXPORT_OPTION_NAME );

        sqlOption = OptionBuilder
        .withLongOpt("sql")
        .withDescription( "export as an sql dump" )
        .create( SQL_OPTION_NAME );


        options.addOption(outputFile);
        options.addOption(userOption);
        options.addOption(passwordOption);
        options.addOption(helpOption);
        options.addOption(importOption);
        options.addOption(exportOption);
        options.addOption(databaseOption);
        options.addOption(sqlOption);
    }

    public DbUnitDataImportExport() {
        defineOptions();
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
            if( line.hasOption( FILE_OPTION_NAME ) ) {
                fileName = line.getOptionValue(FILE_OPTION_NAME);
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
            if( line.hasOption( IMPORT_OPTION_NAME ) ) {
                doExport = false;
            } else if( line.hasOption( EXPORT_OPTION_NAME ) ) {
                doExport = true;
                if ( line.hasOption(SQL_OPTION_NAME) ) {
                    exportAsSql = true;
                }
            }
        }
        catch( ParseException exp ) {
            fail( "Parsing failed.  Reason: " + exp.getMessage() );
        }
    }

    public void importExport() throws FileNotFoundException, ClassNotFoundException, SQLException, DatabaseUnitException, IOException {
        if (doExport) {
            if (exportAsSql) {
                dumpSql(fileName);
            } else {
                dumpData(fileName);
            }
        } else {
            loadDataFromFile(fileName);
        }
    }

    private void dumpSql(String fileName) {
        // execute mysqldump and provide a reasonable error message if
        // mysqldump isn't found on the $PATH.

        System.out.print("dumping sql to: " + fileName + " ... ");

        try
        {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mysqldump --password=" + password +
                                            " --user=" + user +
                                            " --result-file=" + fileName +
                                            " " + databaseName );
            int exitVal = proc.waitFor();

            if (exitVal == 0) {
                System.out.println("done!");
            } else {
                System.err.println("ERROR: Wrong username, password and/or database name!");
            }
        } catch (IOException ioe) {
            System.err.println("ERROR: " + ioe.getMessage());

            System.err.println("Please make sure that \"mysqldump\" is in your PATH.\nSee http://www.mifos.org/developers/install-mifos/install-windows#add-required-environment-variables for an example %Path on Windows.\nOn linux, run \"which mysqldump\" to find out which directory mysqldump is in, and add it to PATH.");
        } catch (InterruptedException ie) {
            System.err.println("ERROR (interruption): " + ie.getLocalizedMessage());
        }
    }

    public void dumpData(String fileName) throws ClassNotFoundException, SQLException, DatabaseUnitException, FileNotFoundException, IOException {
        // database connection
        Class.forName("com.mysql.jdbc.Driver");
        Connection jdbcConnection = null;
        IDataSet fullDataSet;
        try {
            jdbcConnection = DriverManager.getConnection(
                    "jdbc:mysql://localhost/" + databaseName, user, password);
            IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
            connection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, Boolean.TRUE);

            // sequenced data should not be necessary when foreign key constraints
            // are turned off on the connection...
            fullDataSet = connection.createDataSet();

            FlatXmlDataSet.write(fullDataSet, new FileOutputStream(fileName));
        } finally {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }


    }

    private void loadDataFromFile(String fileName) throws DatabaseUnitException, SQLException, IOException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection jdbcConnection = null;

        boolean enableColumnSensing = true;
        IDataSet dataSet = new FlatXmlDataSet(new File(fileName),false,enableColumnSensing);
        try {
            jdbcConnection = DriverManager.getConnection(
                    "jdbc:mysql://localhost/" + databaseName + "?sessionVariables=FOREIGN_KEY_CHECKS=0", user, password);
            IDatabaseConnection databaseConnection = new DatabaseConnection(jdbcConnection);
            databaseConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, Boolean.TRUE);
            DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, dataSet);
        }
        finally {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
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


