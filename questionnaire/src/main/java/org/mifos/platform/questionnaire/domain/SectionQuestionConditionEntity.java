package org.mifos.platform.questionnaire.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sections_questions_condition")
public class SectionQuestionConditionEntity implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "condition_id")
    private Integer conditionId;
    
    @Column(name = "condition_type_id")
    private Integer conditionTypeId;
    
    @Column(name = "value")
    private String value;
    
    @Column(name="additionalValue")
    private String additionalValue;

    public Integer getConditionId() {
        return conditionId;
    }

    public void setConditionId(Integer conditionId) {
        this.conditionId = conditionId;
    }

    public Integer getConditionTypeId() {
        return conditionTypeId;
    }

    public void setConditionTypeId(Integer conditionTypeId) {
        this.conditionTypeId = conditionTypeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAdditionalValue() {
        return additionalValue;
    }

    public void setAdditionalValue(String additionalValue) {
        this.additionalValue = additionalValue;
    }
    
}
