/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

@SuppressWarnings("PMD.TooManyFields") // lots of fields ok for form input case
public class MeetingParameters {
    public static final int MONDAY = 2;
    public static final int TUESDAY = 3;
    public static final int WEDNESDAY = 4;
    public static final int THURSDAY = 5;
    public static final int FRIDAY = 6;
    public static final int SATURDAY = 7;
    
    int weekDay;
    String weekFrequency;
    String meetingPlace;
    
    public int getWeekDay() {
        return this.weekDay;
    }
    
    public void setWeekDay(int weekDay) {
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
}