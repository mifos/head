package org.mifos.platform.questionnaire.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;

@Entity
@Table(name = "question_group_link")
public class QuestionGroupLink implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String CONDITION_TYPE_EQUALS = "QuestionGroupLink.equals";
	public static final String CONDITION_TYPE_NOT_EQUALS = "QuestionGroupLink.notEquals";
	public static final String CONDITION_TYPE_GREATER = "QuestionGroupLink.greater";
	public static final String CONDITION_TYPE_SMALLER = "QuestionGroupLink.smaller";
	public static final String CONDITION_TYPE_RANGE = "QuestionGroupLink.range";
	public static final String CONDITION_TYPE_DATE_RANGE = "QuestionGroupLink.dateRange";
	public static final String CONDITION_TYPE_BEFORE = "QuestionGroupLink.before";
	public static final String CONDITION_TYPE_AFTER = "QuestionGroupLink.after";
	
    @Id
    @GeneratedValue
    @Column(name="id")
    private Integer id;
    
    @Column(name="condition_type_lookup_id")
    private Integer conditionTypeId;
    
    @Formula("(select v.lookup_name from lookup_value v where v.lookup_id = condition_type_lookup_id)")
    private String conditionType;
    
    @Column(name="value")
    private String value;
    
    @Column(name="additional_value")
    private String additionalValue;
    
    @ManyToOne
    @JoinColumn(name="source_section_question_id")
    private SectionQuestion sourceSectionQuestion;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getConditionTypeId() {
        return conditionTypeId;
    }

    public void setConditionTypeId(Integer conditionTypeId) {
        this.conditionTypeId = conditionTypeId;
    }

    public String getConditionType() {
		return conditionType;
	}

	public void setConditionType(String conditionType) {
		this.conditionType = conditionType;
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

	public SectionQuestion getSourceSectionQuestion() {
		return sourceSectionQuestion;
	}

	public void setSourceSectionQuestion(SectionQuestion sourceSectionQuestion) {
		this.sourceSectionQuestion = sourceSectionQuestion;
	}
    
}
