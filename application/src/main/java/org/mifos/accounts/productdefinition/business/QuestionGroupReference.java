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
}
