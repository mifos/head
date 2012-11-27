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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankOfDay;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.config.ClientRules;
import org.mifos.config.exceptions.ConfigurationException;
import org.mifos.config.util.helpers.HiddenMandatoryFieldNamesConstants;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.LegacyPersonnelDao;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.ApplicableAccountFeeDto;
import org.mifos.dto.domain.ClientCreationDetail;
import org.mifos.dto.domain.ImportedClientDetail;
import org.mifos.dto.domain.MeetingDetailsDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.MeetingRecurrenceDto;
import org.mifos.dto.domain.MeetingTypeDto;
import org.mifos.dto.domain.OfficeDto;
import org.mifos.dto.domain.ParsedClientsDto;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.dto.screen.ClientFamilyDetailDto;
import org.mifos.dto.screen.ClientNameDetailDto;
import org.mifos.dto.screen.ClientPersonalDetailDto;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;
import org.mifos.framework.util.helpers.DateUtils;

public class XlsClientsImporter implements MessageSourceAware {

    private static final Short CLIENT_NAME_TYPE = 3;
    private static final Short SPOUSE_NAME_TYPE = 1;
    private static final Short FATHER_NAME_TYPE = 2;

    private static final String CLIENT_ENTITY = "Client.";

    private static final String WEEK_FREQUENCY = "week(s)";
    private static final String MONTH_FREQUENCY = "month(s)";

    private MessageSource messageSource;
    private Locale locale;

    private final CustomerDao customerDao;
    private final LegacyPersonnelDao legacyPersonnelDao;
    private final OfficeDao officeDao;

