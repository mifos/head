package org.mifos.application.ppi.helpers;

import java.lang.reflect.Method;

import org.mifos.application.ppi.business.PPISurvey;

public class PovertyBand {
	
	private int points;
	
	private PovertyBandEnum enumBand;

	public enum PovertyBandEnum {
		VERY_POOR("VeryPoor"),
		POOR("Poor"),
		AT_RISK("AtRisk"),
		NON_POOR("NonPoor");
	
		private String band;
	
		private PovertyBandEnum(String band) {
			this.band = band;
		}
		
		public String getValue() {
			return band;
		}
		
		public static PovertyBandEnum fromString(String band) {
			for (PovertyBandEnum candidate : PovertyBandEnum.values()) {
				if (band.toLowerCase().equals(candidate.getValue())) {
					return candidate;
				}
			}
			
			throw new RuntimeException("no poverty band " + band);
		}
	}
	
	public PovertyBand(String enumValue) {
		setEnumBand(PovertyBandEnum.fromString(enumValue));
	}
	
	public PovertyBand(PovertyBandEnum band) {
		setEnumBand(band);
	}
	
	public int getPoints() {
		return points;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
	
	public void setEnumBand(PovertyBandEnum enumBand) {
		this.enumBand = enumBand;
	}

	public PovertyBandEnum getEnumBand() {
		return enumBand;
	}

	public static PovertyBand fromInt(int points, PPISurvey survey) throws Exception {
		for (PovertyBandEnum candidate : PovertyBandEnum.values()) {
			Method minMethod = survey.getClass().getMethod(
					"get" + candidate.getValue() + "Min", new Class[0]);
			Method maxMethod = survey.getClass().getMethod(
					"get" + candidate.getValue() + "Max", new Class[0]);
			if (points >= (Integer)minMethod.invoke(survey, new Object[0])
					&& points <= (Integer)maxMethod.invoke(survey, new Object[0])) {
				PovertyBand result = new PovertyBand(candidate);
				result.setPoints(points);
				return result;
			}
		}
		throw new RuntimeException("no poverty band for point value " + points);
	}
}
