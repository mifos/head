package org.mifos.framework.util.helpers;

import org.mifos.application.ppi.business.PPIChoice;
import org.mifos.application.ppi.business.PPISurvey;
import org.mifos.application.surveys.business.SurveyInstance;
import org.mifos.application.surveys.business.SurveyResponse;

public class PPICalculator {
	
	public static int calculateScore(SurveyInstance instance) {
		int sum = 0;
		
		if (!PPISurvey.class.isInstance(instance.getSurvey()))
			throw new RuntimeException("Survey is not a PPI survey");
		
		for (SurveyResponse response : instance.getSurveyResponses()) {
			PPIChoice choice = (PPIChoice) response.getChoiceValue();
			sum += choice.getPoints();
		}
		
		if (sum > 100)
			throw new RuntimeException("Index is larger that 100");
		
		
		return sum;
	}
	
	public static boolean scoreLimitsAreValid(PPISurvey survey) {
		/*int sum = 0;
		
		{
			int min = survey.getVeryPoorMin();
			int max = survey.getVeryPoorMax();
			sum += max * (max + 1) / 2 - min * (min + 1) / 2 ;
		}
		
		{
			int min = survey.getPoorMin();
			int max = survey.getPoorMax();
			sum += max * (max + 1) / 2 - min * (min + 1) / 2 ;
		}
		
		{
			int min = survey.getAtRiskMin();
			int max = survey.getAtRiskMax();
			sum += max * (max + 1) / 2 - min * (min + 1) / 2 ;
		}
		
		{
			int min = survey.getNonPoorMin();
			int max = survey.getNonPoorMax();
			sum += max * (max + 1) / 2 - min * (min + 1) / 2 ;
		}*/
		
		return survey.getNonPoorMax() == 100 && survey.getVeryPoorMin() == 0
				&& survey.getVeryPoorMax() == survey.getPoorMin() - 1
				&& survey.getPoorMax() == survey.getAtRiskMin() - 1
				&& survey.getAtRiskMax() == survey.getNonPoorMin() - 1;
	}
	
}