    @Autowired
    public XlsClientsImporter(CustomerDao customerDao, LegacyPersonnelDao legacyPersonnelDao, OfficeDao officeDao) {
        this.customerDao = customerDao;
        this.legacyPersonnelDao = legacyPersonnelDao;
        this.officeDao = officeDao;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public ParsedClientsDto parse(final InputStream input) {

        final List<ValueListElement> buisnessActivitiesList = customerDao.retrieveBusinessActivities();
        final List<ValueListElement> gendersList = customerDao.retrieveGenders();
        final List<ValueListElement> citizenshipsList = customerDao.retrieveCitizenship();
        final List<ValueListElement> ethinicitiesList = customerDao.retrieveEthnicity();
        final List<ValueListElement> educationLevelsList = customerDao.retrieveEducationLevels();
        final List<ValueListElement> handicappedList = customerDao.retrieveHandicapped();
        final List<ValueListElement> povertyStatusList = customerDao.retrievePoverty();
        final List<ValueListElement> maritalStatusList = customerDao.retrieveMaritalStatuses();
        final List<ValueListElement> salutationsList = customerDao.retrieveSalutations();

        final List<OfficeDto> allOfficess = officeDao.findAllOffices();

        final FieldConfig fieldConfig = FieldConfig.getInstance();

        final List<String> globalCustNums = new ArrayList<String>();

        final List<String> errorsList = new ArrayList<String>();
        final List<ImportedClientDetail> parsedClientDetails = new ArrayList<ImportedClientDetail>();

        try {
            final HSSFWorkbook workbook = new HSSFWorkbook(input);
            final HSSFSheet sheet = workbook.getSheetAt(0);

            /* test first data row */
            HSSFRow row = sheet.getRow(XlsImportConstants.FIRST_CLIENT_ROW.value());
            if (row == null) {
                errorsList.add(getMessage(XlsMessageConstants.NOT_ENOUGH_INPUT_ROW));
            }

            @SuppressWarnings("rawtypes")
            Iterator rowIterator = sheet.rowIterator();
            
            /* Skip first rows */
            if (errorsList.isEmpty()) {
                for (int i = 0; i < XlsImportConstants.SKIPPED_ROWS.value(); i++) {
                    if (rowIterator.hasNext()) {
                        rowIterator.next();
                    } else {
                        errorsList.add(getMessage(XlsMessageConstants.NOT_ENOUGH_INPUT_ROW));
                        break;
                    }
                }
            }

            XlsImportConstants currentCell = XlsImportConstants.CLIENT_NUM_CELL;
            int friendlyRowNumber = 0;

            /* Parse client data */
            if (errorsList.isEmpty()) {
                while (rowIterator.hasNext()) {
                    try {
                        row = (HSSFRow) rowIterator.next();
                        friendlyRowNumber = row.getRowNum() + 1;
                        /* Get data from sheet */
                        currentCell = XlsImportConstants.CLIENT_NUM_CELL;
                        String clientGlobalNum = getCellStringValue(row, currentCell);
                        if (StringUtils.isBlank(clientGlobalNum)) {
                            clientGlobalNum = null; // generate number
                        } else {
                            // check for duplicates
                            validateGlobalCustNum(clientGlobalNum, globalCustNums);
                            globalCustNums.add(clientGlobalNum);
                        }

                        currentCell = XlsImportConstants.BRANCH_SHORT_NAME_CELL;
                        final String branchShortName = getCellStringValue(row, currentCell);
                        final Short branchOfficeId = getBranchId(branchShortName, allOfficess);

                        currentCell = XlsImportConstants.GROUP_GLOBAL_NUM_CELL;
                        final String groupGlobalNum = getCellStringValue(row, currentCell);
                        validateGroup(groupGlobalNum);

                        if (StringUtils.isBlank(groupGlobalNum) && branchOfficeId == null) {
                            String error = getRowError(friendlyRowNumber)
                                    + getMessage(XlsMessageConstants.OFFICE_AND_BRANCH);
                            errorsList.add(error);
                            continue;
                        }

                        currentCell = XlsImportConstants.SALUTATION_CELL;
                        final String salutation = getCellStringValue(row, currentCell);
                        final Integer salutationId = getValueElementId(salutation, salutationsList);
                        validateMandatoryField(salutationId);

                        currentCell = XlsImportConstants.FIRST_NAME_CELL;
                        final String clientFirstName = getCellStringValue(row, currentCell);
                        validateMandatoryField(clientFirstName);

                        currentCell = XlsImportConstants.MIDDLE_NAME_CELL;
                        final String clientMiddleName = getCellStringValue(row, currentCell);
                        if (fieldConfig.isFieldManadatory(CLIENT_ENTITY
                                + HiddenMandatoryFieldNamesConstants.MIDDLE_NAME)) {
                            validateMandatoryField(clientMiddleName);
                        }

                        currentCell = XlsImportConstants.LAST_NAME_CELL;
                        final String clientLastName = getCellStringValue(row, currentCell);
                        validateMandatoryField(clientLastName);

                        currentCell = XlsImportConstants.SECOND_LAST_NAME_CELL;
                        final String clientSecondLastName = getCellStringValue(row, currentCell);
                        if (fieldConfig.isFieldManadatory(CLIENT_ENTITY
                                + HiddenMandatoryFieldNamesConstants.SECOND_LAST_NAME)) {
                            validateMandatoryField(clientSecondLastName);
                        }

                        currentCell = XlsImportConstants.GOVERNMENT_ID_CELL;
                        String governmentId = getCellStringValue(row, currentCell);
                        if (fieldConfig.isFieldManadatory(CLIENT_ENTITY
                                + HiddenMandatoryFieldNamesConstants.GOVERNMENT_ID)) {
                            validateMandatoryField(governmentId);
                        }

                        currentCell = XlsImportConstants.DATE_OF_BIRTH_CELL;
                        final Date dateOfBirth = getCellDateValue(row, currentCell);
                        validateMandatoryField(dateOfBirth);
                        validateAge(dateOfBirth);

                        currentCell = XlsImportConstants.GENDER_CELL;
                        final String gender = getCellStringValue(row, currentCell);
                        final Short genderId = intToShort(getValueElementId(gender, gendersList));
                        validateMandatoryField(genderId);

                        currentCell = XlsImportConstants.MARITAL_STATUS_CELL;
                        final String maritalStatus = getCellStringValue(row, currentCell);
                        final Integer maritalStatusId = getValueElementId(maritalStatus, maritalStatusList);
                        if (fieldConfig.isFieldManadatory(CLIENT_ENTITY
                                + HiddenMandatoryFieldNamesConstants.MARITAL_STATUS)) {
                            validateMandatoryField(maritalStatusId);
                        }

                        currentCell = XlsImportConstants.NUMBER_OF_CHILDREN_CELL;
                        final Short numberOfChildren = intToShort(getCellIntegerValue(row, currentCell));
                        if (fieldConfig.isFieldManadatory(CLIENT_ENTITY
                                + HiddenMandatoryFieldNamesConstants.NUMBER_OF_CHILDREN)) {
                            validateMandatoryField(numberOfChildren);
                        }

                        currentCell = XlsImportConstants.CITIZENSHIP_CELL;
                        final String citizenship = getCellStringValue(row, currentCell);
                        final Integer citizenshipId = getValueElementId(citizenship, citizenshipsList);
                        if (fieldConfig.isFieldManadatory(CLIENT_ENTITY
                                + HiddenMandatoryFieldNamesConstants.CITIZENSHIP)) {
                            validateMandatoryField(citizenshipId);
                        }

                        currentCell = XlsImportConstants.ETHINICITY_CELL;
                        final String ethinicity = getCellStringValue(row, currentCell);
                        final Integer ethinicityId = getValueElementId(ethinicity, ethinicitiesList);
                        if (fieldConfig.isFieldHidden(CLIENT_ENTITY + HiddenMandatoryFieldNamesConstants.ETHNICITY)) {
                            validateMandatoryField(ethinicityId);
                        }

                        currentCell = XlsImportConstants.EDUCATION_LEVEL_CELL;
                        final String educationLevel = getCellStringValue(row, currentCell);
                        final Integer educationLevelId = getValueElementId(educationLevel, educationLevelsList);
                        if (fieldConfig.isFieldHidden(CLIENT_ENTITY
                                + HiddenMandatoryFieldNamesConstants.EDUCATION_LEVEL)) {
                            validateMandatoryField(educationLevelId);
                        }

                        currentCell = XlsImportConstants.ACTIVITIES_CELL;
                        final String activites = getCellStringValue(row, currentCell);
                        final Integer activityId = getValueElementId(activites, buisnessActivitiesList);
                        if (fieldConfig.isFieldManadatory(CLIENT_ENTITY
                                + HiddenMandatoryFieldNamesConstants.BUSINESS_ACTIVITIES)) {
                            validateMandatoryField(activityId);
                        }

                        currentCell = XlsImportConstants.POVERTY_STATUS_CELL;
                        final String povertyStatus = getCellStringValue(row, currentCell);
                        final Short povertyStatusId = intToShort(getValueElementId(povertyStatus, povertyStatusList));
                        if (fieldConfig.isFieldManadatory(CLIENT_ENTITY
                                + HiddenMandatoryFieldNamesConstants.POVERTY_STATUS)) {
                            validateMandatoryField(povertyStatusId);
                        }

                        currentCell = XlsImportConstants.HANDICAPPED_CELL;
                        final String handicapped = getCellStringValue(row, currentCell);
                        final Integer handicappedId = getValueElementId(handicapped, handicappedList);
                        if (fieldConfig.isFieldManadatory(CLIENT_ENTITY
                                + HiddenMandatoryFieldNamesConstants.HANDICAPPED)) {
                            validateMandatoryField(handicappedId);
                        }

                        currentCell = XlsImportConstants.SPOUSE_FATHER_RELATIONSHIP_CELL;
                        final String spouseOrFather = getCellStringValue(row, currentCell);
                        final Short spouseFatherNameType = getSpouseNameType(spouseOrFather);

                        final boolean familyMandatory = fieldConfig.isFieldManadatory(CLIENT_ENTITY
                                + HiddenMandatoryFieldNamesConstants.FAMILY_DETAILS);
                        if (familyMandatory) {
                            validateMandatoryField(spouseFatherNameType);
                        }

                        currentCell = XlsImportConstants.SPOUSE_FIRST_NAME_CELL;
                        final String spouseFirstName = getCellStringValue(row, currentCell);
                        if (familyMandatory) {
                            validateMandatoryField(spouseFirstName);
                        }

                        currentCell = XlsImportConstants.SPOUSE_MIDDLE_NAME_CELL;
                        final String spouseMiddleName = getCellStringValue(row, currentCell);

                        currentCell = XlsImportConstants.SPOUSE_SECOND_LAST_NAME_CELL;
                        final String spouseSecondLastName = getCellStringValue(row, currentCell);
                        if (familyMandatory
                                && fieldConfig.isFieldManadatory(CLIENT_ENTITY
                                        + HiddenMandatoryFieldNamesConstants.SPOUSE_FATHER_SECOND_LAST_NAME)) {
                            validateMandatoryField(spouseSecondLastName);
                        }

                        currentCell = XlsImportConstants.SPOUSE_LAST_NAME_CELL;
                        final String spouseLastName = getCellStringValue(row, currentCell);
                        if (familyMandatory) {
                            validateMandatoryField(spouseLastName);
                        }

                        currentCell = XlsImportConstants.ADDRESS_CELL;
                        final String address = getCellStringValue(row, currentCell);
                        if (fieldConfig.isFieldManadatory(CLIENT_ENTITY + HiddenMandatoryFieldNamesConstants.ADDRESS1)) {
                            validateMandatoryField(address);
                        }

                        currentCell = XlsImportConstants.CITY_DISTRICT_CELL;
                        final String cityDistrict = getCellStringValue(row, currentCell);

                        currentCell = XlsImportConstants.STATE_CELL;
                        final String state = getCellStringValue(row, currentCell);

                        currentCell = XlsImportConstants.COUNTRY_CELL;
                        final String country = getCellStringValue(row, currentCell);

                        currentCell = XlsImportConstants.POSTAL_CODE_CELL;
                        final String postalCode = getCellStringValue(row, currentCell);

                        currentCell = XlsImportConstants.TELEPHONE_CELL;
                        final String telephone = getCellStringValue(row, currentCell);

                        currentCell = XlsImportConstants.RECRUITED_BY_CELL;
                        final String recruitedBy = getCellStringValue(row, currentCell);
                        validateMandatoryField(recruitedBy);
                        final Short formedBy = getOfficerId(recruitedBy);

                        currentCell = XlsImportConstants.STATUS_CELL;
                        final String status = getCellStringValue(row, currentCell);
                        final Short statusId = statusToShort(status);

                        currentCell = XlsImportConstants.LOAN_OFFICER_CELL;
                        final String loanOfficer = getCellStringValue(row, currentCell);
                        if (!StringUtils.isBlank(groupGlobalNum) && !StringUtils.isBlank(loanOfficer)) {
                            throw new CellException(getMessage(XlsMessageConstants.LOAN_OFFICER_FOR_GROUP_CLIENT));
                        }
                        
                        currentCell = XlsImportConstants.ACTIVATION_DATE_CELL;
                        final LocalDate activationDate = DateUtils.getLocalDateFromDate(getCellDateValue(row, currentCell));
                        if (activationDate != null && !status.equals(getMessage(XlsMessageConstants.ACTIVE))) {
                            throw new CellException(getMessage(XlsMessageConstants.ACTIVE_STATUS_FOR_ACTIVATION_DATE));
                        }

                        /* Meeting data */

                        currentCell = XlsImportConstants.MEETING_FREQUENCY_CELL;
                        final Integer recurrenceType = getRecurrenceType(getCellStringValue(row, currentCell));

                        currentCell = XlsImportConstants.MEETING_RECUR_EVERY_WEEK_CELL;
                        final Integer recurEveryWeek = getCellIntegerValue(row, currentCell);

                        currentCell = XlsImportConstants.MEETING_ON_WEEK_CELL;
                        final Integer weeklyMeetingDay = getDayValue(getCellStringValue(row, currentCell));

                        currentCell = XlsImportConstants.MEETING_OPT1_DAY_CELL;
                        final Integer opt1Day = getCellIntegerValue(row, currentCell);
                        validateMonthDay(opt1Day);

                        currentCell = XlsImportConstants.MEETING_OPT1_EVERY_CELL;
                        final Integer opt1Every = getCellIntegerValue(row, currentCell);
                        validatePositive(opt1Every);

                        currentCell = XlsImportConstants.MEETING_OPT2_THE_CELL;
                        final Integer opt2The = getDayRankValue(getCellStringValue(row, currentCell));

                        currentCell = XlsImportConstants.MEETING_OPT2_DAY_CELL;
                        final Integer opt2Day = getDayValue(getCellStringValue(row, currentCell));

                        currentCell = XlsImportConstants.MEETING_OPT2_EVERY_CELL;
                        final Integer opt2Every = getCellIntegerValue(row, currentCell);
                        validatePositive(opt2Every);

                        currentCell = XlsImportConstants.MEETING_LOCATION_CELL;
                        final String meetingLocation = getCellStringValue(row, currentCell);

                        Integer recurrenceDayNumber = 0;
                        Integer recurrenceWeekOfMonth = 0;
                        Integer recurrenceDayOfWeek = 0;
                        Integer recurEvery = 0;
                        /*
                         * Validate meeting data
                         */
                        if (!StringUtils.isBlank(groupGlobalNum) && recurrenceType != null) {
                            // no meeting allowed for a group
                            throw new RowException(getMessage(XlsMessageConstants.MEETING_FOR_GROUP));
                        } else if (recurrenceType == null) {
                            // make sure everything is empty
                            if (recurEveryWeek != null || weeklyMeetingDay != null || opt1Day != null
                                    || opt1Every != null || opt2Day != null || opt2Every != null || opt2The != null
                                    || !StringUtils.isBlank(meetingLocation)) {
                                throw new RowException(getMessage(XlsMessageConstants.INCOMPLETE_MEETING_DATA));
                            }
                        } else if (recurrenceType == RecurrenceType.WEEKLY.getValue().intValue()) {
                            // make sure weekly data is set
                            if (recurEveryWeek == null || weeklyMeetingDay == null) {
                                throw new RowException(getMessage(XlsMessageConstants.INCOMPLETE_MEETING_DATA));
                            }
                            // make sure monthly details are empty
                            if (opt1Day != null || opt1Every != null || opt2Day != null || opt2Every != null
                                    || opt2The != null) {
                                throw new RowException(
                                        getMessage(XlsMessageConstants.MONTHLY_MEETING_DETAILS_NOT_EMPTY));
                            }
                            // set data
                            recurrenceDayOfWeek = weeklyMeetingDay;
                            recurEvery = recurEveryWeek;
                            // validate location
                            validateMandatoryField(meetingLocation);
                        } else { // monthly recurrence
                            // make sure weekly details are empty
                            if (recurEveryWeek != null || weeklyMeetingDay != null) {
                                throw new RowException(getMessage(XlsMessageConstants.WEEKLY_MEETING_DETAILS_NOT_EMPTY));
                            }
                            if (opt1Day == null) { // option 2
                                // make sure option 2 is set
                                if (opt2Day == null || opt2Every == null || opt2The == null) {
                                    throw new RowException(getMessage(XlsMessageConstants.INCOMPLETE_MEETING_DATA));
                                }
                                // make sure option 1 is empty
                                if (opt1Every != null) {
                                    throw new RowException(getMessage(XlsMessageConstants.OPTIONS_EXCLUSIVE));
                                }
                                // set data
                                recurrenceWeekOfMonth = opt2The;
                                recurrenceDayOfWeek = opt2Day;
                                recurEvery = opt2Every;
                            } else { // option 1
                                // make sure option 1 is set
                                if (opt1Every == null) {
                                    throw new RowException(getMessage(XlsMessageConstants.INCOMPLETE_MEETING_DATA));
                                }
                                // make sure option 2 is empty
                                if (opt2Day != null || opt2Every != null || opt2The != null) {
                                    throw new RowException(getMessage(XlsMessageConstants.OPTIONS_EXCLUSIVE));
                                }
                                // set data
                                recurrenceDayNumber = opt1Day;
                                recurEvery = opt1Every;
                            }
                            // validate location
                            validateMandatoryField(meetingLocation);
                        }
                        /*
                         * Create meeting data
                         */
                        MeetingDto meetingDto = null;
                        if (recurrenceType != null) {
                            final LocalDate meetingStartDate = new LocalDate();
                            final MeetingRecurrenceDto meetingRecurrenceDto = new MeetingRecurrenceDto(
                                    recurrenceDayNumber, recurrenceWeekOfMonth, recurrenceDayOfWeek);
                            final MeetingDetailsDto meetingDetailsDto = new MeetingDetailsDto(recurrenceType, null,
                                    recurEvery, meetingRecurrenceDto);
                            final MeetingTypeDto meetingTypeDto = new MeetingTypeDto(MeetingType.CUSTOMER_MEETING
                                    .getValue().intValue(), null, null);
                            meetingDto = new MeetingDto(meetingStartDate, meetingLocation, meetingTypeDto,
                                    meetingDetailsDto);
                        } else {

                        }

                        String clientName = buildName(clientFirstName, clientMiddleName, clientLastName,
                                clientSecondLastName);
                        customerDao.validateClientForDuplicateNameOrGovtId(clientName, dateOfBirth, governmentId);

                        /* Create dto's */
                        /* address */
                        final Address addressObject = new Address(address, null, null, cityDistrict, state, country,
                                postalCode, telephone);
                        final AddressDto addressDto = Address.toDto(addressObject);
                        /* Personal details */
                        final ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(CLIENT_NAME_TYPE,
                                salutationId, clientFirstName, clientMiddleName, clientLastName, clientSecondLastName);
                        final ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(ethinicityId,
                                citizenshipId, handicappedId, activityId, maritalStatusId, educationLevelId,
                                numberOfChildren, genderId, povertyStatusId);
                        /* Spouse details */
                        ClientNameDetailDto spouseNameDetailDto = null;
                        if (spouseFatherNameType == null) {
                            spouseNameDetailDto = new ClientNameDetailDto();
                            spouseNameDetailDto.setFirstName("");
                            spouseNameDetailDto.setLastName("");
                        } else {
                            spouseNameDetailDto = new ClientNameDetailDto(spouseFatherNameType, null, spouseFirstName,
                                    spouseMiddleName, spouseLastName, spouseSecondLastName);
                        }
                        /* branch office */
                        Short officeId = 0;
                        Short loanOfficerId = null;
                        Short groupFlagValue = 1;
                        if (StringUtils.isBlank(groupGlobalNum)) {
                            if (statusId == CustomerStatus.CLIENT_ACTIVE.getValue() && meetingDto == null) {
                                String error = getRowError(friendlyRowNumber)
                                        + getMessage(XlsMessageConstants.NO_MEETING_ERROR);
                                errorsList.add(error);
                                continue;
                            }

                            groupFlagValue = 0;
                            officeId = branchOfficeId;

                            List<PersonnelBO> officers = legacyPersonnelDao.getActiveLoanOfficersUnderOffice(officeId);
                            if (officers.isEmpty()) {
                                String error = getCellError(friendlyRowNumber,
                                        XlsImportConstants.BRANCH_SHORT_NAME_CELL)
                                        + getMessage(XlsMessageConstants.NO_OFFICERS_ERROR, branchShortName);
                                errorsList.add(error);
                                continue;
                            }

                            loanOfficerId = null;
                            for (PersonnelBO officer : officers) {
                                if (officer.getDisplayName().equals(loanOfficer)) {
                                    loanOfficerId = officer.getPersonnelId();
                                    break;
                                }
                            }

                        } else {
                            validateGroupStatus(groupGlobalNum, statusId);
                        }

                        /* Not imported values */
                        final boolean trained = false;
                        final Date trainedDate = null;
                        final java.sql.Date mfiJoiningDate = null;
                        final String externalId = "";
                        final InputStream picture = null;
                        final List<ApplicableAccountFeeDto> feesToApply = null;
                        final List<ClientNameDetailDto> familyNames = null;
                        final List<ClientFamilyDetailDto> familyDetails = null;
                        final List<Short> selectedSavingsProducts = null;
                        /* Final dto */
                        final ClientCreationDetail clientCreationDetail = new ClientCreationDetail(selectedSavingsProducts,
                                clientName, statusId, mfiJoiningDate, externalId, addressDto, formedBy, dateOfBirth,
                                governmentId, trained, trainedDate, groupFlagValue, clientNameDetailDto,
                                clientPersonalDetailDto, spouseNameDetailDto, picture, feesToApply, groupGlobalNum,
                                familyNames, familyDetails, loanOfficerId, officeId, activationDate);

                        validateDuplicateCustomers(clientCreationDetail, parsedClientDetails);

                        final ImportedClientDetail importedClientDetail = new ImportedClientDetail(clientCreationDetail,
                                clientGlobalNum, meetingDto);
                        parsedClientDetails.add(importedClientDetail);
                    } catch (RowException ex) {
                        final String error = getRowError(friendlyRowNumber) + ex.getMessage();
                        errorsList.add(error);
                    } catch (CustomerException ex) {
                        final String error = getRowError(friendlyRowNumber)
                                + getMessage(XlsMessageConstants.DUPLICATE_CLIENT_ERROR);
                        errorsList.add(error);
                    } catch (Exception ex) {
                        final String error = getCellError(friendlyRowNumber, currentCell) + ex.getMessage();
                        errorsList.add(error);
                    }
                }
            }
        } catch (Exception ex) {
            errorsList.add(getMessage(XlsMessageConstants.ERROR_READING_DOCUMENT, ex.getMessage()));
        }
        return new ParsedClientsDto(errorsList, parsedClientDetails);
    }

    private String getMessage(final XlsMessageConstants xlsMessageConstant) {
        return messageSource.getMessage(xlsMessageConstant.getText(), null, locale);
    }

    private String getMessage(final XlsMessageConstants xlsMessageConstant, final String param) {
        return messageSource.getMessage(xlsMessageConstant.getText(), new Object[] { param }, locale);
    }

    private String getMessage(final String key) {
        return messageSource.getMessage(key, null, locale);
    }

    private String getMessage(final XlsMessageConstants xlsMessageConstant, final Object[] params) {
        return messageSource.getMessage(xlsMessageConstant.getText(), params, locale);
    }

    private String getRowError(final int friendlyRowNumber) {
        final StringBuilder sb = new StringBuilder(getMessage(XlsMessageConstants.ROW_ERROR,
                String.valueOf(friendlyRowNumber)));
        sb.append(": ");
        return sb.toString();
    }

    private String getCellError(final int friendlyRowNumber, final XlsImportConstants cell) {
        final StringBuilder sb = new StringBuilder(getMessage(XlsMessageConstants.ROW_ERROR,
                String.valueOf(friendlyRowNumber)));
        sb.append(", ");
        sb.append(getMessage(XlsMessageConstants.CELL_ERROR, getMessage(cell.getCellNameKey())));
        sb.append(": ");
        return sb.toString();
    }

    private String getCellStringValue(final HSSFRow row, final XlsImportConstants xlsImportConstant) {
        final HSSFCell cell = row.getCell(xlsImportConstant.value(), HSSFRow.RETURN_BLANK_AS_NULL);
        if (cell != null) {
            switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                HSSFRichTextString richText = cell.getRichStringCellValue();
                return (richText == null) ? "" : richText.getString();
            case HSSFCell.CELL_TYPE_NUMERIC:
                int intVal = (int) cell.getNumericCellValue();
                return String.valueOf(intVal);
            default:
                return "";
            }
        } else {
            return "";
        }
    }

