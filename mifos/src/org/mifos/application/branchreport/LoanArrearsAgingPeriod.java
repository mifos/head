/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
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
package org.mifos.application.branchreport;

import java.io.Serializable;

public enum LoanArrearsAgingPeriod implements Serializable {
	ONE_WEEK("1", 1, 7),
	TWO_WEEK("2", 8, 14),
	THREE_WEEK("3", 15, 21),
	FOUR_WEEK("4", 22, 28),
	FIVE_TO_EIGHT_WEEK("5-8", 29, 56),
	NINE_TO_SIXTEEN_WEEK("9-16", 57, 112),
	SEVENTEEN_TO_THIRTY_TWO_WEEk("17-32", 113, 224),
	THIRTY_TWO_AND_MORE_WEEK("32+", 225, Integer.MAX_VALUE);

	private final Integer minDays;
	private final Integer maxDays;
	private final String description;

	private LoanArrearsAgingPeriod(String description, Integer minDays, Integer maxDays) {
		this.description = description;
		this.minDays = minDays;
		this.maxDays = maxDays;
	}

	public Integer getMaxDays() {
		return maxDays;
	}

	public Integer getMinDays() {
		return minDays;
	}

	public Integer getNotLessThanDays() {
		return Integer.valueOf(maxDays.intValue()+1);
	}

	public String getDescription() {
		return description;
	}
}
