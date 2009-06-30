/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
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

package org.mifos.core;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings({"PMD.SystemPrintln",  // as a command line utility System.out output seems ok 
"PMD.SingularField"}) // Option fields could be local, but for consistency keep them at the class level
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"DM_EXIT"}, justification="Command line tool exit")
public class PseudoLocalizationGenerator {
    private static final Log LOG = LogFactory.getLog(PseudoLocalizationGenerator.class);
	private static final String PREFIX = "@@@";
	private static final String SUFFIX = "###";

    private static final String FILE_OPTION_NAME = "f";
    private static final String LOCALE_OPTION_NAME = "l";
    private static final String DIRECTORY_OPTION_NAME = "d";
    private static final String HELP_OPTION_NAME = "h";
    
    private String locale = "is";
    private String baseFileName = "messages";
    private String directory = ""; 
    
    private final Options options = new Options();
    private Option baseFileNameOption;
    private Option helpOption;
    private Option localeOption;
    private Option directoryOption;
	
    public PseudoLocalizationGenerator() {
        defineOptions();
    }
    
	/**
	 * Work in progress on a pseudolocalizer that can generate
	 * a pseudolocalized properties file on the fly.  Here
	 * pseudolocalization means simply prepending and appending
	 * some characters to each string.
	 * 
	 *   TODO: pseudolocalizer work
	 *   * take multiple properties files as inputs and loop on them
	 *   * integrate this in the maven build using the exec plugin
	 *   * pass prop files in from maven and autogenerate the _is localization
	 *   * remove _is localization from version control
	 */
	public static void main(String[] args) throws IOException {
	    PseudoLocalizationGenerator generator = new PseudoLocalizationGenerator();
	    generator.parseOptions(args);
	    generator.generatePseudoLocalization();
	}
	
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // doesn't seem like an issue for a small utility like this
	public void generatePseudoLocalization() throws IOException {
	    Properties defaultProps = new Properties();
		FileInputStream in = null;
		
		try {
    		in = new FileInputStream(directory + baseFileName + ".properties");
    		defaultProps.load(in);
		} finally {
		    if (in != null) {
		        in.close();
		    }
		}
		
		for (Entry<Object,Object> entry : defaultProps.entrySet()) {
			String key = (String)entry.getKey();
			String value = (String)entry.getValue();
			LOG.info(key + "=" + value);
			StringBuffer buffer = new StringBuffer(PREFIX); 
			buffer.append(value);
			buffer.append(SUFFIX);
			defaultProps.setProperty(key, buffer.toString());
		}
		for (Entry<Object,Object> entry : defaultProps.entrySet()) {
			String key = (String)entry.getKey();
			String value = (String)entry.getValue();
			LOG.info( key + "=" + value);
		}
		
		FileOutputStream out = null;
		try {
		    out = new FileOutputStream(directory + baseFileName + "_" + locale + ".properties");
		    defaultProps.store(out, "---Auto Generated Properties---");
		} finally {
		    out.close();
		}		
	}

    private void defineOptions() {
        baseFileNameOption = OptionBuilder.withArgName( "base file name" )
        .withLongOpt("baseFileName")
        .hasArg()
        .withDescription( "use given base file name to read a <baseFileName>.properties (default= " + baseFileName + ")" )
        .create( FILE_OPTION_NAME );

        localeOption = OptionBuilder.withArgName( "target locale" )
        .withLongOpt("locale")
        .hasArg()
        .withDescription( "target locale to generate (default= " + locale + ")" )
        .create( LOCALE_OPTION_NAME );

        directoryOption = OptionBuilder.withArgName( "directory" )
        .withLongOpt("directory")
        .hasArg()
        .withDescription( "the directory for reading and writing properties files (including trailing separator)" )
        .create( DIRECTORY_OPTION_NAME );

        helpOption = OptionBuilder
        .withLongOpt("help")
        .withDescription( "display help" )
        .create( HELP_OPTION_NAME );


        options.addOption(baseFileNameOption);
        options.addOption(localeOption);
        options.addOption(directoryOption);
        options.addOption(helpOption);        
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
                baseFileName = line.getOptionValue(FILE_OPTION_NAME);
            } 
            if( line.hasOption( LOCALE_OPTION_NAME ) ) {
                locale = line.getOptionValue(LOCALE_OPTION_NAME);
            }
            if( line.hasOption( DIRECTORY_OPTION_NAME ) ) {
                directory = line.getOptionValue(DIRECTORY_OPTION_NAME);
            } else {
                missingOption(directoryOption);
            }
            
        }
        catch( ParseException exp ) {
            fail( "Parsing failed.  Reason: " + exp.getMessage() );
        }
    }
	
    private static void showHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "PseudoLocalizationGenerator", options );   
    }

    private static void missingOption(Option option) {
        fail("Missing required option: " + option.getArgName());
    }

    private static void fail(String string) {
        System.err.println( string );
        System.exit(0);
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getBaseFileName() {
        return baseFileName;
    }

    public void setBaseFileName(String baseFileName) {
        this.baseFileName = baseFileName;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }	
}
