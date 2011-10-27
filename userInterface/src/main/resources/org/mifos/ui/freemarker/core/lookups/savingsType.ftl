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

[#-- 
Lookup savings type name given an ID. 
Defined in org.mifos.accounts.productdefinition.util.helpers.SavingsType
--]
[#macro savingsType savingsTypeId]
    [#-- MANDATORY((short) 1), VOLUNTARY((short) 2) --]
	[#if savingsTypeId == 1]
		[@spring.message "ftl.macro.lookup.savingsType.mandatory" /]
	[#elseif savingsTypeId == 2]
		[@spring.message "ftl.macro.lookup.savingsType.voluntary" /]
	[#else]
		[@spring.message "ftl.macro.lookup.savingsType.undefined" /]
	[/#if]	
[/#macro]
