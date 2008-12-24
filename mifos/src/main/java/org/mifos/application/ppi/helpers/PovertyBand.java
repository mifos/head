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
