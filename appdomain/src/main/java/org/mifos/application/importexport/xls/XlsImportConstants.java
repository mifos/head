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

package org.mifos.application.importexport.xls;

public enum XlsImportConstants {

    SKIPPED_ROWS(3), FIRST_CLIENT_ROW(4), HEADER_ROW(3), TITLE_ROW(1),

    LAST_CELL(30),

    CLIENT_NUM_CELL(0), BRANCH_SHORT_NAME_CELL(1), GROUP_GLOBAL_NUM_CELL(2), SALUTATION_CELL(3), FIRST_NAME_CELL(4), MIDDLE_NAME_CELL(
            5), LAST_NAME_CELL(6), SECOND_LAST_NAME_CELL(7), GOVERNMENT_ID_CELL(8), DATE_OF_BIRTH_CELL(9), GENDER_CELL(
            10), MARITAL_STATUS_CELL(11), NUMBER_OF_CHILDREN_CELL(12), CITIZENSHIP_CELL(13), ETHINICITY_CELL(14), EDUCATION_LEVEL_CELL(
            15), ACTIVITIES_CELL(16), POVERTY_STATUS_CELL(17), HANDICAPPED_CELL(18), SPOUSE_FATHER_RELATIONSHIP_CELL(19), SPOUSE_FIRST_NAME_CELL(
            20), SPOUSE_MIDDLE_NAME_CELL(21), SPOUSE_SECOND_LAST_NAME_CELL(22), SPOUSE_LAST_NAME_CELL(23), ADDRESS_CELL(
            24), CITY_DISTRICT_CELL(25), STATE_CELL(26), COUNTRY_CELL(27), POSTAL_CODE_CELL(28), TELEPHONE_CELL(29), RECRUITED_BY_CELL(
            30), STATUS_CELL(31), LOAN_OFFICER_CELL(32), MEETING_FREQUENCY_CELL(33), MEETING_RECUR_EVERY_WEEK_CELL(34), MEETING_ON_WEEK_CELL(
            35), MEETING_OPT1_DAY_CELL(36), MEETING_OPT1_EVERY_CELL(37), MEETING_OPT2_THE_CELL(38), MEETING_OPT2_DAY_CELL(
            39), MEETING_OPT2_EVERY_CELL(40), MEETING_LOCATION_CELL(41), ACTIVATION_DATE_CELL(42);

    private final short value;

    private XlsImportConstants(int value) {
        this.value = (short) value;
    }

    public short value() {
        return value;
    }

    public String getCellNameKey() {
        StringBuilder sb = new StringBuilder("admin.importexport.xls.cell.");
        sb.append(this.toString().replaceFirst("_CELL", "").toLowerCase());
        return sb.toString();
    }

}
