/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.platform.rest.ui.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.LocalDate;
import org.mifos.application.servicefacade.CollectionSheetDto;
import org.mifos.application.servicefacade.CollectionSheetErrorsDto;
import org.mifos.application.servicefacade.CollectionSheetServiceFacade;
import org.mifos.application.servicefacade.InvalidSaveCollectionSheetReason;
import org.mifos.application.servicefacade.SaveCollectionSheetCustomerAccountDto;
import org.mifos.application.servicefacade.SaveCollectionSheetCustomerDto;
import org.mifos.application.servicefacade.SaveCollectionSheetCustomerLoanDto;
import org.mifos.application.servicefacade.SaveCollectionSheetCustomerSavingDto;
import org.mifos.application.servicefacade.SaveCollectionSheetDto;
import org.mifos.application.servicefacade.SaveCollectionSheetException;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.persistence.CustomerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.codehaus.jackson.JsonParser;

@Controller
public class CollectionSheetRESTController {

    @Autowired
    CollectionSheetServiceFacade collectionSheetServiceFacade;

    @Autowired
    CustomerDao customerDao;

    @RequestMapping(value = "/collectionsheet/customer/id-{customerId}", method = RequestMethod.GET)
    public @ResponseBody
    CollectionSheetDto getCollectionSheet(@PathVariable Integer customerId) {
        LocalDate meetingDate = new LocalDate(customerDao.getLastMeetingDateForCustomer(customerId).getTime());
        return collectionSheetServiceFacade.getCollectionSheet(customerId, meetingDate);
    }

    @RequestMapping(value = "/collectionsheet/save", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> saveCollectionSheet(@RequestBody JSONSaveCollectionsheet request) throws Throwable {
        Map<String, Object> map = new HashMap<String, Object>();
        ObjectMapper om = createObjectMapper();
        List<InvalidSaveCollectionSheetReason> reasons = new ArrayList<InvalidSaveCollectionSheetReason>();
        CollectionSheetErrorsDto errors = null;
        SaveCollectionSheetDto saveCollectionSheetDto = null;
        try {
            saveCollectionSheetDto = om.readValue(request.getJson(), SaveCollectionSheetDto.class);
        } catch (JsonMappingException e) {
            if (e.getCause() instanceof SaveCollectionSheetException) {
                reasons.addAll(((SaveCollectionSheetException) e.getCause()).getInvalidSaveCollectionSheetReasons());
            } else {
                throw e.getCause();
            }
        }
        if (saveCollectionSheetDto != null) {
            try {
                errors = collectionSheetServiceFacade.saveCollectionSheet(saveCollectionSheetDto);
                map.put("errors", errors != null ? errors.getErrorText() : null);
            } catch (MifosRuntimeException e) {
                map.put("errors", e.getMessage());
            }
        }
        map.put("invalidCollectionSheet", reasons);
        return map;
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.getDeserializationConfig().addMixInAnnotations(SaveCollectionSheetDto.class, SaveCollectionSheetDtoMixIn.class);
        om.getDeserializationConfig().addMixInAnnotations(SaveCollectionSheetCustomerDto.class, SaveCollectionSheetCustomerDtoMixIn.class);
        om.getDeserializationConfig().addMixInAnnotations(SaveCollectionSheetCustomerAccountDto.class, SaveCollectionSheetCustomerAccountDtoMixIn.class);
        om.getDeserializationConfig().addMixInAnnotations(SaveCollectionSheetCustomerLoanDto.class, SaveCollectionSheetCustomerLoanDtoMixIn.class);
        om.getDeserializationConfig().addMixInAnnotations(SaveCollectionSheetCustomerSavingDto.class, SaveCollectionSheetCustomerSavingDtoMixIn.class);
        om.getDeserializationConfig().set(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        om.getJsonFactory().configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        return om;
    }

    public static abstract class SaveCollectionSheetDtoMixIn {

        @JsonCreator
        public SaveCollectionSheetDtoMixIn(
                @JsonProperty("saveCollectionSheetCustomers") List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers,
                @JsonProperty("paymentType") Short paymentType,
                @JsonProperty("transactionDate") LocalDate transactionDate,
                @JsonProperty("receiptId") String receiptId,
                @JsonProperty("receiptDate") LocalDate receiptDate,
                @JsonProperty("userId") Short userId)
                throws SaveCollectionSheetException {}

    }

    public static abstract class SaveCollectionSheetCustomerDtoMixIn {

        @JsonCreator
        public SaveCollectionSheetCustomerDtoMixIn(
                @JsonProperty("customerId") Integer customerId,
                @JsonProperty("parentCustomerId") Integer parentCustomerId,
                @JsonProperty("attendanceId") Short attendanceId,
                @JsonProperty("saveCollectionSheetCustomerAccount") SaveCollectionSheetCustomerAccountDto saveCollectionSheetCustomerAccount,
                @JsonProperty("saveCollectionSheetCustomerLoans") List<SaveCollectionSheetCustomerLoanDto> saveCollectionSheetCustomerLoans,
                @JsonProperty("saveCollectionSheetCustomerSavings") List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerSavings,
                @JsonProperty("saveCollectionSheetCustomerIndividualSavings") List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerIndividualSavings)
                throws SaveCollectionSheetException {}

    }

    public static abstract class SaveCollectionSheetCustomerAccountDtoMixIn {

        @JsonCreator
        public SaveCollectionSheetCustomerAccountDtoMixIn(
                @JsonProperty("accountId") Integer accountId,
                @JsonProperty("currencyId") Short currencyId,
                @JsonProperty("totalCustomerAccountCollectionFee") BigDecimal totalCustomerAccountCollectionFee) throws SaveCollectionSheetException{}
    }

    public static abstract class SaveCollectionSheetCustomerLoanDtoMixIn {

        @JsonCreator
        public SaveCollectionSheetCustomerLoanDtoMixIn(
                @JsonProperty("accountId") Integer accountId,
                @JsonProperty("currencyId") Short currencyId,
                @JsonProperty("totalLoanPayment") BigDecimal totalLoanPayment,
                @JsonProperty("totalDisbursement") BigDecimal totalDisbursement) throws SaveCollectionSheetException {}
    }

    public static abstract class SaveCollectionSheetCustomerSavingDtoMixIn {

        @JsonCreator
        public SaveCollectionSheetCustomerSavingDtoMixIn(
                @JsonProperty("accountId") Integer accountId,
                @JsonProperty("currencyId") Short currencyId,
                @JsonProperty("totalDeposit") BigDecimal totalDeposit,
                @JsonProperty("totalWithdrawal") BigDecimal totalWithdrawal) throws SaveCollectionSheetException {}
    }

    public static class JSONSaveCollectionsheet {

        private String json;

        public String getJson() {
            return json;
        }

        public void setJson(String json) {
            this.json = json;
        }
    }
}
