#These are files required for the test case to run

require 'watir'
require 'modules/common/TestClass'
require 'modules/common/inputs'
require 'modules/Clients/ClientCreateEdit'
require 'modules/Centers/CenterCreateEdit'

class ClientTest

#Creating a new client and center object
  clientobject=ClientCreateEdit.new
  centerobject=CenterCreateEdit.new

#Login to the application and connecting to database to get required labels form the DB

  clientobject.client_login
  clientobject.database_connection
  
#Load the Labels,Error Messages and Button Names form the properties file  
  clientobject.propertis_load
  
#Checks for the create new client link, check for the page select group and click cancel from the group select page
  
  clientobject.check_createnewclient_link
  clientobject.select_grouppage_check
  clientobject.click_cancel_from_group_select
  clientobject.no_groups_while_search
  clientobject.check_next_link_in_search_group_page
  clientobject.check_next_link_in_search_group_page
  
#Search for the group by clicking on continue and Select the group from the group select page
 
  clientobject.select_group
  
#checking for various mandatory conditions and checks for appropriate error message 
 
  clientobject.check_all_mandatory_fields_for_client
  clientobject.check_all_mandatory_when_salutation_selected
  clientobject.check_all_mandatory_when_salutation_fname_entered
  clientobject.check_all_mandatory_when_salutation_fname_lname_entered
  clientobject.check_all_mandatory_when_salutation_fname_lname_dob_entered
  clientobject.check_all_mandatory_when_salutation_fname_lname_dob_gender_entered
  clientobject.check_all_mandatory_when_salutation_fname_lname_dob_gender_religion_entered
  clientobject.check_all_mandatory_when_salutation_fname_lname_dob_gender_religion_education_entered
  clientobject.check_all_mandatory_when_salutation_fname_lname_dob_gender_religion_education_SpouseFatherType_entered
  clientobject.check_mandatory_when_SpouseorFatherLastName_AdditionalInformation_not_entered
  clientobject.check_mandatory_when_AdditionalInformation_not_entered
  
#Opens the Fist sheet in Testdata.xls file to create client with all mandatory data
  
  filename=File.expand_path(File.dirname($PROGRAM_NAME))+"/data/testdata.xls"
  clientobject.open(filename,1)
  rowid=-1
  
#  creating a client with different sets of mandatory data it reads the data from the opened .xls file
# and put send the values to create client test cases
  
  while(rowid<$maxrow*$maxcol-1)
    clientobject.read_client_values(rowid,1)
    clientobject.create_client_with_all_mandatory_data(clientobject.Salutation,clientobject.Fname,clientobject.Lname,clientobject.Date,clientobject.Month,\
                                                       clientobject.Year,clientobject.Gender,clientobject.Religion,clientobject.Sorftype,clientobject.Sorffname,\
                                                       clientobject.Sorflname,clientobject.Custom)
    clientobject.check_create_client_mfi_mandatory()
    clientobject.create_client_enter_mfidata(clientobject.Salutation,clientobject.Fname,clientobject.Lname,clientobject.Date,clientobject.Month,\
                                                       clientobject.Year,clientobject.Gender,clientobject.Religion,clientobject.Sorftype,clientobject.Sorffname,\
                                                       clientobject.Sorflname,clientobject.Custom)
    clientobject.submit_data(clientobject.Statusname)
    clientobject.check_client_charges_label()
    clientobject.db_check()
    rowid+=$maxcol
  end
  
#Opening the second sheet in the testdata.xls file
  
  clientobject.open(filename,2)
  rowid=-1
  
#Creating and editing Client with different sets of mandatory and optional data.
#data comes from the second sheet of testdata.xls
  
  while(rowid<$maxrow*$maxcol-1)
    clientobject.read_client_values(rowid,2)
    clientobject.select_group()
    clientobject.client_create_with_all_data(clientobject.Salutation,clientobject.Fname,clientobject.Mname,clientobject.Sname,clientobject.Lname,clientobject.Govtid,\
                                             clientobject.Date,clientobject.Month,clientobject.Year,clientobject.Gender,clientobject.Mstatus,clientobject.Noofchildren,\
                                             clientobject.Religion,clientobject.Education,clientobject.Sorftype,clientobject.Sorffname,clientobject.Sorfmname,\
                                             clientobject.Sorfsname,clientobject.Sorflname,clientobject.Address1,clientobject.Address2,clientobject.Address3,clientobject.City,\
                                             clientobject.State,clientobject.Country,clientobject.Pcode,clientobject.Phone,clientobject.Custom)
    clientobject.click_continue() 
    clientobject.check_create_client_mfi_mandatory
    clientobject.client_create_enter_all_data_mfi(clientobject.Externalid,clientobject.Tdate,clientobject.Tmonth,clientobject.Tyear)
    clientobject.click_preview()
    clientobject.edit_personal_information_from_review(clientobject.Salutation,clientobject.Fname,clientobject.Mname,clientobject.Sname,clientobject.Lname,clientobject.Govtid,\
                                             clientobject.Date,clientobject.Month,clientobject.Year,clientobject.Gender,clientobject.Mstatus,clientobject.Noofchildren,\
                                             clientobject.Religion,clientobject.Education,clientobject.Sorftype,clientobject.Sorffname,clientobject.Sorfmname,\
                                             clientobject.Sorfsname,clientobject.Sorflname,clientobject.Address1,clientobject.Address2,clientobject.Address3,clientobject.City,\
                                             clientobject.State,clientobject.Country,clientobject.Pcode,clientobject.Phone,clientobject.Custom)
    clientobject.edit_mfi_information_from_review(clientobject.Externalid,clientobject.Tdate,clientobject.Tmonth,clientobject.Tyear)
    
