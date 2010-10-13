package org.mifos.accounts.productdefinition.business;

import org.mifos.framework.business.AbstractEntity;

public class QuestionGroupReference extends AbstractEntity {

    private Integer questionGroupId;

    public Integer getQuestionGroupId() {
        return questionGroupId;
    }

    public void setQuestionGroupId(Integer questionGroupId) {
        this.questionGroupId = questionGroupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuestionGroupReference that = (QuestionGroupReference) o;

        if (questionGroupId != null ? !questionGroupId.equals(that.questionGroupId) : that.questionGroupId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return questionGroupId != null ? questionGroupId.hashCode() : 0;
    }
}
