/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.platform.questionnaire.service.dtos;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.mifos.platform.questionnaire.service.QuestionType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.trim;

@XStreamAlias("question")
public class QuestionDto implements Serializable {
    private static final long serialVersionUID = 4062506731931643620L;

    private String nickname;
    private String text;
    private QuestionType type;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    @XStreamImplicit(itemFieldName = "choice")
    private List<ChoiceDto> choices;
    private Integer minValue;
    private Integer maxValue;
    private boolean mandatory;
    private boolean active;
    private boolean editable;
    private boolean showOnPage;
    
    public boolean isShowOnPage() {
		return showOnPage;
	}

	public void setShowOnPage(boolean showOnPage) {
		this.showOnPage = showOnPage;
	}

	@XStreamAsAttribute
    private Integer order;

    public QuestionDto() {
        choices = new ArrayList<ChoiceDto>();
        active = true;
        editable = true;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public List<ChoiceDto> getChoices() {
        return choices;
    }

    public void setChoices(List<ChoiceDto> choices) {
        this.choices = choices;
    }

    public Integer getMinValue() {
        return minValue;
    }

    public void setMinValue(Integer minValue) {
        this.minValue = minValue;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public void trimTitle() {
        this.text = trim(this.text);
    }

    public boolean isTypeWithChoices() {
        return this.type == QuestionType.SINGLE_SELECT || this.type == QuestionType.MULTI_SELECT || this.type == QuestionType.SMART_SELECT;
    }
}
