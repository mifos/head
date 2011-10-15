/*
 *  Copyright 2010 artur.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.mifos.customers.business.service;

import java.util.List;

import org.mifos.accounts.api.CustomerSearchService;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.dto.domain.CustomerDto;

public class CustomerSearchServiceImpl implements CustomerSearchService {

    private CustomerDao customerDao;

    public CustomerSearchServiceImpl(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public List<CustomerDto> findCustomersWithGivenPhoneNumber(String phoneNumber) {
        return customerDao.findCustomersWithGivenPhoneNumber(phoneNumber);
    }

}
