/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

import org.mifos.framework.exceptions.ValidationException;

/**
 * mocked up survey overrides getLikelihood(score) for testing
 */
class MockPPISurvey extends PPISurvey {
	
	@Override
	public PPILikelihood getLikelihood(int score) {
		try {
			return new PPILikelihood(0,100,20.0,55.0);
		} catch (ValidationException e) {
			throw new RuntimeException(e);
		}
	}
	
}