    private Date getCellDateValue(final HSSFRow row, final XlsImportConstants xlsImportConstant) throws Exception {
        HSSFCell cell = null;
        try {
            cell = row.getCell(xlsImportConstant.value(), HSSFRow.RETURN_BLANK_AS_NULL);
            return (cell == null) ? null : cell.getDateCellValue();
        } catch (Exception ex) {
            String invalidDateString = (cell == null) ? "" : getCellStringValue(row, xlsImportConstant);
            throw new Exception(getMessage(XlsMessageConstants.INVALID_DATE, invalidDateString));
        }
    }

    private Integer getCellIntegerValue(final HSSFRow row, final XlsImportConstants xlsImportConstant) {
        final HSSFCell cell = row.getCell(xlsImportConstant.value(), HSSFRow.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return null;
        } else {
            Double val = cell.getNumericCellValue();
            return val.intValue();
        }
    }

    private Integer getValueElementId(final String value, final List<ValueListElement> elements) throws CellException {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        for (ValueListElement element : elements) {
            if (element.getName().toLowerCase().equals(value.toLowerCase())) {
                return element.getId();
            }
        }

        throw new CellException(getMessage(XlsMessageConstants.NOT_FOUND_ERROR, value));

    }

    private String buildName(final String firstName, final String middleName, final String lastName,
            final String secondLastName) {
        final StringBuilder sb = new StringBuilder(firstName);

        if (!StringUtils.isBlank(middleName)) {
            sb.append(" ").append(middleName);
        }

        sb.append(" ").append(lastName);

        if (!StringUtils.isBlank(secondLastName)) {
            sb.append(" ").append(secondLastName);
        }
        return sb.toString();
    }

