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

package org.mifos.application.ppi.business;

import java.util.ArrayList;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.junit.Assert;
import org.junit.Test;
import org.mifos.config.GeneralConfig;
import org.mifos.framework.exceptions.ValidationException;

public class PpiLikelihoodTest {
	private double delta = 0.000001;

	@Test
	public void testLikelihoodConstructor() throws Exception{
		PPILikelihood lh = new PPILikelihood(0, 5, 20.1, 21.2);
		Assert.assertEquals(20.1,lh.getBottomHalfBelowPovertyLinePercent(),delta);
		Assert.assertEquals(21.2,lh.getTopHalfBelowPovertyLinePercent(),delta);
		Assert.assertEquals(41.3,lh.getBelowPovertyLinePercent(),delta);
		Assert.assertEquals(58.7,lh.getAbovePovertyLinePercent(),delta);
	}
	
	@Test
	public void testLikelihoodConstructorZeroBottomHalf() throws Exception {
		PPILikelihood lh = new PPILikelihood(0, 5, 0.0, 21.2);
		Assert.assertEquals(0.0,lh.getBottomHalfBelowPovertyLinePercent(),delta);
		Assert.assertEquals(21.2,lh.getTopHalfBelowPovertyLinePercent(),delta);
		Assert.assertEquals(21.2,lh.getBelowPovertyLinePercent(),delta);
		Assert.assertEquals(78.8,lh.getAbovePovertyLinePercent(),delta);
	}
	
	@Test
	public void testLikelihoodConstructorZeroTopHalf() throws Exception {
		PPILikelihood lh = new PPILikelihood(0, 5, 20.1, 0.0);
		Assert.assertEquals(20.1,lh.getBottomHalfBelowPovertyLinePercent(),delta);
		Assert.assertEquals(0.0,lh.getTopHalfBelowPovertyLinePercent(),delta);
		Assert.assertEquals(20.1,lh.getBelowPovertyLinePercent(),delta);
		Assert.assertEquals(79.9,lh.getAbovePovertyLinePercent(),delta);
	}
	
	@Test
	public void testLikelihoodConstructorBothZero() throws Exception {
		PPILikelihood lh = new PPILikelihood(0, 5, 0.0, 0.0);
		Assert.assertEquals(0.0,lh.getBottomHalfBelowPovertyLinePercent(),delta);
		Assert.assertEquals(0.0,lh.getTopHalfBelowPovertyLinePercent(),delta);
		Assert.assertEquals(0.0,lh.getBelowPovertyLinePercent(),delta);
		Assert.assertEquals(100.0,lh.getAbovePovertyLinePercent(),delta);
	}
	
	@Test(expected=ValidationException.class)
	public void testLikelihoodConstructorScoreOutOfRange()  throws Exception{
		int nonPoorMax = GeneralConfig.getMaxPointsPerPPISurvey();
		PPILikelihood lh = new PPILikelihood(0, nonPoorMax + 1 , 10.5, 30.2);
	}
	
	@Test(expected=ValidationException.class)
	public void testLikelihoodConstructorBottomHalfNegative()  throws Exception{
		PPILikelihood lh = new PPILikelihood(0, 5, -10.5, 30.2);
	}
	
	@Test(expected=ValidationException.class)
	public void testLikelihoodConstructorTopHalfNegative()  throws Exception{
		PPILikelihood lh = new PPILikelihood(0, 5, 10.5, -30.2);
	}
	
	@Test(expected=ValidationException.class)
	public void testLikelihoodConstructorBothNegative() throws Exception {
		PPILikelihood lh = new PPILikelihood(0, 5, 10.5, -30.2);
	}
	
	@Test(expected=ValidationException.class)
	public void testLikelihoodConstructorTooBig() throws Exception {
		PPILikelihood lh = new PPILikelihood(0, 5, 85.5, 30.2);
	}
	
	@Test(expected=ValidationException.class)
	public void testLikelihoodConstructorTopHalfTooBig() throws Exception {
		PPILikelihood lh = new PPILikelihood(0, 5, 110.1, 5.5);
	}
	
	@Test(expected=ValidationException.class)
	public void testLikelihoodConstructorBothTooBig() throws Exception {
		PPILikelihood lh = new PPILikelihood(0, 5, 5.5, 110.5);
	}
	
	@Test(expected=ValidationException.class)
	public void testLikelihoodConstructorPosNeg() throws Exception {
		PPILikelihood lh = new PPILikelihood(0, 5, 105.0, -20.5);
	}
	
	private List<PPILikelihood> createRows() throws ValidationException {
		List<PPILikelihood> likelihoods = new ArrayList<PPILikelihood>();
		likelihoods.add(new PPILikelihood(0, 20, 10.0, 20.0));
		likelihoods.add(new PPILikelihood(21, 40, 10.0, 20.0));
		likelihoods.add(new PPILikelihood(41, 60, 10.0, 20.0));
		likelihoods.add(new PPILikelihood(61, 80, 10.0, 20.0));
		likelihoods.add(new PPILikelihood(81, 100, 10.0, 0.0));
		return likelihoods;
	}
	
	@Test(expected=ValidationException.class)
	public void testScoreRangeOverlap() throws Exception {
		List<PPILikelihood> list = new ArrayList<PPILikelihood>();
		list.add(new PPILikelihood(0, 5, 1, 2));
		// this range overlaps the previous range
		list.add(new PPILikelihood(4, 10, 1, 2));
		PPISurvey survey = new PPISurvey();
		survey.setLikelihoods(list);
	}
	
	@Test(expected=ValidationException.class)
	public void testScoreCoverage() throws Exception {
		List<PPILikelihood> list = new ArrayList<PPILikelihood>();
		list.add(new PPILikelihood(0, 5, 1, 2));
		list.add(new PPILikelihood(6, 30, 1, 2));
		// score 31 is missing
		list.add(new PPILikelihood(32, 100, 1, 2));
		PPISurvey survey = new PPISurvey();
		survey.setLikelihoods(list);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetRowForNegativeScore() throws Exception {
		PPISurvey survey = new PPISurvey();
		survey.setLikelihoods(createRows());
		PPILikelihood l = survey.getLikelihood(-1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetScoreOutOfRange() throws Exception {
		PPISurvey survey = new PPISurvey();
		survey.setLikelihoods(createRows());
		int nonPoorMax = GeneralConfig.getMaxPointsPerPPISurvey();
		PPILikelihood l = survey.getLikelihood(nonPoorMax + 1);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(PpiLikelihoodTest.class);
	}
}
