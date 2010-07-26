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
        	<span class="span-8 paddingLeft">
        		[@spring.message "externalId"/]
        	</span>
        	<span class="span-7 paddingLeft ">
        		<input type="checkbox" name="hideSystemExternalId"  [#if fields.hideSystemExternalId=true]  checked="checked" [/#if] [#if fields.mandatorySystemExternalId=true]  DISABLED [/#if] value="1" onclick="disableFields(this)" />
        	</span>
        	<span class="span-5 paddingLeft">
        		<input type="checkbox" name="mandatorySystemExternalId" value="1" [#if fields.mandatorySystemExternalId=true]  DISABLED [/#if] [#if fields.hideSystemExternalId=true]  DISABLED [/#if]  onclick="disableFields(this)">
        	</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "ethnicity"/]</span>
        	<span class="span-7 paddingLeft ">
        	<input type="checkbox" name="hideSystemEthnicity" [#if fields.hideSystemEthnicity=true]  checked="checked" [/#if] [#if fields.mandatorySystemEthnicity=true]  DISABLED [/#if] value="1" onclick="disableFields(this)">
        	</span>
        	<span class="span-5 paddingLeft"><input type="checkbox" name="mandatorySystemEthnicity" [#if fields.mandatorySystemEthnicity=true]  checked="checked" [/#if] [#if fields.hideSystemEthnicity=true]  DISABLED [/#if] value="1" onclick="disableFields(this)">
        	</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "citizenship"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemCitizenShip" [#if fields.hideSystemCitizenShip=true]  checked="checked" [/#if] [#if fields.mandatorySystemCitizenShip=true]  DISABLED [/#if] value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatorySystemCitizenShip" [#if fields.mandatorySystemCitizenShip=true]  checked="checked" [/#if] [#if fields.hideSystemCitizenShip=true]  DISABLED [/#if] value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "handicapped"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemHandicapped" [#if fields.hideSystemHandicapped=true]  checked="checked" [/#if] [#if fields.mandatorySystemHandicapped=true]  DISABLED [/#if] value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatorySystemHandicapped" [#if fields.mandatorySystemHandicapped=true]  checked="checked" [/#if] [#if fields.hideSystemHandicapped=true]  DISABLED [/#if] value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "educationlevel"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemEducationLevel" [#if fields.hideSystemEducationLevel=true]  checked="checked" [/#if] [#if fields.mandatorySystemEducationLevel=true]  DISABLED [/#if] value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatorySystemEducationLevel" [#if fields.mandatorySystemEducationLevel=true]  checked="checked" [/#if] [#if fields.hideSystemEducationLevel=true]  DISABLED [/#if] value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "photo"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemPhoto" [#if fields.hideSystemPhoto=true]  checked="checked" [/#if] [#if fields.mandatorySystemPhoto=true]  DISABLED [/#if] value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatorySystemPhoto" [#if fields.mandatorySystemPhoto=true]  checked="checked" [/#if] [#if fields.hideSystemPhoto=true]  DISABLED [/#if] value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "assigningClienttopositions"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemAssignClientPostions" [#if fields.hideSystemAssignClientPostions=true]  checked="checked" [/#if] value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "address1"/]</span><span class="span-7 paddingLeft ">&nbsp;</span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatorySystemAddress1" [#if fields.mandatorySystemAddress1=true]  checked="checked" [/#if] value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "address2"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemAddress2" [#if fields.hideSystemAddress2=true]  checked="checked" [/#if] value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "address3"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemAddress3" [#if fields.hideSystemAddress3=true]  checked="checked" [/#if]  value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "city/District"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemCity" [#if fields.hideSystemCity=true]  checked="checked" [/#if] value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "state"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemState" [#if fields.hideSystemState=true]  checked="checked" [/#if]  value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "country"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemCountry" [#if fields.hideSystemCountry=true]  checked="checked" [/#if]  value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "postalCode"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemPostalCode" [#if fields.hideSystemPostalCode=true]  checked="checked" [/#if] value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "receiptIDandDate"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemReceiptIdDate" [#if fields.hideSystemReceiptIdDate=true]  checked="checked" [/#if] value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 last">
        	<span class="span-8 paddingLeft">[@spring.message "collateralTypeandNotes"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemCollateralTypeNotes" [#if fields.hideSystemCollateralTypeNotes=true]  checked="checked" [/#if] value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
    
    </div>
    <div class="span-22 last">
    	<div class="bluedivs span-22 fontBold">
        	<span class="span-8 paddingLeft">[@spring.message "client/systemusersfields"/]</span><span class="span-7 paddingLeft">[@spring.message "hide"/]</span><span class="span-5 paddingLeft">[@spring.message "mandatory"/]</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "middlename"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideClientMiddleName" [#if fields.hideClientMiddleName=true]  checked="checked" [/#if]  value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "secondlastname"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideClientSecondLastName" [#if fields.hideClientSecondLastName=true]  checked="checked" [/#if] [#if fields.mandatoryClientSecondLastName=true]  DISABLED [/#if] value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatoryClientSecondLastName" [#if fields.mandatoryClientSecondLastName=true]  checked="checked" [/#if] [#if fields.hideClientSecondLastName=true]  DISABLED [/#if] value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "governmentID"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideClientGovtId" [#if fields.hideClientGovtId=true]  checked="checked" [/#if] [#if fields.mandatoryClientGovtId=true]  DISABLED [/#if] value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatoryClientGovtId" [#if fields.mandatoryClientGovtId=true]  checked="checked" [/#if] [#if fields.hideClientGovtId=true]  DISABLED [/#if] value="1" onclick="disableFields(this)"></span>
        </div>
                       
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "spouse/fathermiddlename"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideClientSpouseFatherMiddleName" [#if fields.hideClientSpouseFatherMiddleName=true]  checked="checked" [/#if] value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "spouse/fathersecondlastname"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideClientSpouseFatherSecondLastName" [#if fields.hideClientSpouseFatherSecondLastName=true]  checked="checked" [/#if] [#if fields.mandatoryClientSpouseFatherSecondLastName=true]  DISABLED [/#if] value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatoryClientSpouseFatherSecondLastName" [#if fields.mandatoryClientSpouseFatherSecondLastName=true]  checked="checked" [/#if] [#if fields.hideClientSpouseFatherSecondLastName=true]  DISABLED [/#if] value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "phonenumber"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideClientPhone" [#if fields.hideClientPhone=true]  checked="checked" [/#if] [#if fields.mandatoryClientPhone=true]  DISABLED [/#if] value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatoryClientPhone" [#if fields.mandatoryClientPhone=true]  checked="checked" [/#if] [#if fields.hideClientPhone=true]  DISABLED [/#if] value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "trained"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideClientTrained" [#if fields.hideClientTrained=true]  checked="checked" [/#if] [#if fields.mandatoryClientTrained=true]  DISABLED [/#if] value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatoryClientTrained" [#if fields.mandatoryClientTrained=true]  checked="checked" [/#if] [#if fields.hideClientTrained=true]  DISABLED [/#if] value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="span-22 borderbtm">
        	<span class="span-8 paddingLeft">[@spring.message "trainedon"/]</span><span class="span-7 paddingLeft ">&nbsp;</span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatoryClientTrainedOn" [#if fields.mandatoryClientTrainedOn=true]  checked="checked" [/#if]  value="1" onclick="disableFields(this)"></span>
        </div>
        <div class="span-22 last">
        	<span class="span-8 paddingLeft">[@spring.message "business/workactivities"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideClientBusinessWorkActivities" [#if fields.hideClientBusinessWorkActivities=true]  checked="checked" [/#if]  value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
    
    </div>
    <div class="span-22 last">
    	<div class="bluedivs span-22 fontBold">
        	<span class="span-8 paddingLeft">[@spring.message "groupfields"/]</span><span class="span-7 paddingLeft">[@spring.message "hide"/]</span><span class="span-5 paddingLeft">[@spring.message "mandatory"/]</span>
        </div>
        <div class="span-22 last">
        	<span class="span-8 paddingLeft">[@spring.message "trained"/]</span><span class="span-7 paddingLeft "><input type="checkbox" name="hideGroupTrained" [#if fields.hideGroupTrained=true]  checked="checked" [/#if] value="1" onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
    
    </div>
    <div class="span-22 last">
    	<div class="bluedivs span-22 fontBold">
        	<span class="span-8 paddingLeft">[@spring.message "loanAccountFields"/]</span><span class="span-7 paddingLeft">[@spring.message "hide"/]</span><span class="span-5 paddingLeft">[@spring.message "mandatory"/]</span>
        </div>
        <div class="span-22 borderbtm last">
        	<span class="span-8 paddingLeft ">[@spring.message "purposeofLoan"/]</span><span class="span-7 paddingLeft ">&nbsp;</span><span class="span-5 paddingLeft"><input type="checkbox" name="mandatoryLoanAccountPurpose" [#if fields.mandatoryLoanAccountPurpose=true]  checked="checked" [/#if] value="1" onclick="disableFields(this)"></span>
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