package org.mifos.webservices;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.servicefacade.ClientServiceFacade;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.customers.client.business.NameType;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.ApplicableAccountFeeDto;
import org.mifos.dto.domain.ClientCreationDetail;
import org.mifos.dto.domain.CustomerDetailsDto;
import org.mifos.dto.domain.MeetingDetailsDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.MeetingRecurrenceDto;
import org.mifos.dto.domain.MeetingTypeDto;
import org.mifos.dto.domain.SavingsDetailDto;
import org.mifos.dto.screen.ClientFamilyDetailDto;
import org.mifos.dto.screen.ClientNameDetailDto;
import org.mifos.dto.screen.ClientPersonalDetailDto;
import org.mifos.security.AuthenticationAuthorizationServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class ClientRequestEndpoint {

    private static final String NAMESPACE_URI = "http://mifos.org/portfolio/schemas";
    private XPath firstNameExpression;
    private XPath lastNameExpression;
    
    private final AuthenticationAuthorizationServiceFacade authenticationAuthorizationServiceFacade;
    private final ClientServiceFacade clientServiceFacade;
    

    @Autowired
    public ClientRequestEndpoint(final AuthenticationAuthorizationServiceFacade authenticationAuthorizationServiceFacade,
            final ClientServiceFacade clientServiceFacade) throws JDOMException {
        
        this.authenticationAuthorizationServiceFacade = authenticationAuthorizationServiceFacade;
        this.clientServiceFacade = clientServiceFacade;
        
        Namespace namespace = Namespace.getNamespace("p", NAMESPACE_URI);
        
        firstNameExpression = XPath.newInstance("//p:FirstName");
        firstNameExpression.addNamespace(namespace);
        
        lastNameExpression = XPath.newInstance("//p:LastName");
        lastNameExpression.addNamespace(namespace);
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ClientRequest")
    @ResponsePayload
    public Element handleClientRequest(@RequestPayload Element clientRequest) throws JDOMException {
        
        String externalId = null;
        AddressDto addressDto = new AddressDto("here", "", "", "", "", "", "", "");

        List<ApplicableAccountFeeDto> feesToApply = new ArrayList<ApplicableAccountFeeDto>();
        CustomerStatus.CLIENT_ACTIVE.getValue();
        boolean trained = false;
        DateTime mfiJoiningDate = new DateTime().minusWeeks(2);
        DateTime activationDate = new DateTime().minusWeeks(1);
        List<Short> selectedSavingProducts = new ArrayList<Short>();

        Date dateOfBirth = new LocalDate(1990, 1, 1).toDateMidnight().toDate();
        String governmentId ="";
        Date trainedDate = new LocalDate(2000, 1, 1).toDateMidnight().toDate();
        Short groupFlag = YesNoFlag.NO.getValue(); // not in a group

        // magic numbers from default data
        Integer ethnicity = null;
        Integer citizenship = null;
        Integer handicapped = null;
        Integer businessActivities = null;
        Integer educationLevel = null;
        Short numChildren = 0;
        Short gender = 49;
        Short povertyStatus = null;
        Integer maritalStatus = ClientPersonalDetailDto.MARRIED;
        Short clientStatus = 3; // active

        ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(ethnicity,
                citizenship , handicapped , businessActivities , maritalStatus , educationLevel ,
                numChildren , gender, povertyStatus);
        
        InputStream picture = null;
        String parentGroupId = null;
        List<ClientNameDetailDto> familyNames = null;
        List<ClientFamilyDetailDto> familyDetails = null;

        List<SavingsDetailDto> allowedSavingProducts = new ArrayList<SavingsDetailDto>();
        
        // hard code office and loan officer information for now
        Short formedBy = Short.valueOf("1");
        Short loanOfficerId = Short.valueOf("1");
        Short officeId = Short.valueOf("1");
        
        // client name details
        Integer salutation = 1; // mr?
        String firstName = this.firstNameExpression.valueOf(clientRequest);
        String middleName = null;
        String secondLastName = null;
        String lastName = this.lastNameExpression.valueOf(clientRequest);
        String displayName = firstName + " " + lastName;
        
        ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT.getValue(),
                salutation, firstName, middleName, lastName, secondLastName);
        
        ClientNameDetailDto spouseFatherName = new ClientNameDetailDto(NameType.SPOUSE.getValue(),
                salutation, firstName, middleName, lastName, secondLastName);
        
        ClientCreationDetail clientCreationDetail = new ClientCreationDetail(selectedSavingProducts,
                displayName , clientStatus, mfiJoiningDate.toDate(), externalId,
                addressDto, formedBy, dateOfBirth , governmentId , trained, trainedDate , groupFlag,
                clientNameDetailDto, clientPersonalDetailDto, spouseFatherName, picture, feesToApply,
                parentGroupId, familyNames , familyDetails , loanOfficerId, officeId, activationDate.toLocalDate());

        // meeting information - hardcoded for now
        Integer weeklyRecurrenceId = RecurrenceType.WEEKLY.getValue().intValue();
        String recurrenceName = "weekly";
        Integer everyOneWeek = Integer.valueOf(1);
        MeetingRecurrenceDto onMonday = new MeetingRecurrenceDto(null, null, WeekDay.MONDAY.getValue().intValue());
        
        MeetingDetailsDto meetingDetailsDto = new MeetingDetailsDto(weeklyRecurrenceId, recurrenceName, everyOneWeek, onMonday);
        MeetingTypeDto meetingType = new MeetingTypeDto(MeetingType.CUSTOMER_MEETING.getValue().intValue(), "loan officer - customer meeting", "description..");
        MeetingDto meeting = new MeetingDto(new LocalDate(), "meeting location", meetingType, meetingDetailsDto);
        
        // make sure an authentication token is set in security context
        this.authenticationAuthorizationServiceFacade.reloadUserDetailsForSecurityContext("mifos");
        
        CustomerDetailsDto clientResponse = this.clientServiceFacade.createNewClient(clientCreationDetail, meeting,allowedSavingProducts);
        
        return createResponse(clientResponse);
    }

    private Element createResponse(CustomerDetailsDto clientResponse) {
        Namespace namespace = Namespace.getNamespace("p", NAMESPACE_URI);
        Element element = new Element("Account", namespace);
        element.setText(clientResponse.getGlobalCustNum());
        return element;
    }
}