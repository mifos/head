function en_GB_validate(txt,min,max)
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
		if(txt >= min && txt <= max)
		{
			//passed all test
		}
		else
		{
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
function en_GB_OnkeyPress(fmt,element,e )
{
    return genericOnkeyPress(fmt,element,e,ENGLISH.CODE_0,ENGLISH.CODE_1,ENGLISH.CODE_DECIMAL);
}
function en_GB_AlertRange(min,max)
{
   alert(" Value entered should be in range : " + min + " - " + max);
}
function en_GB_Alertformat(format)
{
	alert( "Please enter a valid format  " + format); 
}
function en_GB_AlertMinNumber(min)
{
	alert( "Please enter atleast " +min+ " numbers" ); 
}