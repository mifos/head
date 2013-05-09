package org.mifos.platform.questionnaire.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sections_questions_link")
public class SectionQuestionLinkEntity implements Serializable{

    @Id
    @GeneratedValue
    @Column(name = "link_id")
    private Integer linkId;
    
    @Column(name = "source_section_question_id")
    private Integer sourceSectionQuestionId;

    @Column(name = "linked_section_question_id")
    private Integer linkedSectionQuestionId;
    
    @Column(name = "condition_id")
    private Integer conditionId;

    public Integer getLinkId() {
        return linkId;
    }

    public void setLinkId(Integer linkId) {
        this.linkId = linkId;
    }

    public Integer getSourceSectionQuestionId() {
        return sourceSectionQuestionId;
    }

    public void setSourceSectionQuestionId(Integer sourceSectionQuestionId) {
        this.sourceSectionQuestionId = sourceSectionQuestionId;
    }

    public Integer getLinkedSectionQuestionId() {
        return linkedSectionQuestionId;
    }

    public void setLinkedSectionQuestionId(Integer linkedSectionQuestionId) {
        this.linkedSectionQuestionId = linkedSectionQuestionId;
    }

    public Integer getConditionId() {
        return conditionId;
    }

    public void setConditionId(Integer conditionId) {
        this.conditionId = conditionId;
    }
}
