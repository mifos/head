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
<span id="page.id" title="SysInfo"></span>
<div class=" content">

[@widget.crumbs breadcrumbs /]

    <div class="span-24 margin10lefttop">
        <div><span class=" orangeheading">[@spring.message "systemAdministration.viewsysteminformation.systeminformation" /]</span></div>
        <div class="fontBold">[@spring.message "systemAdministration.viewsysteminformation.welcometotheMifossysteminformationarea" /]</div>
        <div class="clear">&nbsp;</div>

        <table>
        <tr>
            <td class="width235pxfloatleft">[@spring.message "systemAdministration.viewsysteminformation.operatingSystemArch" /]</td>
            <td>${model.systemInformationDto.osName}/${model.systemInformationDto.osArch}/${model.systemInformationDto.osVersion}</td>
        </tr>
        <tr>
            <td>[@spring.message "systemAdministration.viewsysteminformation.javaVendorVersion" /]</td>
            <td>${model.systemInformationDto.javaVendor}/${model.systemInformationDto.javaVersion}</td>
        </tr>
        <tr>
            <td>[@spring.message "systemAdministration.viewsysteminformation.databaseVendor" /]</td>
            <td>${model.systemInformationDto.databaseVendor}/${model.systemInformationDto.databaseVersion}</td>
        </tr>
        <tr>
            <td>[@spring.message "systemAdministration.viewsysteminformation.databaseDriverVersion" /]</td>
            <td>${model.systemInformationDto.databaseDriverName}/${model.systemInformationDto.databaseDriverVersion} </td>
        </tr>
        <tr>
            <td>[@spring.message "systemAdministration.viewsysteminformation.applicationServer" /]</td>
            <td>${model.systemInformationDto.applicationServerInfo}</td>
        </tr>
        <tr>
            <td>[@spring.message "systemAdministration.viewsysteminformation.releaseName" /] </td>
            <td>${model.systemInformationDto.releaseName}</td>
        </tr>
        <tr>
            <td>[@spring.message "systemAdministration.viewsysteminformation.commitIdentifier" /]</td>
            <td>${model.systemInformationDto.commitIdentifier}</td>
        </tr>
        <tr>
            <td>[@spring.message "systemAdministration.viewsysteminformation.buildDate" /]</td>
            <td>${model.systemInformationDto.buildDate}</td>
        </tr>
        <tr>
            <td>[@spring.message "systemAdministration.viewsysteminformation.buildNumber" /]</td>
            <td>${model.systemInformationDto.buildNumber}</td>
        </tr>
        <tr>
            <td>[@spring.message "systemAdministration.viewsysteminformation.customReportsDirectory" /]</td>
            <td>${model.systemInformationDto.customReportsDirectory}</td>
        </tr>
        <tr>
            <td>[@spring.message "systemAdministration.viewsysteminformation.databaseSource" /]</td>
            <td id='sysinfo.text.databaseSource'>${model.systemInformationDto.infoSource}</td>
        </tr>
        <tr>
            <td>[@spring.message "systemAdministration.viewsysteminformation.databaseServer" /]</td>
            <td>${model.systemInformationDto.databaseServer}</td>
        </tr>
        <tr>
            <td>[@spring.message "systemAdministration.viewsysteminformation.databasePort" /]</td>
            <td>${model.systemInformationDto.databasePort}</td>
        </tr>
        <tr>
            <td>[@spring.message "systemAdministration.viewsysteminformation.databaseName" /]</td>
            <td>${model.systemInformationDto.databaseName}</td>
        </tr>
        <tr>
            <td>[@spring.message "systemAdministration.viewsysteminformation.databaseUser" /]</td>
            <td>${model.systemInformationDto.databaseUser}</td>
        </tr>
        <tr>
            <td>[@spring.message "systemAdministration.viewsysteminformation.dateandTime" /]</td>
            <td id='sysinfo.text.dateTime'>${model.systemInformationDto.dateTimeString}</td>
        </tr>
        <tr>
            <td>[@spring.message "systemAdministration.viewsysteminformation.dateandTimeinISO8601format" /]</td>
            <td>${model.systemInformationDto.dateTimeStringIso8601}</td>
        </tr>
        <tr>
            <td>[@spring.message "systemAdministration.viewsysteminformation.osuser" /]</td>
            <td>${model.systemInformationDto.osUser}</td>
        </tr>
        </table>
        <div>
           <div><a  href="appliedUpgrades.ftl">[@spring.message "systemAdministration.viewsysteminformation.appliedUpgrades" /]</a></div>
        </div>
    </div>
   	<div class="clear">&nbsp;</div>
   	<div class ="marginLeft20px">
    [@form.returnToPage  "AdminAction.do?method=load" "button.back" "systeminformation.button.back"/]
    </div>
  </div>
[/@adminLeftPaneLayout]