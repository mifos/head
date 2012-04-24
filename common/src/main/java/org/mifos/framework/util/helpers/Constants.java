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

package org.mifos.framework.util.helpers;

/**
 * Selected constants. This class is deprecated; constants should instead be in
 * a more specific place.
 */
@SuppressWarnings("PMD")
public interface Constants {
    String DELEGATOR = "Delegator";
    String VALUEOBJECT = "ValueObject";
    String BUSINESSPROCESSOR = "BusinessProcessor";
    String CONTEXT = "Context";
    String MIFOSLOCALE = "Mifos_Locale";
    String PREVIOUS_REQUEST = "Previous_Request";
    String PATH = "path";
    String CREATE_SUCCESS = "create_success";
    String UPDATE_SUCCESS = "update_success";
    String UPDATE_FAILURE = "update_failure";
    String NEXT_SUCCESS = "next_success";
    String PREVIEW_SUCCESS = "preview_success";
    String SEARCH_SUCCESS = "search_success";
    String GET_SUCCESS = "get_success";
    String LOAD_SUCCESS = "load_success";
    String PREVIOUS_SUCCESS = "previous_success";
    String CANCEL_SUCCESS = "cancel_success";
    String MANAGE_SUCCESS = "manage_success";
    String DELETE_SUCCESS = "delete_success";
    String BUSINESSPROCESSORIMPLEMENTATION = "BusinessProcessorImplementation";
    String SKIPVALIDATION = "skipValidation";
    String MANAGE_PREVIOUS = "manage_previous";
    String MANAGE_PREVIEW = "manage_preview";
    String STORE_ATTRIBUTE = "store_attribute";
    String FAILURE = "failure";
    String MASTERINFO = "masterinfo";

    String USER_ID = "user_id";
    String BRANCH_ID = "branch_id";
    String OFFICE_NAME = "office_name";
    String SEARCH_NAME = "search_name";
    String SEARCH_RESULTS = "search_name";

    String SEARCH_STRING = "search_string";

    String ALGORITHM = "Algorithm";
    String CREATE = "create";
    String UPDATE = "update";
    String DELETE = "delete";
    short ACTIVE = 1;
    short LOCKED = 1;
    short PASSWORDCHANGED = 1;
    short LOANOFFICER = 1;
    String ROLECHANGEEVENT = "RoleChange";
    String ACTIVITYCHANGEEVENT = "ActivityChange";
    String KEY = "123456789123456789123456";
    // User Context for storing in session
    String USERCONTEXT = "UserContext";
    String TEMPUSERCONTEXT = "Temp_UserContext";
    String SELECTTAG = "select";

    short NO = 0;
    short YES = 1;
    // --------------------added for new framework
    String USER_CONTEXT_KEY = "UserContext";
    String ACTIVITYCONTEXT = "ActivityContext";
    String BUSINESS_KEY = "BusinessKey";
    String VIEW_DATE = "ViewDate";

    String ACCOUNT_TYPE = "AccountType";
    String ACCOUNT_VERSION = "AccountVersion";
    String ACCOUNT_ID = "AccountId";

    String FIELD_CONFIGURATION = "FieldConfiguration";

    String CURRENTFLOWKEY = "currentFlowKey";
    String FLOWMANAGER = "flowManager";

    String ACTION_MAPPING = "actionMapping";

    /**
     * There is a large amount of code which generates randomNum, writes it to
     * the session, and puts it in URLs. Mifos does not actually consult this
     * value - it is to turn off caching in the browser (why not just use the
     * various "don't cache" headers?) See
     * http://wiki.java.net/bin/view/Javatools/BackButton
     *
     * A related machanism is {@link TransactionDemarcate}.
     */
    String RANDOMNUM = "randomNUm";

    String INPUT = "input";
    String ERROR_VERSION_MISMATCH = "error.versionnodonotmatch";
    String LOAN = "loan";
    String EMPTY_STRING = "";
    String ORIGINAL_SCHEDULE_AVAILABLE = "originalScheduleIsAvailable";
    
    String ADJUSTMENT_PAYMENT_TYPE = "adjustmentPaymentType";
    String ADJUSTMENT_IS_REVERT = "isRevert";
    
    String POSSIBLE_ADJUSTMENTS = "possibleAdjustments";
    String ADJUSTED_AMOUNT = "adjAmount";
    
    String ADJ_TYPE_KEY = "adjustmentType";
    String ADJ_SPECIFIC = "adjustSpec";

    String ACCOUNTS_FOR_TRANSFER = "accountsForTransfer";
}
