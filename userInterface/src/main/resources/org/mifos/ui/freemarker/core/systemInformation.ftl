[#ftl]
[#import "spring.ftl" as spring]

[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  [#include "adminLeftPane.ftl" ]
   <span id="page.id" title="SysInfo" />
   <div class=" content leftMargin180">
  	<form method="" action="" name="formname">
    <div class="span-24">
        <div><span class=" orangeheading">[@spring.message "systemInformation" /]</span></div>
        <div class="fontBold">[@spring.message "welcometotheMifossysteminformationarea" /]</div>
        <div class="clear">&nbsp;</div>
        <div class="clear">&nbsp;</div>
        <div class="span-22">
        	<div><span class="span-5" id="sysinfo.text.operationSystem">[@spring.message "operatingSystemArch" /]</span><span class="span-16">${model.systemInformationDto.osName}/${model.systemInformationDto.osArch}/${model.systemInformationDto.osVersion}</span></div>
            <div><span class="span-5" id="sysinfo.text.javaVendor">[@spring.message "javaVendorVersion" /]</span><span class="span-16">${model.systemInformationDto.javaVendor}/${model.systemInformationDto.javaVersion}</span></div>
            <div><span class="span-5" id="sysinfo.text.databaseVendor">[@spring.message "databaseVendor" /]</span><span class="span-16">${model.systemInformationDto.databaseVendor}/${model.systemInformationDto.databaseVersion}</span></div>
            <div><span class="span-5" id="sysinfo.text.driverName">[@spring.message "databaseDriverVersion" /]</span><span class="span-16">${model.systemInformationDto.databaseDriverName}/${model.systemInformationDto.databaseDriverVersion}</span></div>
            <div><span class="span-5" id="sysinfo.text.applicationServerInfo">[@spring.message "applicationServer" /]</span><span class="span-16">${model.systemInformationDto.applicationServerInfo}</span></div>
            <div><span class="span-5" id="sysinfo.text.applicationVersion">[@spring.message "mifosDatabaseVersion" /]</span><span class="span-16">${model.systemInformationDto.applicationVersion}</span></div>
            <div><span class="span-5" id="sysinfo.text.buildCustomReportsDir">[@spring.message "customReportsDirectory" /]</span><span class="span-16">${model.systemInformationDto.customReportsDirectory}</span></div>
            <div><span class="span-5" id="sysinfo.text.commitIdentifier">[@spring.message "commitIdentifier" /]</span><span class="span-16">${model.systemInformationDto.commitIdentifier}</span></div>  
            <div><span class="span-5" id="sysinfo.text.releaseName">[@spring.message "releaseName" /]</span><span class="span-16">${model.systemInformationDto.releaseName}</span></div>
            <div><span class="span-5" id="sysinfo.text.buildDate">[@spring.message "buildDate" /]</span><span class="span-16">${model.systemInformationDto.buildDate}</span></div>
            <div><span class="span-5" id="sysinfo.text.buildNumber">[@spring.message "buildNumber" /]</span><span class="span-16">${model.systemInformationDto.buildNumber}</span></div>
            <div><span class="span-5" id="sysinfo.text.infoSource">[@spring.message "databaseSource" /]</span><span class="span-16">${model.systemInformationDto.infoSource}</span></div>
            <div><span class="span-5" id="sysinfo.text.datebaseServer">[@spring.message "databaseServer" /]</span><span class="span-16">${model.systemInformationDto.databaseServer}</span></div>
            <div><span class="span-5" id="sysinfo.text.databasePort">[@spring.message "databasePort" /]</span><span class="span-16">${model.systemInformationDto.databasePort}</span></div>
            <div><span class="span-5" id="sysinfo.text.datebaseName">[@spring.message "databaseName" /]</span><span class="span-16">${model.systemInformationDto.databaseName}</span></div>
            <div><span class="span-5" id="sysinfo.text.databaseUser">[@spring.message "databaseUser" /]</span><span class="span-16">${model.systemInformationDto.databaseUser}</span></div>
            <div><span class="span-5" id="sysinfo.text.dateTime">[@spring.message "dateandTime" /]</span><span class="span-16">${model.systemInformationDto.dateTimeString}</span></div>
            <div><span class="span-5" id="sysinfo.text.dateTimeIso8601">[@spring.message "dateandTimeinISO8601format" /]</span><span class="span-16">${model.systemInformationDto.dateTimeStringIso8601}</span></div>
            <div><span class="span-5" id="sysinfo.text.osUser">[@spring.message "osuser" /]</span><span class="span-16">${model.systemInformationDto.osUser}</span></div>
        </div>
	</div>
   	</form> 
  </div>
  [@mifos.footer/]

<!-- >>>>>>> f3bfd9df77ade953a70816f101a6ea9a8dfd6440 -->
