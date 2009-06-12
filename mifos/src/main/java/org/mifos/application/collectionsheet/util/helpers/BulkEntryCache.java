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

package org.mifos.application.collectionsheet.util.helpers;

import java.util.HashMap;
import java.util.Map;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientAttendanceBO;
import org.mifos.application.personnel.business.PersonnelBO;

public class BulkEntryCache {

    private Map<Integer, AccountBO> accounts = new HashMap<Integer, AccountBO>();

    private Map<Integer, CustomerBO> customers = new HashMap<Integer, CustomerBO>();

    private Map<Short, PersonnelBO> personnels = new HashMap<Short, PersonnelBO>();

    private Map<Integer, ClientAttendanceBO> clientAttendances = new HashMap<Integer, ClientAttendanceBO>();

    public boolean isPersonnelPresent(Short personnelId) {
        return personnels.containsKey(personnelId);
    }

    public boolean isAccountPresent(Integer accountId) {
        return accounts.containsKey(accountId);
    }

    public boolean isCustomerPresent(Integer customerId) {
        return customers.containsKey(customerId);
    }

    public boolean isClientAttendancePresent(Integer customerId) {
        return clientAttendances.containsKey(customerId);
    }

    public PersonnelBO getPersonnel(Short personnelId) {
        return personnels.get(personnelId);
    }

    public AccountBO getAccount(Integer accountId) {
        return accounts.get(accountId);
    }

    public CustomerBO getCustomer(Integer customerId) {
        return customers.get(customerId);
    }

    public ClientAttendanceBO getClientAttendance(Integer customerId) {
        return clientAttendances.get(customerId);
    }

    public void addPersonnel(Short personnelId, PersonnelBO personnel) {
        personnels.put(personnelId, personnel);
    }

    public void addAccount(Integer accountId, AccountBO account) {
        accounts.put(accountId, account);
    }

    public void addCustomer(Integer customerId, CustomerBO customer) {
        customers.put(customerId, customer);
    }

    public void addClientAttendance(Integer customerId, ClientAttendanceBO clientAttendance) {
        clientAttendances.put(customerId, clientAttendance);
    }

}
