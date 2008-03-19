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
