from selenium import selenium
import unittest, time, re

# change these numbers to set up different combinations
NUM_OFFICES = 1
NUM_CENTERS_PER_OFFICE = 1
NUM_GROUPS_PER_CENTER = 1
NUM_CLIENTS_PER_GROUP = 1

HOST = "localhost"
USER = "mifos"
PASSWORD = "testmifos"

#HOST = "testblue.mifos.org"
#USER = "mifos"
#PASSWORD = "mifos"

class SeedDataManyClients(unittest.TestCase):
   """Set up offices, centers, groups, and clients with 2 loan products and 1 savings product.
      
      Requires Python 2.5 and Selenium-RC to run.
      
      To invoke:
      
      python SeedDataManyClients.py
   
   """

   def setUp(self):
      self.verificationErrors = []
      # if you have both Firefox 2 and 3 installed, change the third argument
      # of this instantiation call to "*chrome /usr/lib/firefox/firefox-2-bin"
      self.selenium = selenium("localhost", 4444, "*chrome", "http://%s:8080" % HOST)
      self.selenium.start()

   def tearDown(self):
      self.selenium.stop()
      self.assertEqual([], self.verificationErrors)

   def testAddSeedData(self):
      sel = self.selenium
      sel.open("/mifos/loginAction.do?method=logout")
      self.failUnless(sel.is_text_present("successfully logged out"))
      sel.type("userName", USER)
      sel.type("password", PASSWORD)
      self.clickAndWaitForPageToLoad("//input[@value='Login']")
      self.failUnless(sel.is_text_present("Welcome,"))
      self.loanProduct1 = self.addLoanProduct(sel)
      self.loanProduct2 = self.addLoanProduct(sel)
      self.savingsProduct1 = self.addSavingsProduct(sel)
      self.addOffices(sel)
      
   def addLoanProduct(self, sel):
      self.clickAndWaitForPageToLoad("link=Admin")
      self.failUnless(sel.is_text_present("Welcome to mifos administrative area"))
      self.clickAndWaitForPageToLoad("link=Define new Loan product")
      self.failUnless(sel.is_text_present("Add a new Loan product."))
      self.failUnless(sel.is_text_present("Enter Loan product information."))
      randomLoanProductName = sel.get_expression(sel.get_eval("'MyLoanProduct' + (new Date()).getTime()"))
      sel.type("prdOfferingName", randomLoanProductName)
      randomShortLoanProductName = sel.get_expression(sel.get_eval("'" + randomLoanProductName + "'.substring(22,26)"))
      sel.type("prdOfferingShortName", randomShortLoanProductName)
      sel.type("description", "auto-generated randomly-named loan product")
      sel.select("prdCategory", "label=Other")
      sel.select("prdApplicableMaster", "label=Clients")
      sel.type("minLoanAmount", "100")
      sel.type("maxLoanAmount", "10000")
      sel.type("defaultLoanAmount", "2000")
      sel.select("interestTypes", "label=Flat")
      sel.type("maxInterestRate", "30")
      sel.type("minInterestRate", "3")
      sel.type("defInterestRate", "3")
      sel.click("freqOfInstallments")
      sel.type("maxNoInstallments", "1000")
      sel.type("defNoInstallments", "11")
      sel.select("interestGLCode", "label=31101")
      sel.select("principalGLCode", "label=1503")
      self.clickAndWaitForPageToLoad("//input[@value='Preview']")
      self.failUnless(sel.is_text_present("Add a new Loan product."))
      self.failUnless(sel.is_text_present("Review & submit"))
      self.failUnless(sel.is_text_present("Product instance name: " + randomLoanProductName))
      self.failUnless(sel.is_text_present("Short name: " + randomShortLoanProductName))
      self.clickAndWaitForPageToLoad("submitBut")
      self.clickAndWaitForPageToLoad("link=View Loan product details now.")
      self.failUnless(sel.is_text_present(randomLoanProductName))
      return randomLoanProductName
      
   def addSavingsProduct(self, sel):
      randomSavingsProductName = sel.get_expression(sel.get_eval("'MySavingsProduct' + (new Date()).getTime()"))
      sel.open("/mifos/custSearchAction.do?method=getHomePage")
      self.clickAndWaitForPageToLoad("link=Admin")

      self.clickAndWaitForPageToLoad("link=Define new Savings product")
      sel.type("prdOfferingName", randomSavingsProductName)
      randomShortSavingsProductName = sel.get_expression(sel.get_eval("'" + randomSavingsProductName + "'.substring(22,26)"))
      sel.type("prdOfferingShortName", randomShortSavingsProductName)
      sel.type("description", "randomly generated savings product")
      sel.select("prdCategory", "label=Other")
      sel.select("prdApplicableMaster", "label=Clients")
      sel.select("savingsType", "label=Voluntary")
      sel.type("recommendedAmount", "0.0")
      sel.type("interestRate", "8")
      sel.select("interestCalcType", "label=Average Balance")
      sel.type("timeForInterestCacl", "30")
      sel.type("freqOfInterest", "12")
      sel.type("minAmntForInt", "100")
      sel.select("depositGLCode", "label=24101")
      sel.select("interestGLCode", "label=41101")
      self.clickAndWaitForPageToLoad("//input[@value='Preview']")
      self.clickAndWaitForPageToLoad("submitBut")
      self.clickAndWaitForPageToLoad("link=View Savings product details now.")
      return randomSavingsProductName

   def addOffices(self, sel):
      for index in xrange(0, NUM_OFFICES):
         print "Office %d:" % index,
         self.addOffice(sel)
      
   def addOffice(self, sel):
      self.clickAndWaitForPageToLoad("link=Admin")
      self.failUnless(sel.is_text_present("Administrative tasks"))
      self.clickAndWaitForPageToLoad("link=Define a new office")
      self.failUnless(sel.is_text_present("Add a new office"))
      self.failUnless(sel.is_text_present("Enter office information"))
      randomOfficeName = sel.get_expression(sel.get_eval("'MyOffice' + (new Date()).getTime()"))
      sel.type("officeName", randomOfficeName)
      randomShortOfficeName = sel.get_expression(sel.get_eval("'" + randomOfficeName + "'.substring(17,21)"))
      sel.type("shortName", randomShortOfficeName)
      sel.select("officeLevel", "label=Branch Office")
      self.waitForPageToLoad()
      sel.select("parentOfficeId", "label=regexp:Mifos\\s+HO")
      self.clickAndWaitForPageToLoad("//input[@value='Preview']")
      self.failUnless(sel.is_text_present("Add a new office"))
      self.failUnless(sel.is_text_present("Review & submit"))
      self.failUnless(sel.is_text_present("Office name: " + randomOfficeName))
      self.failUnless(sel.is_text_present("Office short name: " + randomShortOfficeName))
      self.failUnless(sel.is_text_present("Office type: Branch Office"))
      self.failUnless(sel.is_text_present("regexp:Parent office:.*Head Office"))
      self.clickAndWaitForPageToLoad("//input[@value='Submit']")
      self.failUnless(sel.is_text_present("You have successfully added a new office"))
      self.clickAndWaitForPageToLoad("link=View office details now")
      self.failUnless(sel.is_text_present(randomOfficeName))
      self.clickAndWaitForPageToLoad("link=Admin")
      self.failUnless(sel.is_text_present("Administrative tasks"))
      self.clickAndWaitForPageToLoad("link=Define new system user")
      self.failUnless(sel.is_text_present("Add a new user"))
      self.failUnless(sel.is_text_present("Choose office"))
      self.clickAndWaitForPageToLoad("link=" + randomOfficeName)
      self.failUnless(sel.is_text_present("Add a new user"))
      self.failUnless(sel.is_text_present("Enter user information"))
      randomNum = sel.get_expression(sel.get_eval("new Date().getTime()"))
      sel.type("firstName", "Joe" + randomNum)
      sel.type("lastName", "Guy" + randomNum)
      sel.type("emailId", "joe+" + randomNum + "@example.com")
      sel.type("dobDD", "02")
      sel.type("dobMM", "05")
      sel.type("dobYY", "1938")
      sel.select("gender", "label=Male")
      sel.select("level", "label=Loan Officer")
      sel.type("loginName", "joe" + randomNum)
      sel.type("userPassword", "12345678")
      sel.type("passwordRepeat", "12345678")
      loanOfficerFullName = sel.get_expression("Joe" + randomNum + " Guy" + randomNum)
      self.clickAndWaitForPageToLoad("//input[@value='Preview']")
      self.failUnless(sel.is_text_present("Add a new user"))
      self.failUnless(sel.is_text_present("Review & Submit"))
      self.failUnless(sel.is_text_present("Joe" + randomNum))
      self.clickAndWaitForPageToLoad("submitBtn")
      self.failUnless(sel.is_text_present("You have successfully added a new user"))
      self.clickAndWaitForPageToLoad("link=View user details now")
      self.failUnless(sel.is_text_present("Joe" + randomNum))
      self.clickAndWaitForPageToLoad("link=Clients & Accounts")
      self.failUnless(sel.is_text_present("To review or edit a Client, Group, or account"))
      print "added office %s" % randomOfficeName
      self.addCenters(sel, randomOfficeName, loanOfficerFullName)

   def addCenters(self, sel, officeName, loanOfficerFullName):
      for index in xrange(0, NUM_CENTERS_PER_OFFICE):
         print " Center %d:" % index,
         self.addCenter(sel, officeName, loanOfficerFullName)
      
   def addCenter(self, sel, officeName, loanOfficerFullName):
      self.clickAndWaitForPageToLoad("link=Clients & Accounts")
      self.clickAndWaitForPageToLoad("link=Create new Center")
      self.failUnless(sel.is_text_present("Create a new Center"))
      self.failUnless(sel.is_text_present("Choose Branch Office"))
      self.clickAndWaitForPageToLoad("link=" + officeName)
      self.failUnless(sel.is_text_present("Create New Center"))
      self.failUnless(sel.is_text_present("Enter Center information"))
      randomCenterName = sel.get_expression(sel.get_eval("'MyCenter' + (new Date()).getTime()"))
      sel.type("displayName", randomCenterName)
      sel.select("loanOfficerId", "label=" + loanOfficerFullName)
      self.clickAndWaitForPageToLoad("link=Schedule Meeting")
      self.failUnless(sel.is_text_present("Meeting schedule for Center"))
      sel.type("recurWeek", "1")
      todayDay = sel.get_expression(sel.get_eval("new Date().getDay() + 1"))
      sel.select("weekDay", "value=" + todayDay)
      sel.type("meetingPlace", "Wherever")
      self.clickAndWaitForPageToLoad("//input[@value='Save']")
      self.failUnless(sel.is_text_present("Create New Center"))
      self.failUnless(sel.is_text_present("Enter Center information"))
      self.clickAndWaitForPageToLoad("//input[@value='Preview']")
      self.failUnless(sel.is_text_present("Create a new Center"))
      self.failUnless(sel.is_text_present("Review & submit"))
      self.failUnless(sel.is_text_present(randomCenterName))
      self.clickAndWaitForPageToLoad("submitButton")
      self.failUnless(sel.is_text_present("You have successfully created a new Center"))
      self.failUnless(sel.is_text_present(randomCenterName))
      self.clickAndWaitForPageToLoad("link=View Center details now")
      self.failUnless(sel.is_text_present(randomCenterName))
      print "added center %s" % randomCenterName
      self.addGroups(sel, randomCenterName, loanOfficerFullName)
      
   def addGroups(self, sel, centerName, loanOfficerFullName):
      for index in xrange(0, NUM_GROUPS_PER_CENTER):
         print "  Group %d:" % index,
         self.addGroup(sel, centerName, loanOfficerFullName)

   def addGroup(self, sel, centerName, loanOfficerFullName):
      self.clickAndWaitForPageToLoad("link=Clients & Accounts")
      self.clickAndWaitForPageToLoad("link=Create new Group")
      self.failUnless(sel.is_text_present("Select a Center"))
      sel.type("searchString", centerName)
      self.clickAndWaitForPageToLoad("//input[@value='Search']")
      self.clickAndWaitForPageToLoad("link=%s" % centerName)
      self.failUnless(sel.is_text_present("Create new Group"))
      self.failUnless(sel.is_text_present("Enter Group information"))
      randomGroupName = sel.get_expression(sel.get_eval("'MyGroup' + (new Date()).getTime()"))
      sel.type("displayName", randomGroupName)
      sel.select("formedByPersonnel", "label=" + loanOfficerFullName)
      self.clickAndWaitForPageToLoad("//input[@value='Preview']")
      self.failUnless(sel.is_text_present("Create new Group"))
      self.failUnless(sel.is_text_present("Review & submit"))
      self.failUnless(sel.is_text_present(randomGroupName))
      self.clickAndWaitForPageToLoad("submitBtn2")
      self.failUnless(sel.is_text_present("You have successfully created a new Group"))
      self.failUnless(sel.is_text_present(randomGroupName))
      self.clickAndWaitForPageToLoad("link=View Group details now")
      self.failUnless(sel.is_text_present(randomGroupName))
      self.failUnless(sel.is_text_present("Application Pending Approval"))
      self.clickAndWaitForPageToLoad("link=Edit Group status")
      self.failUnless(sel.is_text_present(randomGroupName))
      self.failUnless(sel.is_text_present("Change status"))
      self.failUnless(sel.is_text_present("Application Pending Approval"))
      sel.click("document.editCustomerStatusActionForm.newStatusId[1]")
      sel.type("notes", "good to go.")
      self.clickAndWaitForPageToLoad("//input[@value='Preview']")
      self.failUnless(sel.is_text_present(randomGroupName))
      self.failUnless(sel.is_text_present("Confirm status change"))
      self.failUnless(sel.is_text_present("Active"))
      self.clickAndWaitForPageToLoad("btn")
      self.failUnless(sel.is_text_present("Active"))
      print "added group %s" % randomGroupName
      self.addClients(sel, randomGroupName, loanOfficerFullName)
      
   def addClients(self, sel, groupName, loanOfficerFullName):
      for index in xrange(0, NUM_CLIENTS_PER_GROUP):
         print "   Client %d:" % index, 
         self.addClientAndProducts(sel, groupName, loanOfficerFullName)
      
   def addClientAndProducts(self, sel, groupName, loanOfficerFullName):
      sel.open("/mifos/custSearchAction.do?method=loadMainSearch")
      sel.type("searchString", groupName)
      self.clickAndWaitForPageToLoad("//input[@value='Search']")
      self.clickAndWaitForPageToLoad("link=glob:%s*" % groupName)
      self.clickAndWaitForPageToLoad("link=Add Client")
      self.failUnless(sel.is_text_present("Create new Client"))
      self.failUnless(sel.is_text_present("Enter personal information"))
      randomNum2 = sel.get_expression(sel.get_eval("new Date().getTime()"))
      sel.select("clientName.salutation", "label=Mr")
      randomClientFirstName = "Stu" + randomNum2
      randomClientLastName = "Client" + randomNum2
      randomClientFullName = "%s %s" % (randomClientFirstName, randomClientLastName)
      sel.type("clientName.firstName", randomClientFirstName)
      sel.type("clientName.lastName", randomClientLastName)
      sel.type("dateOfBirthDD", "03")
      sel.type("dateOfBirthMM", "07")
      sel.type("dateOfBirthYY", "1949")
      sel.select("clientDetailView.gender", "label=Male")
      sel.select("clientDetailView.povertyStatus", "label=Very poor")
      sel.select("spouseName.nameType", "label=Spouse")
      sel.type("spouseName.firstName", "Sis" + randomNum2)
      sel.type("spouseName.lastName", "Last" + randomNum2)
      self.clickAndWaitForPageToLoad("//input[@value='Continue']")
      sel.select("formedByPersonnel", "label=" + loanOfficerFullName)
      self.clickAndWaitForPageToLoad("//input[@value='Preview']")
      self.failUnless(sel.is_text_present("Create new Client"))
      self.failUnless(sel.is_text_present("Review & submit"))
      self.clickAndWaitForPageToLoad("submitButton")
      self.failUnless(sel.is_text_present("You have successfully created a new Client"))
      self.clickAndWaitForPageToLoad("link=View Client details now")
      self.failUnless(sel.is_text_present("Application Pending Approval"))
      self.clickAndWaitForPageToLoad("link=Edit Client status")
      self.failUnless(sel.is_text_present("Change status"))
      self.failUnless(sel.is_text_present("Application Pending Approval"))
      sel.click("document.editCustomerStatusActionForm.newStatusId[1]")
      sel.type("notes", "ready and set.")
      self.clickAndWaitForPageToLoad("//input[@value='Preview']")
      self.failUnless(sel.is_text_present("Confirm status change"))
      self.failUnless(sel.is_text_present("Active"))
      self.clickAndWaitForPageToLoad("btn")
      print "added client %s" % randomClientFullName
      self.addLoanAccount(sel, self.loanProduct1, randomClientFullName)
      self.addLoanAccount(sel, self.loanProduct2, randomClientFullName)
      self.addSavingsAccount(sel, self.savingsProduct1, randomClientFullName)

   def addLoanAccount(self, sel, loanProductName, clientFullName):
      self.clickAndWaitForPageToLoad("link=Loan")
      self.failUnless(sel.is_text_present("Create Loan account"))
      self.failUnless(sel.is_text_present("Enter Loan account information"))
      sel.select("prdOfferingId", "label=" + loanProductName)
      self.clickAndWaitForPageToLoad("continueBtn")
      self.failUnless(sel.is_text_present("Create Loan account"))
      self.failUnless(sel.is_text_present("Enter Loan account information"))
      self.clickAndWaitForPageToLoad("continueButton")
      self.failUnless(sel.is_text_present("Create Loan account"))
      self.failUnless(sel.is_text_present("Review installments"))
      self.clickAndWaitForPageToLoad("previewBtn")
      self.failUnless(sel.is_text_present("Create Loan account"))
      self.failUnless(sel.is_text_present("Preview Loan account information"))
      self.clickAndWaitForPageToLoad("submitForApprovalButton")
      self.failUnless(sel.is_text_present("You have successfully created a new Loan account"))
      self.clickAndWaitForPageToLoad("link=View Loan account details now")
      self.failUnless(sel.is_text_present(loanProductName + " #"))
      self.failUnless(sel.is_text_present("Application Pending Approval"))
      self.clickAndWaitForPageToLoad("link=Edit account status")
      self.failUnless(sel.is_text_present("Change status"))
      self.failUnless(sel.is_text_present("Application Pending Approval"))
      sel.click("document.editStatusActionForm.newStatusId[1]")
      sel.type("notes", "great, go for it.")
      self.clickAndWaitForPageToLoad("//input[@value='Preview']")
      self.failUnless(sel.is_text_present("Confirm status change"))
      self.failUnless(sel.is_text_present("Application Approved"))
      self.clickAndWaitForPageToLoad("btn")
      self.failUnless(sel.is_text_present("Application Approved"))
      self.clickAndWaitForPageToLoad("link=Disburse Loan")
      sel.select("paymentTypeId", "label=Cash")
      self.clickAndWaitForPageToLoad("//input[@value='Review Transaction']")
      self.clickAndWaitForPageToLoad("//input[@value='Submit']")
      self.clickAndWaitForPageToLoad("link=%s" % clientFullName)
      
   def addSavingsAccount(self, sel, savingsProductName, clientFullName):
      self.clickAndWaitForPageToLoad("link=Savings")
      sel.select("selectedPrdOfferingId", "label=%s" % savingsProductName)
      self.clickAndWaitForPageToLoad("//input[@value='Continue']")
      self.clickAndWaitForPageToLoad("//input[@value='Preview']")
      self.clickAndWaitForPageToLoad("approvedButton")
      self.clickAndWaitForPageToLoad("link=View Savings account details now")
      self.clickAndWaitForPageToLoad("link=Edit account status")
      sel.click("//input[@name='newStatusId' and @value='16']")
      sel.type("notes", "super great!")
      self.clickAndWaitForPageToLoad("//input[@value='Preview']")
      self.clickAndWaitForPageToLoad("btn")
      self.clickAndWaitForPageToLoad("link=%s" % clientFullName)
      
   def clickAndWaitForPageToLoad(self, target):
      self.selenium.click(target)
      self.waitForPageToLoad()

   def waitForPageToLoad(self):
      self.selenium.wait_for_page_to_load("60000")

if __name__ == "__main__":
   unittest.main()
