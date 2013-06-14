/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.test.acceptance.framework.center;

import java.util.Random;
import java.util.UUID;

import org.mifos.test.acceptance.framework.center.MeetingParameters.WeekDay;

@SuppressWarnings("PMD.TooManyFields") // lots of fields ok for form input case
public class MeetingParameters {

    public enum WeekDay {

        MONDAY ("Monday", 2),
        TUESDAY ("Tuesday", 3),
        WEDNESDAY ("Wednesday", 4),
        THURSDAY ("Thursday", 5),
        FRIDAY ("Friday", 6),
        SATURDAY ("Saturday", 7);

        private final String statusText;
        private final Integer id;

        private WeekDay(String statusText, Integer id) {
            this.statusText = statusText;
            this.id = id;
        }

        public String getName() {
            return this.statusText;
        }

        public Integer getId() {
            return this.id;
        }
        /**
         * Gets enum value by day of the week number.
         * @param dayOfWeek day of the week number
         * @return enum value where WeekDay.id==dayOfWeek or null
         */
        public static WeekDay findByInt(int dayOfWeek) {
            WeekDay weekDay=null;
            for (WeekDay wd : values()) {
                if(wd.id.equals(dayOfWeek)){
                    weekDay=wd;
                    break;
                }
            }
            return weekDay;
        }

    }
    
    WeekDay weekDay;
    String weekFrequency;
    String meetingPlace;
    private String meetingStartDate;

    public WeekDay getWeekDay() {
        return this.weekDay;
    }

    public void setWeekDay(WeekDay weekDay) {
        this.weekDay = weekDay;
    }

    public String getWeekFrequency() {
        return this.weekFrequency;
    }

    public void setWeekFrequency(String weekFrequency) {
        this.weekFrequency = weekFrequency;
    }

    public String getMeetingPlace() {
        return this.meetingPlace;
    }

    public void setMeetingPlace(String meetingPlace) {
        this.meetingPlace = meetingPlace;
    }

    public String getMeetingStartDate() {
        return meetingStartDate;
    }

    public void setMeetingStartDate(String meetingStartDate) {
        this.meetingStartDate = meetingStartDate;
    }
    
    public static MeetingParameters getRandomMeetingParameters() {
    	Random rand = new Random();
    	MeetingParameters meetingParameters = new MeetingParameters();
    	meetingParameters.setMeetingPlace(UUID.randomUUID().toString());
    	meetingParameters.setWeekFrequency(Integer.toString(rand.nextInt(6) + 1));
    	meetingParameters.setWeekDay(WeekDay.findByInt(rand.nextInt(5) + 2));
    	
    	return meetingParameters;
    }
}