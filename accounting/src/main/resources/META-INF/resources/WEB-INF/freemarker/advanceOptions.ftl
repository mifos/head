 [#ftl]
[#--
* Copyright (c) 2005-2010 Grameen Foundation USA
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
<div style='margin-left:30px'> Current file name format = <b> {MFI prefix} (from Date) to (to Date).xml </b>
<br /> 
Example: ${fileName} 
<form>
<input type=button value="Change/Edit MFI Prefix" onclick="javascript:alert('Not implemented yet');" />
</form>
</div>
<br />
<div style='margin-left:30px'>
<img src="pages/framework/images/bullet_circle.gif" width="9" height="11"/><a href="#" onclick="javascript:deleteCacheDir();">Clean Data Store</a>
</div>