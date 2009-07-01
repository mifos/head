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

package org.mifos.application.branchreport.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.mifos.application.branchreport.BranchReportClientSummaryBO;

public class BranchReportClientSummaryBatchBOExtractor {

    private List<BranchReportClientSummaryBO> clientSummaries;

    public BranchReportClientSummaryBatchBOExtractor(List<BranchReportClientSummaryBO> clientSummaries) {
        this.clientSummaries = clientSummaries;
    }

    public BranchReportClientSummaryBatchBOExtractor() {
        this(new ArrayList<BranchReportClientSummaryBO>());
    }

    private BranchReportClientSummaryBO getClientSummary(Predicate predicate) {
        return (BranchReportClientSummaryBO) CollectionUtils.find(clientSummaries, predicate);
    }

    public BranchReportClientSummaryBO getCenterSummary() {
        return getClientSummary(ClientSummaryPredicates.CENTER_COUNT_MATCHER);
    }

    public BranchReportClientSummaryBO getGroupSummary() {
        return getClientSummary(ClientSummaryPredicates.GROUP_COUNT_MATCHER);
    }

    public BranchReportClientSummaryBO getActiveMembersSummary() {
        return getClientSummary(ClientSummaryPredicates.ACTIVE_MEMBER_MATCHER);
    }

    public BranchReportClientSummaryBO getActiveBorrowersSummary() {
        return getClientSummary(ClientSummaryPredicates.ACTIVE_BORROWERS_MATCHER);
    }

    public BranchReportClientSummaryBO getReplacementsSummary() {
        return getClientSummary(ClientSummaryPredicates.REPLACEMENTS_MATCHER);
    }

    public BranchReportClientSummaryBO getLoanAccountDormantsSummary() {
        return getClientSummary(ClientSummaryPredicates.LOAN_ACCOUNT_DORMANT_COUNT_MATCHER);
    }

    public BranchReportClientSummaryBO getSavingAccountDormantsSummary() {
        return getClientSummary(ClientSummaryPredicates.SAVING_ACCOUNT_DORMANT_COUNT_MATCHER);
    }

    public BranchReportClientSummaryBO getDropOutsSummary() {
        return getClientSummary(ClientSummaryPredicates.DROP_OUT_MATCHER);
    }

    public BranchReportClientSummaryBO getDropOutRateSummary() {
        return getClientSummary(ClientSummaryPredicates.DROP_OUT_RATE_MATCHER);
    }

    public BranchReportClientSummaryBO getOnHoldSummary() {
        return getClientSummary(ClientSummaryPredicates.ON_HOLD_MATCHER);
    }

    public BranchReportClientSummaryBO getActiveSaversSummary() {
        return getClientSummary(ClientSummaryPredicates.ACTIVE_SAVER_MATCHER);
    }

    public BranchReportClientSummaryBO getPortfolioAtRiskSummary() {
        return getClientSummary(ClientSummaryPredicates.PORTFLIO_AT_RISK_MATCHER);
    }

    public Predicate matchAllPredicates(Set<BranchReportClientSummaryBO> branchReportClientSummaries) {
        for (Predicate predicate : ClientSummaryPredicates.values()) {
            if (!CollectionUtils.exists(branchReportClientSummaries, predicate))
                return predicate;
        }
        return null;
    }

    private static enum ClientSummaryPredicates implements Predicate {

        DROP_OUT_RATE_MATCHER("DROP_OUT_RATE_MATCHER", new Predicate() {
            public boolean evaluate(Object object) {
                return ((BranchReportClientSummaryBO) object).isOfTypeDropoutRate();
            }
        }),

        PORTFLIO_AT_RISK_MATCHER("PORTFLIO_AT_RISK_MATCHER", new Predicate() {
            public boolean evaluate(Object object) {
                return ((BranchReportClientSummaryBO) object).isOfTypePortfolioAtRisk();
            }
        }),

        CENTER_COUNT_MATCHER("CENTER_COUNT_MATCHER", new Predicate() {
            public boolean evaluate(Object arg0) {
                return ((BranchReportClientSummaryBO) arg0).isOfTypeCenterCount();
            }
        }),

        GROUP_COUNT_MATCHER("GROUP_COUNT_MATCHER", new Predicate() {
            public boolean evaluate(Object arg0) {
                return ((BranchReportClientSummaryBO) arg0).isOfTypeGroupCount();
            }
        }),

        ACTIVE_MEMBER_MATCHER("ACTIVE_MEMBER_MATCHER", new Predicate() {
            public boolean evaluate(Object arg0) {
                return ((BranchReportClientSummaryBO) arg0).isOfTypeActiveMembers();
            }
        }),

        ACTIVE_BORROWERS_MATCHER("ACTIVE_BORROWERS_MATCHER", new Predicate() {
            public boolean evaluate(Object arg0) {
                return ((BranchReportClientSummaryBO) arg0).isOfTypeActiveBorrowers();
            }
        }),

        REPLACEMENTS_MATCHER("REPLACEMENTS_MATCHER", new Predicate() {
            public boolean evaluate(Object arg0) {
                return ((BranchReportClientSummaryBO) arg0).isOfTypeReplacements();
            }
        }),

        SAVING_ACCOUNT_DORMANT_COUNT_MATCHER("SAVING_ACCOUNT_DORMANT_COUNT_MATCHER", new Predicate() {
            public boolean evaluate(Object arg0) {
                return ((BranchReportClientSummaryBO) arg0).isOfTypeSavingAccountDormantCount();
            }
        }),

        LOAN_ACCOUNT_DORMANT_COUNT_MATCHER("LOAN_ACCOUNT_DORMANT_COUNT_MATCHER", new Predicate() {
            public boolean evaluate(Object arg0) {
                return ((BranchReportClientSummaryBO) arg0).isOfTypeLoanAccountDormantCount();
            }
        }),

        DROP_OUT_MATCHER("DROP_OUT_MATCHER", new Predicate() {
            public boolean evaluate(Object arg0) {
                return ((BranchReportClientSummaryBO) arg0).isOfTypeDropOuts();
            }
        }),

        ON_HOLD_MATCHER("ON_HOLD_MATCHER", new Predicate() {
            public boolean evaluate(Object arg0) {
                return ((BranchReportClientSummaryBO) arg0).isOfTypeOnHolds();
            }
        }),

        ACTIVE_SAVER_MATCHER("ACTIVE_SAVER_MATCHER", new Predicate() {
            public boolean evaluate(Object arg0) {
                return ((BranchReportClientSummaryBO) arg0).isOfTypeActiveSavers();
            }
        });

        private final String predicateName;
        private final Predicate predicate;

        private ClientSummaryPredicates(String predicateName, Predicate predicate) {
            this.predicateName = predicateName;
            this.predicate = predicate;
        }

        @Override
        public String toString() {
            return predicateName;
        }

        public boolean evaluate(Object arg0) {
            return predicate.evaluate(arg0);
        }
    }
}