    private Short getSpouseNameType(final String spouseOrFather) throws CellException {
        if (StringUtils.isBlank(spouseOrFather)) {
            return null;
        }

        final String spouse = getMessage(XlsMessageConstants.SPOUSE);
        if (spouse.equals(spouseOrFather)) {
            return SPOUSE_NAME_TYPE;
        }
        final String father = getMessage(XlsMessageConstants.FATHER);
        if (father.equals(spouseOrFather)) {
            return FATHER_NAME_TYPE;
        }

        throw new CellException(getMessage(XlsMessageConstants.NOT_FOUND_ERROR, spouseOrFather));
    }

    private Short intToShort(final Integer arg) {
        return (arg == null) ? null : arg.shortValue();
    }

    private Short getOfficerId(final String recruitedBy) throws CellException {
        if (StringUtils.isBlank(recruitedBy)) {
            return null;
        } else {
            try {
                PersonnelBO personnelBO = legacyPersonnelDao.getPersonnelByDisplayName(recruitedBy);
                return personnelBO.getPersonnelId();
            } catch (Exception ex) {
                throw new CellException(getMessage(XlsMessageConstants.NOT_FOUND_ERROR, recruitedBy));
            }
        }
    }

    private void validateGlobalCustNum(final String globalCustNum, final List<String> newCustGlobalNums)
            throws CellException {
        if (!StringUtils.isBlank(globalCustNum) && !newCustGlobalNums.contains(globalCustNum)) {
            if (customerDao.findClientBySystemId(globalCustNum) == null
                    && customerDao.findGroupBySystemId(globalCustNum) == null
                    && customerDao.findCenterBySystemId(globalCustNum) == null) {
                return; // success
            }
        }
        throw new CellException(getMessage(XlsMessageConstants.DUPLICATE_GLOBAL_NUM_ERROR, globalCustNum));
    }

