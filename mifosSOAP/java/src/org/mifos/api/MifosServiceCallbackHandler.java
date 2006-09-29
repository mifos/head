
    /**
     * MifosServiceCallbackHandler.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: 1.0 May 05, 2006 (12:31:13 IST)
     */
    package org.mifos.api;

    /**
     *  MifosServiceCallbackHandler Callback class 
     */

    public abstract class MifosServiceCallbackHandler{



	private Object clientData;


	/**
	* User can pass in any object that needs to be accessed once the NonBlocking 
	* Web service call is finished and appropreate method of this CallBack is called.
	* @param clientData Object mechanism by which the user can pass in user data
	* that will be avilable at the time this callback is called.
	*/
	public MifosServiceCallbackHandler(Object clientData){
		this.clientData = clientData;
	}


        
           /**
            * auto generated Axis2 call back method for findLoan method
            *
            */
           public void receiveResultfindLoan(
                    org.mifos.api.MifosServiceStub.FindLoanResponse param1) {
			        //Fill here with the code to handle the response
           }

          /**
           * auto generated Axis2 Error handler
           *
           */
            public void receiveErrorfindLoan(java.lang.Exception e) {
                //Fill here with the code to handle the exception
            }
                


    }
    