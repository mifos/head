
/**

 * con_hi.js    version: 1

 

 * Copyright © 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 *//********************************************************************************
* This file contains the javascript functions for the HINDI language
* author - Rajender saini
* version - 1
/


/*********************************************************************************
* function for doing the validation in hindi 
*  function name : validateHindi
*  arg1(txt) -  string you want to validate
*  arg2(min) - Min value it can take 
*  arg3(max) - Max value it can take 
*  usage - for validation of valuse entered in  hindi 
************************************************************************************/

function hivalidate(txt,min,max)
{
      var 	asci_txt="";
	asci_txt=genricConvertor(txt,hindiToAsci);
      return envalidate(asci_txt,min,max);
}
/*********************************************************************************
* function for doing the validation in hindi 
*  function name :  hindiToAsci
*  usage - for coverting hindi decimal numbers to ascii 
************************************************************************************/

function hindiToAsci(char_code)
{
	return genericMap(char_code,HINDI.CODE_0,HINDI.CODE_9,HINDI.CODE_DECIMAL);
}
/*************************************************************************************
* function enOnkeyPress
* arg1 (e) - event object
* usage - This function is called when key is pressed while focus is in inputbox and locale is hindi
***************************************************************************************/

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
