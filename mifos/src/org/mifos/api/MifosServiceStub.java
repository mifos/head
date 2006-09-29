
        /**
        * MifosServiceStub.java
        *
        * This file was auto-generated from WSDL
        * by the Apache Axis2 version: 1.0 May 05, 2006 (12:31:13 IST)
        */
        package org.mifos.api;

        

        /*
        *  MifosServiceStub java implementation
        */

        
        public class MifosServiceStub extends org.apache.axis2.client.Stub
        {
        //default axis home being null forces the system to pick up the mars from the axis2 library
        public static final java.lang.String AXIS2_HOME = null;
        protected static org.apache.axis2.description.AxisOperation[] _operations;

        //hashmaps to keep the fault mapping
        private java.util.HashMap faultExeptionNameMap = new java.util.HashMap();
        private java.util.HashMap faultExeptionClassNameMap = new java.util.HashMap();
        private java.util.HashMap faultMessageMap = new java.util.HashMap();

	
    private void populateAxisService(){

        //creating the Service
        _service = new org.apache.axis2.description.AxisService("MifosService");
	

        //creating the operations
        org.apache.axis2.description.AxisOperation __operation;
	


        _operations = new org.apache.axis2.description.AxisOperation[1];
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("", "findLoan"));

	    

            _operations[0]=__operation;
            _service.addOperation(__operation);
        
        }

    //populates the faults
    private void populateFaults(){
         


    }

     public MifosServiceStub(org.apache.axis2.context.ConfigurationContext configurationContext,
        java.lang.String targetEndpoint)
        throws java.lang.Exception {
         //To populate AxisService
         populateAxisService();
         populateFaults();
	
	
        _serviceClient = new org.apache.axis2.client.ServiceClient(configurationContext,_service);
        _serviceClient.getOptions().setTo(new org.apache.axis2.addressing.EndpointReference(
                targetEndpoint));
        
            //Set the soap version
            _serviceClient.getOptions().setSoapVersionURI(org.apache.axiom.soap.SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        
    }

    /**
     * Default Constructor
     */
    public MifosServiceStub() throws java.lang.Exception {
        
                    this("http://localhost:8080/mifos/services/MifosService" );
                
    }

    /**
     * Constructor taking the target endpoint
     */
    public MifosServiceStub(java.lang.String targetEndpoint) throws java.lang.Exception {
        this(org.apache.axis2.context.ConfigurationContextFactory.createConfigurationContextFromFileSystem(AXIS2_HOME,null),
                targetEndpoint);
    }



        
                    /**
                    * Auto generated method signature
                    * @see org.mifos.api.MifosService#findLoan
                        * @param param2
                    
                    */
                    public org.mifos.api.MifosServiceStub.FindLoanResponse findLoan(
                    org.mifos.api.MifosServiceStub.FindLoanRequest param2)
                    throws java.rmi.RemoteException
                    
                    {
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[0].getName());
              _operationClient.getOptions().setAction("urn:findLoan");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                    //Style is Doc.
                                    
                                    
                                                 env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                param2,
                                                optimizeContent(new javax.xml.namespace.QName("",
                                                "findLoan")));
                                            

        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
        _messageContext.setEnvelope(env);

        // add the message contxt to the operation client
        _operationClient.addMessageContext(_messageContext);

        //execute the operation client
        _operationClient.execute(true);

         
               org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient.getMessageContext(
                                           org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
                org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
                
                
                           java.lang.Object object = fromOM(
                                        getElement(_returnEnv,"document"),
                                        org.mifos.api.MifosServiceStub.FindLoanResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
                           _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                           return (org.mifos.api.MifosServiceStub.FindLoanResponse)object;
                    
         }catch(org.apache.axis2.AxisFault f){
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExeptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExeptionClassNameMap.get(faultElt.getQName());
                        java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        java.rmi.RemoteException ex=
                                (java.rmi.RemoteException)exceptionClass.newInstance();
                        //message class
                        java.lang.String messageClassName = (java.lang.String)faultMessageMap.get(faultElt.getQName());
                        java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        java.lang.Object messageObject = fromOM(faultElt,messageClass,null);
                        java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                   new java.lang.Class[]{messageClass});
                        m.invoke(ex,new java.lang.Object[]{messageObject});
                        

                        throw ex;
                    }catch(java.lang.ClassCastException e){
                       // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }catch (java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }  catch (java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }   catch (java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                }else{
                    throw f;
                }
            }else{
                throw f;
            }
        }
        }
            
                /**
                * Auto generated method signature for Asynchronous Invocations
                * @see org.mifos.api.MifosService#startfindLoan
                    * @param param2
                
                */
                public  void startfindLoan(
                org.mifos.api.MifosServiceStub.FindLoanRequest param2,final org.mifos.api.MifosServiceCallbackHandler callback)

                throws java.rmi.RemoteException{

              org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[0].getName());
             _operationClient.getOptions().setAction("urn:findLoan");
             _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

          

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env=null;
                    
                                    //Style is Doc.
                                    
                                                 env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), param2, optimizeContent(new javax.xml.namespace.QName("", "findLoan")));
                                            

        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
        _messageContext.setEnvelope(env);

        // add the message contxt to the operation client
        _operationClient.addMessageContext(_messageContext);


                    
                           _operationClient.setCallback(new org.apache.axis2.client.async.Callback() {
                    public void onComplete(
                            org.apache.axis2.client.async.AsyncResult result) {
                        java.lang.Object object = fromOM(getElement(
                                result.getResponseEnvelope(), "document"),
                               org.mifos.api.MifosServiceStub.FindLoanResponse.class,
                               getEnvelopeNamespaces(result.getResponseEnvelope())
                            );
                        callback.receiveResultfindLoan((org.mifos.api.MifosServiceStub.FindLoanResponse) object);
                    }

                    public void onError(java.lang.Exception e) {
                        callback.receiveErrorfindLoan(e);
                    }
                });
                        

          org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
        if ( _operations[0].getMessageReceiver()==null &&  _operationClient.getOptions().isUseSeparateListener()) {
           _callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
          _operations[0].setMessageReceiver(
                    _callbackReceiver);
        }

           //execute the operation client
           _operationClient.execute(false);

                    }
                

       /**
        *  A utility method that copies the namepaces from the SOAPEnvelope
        */
       private java.util.Map getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env){
        java.util.Map returnMap = new java.util.HashMap();
        java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
        while (namespaceIterator.hasNext()) {
            org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
            returnMap.put(ns.getPrefix(),ns.getName());
        }
       return returnMap;
    }

    
	
    private javax.xml.namespace.QName[] opNameArray = null;
	private boolean optimizeContent(javax.xml.namespace.QName opName) {
        

        if (opNameArray == null) {
			return false;
		}
		for (int i = 0; i < opNameArray.length; i++) {
			if (opName.equals(opNameArray[i])) {
				return true;   
			}
		}
		return false;
	}


     
   

    //http://localhost:8080/mifos/services/MifosService
        public static class Loan
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = Loan
                Namespace URI = http://www.mifos.org/services/
                Namespace Prefix = ns1
                */
            


            /**
            * field for Id
            */

            protected int localId ;
           
           

           /**
           * Auto generated getter method
           * @return int
           */
           public  int getId(){
               return localId;
           }

           
            
                    /**
                   * Auto generated setter method
                   * @param param Id
                   */
                   public void setId(int param){
                    
                   this.localId=param;
                   }
                


            /**
            * field for BorrowerName
            */

            protected java.lang.String localBorrowerName ;
           
           

           /**
           * Auto generated getter method
           * @return java.lang.String
           */
           public  java.lang.String getBorrowerName(){
               return localBorrowerName;
           }

           
            
                    /**
                   * Auto generated setter method
                   * @param param BorrowerName
                   */
                   public void setBorrowerName(java.lang.String param){
                    
                   this.localBorrowerName=param;
                   }
                


            /**
            * field for Balance
            */

            protected double localBalance ;
           
           

           /**
           * Auto generated getter method
           * @return double
           */
           public  double getBalance(){
               return localBalance;
           }

           
            
                    /**
                   * Auto generated setter method
                   * @param param Balance
                   */
                   public void setBalance(double param){
                    
                   this.localBalance=param;
                   }
                


            /**
            * field for BalanceCurrencyName
            */

            protected java.lang.String localBalanceCurrencyName ;
           
           

           /**
           * Auto generated getter method
           * @return java.lang.String
           */
           public  java.lang.String getBalanceCurrencyName(){
               return localBalanceCurrencyName;
           }

           
            
                    /**
                   * Auto generated setter method
                   * @param param BalanceCurrencyName
                   */
                   public void setBalanceCurrencyName(java.lang.String param){
                    
                   this.localBalanceCurrencyName=param;
                   }
                

        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName){


        
                 java.util.ArrayList elementList = new java.util.ArrayList();
                 java.util.ArrayList attribList = new java.util.ArrayList();

                
                             elementList.add(new javax.xml.namespace.QName("",
                                                                      "id"));
                            
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));
                                
                             elementList.add(new javax.xml.namespace.QName("",
                                                                      "borrowerName"));
                            
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localBorrowerName));
                                
                             elementList.add(new javax.xml.namespace.QName("",
                                                                      "balance"));
                            
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localBalance));
                                
                             elementList.add(new javax.xml.namespace.QName("",
                                                                      "balanceCurrencyName"));
                            
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localBalanceCurrencyName));
                                

                return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(), attribList.toArray());
            
            

        }



     /**
      *  Factory class that keeps the parse method
      */
    public static class Factory{


        // This is horrible, but the OM implementation of getElementText() does not obey the proper contract.  Specifically, it does
        // does not advance the reader to the END_ELEMENT.  This bug is triggered by calls to getElementText() unpredictably, e.g. it
        // happens with outer (document) elements, but not with inner elements.  The root bug is in OMStAXWrapper.java, which is now part
        // of commons and so cannot just be fixed in axis2.  This method should be removed and the calls to it below replaced with
        // simple calls to getElementText() as soon as this serious bug can be fixed.

        private static java.lang.String getElementTextProperly(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            java.lang.String value = reader.getElementText();
            while (!reader.isEndElement())
                reader.next();
            return value;
        }

        /**
        * static method to create the object
        * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
        *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
        * Postcondition: If this object is an element, the reader is positioned at its end element
        *                If this object is a complex type, the reader is positioned at the end element of its outer element
        */
        public static Loan parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            Loan object = new Loan();
            int event;
            try {
                
                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();

                
                    
                    reader.next();
                            
                                    while (!reader.isStartElement() && !reader.isEndElement())
                                        reader.next();
                                
                            if (reader.isStartElement() && new javax.xml.namespace.QName("","id").equals(reader.getName())){
                            
                                    java.lang.String content = getElementTextProperly(reader);
                                    object.setId(
                                        org.apache.axis2.databinding.utils.ConverterUtil.convertToint(content));
                                      
                                        reader.next();
                                    

                              }  // End of if for expected property start element

                            
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                                }
                                        
                                    while (!reader.isStartElement() && !reader.isEndElement())
                                        reader.next();
                                
                            if (reader.isStartElement() && new javax.xml.namespace.QName("","borrowerName").equals(reader.getName())){
                            
                                    java.lang.String content = getElementTextProperly(reader);
                                    object.setBorrowerName(
                                        org.apache.axis2.databinding.utils.ConverterUtil.convertTostring(content));
                                      
                                        reader.next();
                                    

                              }  // End of if for expected property start element

                            
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                                }
                                        
                                    while (!reader.isStartElement() && !reader.isEndElement())
                                        reader.next();
                                
                            if (reader.isStartElement() && new javax.xml.namespace.QName("","balance").equals(reader.getName())){
                            
                                    java.lang.String content = getElementTextProperly(reader);
                                    object.setBalance(
                                        org.apache.axis2.databinding.utils.ConverterUtil.convertTodouble(content));
                                      
                                        reader.next();
                                    

                              }  // End of if for expected property start element

                            
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                                }
                                        
                                    while (!reader.isStartElement() && !reader.isEndElement())
                                        reader.next();
                                
                            if (reader.isStartElement() && new javax.xml.namespace.QName("","balanceCurrencyName").equals(reader.getName())){
                            
                                    java.lang.String content = getElementTextProperly(reader);
                                    object.setBalanceCurrencyName(
                                        org.apache.axis2.databinding.utils.ConverterUtil.convertTostring(content));
                                      
                                        reader.next();
                                    

                              }  // End of if for expected property start element

                            
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                                }
                            


            } catch (javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }

            return object;
        }

        }//end of factory class

        

        }
           
          
        public static class FindLoanResponse
        implements org.apache.axis2.databinding.ADBBean{
        
                public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
                "http://www.mifos.org/services/",
                "findLoanResponse",
                "ns1");

            


            /**
            * field for Loan
            */

            protected Loan localLoan ;
           
           

           /**
           * Auto generated getter method
           * @return Loan
           */
           public  Loan getLoan(){
               return localLoan;
           }

           
            
                    /**
                   * Auto generated setter method
                   * @param param Loan
                   */
                   public void setLoan(Loan param){
                    
                   this.localLoan=param;
                   }
                

        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName){


        
                 java.util.ArrayList elementList = new java.util.ArrayList();
                 java.util.ArrayList attribList = new java.util.ArrayList();

                
                            elementList.add(new javax.xml.namespace.QName("",
                                                                      "Loan"));
                            
                            
                                    if (localLoan==null){
                                         throw new RuntimeException("Loan cannot be null!!");
                                    }
                                    elementList.add(localLoan);
                                

                return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(), attribList.toArray());
            
            

        }



     /**
      *  Factory class that keeps the parse method
      */
    public static class Factory{


        // This is horrible, but the OM implementation of getElementText() does not obey the proper contract.  Specifically, it does
        // does not advance the reader to the END_ELEMENT.  This bug is triggered by calls to getElementText() unpredictably, e.g. it
        // happens with outer (document) elements, but not with inner elements.  The root bug is in OMStAXWrapper.java, which is now part
        // of commons and so cannot just be fixed in axis2.  This method should be removed and the calls to it below replaced with
        // simple calls to getElementText() as soon as this serious bug can be fixed.

        private static java.lang.String getElementTextProperly(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            java.lang.String value = reader.getElementText();
            while (!reader.isEndElement())
                reader.next();
            return value;
        }

        /**
        * static method to create the object
        * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
        *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
        * Postcondition: If this object is an element, the reader is positioned at its end element
        *                If this object is a complex type, the reader is positioned at the end element of its outer element
        */
        public static FindLoanResponse parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            FindLoanResponse object = new FindLoanResponse();
            int event;
            try {
                
                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();

                
                    
                    reader.next();
                            
                                    while (!reader.isStartElement() && !reader.isEndElement())
                                        reader.next();
                                
                            if (reader.isStartElement() && new javax.xml.namespace.QName("","Loan").equals(reader.getName())){
                            
                                    object.setLoan(Loan.Factory.parse(reader));
                                      
                                        reader.next();
                                    

                              }  // End of if for expected property start element

                            
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                                }
                            


            } catch (javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }

            return object;
        }

        }//end of factory class

        

        }
           
          
        public static class FindLoanRequest
        implements org.apache.axis2.databinding.ADBBean{
        
                public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
                "http://www.mifos.org/services/",
                "findLoanRequest",
                "ns1");

            


            /**
            * field for Id
            */

            protected int localId ;
           
           

           /**
           * Auto generated getter method
           * @return int
           */
           public  int getId(){
               return localId;
           }

           
            
                    /**
                   * Auto generated setter method
                   * @param param Id
                   */
                   public void setId(int param){
                    
                   this.localId=param;
                   }
                

        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName){


        
                 java.util.ArrayList elementList = new java.util.ArrayList();
                 java.util.ArrayList attribList = new java.util.ArrayList();

                
                             elementList.add(new javax.xml.namespace.QName("",
                                                                      "id"));
                            
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));
                                

                return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(), attribList.toArray());
            
            

        }



     /**
      *  Factory class that keeps the parse method
      */
    public static class Factory{


        // This is horrible, but the OM implementation of getElementText() does not obey the proper contract.  Specifically, it does
        // does not advance the reader to the END_ELEMENT.  This bug is triggered by calls to getElementText() unpredictably, e.g. it
        // happens with outer (document) elements, but not with inner elements.  The root bug is in OMStAXWrapper.java, which is now part
        // of commons and so cannot just be fixed in axis2.  This method should be removed and the calls to it below replaced with
        // simple calls to getElementText() as soon as this serious bug can be fixed.

        private static java.lang.String getElementTextProperly(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            java.lang.String value = reader.getElementText();
            while (!reader.isEndElement())
                reader.next();
            return value;
        }

        /**
        * static method to create the object
        * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
        *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
        * Postcondition: If this object is an element, the reader is positioned at its end element
        *                If this object is a complex type, the reader is positioned at the end element of its outer element
        */
        public static FindLoanRequest parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            FindLoanRequest object = new FindLoanRequest();
            int event;
            try {
                
                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();

                
                    
                    reader.next();
                            
                                    while (!reader.isStartElement() && !reader.isEndElement())
                                        reader.next();
                                
                            if (reader.isStartElement() && new javax.xml.namespace.QName("","id").equals(reader.getName())){
                            
                                    java.lang.String content = getElementTextProperly(reader);
                                    object.setId(
                                        org.apache.axis2.databinding.utils.ConverterUtil.convertToint(content));
                                      
                                        reader.next();
                                    

                              }  // End of if for expected property start element

                            
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                                }
                            


            } catch (javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }

            return object;
        }

        }//end of factory class

        

        }
           
          
                private static javax.xml.namespace.QName[] qNameArray = {
                
                };
            
                    private  org.apache.axiom.om.OMElement  toOM(org.mifos.api.MifosServiceStub.FindLoanRequest param, boolean optimizeContent){
                        if (param instanceof org.apache.axis2.databinding.ADBBean){
                            org.apache.axiom.om.impl.builder.StAXOMBuilder builder
                                       = new org.apache.axiom.om.impl.builder.StAXOMBuilder
                            (org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
                               new org.apache.axis2.util.StreamWrapper(param.getPullParser(org.mifos.api.MifosServiceStub.FindLoanRequest.MY_QNAME)));
                            org.apache.axiom.om.OMElement documentElement = builder.getDocumentElement();
                            ((org.apache.axiom.om.impl.OMNodeEx) documentElement).setParent(null); // remove the parent link
                            return documentElement;
                        }else{
                           
                           //todo finish this onece the bean serializer has the necessary methods
                            return null;
                        }
                    }

                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.mifos.api.MifosServiceStub.FindLoanRequest param, boolean optimizeContent){
                        if (param instanceof org.apache.axis2.databinding.ADBBean){
                            org.apache.axis2.databinding.ADBSOAPModelBuilder builder = new
                                    org.apache.axis2.databinding.ADBSOAPModelBuilder(param.getPullParser(org.mifos.api.MifosServiceStub.FindLoanRequest.MY_QNAME),
                                                                                     factory);
                            return builder.getEnvelope();
                        }else{
                           
                           //todo finish this onece the bean serializer has the necessary methods
                            return null;
                        }
                    }
                
                    private  org.apache.axiom.om.OMElement  toOM(org.mifos.api.MifosServiceStub.FindLoanResponse param, boolean optimizeContent){
                        if (param instanceof org.apache.axis2.databinding.ADBBean){
                            org.apache.axiom.om.impl.builder.StAXOMBuilder builder
                                       = new org.apache.axiom.om.impl.builder.StAXOMBuilder
                            (org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
                               new org.apache.axis2.util.StreamWrapper(param.getPullParser(org.mifos.api.MifosServiceStub.FindLoanResponse.MY_QNAME)));
                            org.apache.axiom.om.OMElement documentElement = builder.getDocumentElement();
                            ((org.apache.axiom.om.impl.OMNodeEx) documentElement).setParent(null); // remove the parent link
                            return documentElement;
                        }else{
                           
                           //todo finish this onece the bean serializer has the necessary methods
                            return null;
                        }
                    }

                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.mifos.api.MifosServiceStub.FindLoanResponse param, boolean optimizeContent){
                        if (param instanceof org.apache.axis2.databinding.ADBBean){
                            org.apache.axis2.databinding.ADBSOAPModelBuilder builder = new
                                    org.apache.axis2.databinding.ADBSOAPModelBuilder(param.getPullParser(org.mifos.api.MifosServiceStub.FindLoanResponse.MY_QNAME),
                                                                                     factory);
                            return builder.getEnvelope();
                        }else{
                           
                           //todo finish this onece the bean serializer has the necessary methods
                            return null;
                        }
                    }
                

           /**
           *  get the default envelope
           */
           private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory){
                return factory.getDefaultEnvelope();
           }


            private  java.lang.Object fromOM(
            org.apache.axiom.om.OMElement param,
            java.lang.Class type,
            java.util.Map extraNamespaces){

                try {
                       
                      if (org.mifos.api.MifosServiceStub.FindLoanRequest.class.equals(type)){
                           return org.mifos.api.MifosServiceStub.FindLoanRequest.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                      }
                              
                      if (org.mifos.api.MifosServiceStub.FindLoanResponse.class.equals(type)){
                           return org.mifos.api.MifosServiceStub.FindLoanResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                      }
                              
                } catch (Exception e) {
                     throw new RuntimeException(e);
                }

                return null;
            }

        
   }
   