#Submits the client data with given status, 
#change the status and checks the entity in View change log page
#checks for different labels and functionality of links 
    
    clientobject.submit_data(clientobject.Statusname)
    clientobject.check_client_charges_label()
    clientobject.db_check()
    
#Searching the client from the Clients&Accounts page and editing the personnnel and MFI informations from client details page
#Editing the group membership from client details page
#checking for the view all closed account link functionality

    clientobject.search_client_from_clients_accounts_page()
    clientobject.check_edit_client_personalinformation_link_from_details_page
    clientobject.edit_client_personnel_enter_data(clientobject.Salutation,clientobject.Fname,clientobject.Mname,clientobject.Sname,clientobject.Lname,clientobject.Govtid,\
                                             clientobject.Date,clientobject.Month,clientobject.Year,clientobject.Gender,clientobject.Mstatus,clientobject.Noofchildren,\
                                             clientobject.Religion,clientobject.Education,clientobject.Sorftype,clientobject.Sorffname,clientobject.Sorfmname,\
                                             clientobject.Sorfsname,clientobject.Sorflname,clientobject.Address1,clientobject.Address2,clientobject.Address3,clientobject.City,\
                                             clientobject.State,clientobject.Country,clientobject.Pcode,clientobject.Phone,clientobject.Custom)
    clientobject.check_edit_client_mfi_data_link_from_details_page
    clientobject.edit_client_mfi_enter_data(clientobject.Externalid)
#Checking for edit group membership link
#checking for edit group membership link functionality
#editing the group membership  
   
    clientobject.check_edit_group_membership_link()
    clientobject.click_edit_group_membership_link()
    clientobject.edit_group_membership()
#Checking for the View Summarised historical data link exist
#Checking for the View Summarised historical data link functionality
    clientobject.check_view_summarized_historical_Data_link_exist()
    clientobject.check_view_summarized_historical_Data_link_functinality()
#Checking for View closed account link functinality
    clientobject.check_view_all_closed_accounts_link
    clientobject.click_view_all_closed_accounts_link
#Checking for the Add notes and see all notes links
#Clicking on the links and check the page whether it is redirecting to proper page
#Adding a note
    clientobject.check_add_notes_link
    clientobject.click_add_notes_link
    clientobject.check_mandatory_in_add_notes_page
    clientobject.enter_data_in_add_note
    clientobject.check_for_see_all_notes_link
    clientobject.click_see_all_notes_link    
    
    rowid+=$maxcol
  end
  
 #Opening the third sheet in the testdata.xls file to create client outside the group
  
  clientobject.open(filename,3)
  rowid=-1
  
 #Creating and editing of client when client is out side the group with different sets of mandatory and optional data
 #creating meeting to the client
 #Removing the admin fee
 
  while(rowid<$maxrow*$maxcol-1)
    clientobject.read_client_values(rowid,3)
    clientobject.check_create_client_out_of_group_link
    clientobject.click_create_client_out_of_group_link
    clientobject.create_client_out_of_group(clientobject.Salutation,clientobject.Fname,clientobject.Mname,clientobject.Sname,clientobject.Lname,clientobject.Govtid,clientobject.Date,\
                                            clientobject.Month,clientobject.Year,clientobject.Gender,clientobject.Mstatus,clientobject.Noofchildren,clientobject.Religion,\
                                            clientobject.Education,clientobject.Sorftype,clientobject.Sorffname,clientobject.Sorfmname,clientobject.Sorfsname,clientobject.Sorflname,\
                                            clientobject.Address1,clientobject.Address2,clientobject.Address3,clientobject.City,clientobject.State,clientobject.Country,clientobject.Pcode,\
                                            clientobject.Phone,clientobject.Custom,clientobject.Externalid,clientobject.Tdate,clientobject.Tmonth,clientobject.Tyear)
    clientobject.select_loan_officer
    centerobject.create_meeting(clientobject.Frequncymeeting,clientobject.Monthtype,clientobject.Reccurweek,clientobject.Weekweekday,clientobject.Monthday,clientobject.Monthmonth,\
                                clientobject.Monthrank,clientobject.Monthweek,clientobject.Monthmonthrank,clientobject.Meetingplace)
    clientobject.click_preview
    clientobject.client_create_remove_admin_fee_from_review_page()
    clientobject.submit_data(clientobject.Statusname)
    
 #Check for edit branch membership link
 #check for edit branch membership working
 #Editing the branch membership   
    clientobject.check_edit_branch_member_link_exist
    clientobject.check_edit_branch_member_link_functionality
    clientobject.edit_branch_membership
    rowid+=$maxcol
  end
end
