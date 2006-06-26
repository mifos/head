/********************************************************************************
* This file contains the javascript functions for the ENGLISH language
* author -rajenders
* version - 1
/

/*********************************************************************************
* function for doing the validation in english 
*  function name : validateEnglish
*  arg1(txt) -  string you want to validate
*  arg2(min) - Min value it can take 
*  arg3(max) - Max value it can take 
*  usage - for validation of valuse entered in english
************************************************************************************/

function en_US_validate(txt,min,max)
{
	var locale = document.all.h_user_locale.value;
	
	
	if(min==null)
	//bug id 25531   changed the min from 1 to 0
		min=0;
	
	
	if(max==null)
	{
				
		if(true == pattern.test(txt.toString()))
		{
			if(txt<min)
			//bug id 25531   changed the message.
				 eval(locale_+ "AlertMinNumber(min)");
			max=txt;
		}
		else
		{
		
			var format="";
		    for ( i = 0 ; i < whole_part_length ; i++)
		    {
		     format += "X"
		    }
		    if (fraction_part_length > 1)
		    {
			    format += ".";
			    for ( i = 0 ; i < fraction_part_length ; i++)
			    {
			     format += "X"
		    	}
		    }

			eval(locale+ "_Alertformat(format)");
				return false;
				
		}

		
		
	}
	
	
	
		
	else if( true == pattern.test(txt.toString()))

	{ 
		

		//passed pattern test  check for min max
		if(txt >= min && txt <= max)
		{
			//passed all test
		}
		else
		{
			  //    alert("element should be in range "+min+" - to - " +max );
			  eval(locale + "_AlertRange(min,max)");
				  return false;	
		}

	}
	else
	{
	   var format="";
		for ( i = 0 ; i < whole_part_length ; i++)
		{
		 format += "X"
		}
		if (fraction_part_length > 1)
		{
			format += ".";
			for ( i = 0 ; i < fraction_part_length ; i++)
			{
			 format += "X"
			}
		}
		
			eval(locale+ "_Alertformat(format)");
			return false;
		
	  


	}

	
}
/*************************************************************************************
* function enOnkeyPress
* arg1 (e) - event object
* usage - This function is called when key is pressed while focus is in inputbox and locale is english
***************************************************************************************/

function en_US_OnkeyPress(fmt,element,e )
{
	
    return genericOnkeyPress(fmt,element,e,ENGLISH.CODE_0,ENGLISH.CODE_1,ENGLISH.CODE_DECIMAL);
}

function en_US_AlertRange(min,max)
{
   alert(" Value entered should be in range : " + min + " - " + max);
}
function en_US_Alertformat(format)
{
	alert( "Please enter a valid format  " + format); 
}

function en_US_AlertMinNumber(min)
{
	alert( "Please enter atleast " +min+ " numbers" ); 
}