    private Short statusToShort(final String status) throws CellException {
        if (StringUtils.isBlank(status)) {
            return CustomerStatus.CLIENT_PENDING.getValue();
        } else if (status.equals(getMessage(XlsMessageConstants.PARTIALLY_APPROVED))) {
            return CustomerStatus.CLIENT_PARTIAL.getValue();
        } else if (status.equals(getMessage(XlsMessageConstants.PENDING_APPROVAL))) {
            return CustomerStatus.CLIENT_PENDING.getValue();
        } else if (status.equals(getMessage(XlsMessageConstants.ACTIVE))) {
            return CustomerStatus.CLIENT_ACTIVE.getValue();
        } else {
            throw new CellException(getMessage(XlsMessageConstants.NOT_FOUND_ERROR, status));
        }
    }

    private void validateMandatoryField(final String string) throws CellException {
        if (string == null || StringUtils.isBlank(string)) {
            throw new CellException(getMessage(XlsMessageConstants.MANDATORY_ERROR));
        }
    }

    private void validateMandatoryField(final Object arg) throws CellException {
        if (arg == null) {
            throw new CellException(getMessage(XlsMessageConstants.MANDATORY_ERROR));
        }
    }

    private void validateGroup(final String globalGroupNum) throws CellException {
        if (StringUtils.isBlank(globalGroupNum)) {
            return;
        }

        final GroupBO group = customerDao.findGroupBySystemId(globalGroupNum);
        if (group == null) {
            throw new CellException(getMessage(XlsMessageConstants.GROUP_NOT_FOUND_ERROR, globalGroupNum));
        }

        final CustomerStatus groupStatus = group.getStatus();
        if (groupStatus == CustomerStatus.GROUP_CANCELLED) {
            throw new CellException(getMessage(XlsMessageConstants.GROUP_CANCELED, globalGroupNum));
        } else if (groupStatus == CustomerStatus.GROUP_CLOSED) {
            throw new CellException(getMessage(XlsMessageConstants.GROUP_CLOSED, globalGroupNum));
        }

    }

