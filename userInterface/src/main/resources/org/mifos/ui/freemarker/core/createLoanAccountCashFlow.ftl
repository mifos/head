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
[@headerOnlyLayout]
<script type="text/javascript" src="pages/cashflow/js/captureCashFlow.js"></script>

<span id="page.id" title="captureCashFlow"></span>

<div class="content_panel">
    <form name="captureCashFlowForm" action="${flowExecutionUrl}" method="POST" id="captureCashFlowForm">
        <center>
            <fieldset id="cashFlows" style="width:80%;">
              <legend style="font-size:1em;">[@spring.message "cashflow.heading"/]</legend>
              <div id="allErrorsDiv" class="allErrorsDiv" align="left">
                [@mifosmacros.showAllErrors "cashFlow.*"/]
              </div>
              <div id="note" align="left" >
                   <span class="fontBold"> [@spring.message "cashFlowForm.message"/] </span>
              </div>
              <br/>
              <table class="table_common" border="0">
                <thead>
                  <tr>
                        <th width="25%">[@spring.message "cashflow.months"/]</th>
                        <th width="15%"><span class="red">*</span>[@spring.message "cashflow.expense"/]</th>
                        <th width="15%"><span class="red">*</span>[@spring.message "cashflow.revenue"/]</th>
                        <th width="45%">[@spring.message "cashflow.notes"/]</th>
                  </tr>
                </thead>
                <tbody>
                    [#list cashFlow.monthlyCashFlows as monthlyCashFlow]
                    <tr>
                        <td width="25%">${mifos.date_formatter(monthlyCashFlow.dateTime, "MMMM", Application.LocaleSetting.locale)} ${monthlyCashFlow.year?c}</td>
                        <td width="15%">[@spring.formInput "cashFlow.monthlyCashFlows[${monthlyCashFlow_index}].expense", 'maxlength="30" style="width:100%;" class="amount"' /]</td>
                        <td width="15%">[@spring.formInput "cashFlow.monthlyCashFlows[${monthlyCashFlow_index}].revenue", 'maxlength="30" style="width:100%;" class="amount"' /]</td>
                        <td width="45%">[@spring.formInput "cashFlow.monthlyCashFlows[${monthlyCashFlow_index}].notes", 'maxlength="300" style="width:100%;"' /]</td>
                    </tr>
                    [/#list]
                </tbody>
              </table>
              [#if cashFlow.captureCapitalLiabilityInfo]
                  <table>
                      <tr>
                          <td><span class="red">*</span>[@spring.message "cashFlow.totalCapital"/]</td>
                          <td>[@spring.formInput "cashFlow.totalCapital" ,'class="total-capital-liability"'/]</td>
                      </tr>
                      <tr>
                          <td><span class="red">*</span>[@spring.message "cashFlow.totalLiability"/]</td>
                          <td>[@spring.formInput "cashFlow.totalLiability" ,'class="total-capital-liability"'/]</td>
                      </tr>
                  </table>
              [/#if]
                <center>
                    <div class="button_footer">
                        <div class="button_container">
                            <input type="submit" id="_eventId_capture" name="_eventId_capture" value="[@spring.message "cashflow.submit"/]" class="buttn"/>
                            &nbsp;
                            <input type="submit" id="_eventId_cancel" name="_eventId_cancel" value="[@spring.message "cashflow.cancel"/]"  class="cancel cancelbuttn"/>
                        </div>
                    </div>
                </center>
            </fieldset>
        </center>
    </form>
</div>
[/@headerOnlyLayout]