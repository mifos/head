 [#ftl]
[#--
* Copyright Grameen Foundation USA
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
<div id='table'>
        <table>
            <tr>
                <th class='drawtablehd' colspan=2>File name</th>
                <th class='drawtablehd' colspan=2>Action</th>
            </tr>
         [#list exports as instance ]
             <tr>
                 <td class="drawtablerow" colspan=2>${instance.fileName}</td>
                [#if instance.isExistInCache]
                <td class="drawtablerow">
                    <a href='renderAccountingData.ftl?fromDate=${instance.startDate}&toDate=${instance.endDate}' title='This export has already been generated'>
                        View Details
                    </a>
                    &nbsp; (Generated on ${instance.lastModified})
                </td>
                [#else]
                <td class="drawtablerow" colspan=2>
                    <a href='renderAccountingData.ftl?fromDate=${instance.startDate}&toDate=${instance.endDate}' title='This export has not been generated'>
                        Generate Export and View Details
                    </a>
                </td>
                [/#if]
             </tr>
        [/#list]
        </table>
  </div>