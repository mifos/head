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

package org.mifos.platform.questionnaire.domain;

import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.components.fieldConfiguration.business.EntityMaster;

import javax.persistence.*;

@NamedQueries( {
        @NamedQuery(
                name = "EventSourceEntity.retrieveCountByEventAndSource",
                query = "select count(*) from EventSourceEntity eventSource where " +
                        "eventSource.event.name = ? and eventSource.source.entityType = ?"
        ),
        @NamedQuery(
                name = "EventSourceEntity.retrieveByEventAndSource",
                query = "select eventSource from EventSourceEntity eventSource where " +
                        "eventSource.event.name = ? and eventSource.source.entityType = ?"
        )
})

@Entity
@Table(name = "event_sources")
public class EventSourceEntity extends AbstractEntity {
    private static final long serialVersionUID = 110709803540955521L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Short id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_type_id")
    private EntityMaster source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private EventEntity event;

    @Column(name = "description")
    private String description;

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public EntityMaster getSource() {
        return source;
    }

    public void setSource(EntityMaster source) {
        this.source = source;
    }

    public EventEntity getEvent() {
        return event;
    }

    public void setEvent(EventEntity event) {
        this.event = event;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventSourceEntity that = (EventSourceEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
