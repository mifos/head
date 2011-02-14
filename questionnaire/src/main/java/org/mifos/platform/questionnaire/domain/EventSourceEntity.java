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

import org.mifos.framework.business.EntityMaster;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import java.io.Serializable;

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
        ),
        @NamedQuery(
                name = "EventSourceEntity.retrieveAllEventSourcesOrdered",
                query = "select eventSource from EventSourceEntity eventSource " +
                        "order by eventSource.source.id, eventSource.event.id"
        )
})

@Entity
@Table(name = "event_sources")
@SuppressWarnings("PMD")
public class EventSourceEntity implements Serializable {
    //TODO: For the time being to resolve dependencies
    // should extend AbstractEntity? move AbstractEntity to common module first
    private static final long serialVersionUID = 110709803540955521L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_type_id")
    private EntityMaster source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private EventEntity event;

    @Column(name = "description")
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