    private void validateGroupStatus(final String globalGroupNum, final Short customerStatusId) throws RowException {
        final CustomerStatus groupStatus = customerDao.findGroupBySystemId(globalGroupNum).getStatus();
        final CustomerStatus clientStatus = CustomerStatus.fromInt(customerStatusId);

        if (isGroupStatusLower(clientStatus, groupStatus)) {
            throw new RowException(getMessage(XlsMessageConstants.GROUP_BAD_STATUS));
        }
    }

    private boolean isGroupStatusLower(final CustomerStatus clientStatus, final CustomerStatus groupStatus) {
        if (clientStatus.equals(CustomerStatus.CLIENT_PENDING)) {
            if (groupStatus.equals(CustomerStatus.GROUP_PARTIAL)) {
                return true;
            }
        } else if (clientStatus.equals(CustomerStatus.CLIENT_ACTIVE) || clientStatus.equals(CustomerStatus.CLIENT_HOLD)) {
            if (groupStatus.equals(CustomerStatus.GROUP_PARTIAL) || groupStatus.equals(CustomerStatus.GROUP_PENDING)) {
                return true;
            }
        }
        return false;
    }

    private Short getBranchId(final String branchShortName, final List<OfficeDto> allOfficess) throws CellException {

        if (StringUtils.isBlank(branchShortName)) {
            return null;
        }

        for (OfficeDto office : allOfficess) {
            if (office.getOfficeShortName().equals(branchShortName)) {
                return office.getId();
            }
        }
        throw new CellException(getMessage(XlsMessageConstants.OFFICE_NOT_FOUND_ERROR, branchShortName));
    }

