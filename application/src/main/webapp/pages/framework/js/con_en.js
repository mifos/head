function envalidate(txt,min,max)
{
	locale = document.all.h_user_locale.value;	
	if(min==null)
		min=1;
	if(max==null)
	{
		if(true == pattern.test(txt.toString()))
		{
			if(txt<min)
			    eval(locale+ "AlertMinNumber(min)");
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
			eval(locale+ "Alertformat(format)");
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
			  eval(locale + "AlertRange(min,max)");
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
		eval(locale+ "Alertformat(format)");
		return false;
	}
}
function enOnkeyPress(element,e,fmt )
{
    return genericOnkeyPress(fmt,element,e,ENGLISH.CODE_0,ENGLISH.CODE_1,ENGLISH.CODE_DECIMAL);
}
function enAlertRange(min,max)
{
   alert(" Value entered should be in range : " + min + " - " + max);
}
function enAlertformat(format)
{
	alert( "Please enter a valid format  " + format); 
}
function enAlertMinNumber(min)
{
	alert( "Please enter atleast " +min+ " numbers" ); 
}