

        /**
         * MifosServiceMessageReceiverInOut.java
         *
         * This file was auto-generated from WSDL
         * by the Apache Axis2 version: 1.0 May 05, 2006 (12:31:13 IST)
         */
        package org.mifos.api;

        /**
         *  MifosServiceMessageReceiverInOut message receiver
         */

        public class MifosServiceMessageReceiverInOut extends org.apache.axis2.receivers.AbstractInOutSyncMessageReceiver{


        public void invokeBusinessLogic(org.apache.axis2.context.MessageContext msgContext, org.apache.axis2.context.MessageContext newMsgContext)
        throws org.apache.axis2.AxisFault{

        try {

        // get the implementation class for the Web Service
        Object obj = getTheImplementationObject(msgContext);

        //Inject the Message Context if it is asked for
        org.apache.axis2.engine.DependencyManager.configureBusinessLogicProvider(obj, msgContext.getOperationContext());

        MifosServiceSkeletonInterface skel = (MifosServiceSkeletonInterface)obj;
        //Out Envelop
        org.apache.axiom.soap.SOAPEnvelope envelope = null;
        //Find the axisOperation that has been set by the Dispatch phase.
        org.apache.axis2.description.AxisOperation op = msgContext.getOperationContext().getAxisOperation();
        if (op == null) {
        throw new org.apache.axis2.AxisFault("Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
        }

        String methodName;
        if(op.getName() != null & (methodName = op.getName().getLocalPart()) != null){

        


            if("findLoan".equals(methodName)){


            org.mifos.www.services.FindLoanResponse param5 = null;
            
                    //doc style
                    param5 =skel.findLoan(
                            (org.mifos.www.services.FindLoanRequest)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    org.mifos.www.services.FindLoanRequest.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope())));
                        
                        envelope = toEnvelope(getSOAPFactory(msgContext), param5, false);
                      

            }
        

        newMsgContext.setEnvelope(envelope);
        }
        }
            catch (Exception e) {
              throw org.apache.axis2.AxisFault.makeFault(e);
            }
        }
         
        //
                private static javax.xml.namespace.QName[] qNameArray = {
                
                };
            
                    private  org.apache.axiom.om.OMElement  toOM(org.mifos.www.services.FindLoanRequest param, boolean optimizeContent){
                        if (param instanceof org.apache.axis2.databinding.ADBBean){
                            org.apache.axiom.om.impl.builder.StAXOMBuilder builder
                                       = new org.apache.axiom.om.impl.builder.StAXOMBuilder
                            (org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
                               new org.apache.axis2.util.StreamWrapper(param.getPullParser(org.mifos.www.services.FindLoanRequest.MY_QNAME)));
                            org.apache.axiom.om.OMElement documentElement = builder.getDocumentElement();
                            ((org.apache.axiom.om.impl.OMNodeEx) documentElement).setParent(null); // remove the parent link
                            return documentElement;
                        }else{
                           
                           //todo finish this onece the bean serializer has the necessary methods
                            return null;
                        }
                    }

                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.mifos.www.services.FindLoanRequest param, boolean optimizeContent){
                        if (param instanceof org.apache.axis2.databinding.ADBBean){
                            org.apache.axis2.databinding.ADBSOAPModelBuilder builder = new
                                    org.apache.axis2.databinding.ADBSOAPModelBuilder(param.getPullParser(org.mifos.www.services.FindLoanRequest.MY_QNAME),
                                                                                     factory);
                            return builder.getEnvelope();
                        }else{
                           
                           //todo finish this onece the bean serializer has the necessary methods
                            return null;
                        }
                    }
                
                    private  org.apache.axiom.om.OMElement  toOM(org.mifos.www.services.FindLoanResponse param, boolean optimizeContent){
                        if (param instanceof org.apache.axis2.databinding.ADBBean){
                            org.apache.axiom.om.impl.builder.StAXOMBuilder builder
                                       = new org.apache.axiom.om.impl.builder.StAXOMBuilder
                            (org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
                               new org.apache.axis2.util.StreamWrapper(param.getPullParser(org.mifos.www.services.FindLoanResponse.MY_QNAME)));
                            org.apache.axiom.om.OMElement documentElement = builder.getDocumentElement();
                            ((org.apache.axiom.om.impl.OMNodeEx) documentElement).setParent(null); // remove the parent link
                            return documentElement;
                        }else{
                           
                           //todo finish this onece the bean serializer has the necessary methods
                            return null;
                        }
                    }

                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.mifos.www.services.FindLoanResponse param, boolean optimizeContent){
                        if (param instanceof org.apache.axis2.databinding.ADBBean){
                            org.apache.axis2.databinding.ADBSOAPModelBuilder builder = new
                                    org.apache.axis2.databinding.ADBSOAPModelBuilder(param.getPullParser(org.mifos.www.services.FindLoanResponse.MY_QNAME),
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
                       
                      if (org.mifos.www.services.FindLoanRequest.class.equals(type)){
                           return org.mifos.www.services.FindLoanRequest.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                      }
                              
                      if (org.mifos.www.services.FindLoanResponse.class.equals(type)){
                           return org.mifos.www.services.FindLoanResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                      }
                              
                } catch (Exception e) {
                     throw new RuntimeException(e);
                }

                return null;
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


        }//end of class
    