    private void validateDuplicateCustomers(final ClientCreationDetail client,
            final List<ImportedClientDetail> importedClients) throws RowException {
        final Date dob = client.getDateOfBirth();
        final String clientName = client.getClientName();

        for (ImportedClientDetail importedClient : importedClients) {
            final Date otherDob = importedClient.getClientCreationDetail().getDateOfBirth();
            final String otherName = importedClient.getClientCreationDetail().getClientName();

            if (dob.equals(otherDob) && clientName.equals(otherName)) {
                throw new RowException(getMessage(XlsMessageConstants.DUPLICATE_CLIENT_ERROR));
            }
        }
    }

    private void validateAge(final Date dateOfBirth) throws CellException, ConfigurationException {
        final DateMidnight dob = new DateMidnight(dateOfBirth);
        final DateTime now = new DateTime();
        final Years age = Years.yearsBetween(dob, now);

        final int minimumAge = ClientRules.getMinimumAge();
        final int maximumAge = ClientRules.getMaximumAge();

        if (age.getYears() < 0) {
            throw new CellException(getMessage(XlsMessageConstants.FUTURE_DATE));
        } else if (ClientRules.isAgeCheckEnabled() && (age.getYears() < minimumAge || age.getYears() > maximumAge)) {
            throw new CellException(getMessage(XlsMessageConstants.INVALID_AGE, new Object[] { minimumAge, maximumAge }));
        }
    }

