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

[@mifos.crumbs breadcrumbs /]

    <div class="span-24 margin10lefttop">
        <div><span class=" orangeheading">[@spring.message "systemAdministration.viewsysteminformation.systeminformation" /]</span></div>
        <div class="fontBold">[@spring.message "systemAdministration.viewsysteminformation.welcometotheMifossysteminformationarea" /]</div>
        <div class="clear">&nbsp;</div>
        
        <table border="1" style="word-break: break-all">
        
        <tr>
        <td style="word-wrap:break-word"><span class="width235pxfloatleft" id="sysinfo.text.operationSystem">[@spring.message "systemAdministration.viewsysteminformation.operatingSystemArch" /]</td>
        <td style="word-wrap:break-word"><span class="">${model.systemInformationDto.osName}/${model.systemInformationDto.osArch}/${model.systemInformationDto.osVersion}</span></td>
        </tr>
        
        <tr>
        <td><span class="width235pxfloatleft" id="sysinfo.text.javaVendor">[@spring.message "systemAdministration.viewsysteminformation.javaVendorVersion" /]</td>
        <td><span class="">${model.systemInformationDto.javaVendor}/${model.systemInformationDto.javaVersion}</span></td>
        </tr>
        
        <tr>
        <td><span class="width235pxfloatleft" id="sysinfo.text.databaseVendor">[@spring.message "systemAdministration.viewsysteminformation.databaseVendor" /]</span></td>
        <td><span class="">${model.systemInformationDto.databaseVendor}/${model.systemInformationDto.databaseVersion}</span></td>
        </tr>
        
        <tr>
        <td><span class="width235pxfloatleft" id="sysinfo.text.driverName">[@spring.message "systemAdministration.viewsysteminformation.databaseDriverVersion" /]</span></td>
        <td><span class="">${model.systemInformationDto.databaseDriverName}/${model.systemInformationDto.databaseDriverVersion}</span> </td>
        </tr>
        
        <tr>
        <td><span class="width235pxfloatleft" id="sysinfo.text.applicationServerInfo">[@spring.message "systemAdministration.viewsysteminformation.applicationServer" /]</span></td>
        <td><span class="">${model.systemInformationDto.applicationServerInfo}</span></td>
        </tr>
        
        <tr>
        <td><span class="width235pxfloatleft" id="sysinfo.text.releaseName">[@spring.message "systemAdministration.viewsysteminformation.releaseName" /]</span> </td>
        <td><span class="">${model.systemInformationDto.releaseName}</span></td>
        </tr>
        
        <tr>
        <td><span class="width235pxfloatleft" id="sysinfo.text.commitIdentifier">[@spring.message "systemAdministration.viewsysteminformation.commitIdentifier" /]</span></td>
        <td><span class="">${model.systemInformationDto.commitIdentifier}</span></td>
        </tr>
        
        <tr>
        <td><span class="width235pxfloatleft" id="sysinfo.text.buildDate">[@spring.message "systemAdministration.viewsysteminformation.buildDate" /]</span></td>
        <td><span class="">${model.systemInformationDto.buildDate}</span></td>
        </tr>
        
        <tr>
        <td><span class="width235pxfloatleft" id="sysinfo.text.buildNumber">[@spring.message "systemAdministration.viewsysteminformation.buildNumber" /]</span></td>
        <td><span class="">${model.systemInformationDto.buildNumber}</span></td>
        </tr>
        
        <tr>
        <td><span class="width235pxfloatleft" id="sysinfo.text.buildCustomReportsDir">[@spring.message "systemAdministration.viewsysteminformation.customReportsDirectory" /]</span></td>
        <td><span class="">${model.systemInformationDto.customReportsDirectory}</span></td>
        </tr>
        
        <tr>
        <td><span class="width235pxfloatleft" id="sysinfo.text.infoSource">[@spring.message "systemAdministration.viewsysteminformation.databaseSource" /]</span></td>
        <td><span class="">${model.systemInformationDto.infoSource}</span></td>
        </tr>
        
        <tr>
        <td><span class="width235pxfloatleft" id="sysinfo.text.datebaseServer">[@spring.message "systemAdministration.viewsysteminformation.databaseServer" /]</span></td>
        <td><span class="">${model.systemInformationDto.databaseServer}</span></td>
        </tr>
        
        <tr>
        <td><span class="width235pxfloatleft" id="sysinfo.text.databasePort">[@spring.message "systemAdministration.viewsysteminformation.databasePort" /]</span></td>
        <td><span class="">${model.systemInformationDto.databasePort}</span></td>
        </tr>
        
        <tr>
        <td><span class="width235pxfloatleft" id="sysinfo.text.datebaseName">[@spring.message "systemAdministration.viewsysteminformation.databaseName" /]</span></td>
        <td><span class="">${model.systemInformationDto.databaseName}</span></td>
        </tr>
        
        <tr>
        <td><span class="width235pxfloatleft" id="sysinfo.text.databaseUser">[@spring.message "systemAdministration.viewsysteminformation.databaseUser" /]</span></td>
        <td><span class="">${model.systemInformationDto.databaseUser}</span></td>
        </tr>
        
        <tr>
        <td><span class="width235pxfloatleft">[@spring.message "systemAdministration.viewsysteminformation.dateandTime" /]</span></td>
        <td><span id="sysinfo.text.dateTime" class="">${model.systemInformationDto.dateTimeString}</span></td>
        </tr>
        
        <tr>
        <td><span class="width235pxfloatleft" id="sysinfo.text.dateTimeIso8601">[@spring.message "systemAdministration.viewsysteminformation.dateandTimeinISO8601format" /]</span></td>
        <td><span class="">${model.systemInformationDto.dateTimeStringIso8601}</span></td>
        </tr>
        
        <tr>
        <td><span class="width235pxfloatleft" id="sysinfo.text.osUser">[@spring.message "systemAdministration.viewsysteminformation.osuser" /]</span></td>
        <td><span class="">${model.systemInformationDto.osUser}</span></td>
        </tr>
        
        </table>
        
        <div>
       	<div><span class="width235pxfloatleft" id="sysinfo.text.applicationVersion"><a  href="appliedUpgrades.ftl">[@spring.message "systemAdministration.viewsysteminformation.appliedUpgrades" /]</a></span></div>
        </div>
    </div>
  </div>
[/@adminLeftPaneLayout]