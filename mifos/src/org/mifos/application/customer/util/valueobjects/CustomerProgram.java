

/**

 * CustomerProgram.java    version: xxx



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

      package org.mifos.application.customer.util.valueobjects;

       import org.mifos.framework.util.valueobjects.ValueObject;

	   import org.mifos.application.program.util.valueobjects.Program;

/**
* A class that represents a row in the customer program table.
*/
       public class CustomerProgram extends ValueObject
       {

		public CustomerProgram()
		{

		}


      	  private java.lang.Short programId;


           private java.lang.Integer customerId;

           private Program program;



 /**
    * Set the program object
    * @param program
    */
           public void setProgram(Program program)
           {
			     this.program = program;
			     programId = program.getProgramId();
		   }
  /**
    * Return the program
    * @return Program
    */
           public Program getProgram()
           {
			     return program;
		   }



 /**
    * Set the customer associated to the program
    * @param customer
    */
		   public void setCustomer(Customer customer)
		   {
			  this.customerId = customer.getCustomerId();
		   }
 /**
    * Set the program
    * @param programId
    */

           public void setProgramId(java.lang.Short programId)
           {
                this.programId = programId;
           }

  /**
    * Return the program
    * @return java.lang.Short
    */
   		  public java.lang.Short getProgramId()
           {
                return programId;
           }
 /**
    * Set the customer
    * @param customerId
    */
           public void setCustomerId(java.lang.Integer customerId)
           {
                this.customerId = customerId;
           }

 /**
    * Return the customer
    * @return java.lang.Integer
    */
   		   public java.lang.Integer getCustomerId()
           {
                return customerId;
           }






       }

