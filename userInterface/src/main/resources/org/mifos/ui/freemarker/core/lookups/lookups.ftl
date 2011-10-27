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
Add all widgets here. Spring is configured to auto load this file, and by extension, all widgets declared here. 

Macros for looking up a message key given a value. 

This is a stop gap solution. Ideally lookup should be the responsibility of the 
class that defines the concept.
--]

[#include "recurrencyType.ftl" /]
[#include "savingsType.ftl" /]

[#-- 
Generic lookup macro. Lookup values are stored in "map". Map keys and lookupKey should be of type String. 
--]
[#macro fromMap map lookupKey]
	[#list map?keys as key]
		[#if key == lookupKey ]
			[@spring.message map[key] /]
		[/#if]
	[/#list]
[/#macro]

[#-- 
Non localised lookup macro. Lookup values are stored in "map". Map keys and lookupKey should be of type String. 
--]
[#macro fromNonLocalisedMap map lookupKey]
	[#list map?keys as key]
		[#if key == lookupKey ]
			${map[key]}
		[/#if]
	[/#list]
[/#macro]

[#-- 
Generic lookup macro. Lookup values are stored in a list of ListElement. 
--]
[#macro fromList lookupList lookupKey]
	[#list lookupList as lookup]
		[#if lookup.id?string == lookupKey]
			${lookup.name}
		[/#if]
	[/#list]
[/#macro]
