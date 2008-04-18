count=0;
function  DecimalMap(code_0,code_9,code_decimal)
{
	this.CODE_0=code_0;
	this.CODE_9=code_9;
	this.CODE_DECIMAL=code_decimal;
}
ENGLISH= new DecimalMap(48,57,46);
HINDI=new DecimalMap(2406,2415,46);
ARABIC= new DecimalMap(1632,1641,1643);
SPANISH= new DecimalMap(48,57,46);
function doValidation(element,min,max,fmt)
{
	var locale = document.all.h_user_locale.value;	
	var txt=element.value;
	if( txt == "" ) return true;	
	if(txt == "."||txt == "0."||txt == ".0") element.value="0";
	var patt0= new RegExp("^[0]*[\.]{0,1}[0]*$");
	var patt1= new RegExp("^[0]*[\.]{0,1}$");
	var patt2= new RegExp("^[\.]{0,1}[0]*$");
	if( true == patt0.test(txt.toString()) ) element.value="0";
	if( true == patt1.test(txt.toString()) ) element.value="0";
	if( true == patt2.test(txt.toString()) ) element.value="0";
	firstTimekeyPress=true
	return true;
	
	makeRegEx(fmt,txt);
    try
    {
		return eval(locale + "_validate( txt , " + min + "," + max + ")" ); 
	}
	catch ( e )
	{
		//most probabely language not supported 
	}
}
function makeRegEx(fmt,txt)
{
	whole_part_length="";
	fraction_part_length="";

	if(fmt==null)
	{
		fmt=txt;
	}
	else
	{
		fmt=fmt.toString();
		var index=fmt.indexOf('.');

		if ( -1 == index)
		{
			if ( fmt  < 1 ) 
			{
				   whole_part_length="";
				fraction_part_length="";
			}
			else
			{
				whole_part_length=fmt;
				fraction_part_length=""	
			}
		}
		else
		{
			if ( fmt.substring(0, index) < 1 )
			{
				whole_part_length="";
			}
			else
			{
				whole_part_length=fmt.substring(0, index);	
			}
			if ( fmt.substring(index+1,fmt.length) < 1 )
			{
				fraction_part_length="";
			}
			else
			{
			    fraction_part_length=fmt.substring(index+1,fmt.length);	
			}
		}
	}
	if(( whole_part_length ==  "" ) && ( fraction_part_length == "" ) )
	{
		pattern= new RegExp("^[0-9]*[\.]{0,1}[0-9]*$");
	}
	else if ( ( whole_part_length ==  "" )  && ( fraction_part_length != "" ) )
	{
		pattern= new RegExp("[\.][0-9]{1,"+fraction_part_length+"}$"); 
	}
	else if ( ( whole_part_length != "" )  && ( fraction_part_length ==  ""  ) )
	{
		pattern= new RegExp("^[0-9]{1,"+whole_part_length+"}$");
	}
	else
	{
		pattern=  new RegExp("^[0-9]{1,"+whole_part_length+"}[\.][0-9]{1,"+fraction_part_length+"}$"); 
	}
}
function genricConvertor(txt,convertor)
{
	var  char_code;
	var  asci_value;
	var asci_txt="";
	
	for(i=0;i<txt.length;i++)
	{
		char_code=txt.charCodeAt(i);
		asci_value=eval("convertor(" + char_code + ")");
		output = eval("String.fromCharCode(" + asci_value + ")");
		asci_txt += output;
	}
	return asci_txt;
}
function revGenricConvertor(txt,convertor)
{
	var  char_code;
	var  unicode_value;
	var unicode_txt="";
	
	for(i=0;i<txt.length;i++)
	{
		char_code=txt.charCodeAt(i);
		unicode_value=eval("convertor(" + char_code + ")");
		output = eval("String.fromCharCode(" + unicode_value + ")");
		unicode_txt += output;
	}
	return asci_txt;
}
function genericMap(char_code,code_0,code_9,code_decimal)
{
	if( ( char_code >= code_0 ) && ( char_code <= code_9 ) )
	{
		return ENGLISH.CODE_0 + ( char_code - code_0);
	}
	else if ( char_code == code_decimal) 
	{
		return ENGLISH.CODE_DECIMAL;
	}
	return char_code;
}
function RevGenericMap(char_code,code_0,code_decimal)
{
	if( ( char_code >= ENGLISH.CODE_0 ) && ( char_code <= ENGLISH.CODE_9 ) )
	{
		return code_0 + ( char_code - ENGLISH.CODE_0);
	}
	else if ( char_code == ENGLISH.CODE_DECIMAL) 
	{
		return ENGLISH.CODE_DECIMAL;
	}
	return char_code;
}
function chekDecimal(element, e,fmt)
{
    var locale = document.all.h_user_locale.value;
	try 
	{
		return eval ( locale + "_OnkeyPress(element, e,fmt ) " );
	}
	catch (e)
	{
	  //error may come if language not supported
	}
}
function genericOnkeyPress(fmt,element ,e,code_0,code_9,decimal_code)
{
	count=0;
	makeRegEx2(fmt);
	 e = (e) ? e : event;

	var  charCode= (e.intCode) ? e.intCode : ((e.keyCode) ? e.keyCode : ((e.which) ? e.which : 0));
		var txt=element.value;
		var index=txt.indexOf('.');
		if ( -1 != index && charCode==46 ) return false;
	//alert("charCode1="+charCode);	
	if( charCode ==13 || charCode ==110 || charCode ==190)
	{
		if(txt == ".") element.value="0.";
	}
	if(charCode == 190)
	{	
		return false;
	}
	if(charCode>105 && charCode<123 )
	{	
		return false;
	}
	else if(charCode>90 && charCode<96)
	{
		return false;
	}
	else if(charCode>122 && charCode<127)
	{
		return false;
	}
	else if(charCode>=32 && charCode<46)
	{
		return false;
	}
	else if(charCode>57 && charCode<65)
	{
		return false;
	}
	else if (charCode>64 && charCode<91)
	{
		return false;
	}
	else if(charCode==47)
	{
		return false;
	}
	else if(charCode == 8  || ( charCode > 34 && charCode < 41))
	{
		return false;
	}
	else
	{
		if( txt =="") return true;
		if(pattern2 !=null&&pattern2 !="")
		 if( true == pattern2.test(txt.toString()) ) return true;
		if(pattern3 !=null&&pattern3 !="")
		if( true == pattern3.test(txt.toString())) return true;
		if(pattern4 !=null&&pattern4 !="")
		if( true == pattern4.test(txt.toString())) return true;
		if(pattern5 !=null&&pattern5 !="")
		if( true == pattern5.test(txt.toString())) return true;
		element.value=savedText;
		return false;
	}
}
function onKeyPressForFileComponent(field)
{
	var  charCode= (event.intCode) ? event.intCode : ((event.keyCode) ? event.keyCode : ((event.which) ? event.which : 0));
	if( charCode ==13)
	{
		return false;
	}
	if(charCode>47 && charCode<58)
	{	
		return false;
	}
	if(charCode>96 && charCode<123)
	{	
		return false;
	}
	else if(charCode>90 && charCode<97)
	{
		return false;
	}
	else if(charCode>122 && charCode<127)
	{
		return false;
	}
	else if(charCode>=32 && charCode<46)
	{
		return false;
	}
	else if(charCode>57 && charCode<65)
	{
		return false;
	}
	else if(charCode==47)
	{
		return false;
	}
	else if(charCode == 8  || ( charCode > 34 && charCode < 41))
	{
		return false;
	}
	return true;
}
function saveText(element){
  if( count >0) return false;		
  savedText = element.value;
}
function makeRegEx2(fmt2)
{
	whole_part_length2="";
	fraction_part_length2="";
	//extract the digit before decimal and after decimal
	if ( fmt2 ==  "")
	{
	   alert("no fomat specified");
	}
	else
	{
		fmt2=fmt2.toString();
		var index=fmt2.indexOf('.');
		if ( -1 == index)
		{
			if ( fmt2  < 1 ) 
			{
				   whole_part_length2="";
				fraction_part_length2="";
			}
			else
			{
				whole_part_length2=fmt2;
				fraction_part_length2=""	
			}
		}
		else
		{
			if ( fmt2.substring(0, index) < 1 )
			{
				whole_part_length2="";
			}
			else
			{
				whole_part_length2=fmt2.substring(0, index);	
			}
			if ( fmt2.substring(index+1,fmt2.length) < 1 )
			{
				fraction_part_length2="";
			}
			else
			{
			    fraction_part_length2=fmt2.substring(index+1,fmt2.length);	
			}
		}
	}
	pattern1="";
	pattern2="";
	pattern3="";
	pattern4="";
	pattern5="";

	if(( whole_part_length2 ==  "" ) && ( fraction_part_length2 == "" ) )
	{
		// if nothing is specified then assume any number before decimal and after decimal
		pattern1= new RegExp("^[0-9]*[\.]{0,1}[0-9]*$");
	}
	else if ( ( whole_part_length2 ==  "" )  && ( fraction_part_length2 != "" ) )
	{
		pattern2= new RegExp("[\.][0-9]{1,"+fraction_part_length2+"}$"); 
	}
	else if ( ( whole_part_length2 != "" )  && ( fraction_part_length2 ==  ""  ) )
	{
		pattern3= new RegExp("^[0-9]{1,"+whole_part_length2+"}$");
	}
	else
	{
		pattern2= new RegExp("^[\.][0-9]{1,"+fraction_part_length2+"}$"); 
		pattern3= new RegExp("^[0-9]{1,"+whole_part_length2+"}$");
		pattern4= new RegExp("^[0-9]{1,"+whole_part_length2+"}[\.]$");
		pattern5= new RegExp("^[0-9]{1,"+whole_part_length2+"}[\.][0-9]{1,"+fraction_part_length2+"}$"); 
	}
}
function keyPress(fmt,element ,e){
 if(!genericOnkeyPress1(fmt,element ,e))	
	return false;
  if( count >0) return false;	
  else count++;	
}
function genericOnkeyPress1(fmt,element ,e)
{	
	makeRegEx2(fmt);
	 e = (e) ? e : event;

	var  charCode= (e.intCode) ? e.intCode : ((e.keyCode) ? e.keyCode : ((e.which) ? e.which : 0));
	var txt=element.value;
	var index=txt.indexOf('.');
	if ( -1 != index && charCode==46 ) return false;
	if( charCode ==13)
	{
		if(txt == ".") element.value="0.";
		if(txt == "."||txt == "0."||txt == ".0") element.value="0";
		var patt0= new RegExp("^[0]*[\.]{0,1}[0]*$");
		var patt1= new RegExp("^[0]*[\.]{0,1}$");
		var patt2= new RegExp("^[\.]{0,1}[0]*$");
		if( true == patt0.test(txt.toString()) ) element.value="0";
		if( true == patt1.test(txt.toString()) ) element.value="0";
		if( true == patt2.test(txt.toString()) ) element.value="0";
	}
	if(charCode>96 && charCode<123 )
	{	
		return false;
	}
	else if(charCode>90 && charCode<97)
	{
		return false;
	}
	else if(charCode>122 && charCode<127)
	{
		return false;
	}
	else if(charCode>=32 && charCode<37)
	{
		return false;
	}
	else if(charCode>40 && charCode<46)
	{
		return false;
	}
	else if(charCode>57 && charCode<65)
	{
		return false;
	}
	else if (charCode>64 && charCode<91)
	{
		return false;
	}
	else if(charCode==47)
	{
		return false;
	}	
	else
	{
		if( txt =="") return true;
		if(pattern2 !=null&&pattern2 !="")
		 if( true == pattern2.test(txt.toString()) ) return true;
		if(pattern3 !=null&&pattern3 !="")
		if( true == pattern3.test(txt.toString())) return true;
		if(pattern4 !=null&&pattern4 !="")
		if( true == pattern4.test(txt.toString())) return true;
		if(pattern5 !=null&&pattern5 !="")
		if( true == pattern5.test(txt.toString())) return true;
		return false;
	}
}