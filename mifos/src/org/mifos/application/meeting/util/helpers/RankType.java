package org.mifos.application.meeting.util.helpers;

public enum RankType {

	FIRST((short) 1), SECOND((short) 2), THIRD((short) 3), FOURTH((short) 4), LAST((short) 5);

	Short value;

	RankType(Short value) {
		this.value = value;
	}
	public Short getValue() {
		return value;
	}
	
	public static RankType getRankType(Short value){
		for (RankType rank : RankType.values()) 
			if (rank.getValue().equals(value))
				return rank;
		return null;
	}
}
