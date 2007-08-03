
            /**
            * Loan.java
            *
            * This file was auto-generated from WSDL
            * by the Apache Axis2 version: #axisVersion# #today#
            */

            package org.mifos.www.services;
            /**
            *  Loan bean class
            */
        
        public  class Loan
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
           
          