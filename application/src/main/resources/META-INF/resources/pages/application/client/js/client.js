 function chkForValidDates(){
	if(!(chkForDateOfBirthDate())){
		return false;
	}
	if (clientCustActionForm.fieldTypeList.length!= undefined && clientCustActionForm.fieldTypeList.length!= null){
		for(var i=0; i <=clientCustActionForm.fieldTypeList.length;i++){
			if (clientCustActionForm.fieldTypeList[i]!= undefined){
				if(clientCustActionForm.fieldTypeList[i].value == "3"){
					var customFieldDate = document.getElementById("customField["+i+"].fieldValue");
					var customFieldDateFormat = document.getElementById("customField["+i+"].fieldValueFormat");
				 	var customFieldDateYY = document.getElementById("customField["+i+"].fieldValueYY");
					var dateValue = customFieldDate.value;
					if(!(validateMyForm(customFieldDate,customFieldDateFormat,customFieldDateYY)))
						return false;
				}
			}
	 	}
	 }
}
function chkForDateOfBirthDate(){
	 var statusIdValue = document.getElementById("status").value;
  	 if (statusIdValue!=3){
  	 	var dateOfBirth = document.getElementById("dateOfBirth");
  	 	var dateOfBirthFormat = document.getElementById("dateOfBirthFormat");
  	 	var dateOfBirthYY = document.getElementById("dateOfBirthYY");
		return (validateMyForm(dateOfBirth,dateOfBirthFormat,dateOfBirthYY));
 	 }
 	 else{
 	 	return true;
 	 }
}