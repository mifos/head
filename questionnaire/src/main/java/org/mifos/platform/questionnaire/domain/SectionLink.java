package org.mifos.platform.questionnaire.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQueries( {
    @NamedQuery(
            name = "SectionLink.retrieveDependentSectionLinksFromQuestion",
            query = "from SectionLink s where " +
                    "s.questionGroupLink.sourceSectionQuestion.id = ?"
    ),
    @NamedQuery(
            name = "SectionLink.retrieveSectionIdByQuestionGroupIdAndName",
            query = "from Section s where " +
                    "s.id = :id and s.name = :name"
    )
})
@Entity
@Table(name = "section_link")
public class SectionLink implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name="id")
    private Integer id;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="question_group_link_id")
    private QuestionGroupLink questionGroupLink;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="affected_section_id")
    private Section affectedSection;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public QuestionGroupLink getQuestionGroupLink() {
        return questionGroupLink;
    }

    public void setQuestionGroupLink(QuestionGroupLink questionGroupLink) {
        this.questionGroupLink = questionGroupLink;
    }

    public Section getAffectedSection() {
        return affectedSection;
    }

    public void setAffectedSection(Section affectedSection) {
        this.affectedSection = affectedSection;
    }
    
}
