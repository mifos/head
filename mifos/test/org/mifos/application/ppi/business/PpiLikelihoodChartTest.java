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
 * 
 */

package org.mifos.application.ppi.business;

import org.junit.*;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.application.ppi.business.PpiLikelihood;
import org.mifos.application.ppi.business.PpiLikelihoodChart;
import java.util.ArrayList;

import junit.framework.JUnit4TestAdapter;

import static org.junit.Assert.*;

public class PpiLikelihoodChartTest {
	
	private ArrayList<PpiLikelihood> createRows (int size) throws ValidationException {
		ArrayList<PpiLikelihood>testLikelihoods = new ArrayList<PpiLikelihood>();
		for (int i = 0; i < size; i++) {
			testLikelihoods.add(new PpiLikelihood((i+1)/10, (i+2)/10));
		}
		return testLikelihoods;
	}
	
	@Test
	public void testChartValuesHappyPath() throws Exception {
		double delta = 0.00001;
		PpiLikelihoodChart c = new PpiLikelihoodChart(createRows(101));
		
		for (int i = 0; i < 101; i++) {
			assertEquals(c.getRow(i).getBottomHalfBelowPovertyLinePercent(), (i+1)/10, delta);
			assertEquals(c.getRow(i).getTopHalfBelowPovertyLinePercent(), (i+2)/10, delta);
		}
	}
	
	@Test(expected=ValidationException.class)
	public void testWrongSize() throws Exception {
		PpiLikelihoodChart c102 = new PpiLikelihoodChart(createRows(102));
		PpiLikelihoodChart c100 = new PpiLikelihoodChart(createRows(100));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetRowForNegativeScore() throws Exception {
		PpiLikelihoodChart c = new PpiLikelihoodChart(createRows(101));
		PpiLikelihood l = c.getRow(-1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetRowForTooBigScore() throws Exception {
		PpiLikelihoodChart c = new PpiLikelihoodChart(createRows(101));
		PpiLikelihood l = c.getRow(101);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(PpiLikelihoodChartTest.class);
	}


}
