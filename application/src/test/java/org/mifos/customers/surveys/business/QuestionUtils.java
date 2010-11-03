/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */

package org.mifos.customers.surveys.business;

import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.customers.surveys.helpers.QuestionState;

import java.util.ArrayList;
import java.util.List;

public class QuestionUtils {
    public static Question getNumericQuestion(String title, int min, int max) {
        Question question = new Question();
        question.setAnswerType(AnswerType.NUMBER);
        setText(title, question);
        question.setNumericMin(min);
        question.setNumericMax(max);
        question.setQuestionState(QuestionState.ACTIVE);
        return question;
    }

    public static Question getDateQuestion(String title) {
        Question question = new Question();
        setText(title, question);
        question.setAnswerType(AnswerType.DATE);
        question.setQuestionState(QuestionState.ACTIVE);
        return question;
    }

    public static Question getSingleSelectQuestion(String title, String... choices) {
        return getQuestionWithChoices(title, AnswerType.SINGLESELECT, choices);
    }

    public static Question getMultiSelectQuestion(String title, String... choices) {
        return getQuestionWithChoices(title, AnswerType.MULTISELECT, choices);
    }

    private static void setText(String text, Question question) {
        question.setQuestionText(text);
    }

    private static List<QuestionChoice> getQuestionChoices(String[] choices) {
        List<QuestionChoice> questionChoices = new ArrayList<QuestionChoice>();
        for (String choice : choices) {
            questionChoices.add(new QuestionChoice(choice));
        }
        return questionChoices;
    }

    private static Question getQuestionWithChoices(String title, AnswerType answerType, String... choices) {
        Question question = new Question();
        setText(title, question);
        question.setAnswerType(answerType);
        question.setChoices(getQuestionChoices(choices));
        question.setQuestionState(QuestionState.ACTIVE);
        return question;
    }
}
