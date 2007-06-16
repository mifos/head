package org.mifos.application.meeting.util.helpers;

public enum WeekDay {
	SUNDAY((short) 1), 
	MONDAY((short) 2), 
	TUESDAY((short) 3), 
	WEDNESDAY((short) 4), 
	THURSDAY((short) 5), 
	FRIDAY((short) 6), 
	SATURDAY((short) 7);

	Short value;

	WeekDay(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

	public static WeekDay getWeekDay(int value){
		for (WeekDay weekday : WeekDay.values()) {
			if (weekday.value == value) {
				return weekday;
			}
		}
		throw new RuntimeException("no week day " + value);
	}

	public WeekDay next() {
		if (this == SATURDAY) {
			return SUNDAY;
		}
		return getWeekDay(value + 1);
	}

	public String getPropertiesKey() {
		return "WeekDay." + toString();
	}
}
