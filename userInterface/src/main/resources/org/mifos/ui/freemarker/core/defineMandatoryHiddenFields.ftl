[#ftl]
[#--
* Copyright (c) 2005-2011 Grameen Foundation USA
*  All rights reserved.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
*  implied. See the License for the specific language governing
*  permissions and limitations under the License.
*
*  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
*  explanation of the license and how it is applied.
--]
[#include "layout.ftl"]
[@adminLeftPaneLayout]
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
        for (z = 0; z < theForm.length; z++) {
            if (theForm[z].type == 'checkbox') {
                disableFields(theForm[z]);
            }
        }
    }
</script>
<!--  Main Content Begins-->
<span id="page.id" title="defineMandatoryHiddenFields" />
<div class="content">
[@widget.crumbs breadcrumbs /]
<p>&nbsp;&nbsp;</p>

<form method="post" action="defineMandatoryHiddenFields.ftl" name="defineMandatoryHiddenFields">
<p class="orangeheading"
   style="margin-left:20px; margin-bottom:15px;">[@spring.message "manadatoryHiddenFields.definemandatory/hiddenfields"/]</p>

<div class="span-22 last">
    <div class="bluedivs span-22 fontBold">
        <span class="span-8 paddingLeft">[@spring.message "systemwidefields"/]</span><span
            class="span-7 paddingLeft">[@spring.message "manadatoryHiddenFields.hide"/]</span><span
            class="span-5 paddingLeft">[@spring.message "manadatoryHiddenFields.mandatory"/]</span>
    </div>

    <div class="span-22 borderbtm">
            <span class="span-8 paddingLeft">
                [@spring.message "manadatoryHiddenFields.externalId" /]
            </span>
            <span class="span-7 paddingLeft ">
                [@spring.bind "fields.hideSystemExternalId" /]
                    <input type="hidden" name="_${spring.status.expression}" value="false"/>
                <input type="checkbox" id="${spring.status.expression}" name="${spring.status.expression}"
                       [#if spring.status.value?? && spring.status.value?string=="true"]checked="true" [/#if]
                    [#if fields.mandatorySystemExternalId=true]  disabled [/#if] onclick="disableFields(this)"/>
            </span>
            <span class="span-5 paddingLeft">
                [@spring.bind "fields.mandatorySystemExternalId" /]
                    <input type="hidden" name="_${spring.status.expression}" value="false"/>
                <input type="checkbox" id="${spring.status.expression}" name="${spring.status.expression}"
                       [#if spring.status.value?? && spring.status.value?string=="true"]checked="true" [/#if]
                    [#if fields.hideSystemExternalId=true]  disabled [/#if] onclick="disableFields(this)"/>
            </span>
    </div>

    <div class="span-22 borderbtm">
            <span class="span-8 paddingLeft">
            [@spring.message "manadatoryHiddenFields.ethnicity" /]
            </span>
            <span class="span-7 paddingLeft ">
            [@spring.bind "fields.hideSystemEthnicity" /]
                <input type="hidden" name="_${spring.status.expression}" value="false"/>
                <input type="checkbox" id="${spring.status.expression}" name="${spring.status.expression}"
                       [#if spring.status.value?? && spring.status.value?string=="true"]checked="true" [/#if]
                    [#if fields.mandatorySystemEthnicity=true]  disabled [/#if] onclick="disableFields(this)"/>
            </span>
            <span class="span-5 paddingLeft">
            [@spring.bind "fields.mandatorySystemEthnicity" /]
                <input type="hidden" name="_${spring.status.expression}" value="false"/>
                <input type="checkbox" id="${spring.status.expression}" name="${spring.status.expression}"
                       [#if spring.status.value?? && spring.status.value?string=="true"]checked="true" [/#if]
                    [#if fields.hideSystemEthnicity=true]  disabled [/#if] onclick="disableFields(this)"/>
            </span>
    </div>
    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">
            [@spring.message "manadatoryHiddenFields.citizenship" /]
        </span>
        <span class="span-7 paddingLeft "><input type="checkbox"
                                                 name="hideSystemCitizenShip" [#if fields.hideSystemCitizenShip=true]
                                                 checked="checked" [/#if] [#if fields.mandatorySystemCitizenShip=true]
                                                 DISABLED [/#if] value="1" onclick="disableFields(this)"></span><span
            class="span-5 paddingLeft"><input type="checkbox"
                                              name="mandatorySystemCitizenShip" [#if fields.mandatorySystemCitizenShip=true]
                                              checked="checked" [/#if] [#if fields.hideSystemCitizenShip=true]
                                              DISABLED [/#if] value="1" onclick="disableFields(this)"></span>
    </div>
    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">
            [@spring.message "manadatoryHiddenFields.handicapped" /]
        </span>
        <span class="span-7 paddingLeft "><input type="checkbox"
                                                 name="hideSystemHandicapped" [#if fields.hideSystemHandicapped=true]
                                                 checked="checked" [/#if] [#if fields.mandatorySystemHandicapped=true]
                                                 DISABLED [/#if] value="1" onclick="disableFields(this)"></span><span
            class="span-5 paddingLeft"><input type="checkbox"
                                              name="mandatorySystemHandicapped" [#if fields.mandatorySystemHandicapped=true]
                                              checked="checked" [/#if] [#if fields.hideSystemHandicapped=true]
                                              DISABLED [/#if] value="1" onclick="disableFields(this)"></span>
    </div>
    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">[@spring.message "manadatoryHiddenFields.educationlevel"/] </span>
        <span class="span-7 paddingLeft "><input type="checkbox"
                                                 name="hideSystemEducationLevel" [#if fields.hideSystemEducationLevel=true]
                                                 checked="checked" [/#if] [#if fields.mandatorySystemEducationLevel=true]
                                                 DISABLED [/#if] value="1" onclick="disableFields(this)"></span><span
            class="span-5 paddingLeft"><input type="checkbox"
                                              name="mandatorySystemEducationLevel" [#if fields.mandatorySystemEducationLevel=true]
                                              checked="checked" [/#if] [#if fields.hideSystemEducationLevel=true]
                                              DISABLED [/#if] value="1" onclick="disableFields(this)"></span>
    </div>
    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">[@spring.message "manadatoryHiddenFields.photo"/] </span>
        <span class="span-7 paddingLeft "><input type="checkbox"
                                                 name="hideSystemPhoto" [#if fields.hideSystemPhoto=true]
                                                 checked="checked" [/#if] [#if fields.mandatorySystemPhoto=true]
                                                 DISABLED [/#if] value="1" onclick="disableFields(this)"></span><span
            class="span-5 paddingLeft"><input type="checkbox"
                                              name="mandatorySystemPhoto" [#if fields.mandatorySystemPhoto=true]
                                              checked="checked" [/#if] [#if fields.hideSystemPhoto=true]
                                              DISABLED [/#if] value="1" onclick="disableFields(this)"></span>
    </div>
    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">
            [@spring.message "manadatoryHiddenFields.assigningClienttopositions" /]
        </span>
        <span class="span-7 paddingLeft "><input type="checkbox"
                                                 name="hideSystemAssignClientPostions" [#if fields.hideSystemAssignClientPostions=true]
                                                 checked="checked" [/#if] value="1"
                                                 onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
    </div>
    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">
            [@spring.message "manadatoryHiddenFields.address1" /]
        </span>
        <span class="span-7 paddingLeft ">&nbsp;</span>
        <span class="span-5 paddingLeft"><input type="checkbox"
                                                name="mandatorySystemAddress1" [#if fields.mandatorySystemAddress1=true]
                                                checked="checked" [/#if] value="1" onclick="disableFields(this)"></span>
    </div>
    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">
            [@spring.message "manadatoryHiddenFields.address2" /]
        </span>
        <span class="span-7 paddingLeft "><input type="checkbox"
                                                 name="hideSystemAddress2" [#if fields.hideSystemAddress2=true]
                                                 checked="checked" [/#if] value="1"
                                                 onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
    </div>
    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">
            [@spring.message "manadatoryHiddenFields.address3" /]
        </span>
        <span class="span-7 paddingLeft "><input type="checkbox"
                                                 name="hideSystemAddress3" [#if fields.hideSystemAddress3=true]
                                                 checked="checked" [/#if]  value="1"
                                                 onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
    </div>
    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">[@spring.message "manadatoryHiddenFields.city/District"/] </span>
        <span class="span-7 paddingLeft "><input type="checkbox" name="hideSystemCity" [#if fields.hideSystemCity=true]
                                                 checked="checked" [/#if] value="1"
                                                 onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
    </div>
    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">
            [@spring.message "manadatoryHiddenFields.state" /]
        </span>
        <span class="span-7 paddingLeft "><input type="checkbox"
                                                 name="hideSystemState" [#if fields.hideSystemState=true]
                                                 checked="checked" [/#if]  value="1"
                                                 onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
    </div>
    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">[@spring.message "manadatoryHiddenFields.country"/] </span>
        <span class="span-7 paddingLeft "><input type="checkbox"
                                                 name="hideSystemCountry" [#if fields.hideSystemCountry=true]
                                                 checked="checked" [/#if]  value="1"
                                                 onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
    </div>
    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">
            [@spring.message "manadatoryHiddenFields.postalCode" /]
        </span>
        <span class="span-7 paddingLeft "><input type="checkbox"
                                                 name="hideSystemPostalCode" [#if fields.hideSystemPostalCode=true]
                                                 checked="checked" [/#if] value="1"
                                                 onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
    </div>
    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">[@spring.message "manadatoryHiddenFields.receiptIDandDate"/] </span>
        <span class="span-7 paddingLeft "><input type="checkbox"
                                                 name="hideSystemReceiptIdDate" [#if fields.hideSystemReceiptIdDate=true]
                                                 checked="checked" [/#if] value="1"
                                                 onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
    </div>
    <div class="span-22 last">
        <span class="span-8 paddingLeft">[@spring.message "manadatoryHiddenFields.collateralTypeandNotes"/] </span>
        <span class="span-7 paddingLeft "><input type="checkbox"
                                                 name="hideSystemCollateralTypeNotes" [#if fields.hideSystemCollateralTypeNotes=true]
                                                 checked="checked" [/#if] value="1"
                                                 onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
    </div>
</div>

<div class="span-22 last">
    <div class="bluedivs span-22 fontBold">
        <span class="span-8 paddingLeft">
            [@spring.message "manadatoryHiddenFields.client/systemusersfields" /]
        </span>
        <span class="span-7 paddingLeft">[@spring.message "manadatoryHiddenFields.hide"/]</span><span
            class="span-5 paddingLeft">[@spring.message "manadatoryHiddenFields.mandatory"/]</span>
    </div>
    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">[@spring.message "manadatoryHiddenFields.middlename"/] </span>
        <span class="span-7 paddingLeft "><input type="checkbox"
                                                 name="hideClientMiddleName" [#if fields.hideClientMiddleName=true]
                                                 checked="checked" [/#if] [#if fields.mandatoryClientMiddleName=true]
                                               DISABLED [/#if] value="1"
                                                 onclick="disableFields(this)"></span>
        <span class="span-5 paddingLeft"><input type="checkbox"
                                                name="mandatoryClientMiddleName" [#if fields.mandatoryClientMiddleName=true] 
                                                checked="checked" [/#if] [#if fields.hideClientMiddleName=true]
                                                DISABLED [/#if] value="1"
                                                onclick="disableFields(this)"></span>
    </div>
    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">[@spring.message "manadatoryHiddenFields.secondlastname"/] </span><span
            class="span-7 paddingLeft "><input type="checkbox"
                                               name="hideClientSecondLastName" [#if fields.hideClientSecondLastName=true]
                                               checked="checked" [/#if] [#if fields.mandatoryClientSecondLastName=true]
                                               DISABLED [/#if] value="1" onclick="disableFields(this)"></span><span
            class="span-5 paddingLeft"><input type="checkbox"
                                              name="mandatoryClientSecondLastName" [#if fields.mandatoryClientSecondLastName=true]
                                              checked="checked" [/#if] [#if fields.hideClientSecondLastName=true]
                                              DISABLED [/#if] value="1" onclick="disableFields(this)"></span>
    </div>
    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">
            [@spring.message "manadatoryHiddenFields.governmentID" /]
        </span>
        <span class="span-7 paddingLeft "><input type="checkbox"
                                                 name="hideClientGovtId" [#if fields.hideClientGovtId=true]
                                                 checked="checked" [/#if] [#if fields.mandatoryClientGovtId=true]
                                                 DISABLED [/#if] value="1" onclick="disableFields(this)"></span>
        <span class="span-5 paddingLeft"><input type="checkbox"
                                                name="mandatoryClientGovtId" [#if fields.mandatoryClientGovtId=true]
                                                checked="checked" [/#if] [#if fields.hideClientGovtId=true]
                                                DISABLED [/#if] value="1" onclick="disableFields(this)"></span>
    </div>

    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">[@spring.message "manadatoryHiddenFields.martialStatus"/] </span>
        <span class="span-7 paddingLeft">&nbsp;</span>
        <span class="span-5 paddingLeft"><input type="checkbox"
                                                name="mandatoryMaritalStatus" [#if fields.mandatoryMaritalStatus=true]
                                                checked="checked" [/#if] value="1" onclick="disableFields(this)"></span>
    </div>

    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">[@spring.message "manadatoryHiddenFields.povertyStatus"/] </span>
        <span class="span-7 paddingLeft "><input type="checkbox"
                                                 name="hideClientPovertyStatus" [#if fields.hideClientPovertyStatus=true]
                                                 checked="checked" [/#if] [#if fields.mandatoryClientPovertyStatus=true]
                                                 DISABLED [/#if] value="1" onclick="disableFields(this)"></span>
        <span class="span-5 paddingLeft"><input type="checkbox"
                                                name="mandatoryClientPovertyStatus" [#if fields.mandatoryClientPovertyStatus=true]
                                                checked="checked" [/#if] [#if fields.hideClientPovertyStatus=true]
                                                DISABLED [/#if] value="1" onclick="disableFields(this)"></span>
    </div>

    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">[@spring.message "manadatoryHiddenFields.familyDetails"/] </span>
        <span class="span-7 paddingLeft">&nbsp;</span>
        <span class="span-5 paddingLeft"><input type="checkbox"
                                                name="mandatoryClientFamilyDetails" [#if fields.mandatoryClientFamilyDetails=true]
                                                checked="checked" [/#if] value="1" onclick="disableFields(this)"></span>
    </div>

    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">[@spring.message "manadatoryHiddenFields.spouse/fathermiddlename"/] </span><span
            class="span-7 paddingLeft "><input type="checkbox"
                                               name="hideClientSpouseFatherMiddleName" [#if fields.hideClientSpouseFatherMiddleName=true]
                                               checked="checked" [/#if] value="1"
                                               onclick="disableFields(this)"></span><span class="span-5 paddingLeft">&nbsp;</span>
    </div>
    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">[@spring.message "manadatoryHiddenFields.spouse/fathersecondlastname"/] </span><span
            class="span-7 paddingLeft "><input type="checkbox"
                                               name="hideClientSpouseFatherSecondLastName" [#if fields.hideClientSpouseFatherSecondLastName=true]
                                               checked="checked" [/#if] [#if fields.mandatoryClientSpouseFatherSecondLastName=true]
                                               DISABLED [/#if] value="1" onclick="disableFields(this)"></span><span
            class="span-5 paddingLeft"><input type="checkbox"
                                              name="mandatoryClientSpouseFatherSecondLastName" [#if fields.mandatoryClientSpouseFatherSecondLastName=true]
                                              checked="checked" [/#if] [#if fields.hideClientSpouseFatherSecondLastName=true]
                                              DISABLED [/#if] value="1" onclick="disableFields(this)"></span>
    </div>
    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">[@spring.message "manadatoryHiddenFields.phonenumber"/] </span><span
            class="span-7 paddingLeft "><input type="checkbox" name="hideClientPhone" [#if fields.hideClientPhone=true]
                                               checked="checked" [/#if] [#if fields.mandatoryClientPhone=true]
                                               DISABLED [/#if] value="1" onclick="disableFields(this)"></span><span
            class="span-5 paddingLeft"><input type="checkbox"
                                              name="mandatoryClientPhone" [#if fields.mandatoryClientPhone=true]
                                              checked="checked" [/#if] [#if fields.hideClientPhone=true]
                                              DISABLED [/#if] value="1" onclick="disableFields(this)"></span>
    </div>
    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">[@spring.message "manadatoryHiddenFields.trained"/] </span><span
            class="span-7 paddingLeft "><input type="checkbox"
                                               name="hideClientTrained" [#if fields.hideClientTrained=true]
                                               checked="checked" [/#if] [#if fields.mandatoryClientTrained=true]
                                               DISABLED [/#if] value="1" onclick="disableFields(this)"></span><span
            class="span-5 paddingLeft"><input type="checkbox"
                                              name="mandatoryClientTrained" [#if fields.mandatoryClientTrained=true]
                                              checked="checked" [/#if] [#if fields.hideClientTrained=true]
                                              DISABLED [/#if] value="1" onclick="disableFields(this)"></span>
    </div>
    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">[@spring.message "manadatoryHiddenFields.trainedon"/] </span><span
            class="span-7 paddingLeft ">&nbsp;</span><span class="span-5 paddingLeft"><input type="checkbox"
                                                                                             name="mandatoryClientTrainedOn" [#if fields.mandatoryClientTrainedOn=true]
                                                                                             checked="checked" [/#if]
                                                                                             value="1"
                                                                                             onclick="disableFields(this)"></span>
    </div>
    <div class="span-22 borderbtm">
        <span class="span-8 paddingLeft">[@spring.message "manadatoryHiddenFields.business/workactivities"/] </span>
        <span class="span-7 paddingLeft "><input type="checkbox"
                                                 name="hideClientBusinessWorkActivities" [#if fields.hideClientBusinessWorkActivities=true]
                                                 checked="checked" [/#if]  value="1"
                                                 onclick="disableFields(this)"></span>
        <span class="span-5 paddingLeft">&nbsp;</span>
    </div>

    <div class="span-22 last">
        <span class="span-8 paddingLeft">[@spring.message "manadatoryHiddenFields.numofchildren"/] </span>
        <span class="span-7 paddingLeft">&nbsp;</span>
        <span class="span-5 paddingLeft"><input type="checkbox"
                                                name="mandatoryNumberOfChildren" [#if fields.mandatoryNumberOfChildren=true]
                                                checked="checked" [/#if]  value="1"
                                                onclick="disableFields(this)"></span>
    </div>
</div>
<div class="span-22 last">
    <div class="bluedivs span-22 fontBold">
        <span class="span-8 paddingLeft">
            [@spring.message "manadatoryHiddenFields.groupfields" /]
        </span>
        <span class="span-7 paddingLeft">[@spring.message "manadatoryHiddenFields.hide"/]</span>
        <span class="span-5 paddingLeft">[@spring.message "manadatoryHiddenFields.mandatory"/]</span>
    </div>
    <div class="span-22 last">
        <span class="span-8 paddingLeft">[@spring.message "manadatoryHiddenFields.trained"/] </span>
        <span class="span-7 paddingLeft "><input type="checkbox"
                                                 name="hideGroupTrained" [#if fields.hideGroupTrained=true]
                                                 checked="checked" [/#if] value="1"
                                                 onclick="disableFields(this)"></span>
        <span class="span-5 paddingLeft">&nbsp;</span>
    </div>
</div>
<div class="span-22 last">
    <div class="bluedivs span-22 fontBold">
        <span class="span-8 paddingLeft">[@spring.message "manadatoryHiddenFields.loanAccountFields"/] </span>
        <span class="span-7 paddingLeft">[@spring.message "manadatoryHiddenFields.hide"/]</span>
        <span class="span-5 paddingLeft">[@spring.message "manadatoryHiddenFields.mandatory"/]</span>
    </div>
    <div class="span-22 borderbtm last">
        <span class="span-8 paddingLeft ">[@spring.message "manadatoryHiddenFields.purposeofLoan"/] </span>
        <span class="span-7 paddingLeft ">&nbsp;</span>
        <span class="span-5 paddingLeft"><input type="checkbox"
                                                name="mandatoryLoanAccountPurpose" [#if fields.mandatoryLoanAccountPurpose=true]
                                                checked="checked" [/#if] value="1" onclick="disableFields(this)"></span>
    </div>
    <div class="span-22 borderbtm last">
        <span class="span-8 paddingLeft ">[@spring.message "manadatoryHiddenFields.sourceOfFund"/] </span>
        <span class="span-7 paddingLeft ">&nbsp;</span>
        <span class="span-5 paddingLeft"><input type="checkbox"
                                                name="mandatoryLoanSourceOfFund" [#if fields.mandatoryLoanSourceOfFund=true]
                                                checked="checked" [/#if] value="1" onclick="disableFields(this)"></span>
    </div>
    <div class="clear">&nbsp;</div>
</div>

<div class="span-22 prepend-9">
    <input type="submit" class="buttn" name="SUBMIT" value="[@spring.message "submit"/]"/>
    <input type="Submit" class="buttn2" name="CANCEL" value="[@spring.message "cancel"/]"/>
</div>
</form>
</div><!--Main Content Ends-->




[/@adminLeftPaneLayout]
<script>
    disableAllFields();
</script>