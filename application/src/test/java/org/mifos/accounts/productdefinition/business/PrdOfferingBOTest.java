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

package org.mifos.accounts.productdefinition.business;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.mifos.domain.builders.LoanProductBuilder;
import org.testng.annotations.Test;

@Test(groups = { "unit", "fastTestsSuite" }, dependsOnGroups = { "productMixTestSuite" })
public class PrdOfferingBOTest extends TestCase {

    public void testReturnTrueForEqualsIfPrdOfferingIdIsSame() {
        LoanOfferingBO loanOfferingBO = LoanOfferingBO.createInstanceForTest(Short.valueOf("1234"));
        Assert.assertTrue(loanOfferingBO.equals(LoanOfferingBO.createInstanceForTest(Short.valueOf("1234"))));
        Assert.assertFalse(loanOfferingBO.equals(LoanOfferingBO.createInstanceForTest(Short.valueOf("4321"))));
        Assert.assertFalse(loanOfferingBO.equals(SavingsOfferingBO.createInstanceForTest(Short.valueOf("1234"))));
    }

    public void testReturnTrueForIsOfSameOfferingIfPrdOfferingIdIsSame() {
        LoanOfferingBO loanOfferingBO = LoanOfferingBO.createInstanceForTest(Short.valueOf("1234"));
        Assert.assertTrue(loanOfferingBO.isOfSameOffering(LoanOfferingBO.createInstanceForTest(Short.valueOf("1234"))));
        Assert.assertFalse(loanOfferingBO.isOfSameOffering(LoanOfferingBO.createInstanceForTest(Short.valueOf("4321"))));
    }

    public void testMergeQuestionGroups() {
        LoanOfferingBO loanOfferingBO = new LoanProductBuilder().buildForUnitTests();
        Set<QuestionGroupReference> questionGroupReferences = getQustionGroups(1, 2, 3, 4);
        loanOfferingBO.mergeQuestionGroups(questionGroupReferences);
        Set<QuestionGroupReference> questionGroups = loanOfferingBO.getQuestionGroups();
        assertThat(questionGroups, is(questionGroupReferences));
        questionGroupReferences = getQustionGroups(2, 5);
        loanOfferingBO.mergeQuestionGroups(questionGroupReferences);
        questionGroups = loanOfferingBO.getQuestionGroups();
        assertThat(questionGroups.size(), is(2));
        QuestionGroupReference[] qgRefs = questionGroups.toArray(new QuestionGroupReference[questionGroups.size()]);
        assertThat(qgRefs[0].getQuestionGroupId(), is(2));
        assertThat(qgRefs[1].getQuestionGroupId(), is(5));
        loanOfferingBO.mergeQuestionGroups(null);
        assertThat(loanOfferingBO.getQuestionGroups(), is(nullValue()));
    }

    private Set<QuestionGroupReference> getQustionGroups(int... questionGroupIds) {
        Set<QuestionGroupReference> questionGroupReferences = new HashSet<QuestionGroupReference>();
        for (int questionGroupId : questionGroupIds) {
            questionGroupReferences.add(makeQuestionGroupRef(questionGroupId));
        }
        return questionGroupReferences;
    }

    private QuestionGroupReference makeQuestionGroupRef(int questionGroupId) {
        QuestionGroupReference questionGroupReference = new QuestionGroupReference();
        questionGroupReference.setQuestionGroupId(questionGroupId);
        return questionGroupReference;
    }

}
