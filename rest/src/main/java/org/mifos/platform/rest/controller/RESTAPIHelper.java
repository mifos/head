package org.mifos.platform.rest.controller;

import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.LocalDate;
import org.mifos.application.servicefacade.CreateClientNameDetailDto;
import org.mifos.application.servicefacade.CreatePersonalDetailDto;
import org.mifos.application.servicefacade.CreationAccountPenaltyDto;
import org.mifos.application.servicefacade.CreationAddresDto;
import org.mifos.application.servicefacade.CreationFeeDto;
import org.mifos.application.servicefacade.CreationGLIMAccountsDto;
import org.mifos.application.servicefacade.CreationMeetingDto;

public class RESTAPIHelper {

    public static class ErrorMessage {
        public static final String INVALID_AMOUNT = "please specify correct amount";
        public static final String NON_NEGATIVE_AMOUNT = "amount must be grater than 0";
        public static final String NOT_ACTIVE_ACCOUNT = "account is not in active state.";
        public static final String INVALID_NOTE = "note is not specified";
        public static final String INVALID_FEE_ID = "invalid fee Id";
        public static final String INVALID_DATE_STRING = "string is not valid date";
        public static final String FUTURE_DATE = "Date can not be a future date.";
        public static final String INVALID_PAYMENT_TYPE_ID = "invalid payment type Id";
        public static final String INVALID_PRODUCT_ID = "invalid product Id";
        public static final String INVALID_GLOABAL_CUSTOMER_NUM = "invalid global customer number";
        public static final String INVALID_MEETING = "meeting can not be null";
        public static final String INVALID_DISPLAY_NAME = "invalid customer name";
        public static final String INVALID_LOAN_OFFICER_ID = "invalid loan officer id";
        public static final String INVALID_OFFICE_ID = "invalid office id";
        public static final String INVALID_MFI_DATE = "mfiJoiningDate is mandatory";
        public static final String INVALID_GLIM_AMOUNT = "individual amont must be equal glim amount";
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class CreationAddresDtoMixIn {

        @JsonCreator
        public CreationAddresDtoMixIn(@JsonProperty("address1") String address1,
                @JsonProperty("address2") String address2, @JsonProperty("address3") String address3,
                @JsonProperty("city") String city, @JsonProperty("state") String state,
                @JsonProperty("country") String country, @JsonProperty("zip") String zip,
                @JsonProperty("phoneNumber") String phoneNumber) {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class CreationFeeDtoMixIn {

        @JsonCreator
        public CreationFeeDtoMixIn(@JsonProperty("feeId") Integer feeId, @JsonProperty("amount") String amount) {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class CreationMeetingDtoMixIn {

        @JsonCreator
        public CreationMeetingDtoMixIn(@JsonProperty("meetingStartDate") LocalDate meetingStartDate,
                @JsonProperty("meetingPlace") String meetingPlace,
                @JsonProperty("recurrenceType") Short recurrenceType, @JsonProperty("dayNumber") Short dayNumber,
                @JsonProperty("weekDay") Short weekDay, @JsonProperty("rankOfDay") Short rankOfDay,
                @JsonProperty("recurAfter") Short recurAfter) {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class CenterCreationDetailMixIn {

        @JsonCreator
        public CenterCreationDetailMixIn(@JsonProperty("mfiJoiningDate") LocalDate mfiJoiningDate,
                @JsonProperty("displayName") String displayName, @JsonProperty("externalId") String externalId,
                @JsonProperty("loanOfficerId") Integer loanOfficerId, @JsonProperty("officeId") Integer officeId,
                @JsonProperty("address") CreationAddresDto creationAddresDto,
                @JsonProperty("accountFees") List<CreationFeeDto> creationFeeDto,
                @JsonProperty("meeting") CreationMeetingDto meeting) {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class CreateGroupCreationDetailDtoMixIn {

        @JsonCreator
        public CreateGroupCreationDetailDtoMixIn(@JsonProperty("centerId") Short centerId,
                @JsonProperty("officeId") Short officeId, @JsonProperty("loanOfficerId") Short loanOfficerId,
                @JsonProperty("displayName") String displayName, @JsonProperty("externalId") String externalId,
                @JsonProperty("address") CreationAddresDto creationAddresDto,
                @JsonProperty("accountFees") List<CreationFeeDto> creationFeeDto,
                @JsonProperty("customerStatus") Short customerStatus, @JsonProperty("trained") Boolean trained,
                @JsonProperty("trainedDate") String trainedOn, @JsonProperty("mfiJoiningDate") String mfiJoiningDate,
                @JsonProperty("activationDate") String activationDate,
                @JsonProperty("parentSystemId") String parentSystemId,
                @JsonProperty("meeting") CreationMeetingDto meeting) {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class CreateClientCreationDetailMixIn {

        @JsonCreator
        public CreateClientCreationDetailMixIn(@JsonProperty("officeId") Short officeId,
                @JsonProperty("loanOfficerId") Short loanOfficerId, @JsonProperty("formedBy") Short formedBy,
                @JsonProperty("parentGroupId") String parentGroupId, @JsonProperty("externalId") String externalId,
                @JsonProperty("groupFlag") Short groupFlag, @JsonProperty("governmentId") String governmentId,
                @JsonProperty("trained") boolean trained, @JsonProperty("trainedDate") LocalDate trainedDate,
                @JsonProperty("dateOfBirth") LocalDate dateOfBirth,
                @JsonProperty("activationDate") LocalDate activationDate,
                @JsonProperty("mfiJoiningDate") LocalDate mfiJoiningDate,
                @JsonProperty("customerStatus") Short customerStatus,
                @JsonProperty("personalDetail") CreatePersonalDetailDto personalDetail,
                @JsonProperty("clientNameDetail") CreateClientNameDetailDto clientNameDetail,
                @JsonProperty("address") CreationAddresDto address,
                @JsonProperty("accountFees") List<CreationFeeDto> accountFees,
                @JsonProperty("meeting") CreationMeetingDto meeting) {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class CreateClientNameDetailDtoMixIn {

        @JsonCreator
        public CreateClientNameDetailDtoMixIn(@JsonProperty("salutationId") Integer salutation,
                @JsonProperty("firstName") String firstName, @JsonProperty("middleName") String middleName,
                @JsonProperty("lastName") String lastName, @JsonProperty("secondLastName") String secondLastName) {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class CreatePersonalDetailDtoMixIn {

        @JsonCreator
        public CreatePersonalDetailDtoMixIn(@JsonProperty("ethnicity") Integer ethnicity,
                @JsonProperty("citizenship") Integer citizenship, @JsonProperty("handicapped") Integer handicapped,
                @JsonProperty("businessActivities") Integer businessActivities,
                @JsonProperty("maritalStatus") Integer maritalStatus,
                @JsonProperty("educationLevel") Integer educationLevel,
                @JsonProperty("numberOfChildren") Short numberOfChildren, @JsonProperty("gender") Short genderId,
                @JsonProperty("povertyStatus") Short povertyStatus) {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class CreationLoanAccountDtoMixIn {

        @JsonCreator
        public CreationLoanAccountDtoMixIn(@JsonProperty("customerId") Integer customerId,
                @JsonProperty("glim") Boolean glim, @JsonProperty("productId") Integer productId,
                @JsonProperty("accountState") Integer accountState, @JsonProperty("loanAmount") Double loanAmount,
                @JsonProperty("interesRate") Double interesRate,
                @JsonProperty("disbusmentDate") LocalDate disbursnebtDate,
                @JsonProperty("disbursalPaymentTypeId") Short disbursalPaymentTypeId,
                @JsonProperty("numberofInstallments") Integer numberOfInstallments,
                @JsonProperty("graceDuration") Integer graceDuration,
                @JsonProperty("sourceOfFoundId") Integer sourceOfFoundId,
                @JsonProperty("loanPurposeId") Integer loanPurposeId,
                @JsonProperty("collateralTypeId") Integer collateralTypeId,
                @JsonProperty("collateralNotes") String collateralNotes, @JsonProperty("externalId") String externalId,
                @JsonProperty("accountFees") List<CreationFeeDto> accountFees,
                @JsonProperty("accountPenalties") List<CreationAccountPenaltyDto> accountPenalties,
                @JsonProperty("minNumOfInstallments") Integer minNumOfInstallments,
                @JsonProperty("maxNumOfInstallments") Integer maxNumOfInstallments,
                @JsonProperty("minAllowedLoanAmount") Double minAllowedLoanAmount,
                @JsonProperty("maxAllowedLoanAmount") Double maxAllowedLoanAmount,
                @JsonProperty("predefinedAccountNumber") String predefinedAccountNumber,
                @JsonProperty("flagId") Short flagId,
                @JsonProperty("glimAccounts") List<CreationGLIMAccountsDto> glimAccounts) {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class CreationGLIMAccountsDtoMixIn {

        @JsonCreator
        public CreationGLIMAccountsDtoMixIn(@JsonProperty("globalId") String globalId,
                @JsonProperty("ammount") Double ammount, @JsonProperty("loanPurposeId") Integer loanPurposeId) {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class CreationAccountPenaltyDtoMixIn {

        @JsonCreator
        public CreationAccountPenaltyDtoMixIn(@JsonProperty("penaltyId") Integer penaltyId,
                @JsonProperty("amount") String amount) {
        }

    }
}
