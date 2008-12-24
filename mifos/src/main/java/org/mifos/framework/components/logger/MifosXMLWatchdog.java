/** 
 * MifosXMLWatchdog.java version:1.0
 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.
 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 
 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 
 * and how it is applied. 
 *
 */
package org.mifos.framework.components.logger;


import org.apache.log4j.LogManager;
import org.apache.log4j.helpers.FileWatchdog;

/**	
 *  This class inherits from the FileWatchdog of log4j and is used to monitor the log4j configuration file for any changes.
 */ 

public class MifosXMLWatchdog extends FileWatchdog {
	
	/**
	   * Constructor: 
	   * It simply calls the super to initialise the thread with the file name thai it has to poll.
	   * @param filename: The file to be polled
	   */
	  
	  protected MifosXMLWatchdog(String filename) {
		 super(filename);
		
		
	  }//end-constructor
	/**
	 * This method is overridden so that whenever there is a change in the XML file 
	 * (which is used to configure the Log4j logger), it creates an object of MifosDomConfigurator 
	 * and calls the doConfigure on it.
	 */
	  
	  @Override
	protected void doOnChange() {
		  new MifosDOMConfigurator().doConfigure(filename,LogManager.getLoggerRepository());
	  }//end-method doOnChange
	
	  
}//~
