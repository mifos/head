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

@SuppressWarnings("serial")
@Entity
@Table(name = "question_group_link")
public class QuestionGroupLink implements Serializable {

    @Id
    @GeneratedValue
    @Column(name="id")
    private Integer id;
    
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
