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

[@layout.header "title" /]
[@widget.topNavigationNoSecurity currentTab="Admin" /]

  <!--  Main Content Begins-->
<span id="page.id" title="UndoImportConfirmation"></span>
[@i18n.formattingInfo /] 
<script type="text/javascript">
function fnCancel(form) {
    form.action="viewImportedTransactions.ftl";
    form.submit();
}
</script>
<div class="content definePageMargin">
    <div class="borders margin20lefttop width90prc">
        <div class="borderbtm width100prc height25px">
            <p class="span-17 timelineboldorange arrowIMG  padding20left" style="width:50%">
                [@spring.message "ftlDefinedLabels.undoImport.undoImportInformation" /]
            </p>
        </div>

      <div class="margin20lefttop">
        <form method="post" action="processImportedTransactionUndo.ftl" name="processImportedTransactionUndoForm">
          <p class="font15 margin5bottom">
            <span class="orangeheading">
                [@spring.message "ftlDefinedLabels.undoImport.undoImportInformationReview" /] </br>
            </span>
          </p>
            <p class="font15 margin5bottom">
                <span class="fontnormal">
                    [@spring.message "ftlDefinedLabels.undoImport.reviewInformation" /] </br>
                
                </span>
            </p>
            [#list invalid_trxns?keys as key]
                [#assign arguments = ["${key}", "${invalid_trxns[key]}"]/]
                <span class="red">[@spring.messageArgs "ftlDefinedLabels.undoImport.invalidTrxn" arguments /]</span></br>
            [/#list]
            </br>
            [#list valid_trxns?keys as key]
                 [#assign arguments = ["${key}", "${valid_trxns[key]}"]/]
                <span >[@spring.messageArgs "ftlDefinedLabels.undoImport.validTrxn" arguments /]</span></br>
            [/#list]
         
          <div class="clear">&nbsp;</div>
            <div class="buttonsSubmitCancel margin20right">
                 <input type="hidden" name="fileName" value="${fileName}"/>
                 <input id='submit' type="submit" class="buttn" value="[@spring.message "submit"/]"/>
                 <input class="buttn2" type="submit" name="cancel" value="[@spring.message "cancel"/]" onclick='fnCancel(this.form)'/>
            </div> 
          <div class="clear">&nbsp;</div>
        </form>
      </div>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
  [@layout.footer/]
