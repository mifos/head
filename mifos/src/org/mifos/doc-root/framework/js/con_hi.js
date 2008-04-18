/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

function hivalidate(txt,min,max)
{
      var 	asci_txt="";
	asci_txt=genricConvertor(txt,hindiToAsci);
      return envalidate(asci_txt,min,max);
}
function hindiToAsci(char_code)
{
	return genericMap(char_code,HINDI.CODE_0,HINDI.CODE_9,HINDI.CODE_DECIMAL);
}
function hiOnkeyPress(fmt,element,e)
{
	return genericOnkeyPress(fmt,element,e,HINDI.CODE_0,HINDI.CODE_9,HINDI.CODE_DECIMAL);
}
function asciiToHindi(char_code)
{
	return revGenericMap(char_code,HINDI.CODE_0,HINDI.CODE_DECIMAL);
}
function hiAlertRange(min,max)
{
   var hindiText1=revGenricConvertor(min.toString(),asciiToHindi);
   alert(hindiText1);
   var hindiText2=revGenricConvertor(max.toString(),asciiToHindi);
   alert(hindiText2);
}