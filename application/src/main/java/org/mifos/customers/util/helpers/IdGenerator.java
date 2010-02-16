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

package org.mifos.customers.util.helpers;

/**
 * This is the class to generate global customer number for the customer. The
 * global customer number for a customer is a combination of the globalcustomer
 * number of the office to which the customer belongs,its level and the max
 * count of the number of customers under that office
 */
public class IdGenerator {

    /**
     * This method is used to generate the global customer number for a
     * particular customer. A global customer number is generated from the
     * office global customer number and the current max customerId in that
     * office. EG: if the office global customer number is BRANCH01 and max id
     * of customers in that branch is 14 then the new id generated will be
     * BRANCH01-000000015
     * 
     * @param officeGlobalNum
     *            Global customer number for the office of the customer
     * @param customerLevelId
     *            Level of the customer
     * @param maxCustomerCount
     *            Number of customers in that branch till now
     * @return The global customer number that is generated
     */
    public static String generateSystemId(String officeGlobalNum, int maxCustomerId) {
        String customerId = "";

        int numberOfZeros = 9 - String.valueOf(maxCustomerId).length();
        for (int i = 0; i < numberOfZeros; i++) {
            customerId = customerId + "0";
        }
        customerId = customerId + (maxCustomerId + 1);
        String customerGlobalNum = officeGlobalNum + "-" + customerId;
        return customerGlobalNum;
    }

    /**
     * ADDED TO TEST GLOBAL ID GENERATION This method is used to generate the
     * global customer number for a particular customer. A global customer
     * number is generated from the office global customer number and the
     * current max customerId in that office. EG: if the office global customer
     * number is BRANCH01 and max id of customers in that branch is 14 then the
     * new id generated will be BRANCH01-000000015
     * 
     * @param officeGlobalNum
     *            Global customer number for the office of the customer
     * @param customerLevelId
     *            Level of the customer
     * @param maxCustomerCount
     *            Number of customers in that branch till now
     * @return The global customer number that is generated
     */
    public static String generateSystemIdForCustomer(String officeGlobalNum, int maxCustomerId) {
        String customerId = "";

        int numberOfZeros = 9 - String.valueOf(maxCustomerId).length();
        for (int i = 0; i < numberOfZeros; i++) {
            customerId = customerId + "0";
        }
        customerId = customerId + (maxCustomerId);
        String customerGlobalNum = officeGlobalNum + "-" + customerId;
        return customerGlobalNum;
    }
}