    private Integer getRecurrenceType(final String meetingFrequency) throws CellException {
        RecurrenceType recurrenceType;

        if (meetingFrequency.equals(WEEK_FREQUENCY)) {
            recurrenceType = RecurrenceType.WEEKLY;
        } else if (meetingFrequency.equals(MONTH_FREQUENCY)) {
            recurrenceType = RecurrenceType.MONTHLY;
        } else if (StringUtils.isBlank(meetingFrequency)) {
            recurrenceType = null;
        } else {
            throw new CellException(getMessage(XlsMessageConstants.NOT_FOUND_ERROR, meetingFrequency));
        }
        return (recurrenceType == null) ? null : recurrenceType.getValue().intValue();
    }

    private Integer getDayValue(final String dayString) throws CellException {
        WeekDay day;

        if (dayString.equals(getMessage(XlsMessageConstants.WEEKDAYS_MONDAY))) {
            day = WeekDay.MONDAY;
        } else if (dayString.equals(getMessage(XlsMessageConstants.WEEKDAYS_TUESDAY))) {
            day = WeekDay.TUESDAY;
        } else if (dayString.equals(getMessage(XlsMessageConstants.WEEKDAYS_WEDNESDAY))) {
            day = WeekDay.WEDNESDAY;
        } else if (dayString.equals(getMessage(XlsMessageConstants.WEEKDAYS_THURSDAY))) {
            day = WeekDay.THURSDAY;
        } else if (dayString.equals(getMessage(XlsMessageConstants.WEEKDAYS_FRIDAY))) {
            day = WeekDay.FRIDAY;
        } else if (dayString.equals(getMessage(XlsMessageConstants.WEEKDAYS_SATURDAY))) {
            day = WeekDay.SATURDAY;
        } else if (StringUtils.isBlank(dayString)) {
            day = null;
        } else if (dayString.equals(getMessage(XlsMessageConstants.WEEKDAYS_SUNDAY))) {
            throw new CellException(getMessage(XlsMessageConstants.SUNDAY_MEETING));
        } else {
            throw new CellException(getMessage(XlsMessageConstants.INVALID_DAY, dayString));
        }

        return (day == null) ? null : day.getValue().intValue();
    }

    private Integer getDayRankValue(final String rankString) throws CellException {
        RankOfDay rankOfDay;

        if (StringUtils.isBlank(rankString)) {
            rankOfDay = null;
        } else if (getMessage(XlsMessageConstants.DAYRANK_FIRST).equals(rankString)) {
            rankOfDay = RankOfDay.FIRST;
        } else if (getMessage(XlsMessageConstants.DAYRANK_SECOND).equals(rankString)) {
            rankOfDay = RankOfDay.SECOND;
        } else if (getMessage(XlsMessageConstants.DAYRANK_THIRD).equals(rankString)) {
            rankOfDay = RankOfDay.THIRD;
        } else if (getMessage(XlsMessageConstants.DAYRANK_FOURTH).equals(rankString)) {
            rankOfDay = RankOfDay.FOURTH;
        } else if (getMessage(XlsMessageConstants.DAYRANK_LAST).equals(rankString)) {
            rankOfDay = RankOfDay.LAST;
        } else {
            throw new CellException(getMessage(XlsMessageConstants.NOT_FOUND_ERROR, rankString));
        }

        return (rankOfDay == null) ? null : rankOfDay.getValue().intValue();
    }

    private void validatePositive(final Number number) throws CellException {
        if (number != null && number.intValue() <= 0) {
            throw new CellException(getMessage(XlsMessageConstants.NOT_POSITIVE_NUMBER));
        }
    }

    private void validateMonthDay(final Integer day) throws CellException {
        if (day != null && (day <= 0 || day > 31)) {
            throw new CellException(getMessage(XlsMessageConstants.INVALID_MONTH));
        }
    }

}
