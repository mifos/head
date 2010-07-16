[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
<div class="sidebar ht950">
[#include "adminLeftPane.ftl" ]
</div>

<script type="text/javascript"> 
    function disableFields(theElement) {
        var theForm = theElement.form, z = 0, type = theElement.name.charAt(0), secondBox;
        if (type == 'h') {
            secondBox = document.getElementsByName('mandatory'.concat(theElement.name.substr(4)));
            if (secondBox.length != 0) {
                secondBox[0].disabled = theElement.checked;
            }
        }
        else {
            secondBox = document.getElementsByName('hide'.concat(theElement.name.substr(9)));
            if (secondBox.length != 0 && secondBox[0].checked == false) {
                secondBox[0].disabled = theElement.checked;
            }
        }
    }
 
    function disableAllFields() {
        var theForm = document.getElementsByName('hiddenmandatoryconfigurationactionform')[0];
        for(z=0; z<theForm.length;z++){
            if(theForm[z].type == 'checkbox'){
                disableFields(theForm[z]);
            }
        }
    }
</script> 



  <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  [@mifos.crumb url="definemandatory/hiddenfields" /]
  <p>&nbsp;&nbsp;</p>
  	<form method="post" action="defineMandatoryHiddenFields.ftl" name="defineMandatoryHiddenFields">
  	 <p class="orangeheading">[@spring.message "definemandatory/hiddenfields"/]</p>
    <div class="span-22 last">
    	<div class="bluedivs span-22 fontBold">
        	<span class="span-8 paddingLeft">[@spring.message "systemwidefields"/]</span><span class="span-7 paddingLeft">[@spring.message "hide"/]</span><span class="span-5 paddingLeft">[@spring.message "mandatory"/]</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "externalId"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemExternalId" value="1" onclick="disableFields(this)" /></span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatorySystemExternalId" value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "ethnicity"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemEthnicity" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatorySystemEthnicity" value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "citizenship"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemCitizenShip" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatorySystemCitizenShip" value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "handicapped"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemHandicapped" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatorySystemHandicapped" value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "educationlevel"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemEducationLevel" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatorySystemEducationLevel" value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "photo"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemPhoto" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatorySystemPhoto" value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "assigningClienttopositions"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemAssignClientPostions" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "address1"/]</span><span class="span-7 paddingLeft ">&nbsp;</span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatorySystemAddress1" value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "address2"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemAddress2" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "address3"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemAddress3" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "city/District"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemCity" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "state"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemState" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "country"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemCountry" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "postalCode"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemPostalCode" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "receiptIDandDate"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemReceiptIdDate" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 last">
        	<span class="span-8 paddingLeft">[@spring.message "collateralTypeandNotes"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemCollateralTypeNotes" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
    
    </div>
    <div class="span-22 last">
    	<div class="bluedivs span-22 fontBold">
        	<span class="span-8 paddingLeft">[@spring.message "client/systemusersfields"/]</span><span class="span-7 paddingLeft">[@spring.message "hide"/]</span><span class="span-5 paddingLeft">[@spring.message "mandatory"/]</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "middlename"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideClientMiddleName" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "secondlastname"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideClientLastName" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatoryClientLastName" value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "governmentID"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideClientGovtId" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatoryClientGovtId" value="1" onclick="disableFields(this)"></span>
        </div>
                       
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "spouse/fathermiddlename"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideClientSpouseFatherMiddleInformation" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "spouse/fathersecondlastname"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideClientSpouseFatherLastInformation" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatoryClientSpouseFatherLastInformation" value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "phonenumber"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideClientPhone" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatoryClientPhone" value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "trained"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideClientTrained" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatoryClientTrained" value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "trainedon"/]</span><span class="span-7 paddingLeft ">&nbsp;</span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatoryClientTrainedOn" value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="span-22 last">
        	<span class="span-8 paddingLeft">[@spring.message "business/workactivities"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideClientBusinessWorkActivities" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
    
    </div>
    <div class="span-22 last">
    	<div class="bluedivs span-22 fontBold">
        	<span class="span-8 paddingLeft">[@spring.message "groupfields"/]</span><span class="span-7 paddingLeft">[@spring.message "hide"/]</span><span class="span-5 paddingLeft">[@spring.message "mandatory"/]</span>
        </div>
        <div class="span-22 last">
        	<span class="span-8 paddingLeft">[@spring.message "trained"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideGroupTrained" value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
    
    </div>
    <div class="span-22 last">
    	<div class="bluedivs span-22 fontBold">
        	<span class="span-8 paddingLeft">[@spring.message "loanAccountFields"/]</span><span class="span-7 paddingLeft">[@spring.message "hide"/]</span><span class="span-5 paddingLeft">[@spring.message "mandatory"/]</span>
        </div>
        <div class="span-22 borderbtm last">
        	<span class="span-8 paddingLeft ">[@spring.message "purposeofLoan"/]</span><span class="span-7 paddingLeft ">&nbsp;</span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatoryLoanAccountPurpose" value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="clear">&nbsp;</div>
    </div>

    <div class="span-22 prepend-9">
    	<input type="submit" class="buttn" name="submit" value="Submit"  />
        <input type="button" class="buttn2" name="cancel" value="Cancel" onclick="location.href='admin.ftl'" />
    </div>
   	</form> 
  </div><!--Main Content Ends-->




[@mifos.footer/] 
<script> 
disableAllFields();
</script> 