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

import junit.framework.JUnit4TestAdapter;

import org.junit.*;
import org.mifos.framework.exceptions.ValidationException;

import static org.junit.Assert.*;

public class PpiLikelihoodTest {
	private double delta = 0.000001;

	@Before 
	public void setUp() {
	}

	@After
	public void tearDown() {
	}
	
	@Test
	public void testLikelihoodConstructor() throws Exception{
		PpiLikelihood lh = new PpiLikelihood(20.1, 21.2);
		Assert.assertEquals(20.1,lh.getBottomHalfBelowPovertyLinePercent(),delta);
		Assert.assertEquals(21.2,lh.getTopHalfBelowPovertyLinePercent(),delta);
		Assert.assertEquals(41.3,lh.getBelowPovertyLinePercent(),delta);
		Assert.assertEquals(58.7,lh.getAbovePovertyLinePercent(),delta);
	}
	
	
	@Test
	public void testLikelihoodConstructorZeroBottomHalf() throws Exception {
		PpiLikelihood lh = new PpiLikelihood(0.0, 21.2);
		Assert.assertEquals(0.0,lh.getBottomHalfBelowPovertyLinePercent(),delta);
		Assert.assertEquals(21.2,lh.getTopHalfBelowPovertyLinePercent(),delta);
		Assert.assertEquals(21.2,lh.getBelowPovertyLinePercent(),delta);
		Assert.assertEquals(78.8,lh.getAbovePovertyLinePercent(),delta);
	}
	
	
	@Test
	public void testLikelihoodConstructorZeroTopHalf() throws Exception {
		PpiLikelihood lh = new PpiLikelihood(20.1, 0.0);
		Assert.assertEquals(20.1,lh.getBottomHalfBelowPovertyLinePercent(),delta);
		Assert.assertEquals(0.0,lh.getTopHalfBelowPovertyLinePercent(),delta);
		Assert.assertEquals(20.1,lh.getBelowPovertyLinePercent(),delta);
		Assert.assertEquals(79.9,lh.getAbovePovertyLinePercent(),delta);
	}
	
	@Test
	public void testLikelihoodConstructorBothZero() throws Exception {
		PpiLikelihood lh = new PpiLikelihood(0.0,0.0);
		Assert.assertEquals(0.0,lh.getBottomHalfBelowPovertyLinePercent(),delta);
		Assert.assertEquals(0.0,lh.getTopHalfBelowPovertyLinePercent(),delta);
		Assert.assertEquals(0.0,lh.getBelowPovertyLinePercent(),delta);
		Assert.assertEquals(100.0,lh.getAbovePovertyLinePercent(),delta);
	}
	
	@Test(expected=ValidationException.class)
	public void testLikelihoodConstructorBottomHalfNegative()  throws Exception{
		PpiLikelihood lh = new PpiLikelihood(-10.5,30.2);
	}
	
	@Test(expected=ValidationException.class)
	public void testLikelihoodConstructorTopHalfNegative()  throws Exception{
		PpiLikelihood lh = new PpiLikelihood(10.5,-30.2);
	}
	
	@Test(expected=ValidationException.class)
	public void testLikelihoodConstructorBothNegative() throws Exception {
		PpiLikelihood lh = new PpiLikelihood(10.5,-30.2);
	}
	
	@Test(expected=ValidationException.class)
	public void testLikelihoodConstructorTooBig() throws Exception {
		PpiLikelihood lh = new PpiLikelihood(85.5,30.2);
	}
	
	@Test(expected=ValidationException.class)
	public void testLikelihoodConstructorTopHalfTooBig() throws Exception {
		PpiLikelihood lh = new PpiLikelihood(110.1,5.5);
	}
	
	@Test(expected=ValidationException.class)
	public void testLikelihoodConstructorBothTooBig() throws Exception {
		PpiLikelihood lh = new PpiLikelihood(5.5, 110.5);
	}
	
	@Test(expected=ValidationException.class)
	public void testLikelihoodConstructorPosNeg() throws Exception {
		PpiLikelihood lh = new PpiLikelihood(105.0,-20.5);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(PpiLikelihoodTest.class);
	}



}
