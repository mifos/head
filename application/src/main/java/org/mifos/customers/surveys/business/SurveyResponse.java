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

package org.mifos.customers.surveys.business;

import java.util.Date;

import org.mifos.customers.ppi.business.PPIChoice;
import org.mifos.customers.surveys.exceptions.SurveyExceptionConstants;
import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.util.helpers.DateUtils;

public class SurveyResponse implements Comparable<SurveyResponse> {
    private int responseId;

    private SurveyInstance instance;

    private SurveyQuestion surveyQuestion;

    private String freetextValue;

    private Date dateValue;

    private QuestionChoice choiceValue;

    private String multiSelectValue;

    private Double numberValue;

    public SurveyResponse(SurveyInstance instance, SurveyQuestion question) {
        setInstance(instance);
        setSurveyQuestion(question);
    }

    public SurveyResponse() {
    }

    public Question getQuestion() {
        if (getSurveyQuestion() == null)
            return null;
        return getSurveyQuestion().getQuestion();
    }

    public void setQuestion(Question question) {
        getSurveyQuestion().setQuestion(question);
    }

    public SurveyInstance getInstance() {
        return instance;
    }

    public void setInstance(SurveyInstance instance) {
        this.instance = instance;
    }

    public int getResponseId() {
        return responseId;
    }

    public void setResponseId(int responseId) {
        this.responseId = responseId;
    }

    public QuestionChoice getChoiceValue() {
        return choiceValue;
    }

    public void setChoiceValue(QuestionChoice choice) throws ApplicationException {
        if (getQuestion().getAnswerTypeAsEnum() != AnswerType.CHOICE) {
            throw new ApplicationException(SurveyExceptionConstants.WRONG_RESPONSE_TYPE);
        }
        this.choiceValue = choice;
    }

    public void setMultiSelectValue(String multiSelectValue) {
        this.multiSelectValue = multiSelectValue;
    }

    public String getMultiSelectValue() {
        return multiSelectValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) throws ApplicationException {
        if (dateValue != null && getQuestion().getAnswerTypeAsEnum() != AnswerType.DATE) {
            throw new ApplicationException(SurveyExceptionConstants.WRONG_RESPONSE_TYPE);
        }
        this.dateValue = dateValue;
    }

    public String getFreetextValue() {
        return freetextValue;
    }

    public void setFreetextValue(String freetextValue) throws ApplicationException {
        if (freetextValue != null && getQuestion().getAnswerTypeAsEnum() != AnswerType.FREETEXT) {
            throw new ApplicationException(SurveyExceptionConstants.WRONG_RESPONSE_TYPE);
        }
        this.freetextValue = freetextValue;
    }

    public Double getNumberValue() {
        return numberValue;
    }

    public void setNumberValue(Double numberValue) throws ApplicationException {
        if (numberValue != null && getQuestion().getAnswerTypeAsEnum() != AnswerType.NUMBER) {
            throw new ApplicationException(SurveyExceptionConstants.WRONG_RESPONSE_TYPE);
        }
        this.numberValue = numberValue;
    }

    public Object getValue() {
        AnswerType answerType = getQuestion().getAnswerTypeAsEnum();

        if (answerType == AnswerType.FREETEXT) {
            return getFreetextValue();
        }

        else if (answerType == AnswerType.NUMBER) {
            return getNumberValue();
        }

        else if (answerType == AnswerType.DATE) {
            return getDateValue();
        }

        else if (answerType == AnswerType.CHOICE) {
            return getChoiceValue();
        }

        else {
            return null;
        }
    }

    @Override
    public String toString() {
        AnswerType answerType = getQuestion().getAnswerTypeAsEnum();

        if (answerType == AnswerType.FREETEXT) {
            return getFreetextValue();
        }

        else if (answerType == AnswerType.NUMBER) {
            return Double.toString(numberValue);
        }

        else if (answerType == AnswerType.DATE) {
            return DateUtils.makeDateAsSentFromBrowser(getDateValue());
        }

        else if (answerType == AnswerType.CHOICE) {
            return Integer.toString(getChoiceValue().getChoiceId());
        }

        else if (answerType == AnswerType.MULTISELECT) {
            return multiSelectValue;
        }

        else {
            return null;
        }
    }

    public void setStringValue(String value) throws ApplicationException, InvalidDateException {
        if (getQuestion() == null || getQuestion().getAnswerTypeAsEnum() == null) {
            throw new ApplicationException(SurveyExceptionConstants.NO_ANSWER_TYPE_YET);
        }

        AnswerType answerType = getQuestion().getAnswerTypeAsEnum();
        if (answerType == AnswerType.FREETEXT) {
            setFreetextValue(value);

        } else if (answerType == AnswerType.MULTISELECT) {
            setMultiSelectValue(value);
        } else if (answerType == AnswerType.DATE) {
            setDateValue(DateUtils.getDate(value));
        } else if (answerType == AnswerType.NUMBER) {
            double numberValue = Double.parseDouble(value);
            setNumberValue(numberValue);
        } else if (answerType == AnswerType.DATE) {
            Date dateValue = DateUtils.getDateAsSentFromBrowser(value);
            setDateValue(dateValue);
        } else if (answerType == AnswerType.CHOICE) {
            int choiceId = Integer.parseInt(value);
            QuestionChoice choice = null;
            for (QuestionChoice qc : getQuestion().getChoices()) {
                if (qc.getChoiceId() == choiceId) {
                    choice = qc;
                    break;
                }
            }
            if (choice == null)
                throw new ApplicationException(SurveyExceptionConstants.NOT_CHOICE_TYPE);

            setChoiceValue(choice);
        }
    }

    public void setValue(Object value) throws ApplicationException {

        if (getQuestion() == null || getQuestion().getAnswerTypeAsEnum() == null) {
            throw new ApplicationException(SurveyExceptionConstants.NO_ANSWER_TYPE_YET);
        }

        AnswerType answerType = getQuestion().getAnswerTypeAsEnum();

        try {
            if (answerType == AnswerType.FREETEXT) {
                setFreetextValue((String) value);
            }

            else if (answerType == AnswerType.DATE) {
                setDateValue((Date) value);
            }

            else if (answerType == AnswerType.NUMBER) {
                setNumberValue((Double) value);
            }

            else if (answerType == AnswerType.CHOICE) {
                setChoiceValue((QuestionChoice) value);
            }

            else if (answerType == AnswerType.MULTISELECT) {
                setMultiSelectValue((String) value);
            }
        } catch (ClassCastException e) {
            throw new ApplicationException(SurveyExceptionConstants.WRONG_RESPONSE_TYPE);
        }
    }

    public void setSurveyQuestion(SurveyQuestion surveyQuestion) {
        this.surveyQuestion = surveyQuestion;
    }

    public SurveyQuestion getSurveyQuestion() {
        return surveyQuestion;
    }

    @Override
    public final boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof SurveyResponse)) {
            return false;
        }

        SurveyResponse response = (SurveyResponse) o;
        return response.getResponseId() == responseId;
    }

    @Override
    public int hashCode() {
        return new Integer(responseId).hashCode();
    }

    public int compareTo(SurveyResponse o) {
        return getSurveyQuestion().compareTo(o.getSurveyQuestion());
    }

    /**
     * this method is added to test PpiSurveyInstance.computeScore That method
     * needs to query each response for its points. TODO: This needs to be
     * cleaned up so that the method is queried only for PpiSurveyInstances.
     */
    public int getPoints() {
        PPIChoice choice = (PPIChoice) getChoiceValue();
        return choice.getPoints();
    }
}
