/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.customers.surveys.business;

import junit.framework.TestCase;

import org.mifos.framework.TestUtils;
import org.testng.annotations.Test;

@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class QuestionTest extends TestCase {

    public void testEquals() {
        Question question1 = new Question("what color?");
        question1.setQuestionId(5);

        Question question2 = new Question("what shape?");
        question2.setQuestionId(5);

        Question subclass = new Question("what goes there?") {
        };
        subclass.setQuestionId(5);

        Question question3 = new Question("what shape?");
        question3.setQuestionId(7);

        TestUtils.verifyBasicEqualsContract(new Question[] { question1, question2, subclass },
                new Question[] { question3 });
    }
}
